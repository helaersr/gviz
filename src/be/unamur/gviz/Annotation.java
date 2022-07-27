package be.unamur.gviz;

import java.sql.ResultSet;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import be.unamur.gviz.Database.Id;

public class Annotation {
  public enum Field {
  	INTERPRO("InterPro id","http://www.ebi.ac.uk/interpro/ISearch?query="),
  	KEGG_ID("KEGG id","http://www.genome.jp/dbget-bin/www_bget?hsa:"),
  	KEGG_ENZYME("KEGG Enzyme id","http://www.genome.jp/dbget-bin/www_bget?"),
  	PUBMED("PubMed references","http://www.ncbi.nlm.nih.gov/pubmed/"),
  	BP("Biological processes","http://amigo.geneontology.org/cgi-bin/amigo/term-details.cgi?term=GO:"),
  	CC("Cellular components","http://amigo.geneontology.org/cgi-bin/amigo/term-details.cgi?term=GO:"),
  	MF("Molecular functions","http://amigo.geneontology.org/cgi-bin/amigo/term-details.cgi?term=GO:");
  private final String name;
  private final String http;
  Field(String name, String http){this.name = name;this.http=http;}
  public String getName(){
  	return name ;
  }
  public String toURL(String id){
  	return http + id;
  }
  public static Field valueOfName(String name) throws Exception {
  	for (Field i : values()){
  		if (i.name.equals(name)) return i;
  	}
  	throw new Exception("Unknown field : " + name);
  }
  }
	
  public enum PathWaySource {
  	Kegg("Kegg","http://www.genome.jp/kegg-bin/show_pathway?"),
  	GenMAPP("GenMAPP","");
  private final String name;
  private final String http;
  PathWaySource(String name, String http){this.name = name;this.http=http;}
  public String getName(){
  	return name ;
  }
  public String toURL(String id){
  	return http + id;
  }
  public static PathWaySource valueOfName(String name) throws Exception {
  	for (PathWaySource i : values()){
  		if (i.name.equals(name)) return i;
  	}
  	throw new Exception("Unknown pathway source : " + name);
  }
  }
  
	public final String probeId;
	private final EnumMap<Id, Set<String>> identifiers = new EnumMap<Id, Set<String>>(Id.class);
	public final String description;
	public final String chromosomeLocation;
	public final Set<Ontology> interPro = new TreeSet<Ontology>();
	public final Set<Ontology> bp = new TreeSet<Ontology>();
	public final Set<Ontology> cc = new TreeSet<Ontology>();
	public final Set<Ontology> mf = new TreeSet<Ontology>();
	public final Set<String> keggEnzymeId = new TreeSet<String>();
	public final Set<String> pubmed = new TreeSet<String>();
	public final Set<Pathway> pathways = new TreeSet<Pathway>();
	
	private class Ontology implements Comparable<Ontology> {
		public final String id;
		public final String description;
		public final String source;
		
		public Ontology(String dbString){
			String[] parts = dbString.split(" // ");
			id = parts[0];
			description = (parts.length > 1) ? parts[1] : "";
			source = (parts.length > 2) ? parts[2] : "";
		}

		public int compareTo (Ontology o) {
			return id.compareTo(o.id);
		}
	}
	
	private class Pathway implements Comparable<Pathway> {
		public final String id;
		public final String name;
		public final PathWaySource source;
		
		public Pathway(String dbString){
			String[] parts = dbString.split(" // ");
			name = parts[0].replace('_', ' ');
			source = PathWaySource.valueOf((parts.length > 1) ? parts[1] : "");
			id = "-";
		}
		
		public Pathway(String id, String name){
			this.id = id.replace("path:", "");
			this.name = name;
			source = PathWaySource.Kegg;
		}
		
		public int compareTo (Pathway p) {
			return name.compareTo(p.name);
		}
	}
	
	public Annotation(String probeId) throws Exception {
		this.probeId = probeId;
		ResultSet res = gViz.DB.accessPathex().select("SELECT * FROM "+gViz.DB.annotation_table+" WHERE ProbeSetID = '"+probeId+"';");
		if (res.next()){
			for (Id identifier : Id.values()){
				Set<String> set = new TreeSet<String>();
				for (String id : res.getString(identifier.field()).split(" /// ")){
					if (!id.equals("---")) set.add(id);
				}
				identifiers.put(identifier, set);
			}
			description = res.getString("GeneTitle");
			chromosomeLocation = res.getString("ChromosomalLocation");
			for (String dbString : res.getString("InterPro").split(" /// ")){
				if (!dbString.equals("---"))	interPro.add(new Ontology(dbString));
			}
			for (String dbString : res.getString("GeneOntologyBiologicalProcess").split(" /// ")){
				if (!dbString.equals("---"))	bp.add(new Ontology(dbString));
			}
			for (String dbString : res.getString("GeneOntologyCellularComponent").split(" /// ")){
				if (!dbString.equals("---")) cc.add(new Ontology(dbString));
			}
			for (String dbString : res.getString("GeneOntologyMolecularFunction").split(" /// ")){
				if (!dbString.equals("---")) mf.add(new Ontology(dbString));
			}
			for (String dbString : res.getString("EC").split(" /// ")){
				if (!dbString.equals("---")) keggEnzymeId.add(dbString);
			}
			for (String dbString : res.getString("Pathway").split(" /// ")){
				if (!dbString.equals("---")) pathways.add(new Pathway(dbString));
			}
		}else{
			throw new Exception("Probe id " + probeId + " was not found in the database.");
		}
		res.close();
		for (String entrez : identifiers.get(Id.ENTREZ)){
			res = gViz.DB.accessPathex().select("SELECT pubmed_id FROM gene_pubmed WHERE entrezgene_id = '"+entrez+"';");
			while (res.next()){
				pubmed.add(res.getString(1));
			}
			res.close();
			res = gViz.DB.accessPathex().select("SELECT path FROM path_gene WHERE gene = 'hsa:"+entrez+"';");
			Set<String> temp = new HashSet<String>(); 
			while (res.next()){
				temp.add(res.getString(1));
			}
			res.close();
			res = gViz.DB.accessPathex().select("SELECT path, name FROM path_name WHERE path IN ("+Database.makeSqlList(temp)+");");
			while (res.next()){
				String path = res.getString(1);
				temp.remove(path);
				pathways.add(new Pathway(path, res.getString(2)));
			}
			res.close();
			for (String p : temp){
				pathways.add(new Pathway(p, "?"));
			}
		}
	}
	
	public int getCount(Id identifier){
		return identifiers.get(identifier).size();
	}
	
	public int getCount(Field field){
		switch(field){
		case INTERPRO:
			return interPro.size();
		case KEGG_ID:
			return identifiers.get(Id.ENTREZ).size();
		case KEGG_ENZYME:
			return keggEnzymeId.size();
		case PUBMED:
			return pubmed.size();
		case BP:
			return bp.size();
		case CC:
			return cc.size();
		case MF:
			return mf.size();
		default:
			return 0;
		}
	}
	
	public int getCount(PathWaySource pathwaySource){
		int count = 0;
		for (Pathway p : pathways){
			if (p.source == pathwaySource) count++;
		}
		return count;
	}
	
	public String getURL(Id identifier){
		StringBuilder res = new StringBuilder();
		for (String id : identifiers.get(identifier)){
			res.append("<a href=\""+identifier.toURL(id)+"\">"+id+"</a>, ");
		}
		if (res.length() > 0) res.setLength(res.length()-2);
		return res.toString();
	}
	
	public String getURL(Field field){
		StringBuilder res = new StringBuilder();
		switch(field){
		case INTERPRO:
			for (Ontology o : interPro){
				res.append("<a href=\""+field.toURL(o.id)+"\">"+o.id+"</a> : "+o.description+" (pval of "+o.source+"), ");
			}
			if (res.length() > 0) res.setLength(res.length()-2);
			break;
		case KEGG_ID:
			for (String id : identifiers.get(Id.ENTREZ)){
				res.append("<a href=\""+field.toURL(id)+"\">hsa:"+id+"</a>, ");
			}
			if (res.length() > 0) res.setLength(res.length()-2);
			break;
		case KEGG_ENZYME:
			for (String id : keggEnzymeId){
				res.append("<a href=\""+field.toURL(id)+"\">"+id+"</a>, ");
			}			
			if (res.length() > 0) res.setLength(res.length()-2);
			break;
		case PUBMED:
			for (String id : pubmed){
				res.append("<a href=\""+field.toURL(id)+"\">"+id+"</a>, ");
			}
			if (res.length() > 0) res.setLength(res.length()-2);
			break;
		case BP:
			for (Ontology o : bp){
				res.append("<a href=\""+field.toURL(o.id)+"\">"+o.id+"</a> : "+o.description+" (<i>"+o.source+"</i>)<br>");
			}
			if (res.length() > 0) res.setLength(res.length()-3);
			break;
		case CC:
			for (Ontology o : cc){
				res.append("<a href=\""+field.toURL(o.id)+"\">"+o.id+"</a> : "+o.description+" (<i>"+o.source+"</i>)<br>");
			}
			if (res.length() > 0) res.setLength(res.length()-3);
			break;
		case MF:
			for (Ontology o : mf){
				res.append("<a href=\""+field.toURL(o.id)+"\">"+o.id+"</a> : "+o.description+" (<i>"+o.source+"</i>)<br>");
			}
			if (res.length() > 0) res.setLength(res.length()-3);
			break;
		default:
			break;
		}
		return res.toString();
	}
	
	public String getURL(PathWaySource source){
		StringBuilder res = new StringBuilder();
		for (Pathway p : pathways){
			if (p.source == source){
				switch(source){
				case GenMAPP:
					res.append(p.name+"<br>");
					break;
				case Kegg:
					res.append("<a href=\""+p.source.toURL(p.id)+"\">"+p.id+"</a> : "+p.name+"<br>");
					break;
				}			
			}
		}
		if (res.length() > 0) res.setLength(res.length()-3);
		return res.toString();		
	}
}
