package be.unamur.gviz;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import javax.swing.border.*;

public class SearchBox extends JFrame implements VertexSelection {

	public enum Criteria {
  	DESCRIPTION("Gene description", "GeneTitle"),
  	PROBE_SET("Probe set id","ProbeSetID"),
  	ENTREZ("Entrez gene id","EntrezGene"),
  	ENSEMBL("Ensembl gene id","Ensembl"),
  	GENE_SYMBOL("Gene symbol","Genesymbol"),
  	UNIGENE("Unigene id","UniGeneID"),
  	SWISS_PROT("Swiss-Prot id","SwissProt"),
  	OMIM("OMIM id","OMIM"),
  	INTER_PRO("InterPro id","InterPro"),
  	KEGG_ID("KEGG id","EntrezGene"),
  	KEGG_ENZYME("KEGG Enzyme id","EC"),
  	BP("Biological processes","GeneOntologyBiologicalProcess"),
  	CC("Cellular components","GeneOntologyCellularComponent"),
  	MF("Molecular functions","GeneOntologyMolecularFunction");
  private final String name, field;
  Criteria(String name, String field){
  	this.name = name;this.field = field;}
  public String toString(){
  	return name ;
  }
  public String field(){
  	return field;
  }
  public static Criteria valueOfName(String name) throws Exception {
  	for (Criteria i : values()){
  		if (i.name.equals(name)) return i;
  	}
  	throw new Exception("Unknown criteria : " + name);
  }
  }
	
	private final MainFrame parent;
	private final GridBagLayout gridBagLayout1 = new GridBagLayout();
	private final JTextPane searchTextPane = new JTextPane();
	private final JTextField searchTextField = new JTextField();
	private final JButton searchButton = new JButton();
	private final JButton clearButton = new JButton();
	private final JSplitPane jSplitPane1 = new JSplitPane();
	private final JScrollPane infoScrollPane = new JScrollPane();
	private final JPanel listPanel = new JPanel();
  private final NodeInfoPanel descriptionPanel;
  private final GridBagLayout gridBagLayout2 = new GridBagLayout();
  private final JScrollPane listScrollPane = new JScrollPane();
  private final DefaultListModel geneListModel = new DefaultListModel();
  private final JList geneList = new JList(geneListModel);
  private final JButton exportButton = new JButton();
  private final JButton selectionButton = new JButton();
  private final TitledBorder geneListBorder = new TitledBorder("Results");
  private final JComboBox criteriaBox = new JComboBox(Criteria.values());
  private Set<String> result;

  public SearchBox(MainFrame parent) {
  	this.parent = parent;
    setTitle("Search");
    descriptionPanel = new NodeInfoPanel(parent, this, "Select a gene to get more info ...");
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    searchTextPane.setBackground(SystemColor.control);
    searchTextPane.setEnabled(true);
    searchTextPane.setEditable(false);
    searchTextPane.setText("This tool allows searching "+parent.dataset.getIdentifiersType()+" using different criteria.\nYou can then select one or more resulting id's to build a graph in gViz.\nBeware that the probe set id is always used as reference for conversion. For example if you search for Ensembl id's when using Entrez id's in your dataset, gViz will find all the probe set id's associated the resulting set of Ensembl id's, and display all the Entrez id's associated to those probe set id's.");
    this.getContentPane().setLayout(gridBagLayout1);
    searchTextField.setSelectionStart(11);
    searchTextField.setText("");
    searchTextField.addKeyListener(new GeneFetchingTool_searchTextField_keyAdapter(this));
    searchButton.setText("SEARCH");
    searchButton.addActionListener(new GeneFetchingTool_searchButton_actionAdapter(this));
    clearButton.setText("CLEAR");
    clearButton.addActionListener(new GeneFetchingTool_clearButton_actionAdapter(this));
    jSplitPane1.setResizeWeight(0);
    listPanel.setLayout(gridBagLayout2);
    exportButton.setToolTipText("Export list to an Excel file");
    exportButton.setIcon(MainFrame.imageExport);
    exportButton.addActionListener(new GeneFetchingTool_exportButton_actionAdapter(this));
    selectionButton.setToolTipText("Build graph with selection");
    selectionButton.setIcon(MainFrame.imageLayoutKK);
    selectionButton.addActionListener(new GeneFetchingTool_queryButton_actionAdapter(this));
    geneList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    geneList.addMouseListener(new GeneFetchingTool_geneList_mouseAdapter(this));
    geneList.addKeyListener(new GeneFetchingTool_geneList_keyAdapter(this));
    listScrollPane.setBorder(geneListBorder);
    infoScrollPane.getViewport().add(descriptionPanel);
    this.getContentPane().add(searchTextPane,   new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(searchTextField,    new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(criteriaBox,     new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(searchButton,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
    		,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(clearButton,    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(jSplitPane1,    new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    jSplitPane1.add(listPanel, JSplitPane.TOP);
    jSplitPane1.add(infoScrollPane, JSplitPane.BOTTOM);
    listPanel.add(listScrollPane,       new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
    listScrollPane.setViewportView(geneList);
    listPanel.add(selectionButton,    new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    listPanel.add(exportButton,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

  private void search(){
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    geneListModel.removeAllElements();
    try {
      String query = searchTextField.getText();
      Criteria criteria = Criteria.valueOfName(criteriaBox.getSelectedItem().toString());
      if (criteria == Criteria.KEGG_ID){
      	query = query.replaceAll("hsa:", "").replaceAll("HSA:", "");
      }
      result = gViz.DB.search(query, criteria, parent.dataset.getIdentifiersType());
      result.retainAll(parent.dataset.getVerticesAsList());
      geneListBorder.setTitle(result.size() + " results found");
      for (String gene : result) {
        geneListModel.addElement(gene);
      }
      listScrollPane.repaint();
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Error : " + ex.getMessage(), "Search query",
                                    JOptionPane.ERROR_MESSAGE, MainFrame.imageRemoveNonHighlighted);
    }
    setCursor(Cursor.getDefaultCursor());
  }

  public Collection<String> getSelectedVertices(){
  	Set<String> selection = new TreeSet<String>();
  	for (Object o : geneList.getSelectedValues()){
  		selection.add(o.toString());
  	}
  	return selection;
  }
  
  void searchButton_actionPerformed(ActionEvent e) {
    search();
  }

  void clearButton_actionPerformed(ActionEvent e) {
    searchTextField.setText("");
  }

  void exportButton_actionPerformed(ActionEvent e) {
  	new Thread(new Runnable(){
  		public void run(){
  	    char endl = '\n';
  	    File file;
  	    FileDialog chooser = new FileDialog(parent, "Save as", FileDialog.SAVE);
  	    chooser.setVisible(true);
  	    if (chooser.getFile() != null) {
  	      file = new File(chooser.getDirectory() + chooser.getFile());
  	      file = new File(file.getPath() + ".xls");
  	      try {
  	        FileWriter fw = new FileWriter(file);
  	        for (String id : result){
  	        	fw.write(id + endl);
  	        }
  	        fw.close();
  	      }catch (Exception ex){
  	        ex.printStackTrace();
  	        JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage(), "Exporting results",
  	                                      JOptionPane.ERROR_MESSAGE);
  	      }
  	    }
  		}
  	}).start();
  }

  void queryButton_actionPerformed(ActionEvent e) {
  	new Thread(new Runnable(){
  		public void run(){
  			Set<String> selection = new HashSet<String>();
  			for (Object o : geneList.getSelectedValues()){
  				selection.add(o.toString());
  			}
  			parent.leftToolBar.setSelectedGenes(selection);
  			parent.toFront();
  		}
  	}).start();
  }


  void searchTextField_keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER){
      search();
    }
  }

  void geneList_mouseClicked(MouseEvent e) {
  	setSelectedResults();
  }

  void geneList_keyReleased(KeyEvent e) {
  	setSelectedResults();
  }

  private void setSelectedResults(){
    if (!geneListModel.isEmpty()){
    	Set<String> selection = new TreeSet<String>();
    	for (Object o : geneList.getSelectedValues()){
    		selection.add(o.toString());
    	}
      if (selection.size() == 1){
      	descriptionPanel.setSelectedVertex(selection.iterator().next()); 
      }else if (selection.size() > 0){
      	descriptionPanel.setGeneList(selection); 
      }
    }  	
  }

}

class GeneFetchingTool_searchButton_actionAdapter implements java.awt.event.ActionListener {
  SearchBox adaptee;

  GeneFetchingTool_searchButton_actionAdapter(SearchBox adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.searchButton_actionPerformed(e);
  }
}

class GeneFetchingTool_clearButton_actionAdapter implements java.awt.event.ActionListener {
  SearchBox adaptee;

  GeneFetchingTool_clearButton_actionAdapter(SearchBox adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.clearButton_actionPerformed(e);
  }
}

class GeneFetchingTool_exportButton_actionAdapter implements java.awt.event.ActionListener {
  SearchBox adaptee;

  GeneFetchingTool_exportButton_actionAdapter(SearchBox adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.exportButton_actionPerformed(e);
  }
}

class GeneFetchingTool_queryButton_actionAdapter implements java.awt.event.ActionListener {
  SearchBox adaptee;

  GeneFetchingTool_queryButton_actionAdapter(SearchBox adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.queryButton_actionPerformed(e);
  }
}

class GeneFetchingTool_searchTextField_keyAdapter extends java.awt.event.KeyAdapter {
  SearchBox adaptee;

  GeneFetchingTool_searchTextField_keyAdapter(SearchBox adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.searchTextField_keyReleased(e);
  }
}

class GeneFetchingTool_geneList_mouseAdapter extends java.awt.event.MouseAdapter {
  SearchBox adaptee;

  GeneFetchingTool_geneList_mouseAdapter(SearchBox adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.geneList_mouseClicked(e);
  }
}

class GeneFetchingTool_geneList_keyAdapter extends java.awt.event.KeyAdapter {
  SearchBox adaptee;

  GeneFetchingTool_geneList_keyAdapter(SearchBox adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.geneList_keyReleased(e);
  }
}
