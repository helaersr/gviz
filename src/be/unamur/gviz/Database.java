package be.unamur.gviz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import be.unamur.gviz.SearchBox.Criteria;

public class Database {
	private final String host;
	private final String user;
	private final String passwd;
	public final String annotation_table;
  private String driver = "com.mysql.jdbc.Driver";
  private final Driver pathex;
  
  public enum Id {
  	PROBE_SET("Probe set id","ProbeSetID","https://www.affymetrix.com/LinkServlet?probeset="),
  	ENTREZ("Entrez gene id","EntrezGene","http://www.ncbi.nlm.nih.gov/gene/"),
  	ENSEMBL("Ensembl gene id","Ensembl","http://www.ensembl.org/Homo_sapiens/geneview?gene="),
  	GENE_SYMBOL("Gene symbol","Genesymbol","http://www.ncbi.nlm.nih.gov/gene?term="),
  	UNIGENE("Unigene id","UniGeneID","http://www.ncbi.nlm.nih.gov/unigene?term="),
  	SWISS_PROT("Swiss-Prot id","SwissProt","http://www.uniprot.org/uniprot/"),
  	OMIM("OMIM id","OMIM","http://www.ncbi.nlm.nih.gov/omim/");
  private final String name, field, http;
  Id(String name, String field, String http){
  	this.name = name;this.field = field;this.http=http;}
  public String toString(){
  	return name ;
  }
  public String field(){
  	return field;
  }
  public String toURL(String id){
  	return http + id;
  }
  public static Id valueOfName(String name) throws Exception {
  	for (Id i : values()){
  		if (i.name.equals(name)) return i;
  	}
  	throw new Exception("Unknown id : " + name);
  }
  }
  
  public class Driver {
    private Connection con;
    private Statement stm;
    private String database;
    
    public Driver() {
    }
    
    /**
     * Connect to a database on a mysql server, using the current host.
     * @param database String
     * @throws Exception
     */
    private void connect(String database) throws Exception {
      this.database = database;
      Class.forName(driver);
      con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, passwd);
      stm = con.createStatement();
    }

    /**
     * Disconnect from the mysql server
     */
    public void disconnect() {
      try {
        stm.close();
        con.close();
      } catch (Exception ignored) {}
    }

    /**
     * Perform a select on the database.
     * @param sqlStatement the sql statement
     * @throws Exception sql
     * @return the SQL resultset
     */
    public ResultSet select(String sqlStatement) throws Exception {
      //System.out.println(sqlStatement);
      if (con.isClosed()) {
        connect(database);
      }
      try {
        return stm.executeQuery(sqlStatement);
      } catch (SQLException ex) {
        System.out.println("SQL statement throwing exception : " + sqlStatement);
        throw ex;
      }
    }

    /**
     * Perform an update of the database.
     * @param sqlStatement the sql statement
     * @throws Exception sql
     * @return int
     */
    public int update(String sqlStatement) throws Exception {
      //System.out.println(sqlStatement);
      if (stm.getConnection().isClosed()) {
        disconnect();
        connect(database);
      }
      try {
        return stm.executeUpdate(sqlStatement);
     } catch (SQLException ex) {
        System.out.println("SQL statement throwing exception : " + sqlStatement);
        throw ex;
      }
    }

    /**
     * Cancel active query
     * @throws Exception
     */
    public void cancel() throws Exception {
      stm.cancel();
    }
  }
  
  public Database(String host, String user, String passwd, String annotationTable) throws Exception {
  	this.host = host;
  	this.user = user;
  	this.passwd = passwd;
  	if (annotationTable != null){
    	this.annotation_table = annotationTable;
  	}else{
  		this.annotation_table = "probeset_annotation";
  	}
  	pathex = new Driver();
  	pathex.connect("pe_gviz");
  }
  
  public final Driver accessPathex(){
  	return pathex;
  }
  
  /**
   * Return all elements of given Set in sql format : 'elem1','elem2','elem3'...
   * @param h a Set of elements
   * @return elements in sql format
   */
  public static String makeSqlList(Collection<?> h) {
    return h.toString().replaceAll(", ", "','").replace('[', '\'').replace(']', '\'');
  }

  public Set<String> convert(String id, Id source, Id target) throws Exception{
  	ResultSet res = pathex.select("SELECT DISTINCT " + target.field() + " FROM "+annotation_table+" WHERE " + source.field() + " = '"+id+"';");
  	Set<String> set = new TreeSet<String>();
  	while (res.next()){
  		String[] split = res.getString(1).split(" /// ");
  		for (String s : split)
  			if (!s.equals("---")) set.add(s);	
  	}
  	return set;
  }
  
  public Map<String,Set<String>> convert(Collection<String> ids, Id source, Id target) throws Exception {
  	ResultSet res = pathex.select("SELECT DISTINCT " + source.field() + ", " + target.field() + 
  			" FROM "+annotation_table+" WHERE " + source.field() + " IN("+makeSqlList(ids)+");");
  	Map<String,Set<String>> map = new HashMap<String, Set<String>>();
  	while (res.next()){
  		String key = res.getString(1);
  		Set<String> set;
  		if (map.containsKey(key)) set = map.get(key);
  		else set = new HashSet<String>();
  		String[] split = res.getString(2).split(" /// ");
  		for (String s : split)
  			if (!s.equals("---")) set.add(s);
  		map.put(key, set);
  	}
  	return map;
  }
    
  public Set<String> search(String query, Criteria criteria, Id datasetIds) throws Exception {
  	ResultSet res = pathex.select("SELECT DISTINCT " + datasetIds.field() + 
  			" FROM "+annotation_table+" WHERE INSTR(lcase("+criteria.field()+"), lcase('" + query+"'));");
  	Set<String> set = new TreeSet<String>();
  	while (res.next()){
  		String[] split = res.getString(1).split(" /// ");
  		for (String s : split)
  			if (!s.equals("---")) set.add(s);	
  	}
  	return set;
  }
  
  public void fillPathwaysMaps(Map<String, String> pathwayIdToDesc, Map<String, String> pathwayDescToId) throws Exception {
  	ResultSet res = pathex.select("SELECT path, name FROM path_name;");
  	while (res.next()){
  		String id = res.getString(1);
  		String path = res.getString(2);
  		pathwayIdToDesc.put(id, path);
  		pathwayDescToId.put(path, id);
  	}
  }
  
  public Map<String,Set<String>> fetchGenesInPathway(String pathId) throws Exception {
  	ResultSet res = pathex.select("SELECT DISTINCT gene1, gene2 FROM path_gene, gene_gene_score WHERE path = '"+pathId+"' AND (gene = gene1 OR gene = gene2);");
  	Map<String,Set<String>> map = new HashMap<String, Set<String>>();
  	while (res.next()){
  		String gene1 = res.getString(1).replace("hsa:", "");
  		String gene2 = res.getString(2).replace("hsa:", "");
  		Set<String> val1;
  		if (map.containsKey(gene1)) val1 = map.get(gene1);
  		else val1 = new HashSet<String>();
  		val1.add(gene2);
  		map.put(gene1, val1);
  		Set<String> val2;
  		if (map.containsKey(gene2)) val2 = map.get(gene2);
  		else val2 = new HashSet<String>();
  		val2.add(gene1);
  		map.put(gene2, val2);
  	}
  	res.close();
  	return map;
  }
}
