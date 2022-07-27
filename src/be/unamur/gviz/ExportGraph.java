package be.unamur.gviz;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import edu.uci.ics.jung.graph.*;

public class ExportGraph extends JDialog {
  public final static String ADJACENCE = "TXT: Adjacence matrix";
  public final static String GraphML = "XML: GraphML:";
  public final static String GML = "GML: Graph Modeling Langage";
  public final static String LGL = "LGL: Large Graph Layout";
  public final static String NCOL = "NCOL: Vertices couples";

  JFrame frame;
  Graph<String, Edge> G;
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField fileTextField = new JTextField();
  JButton browseButton = new JButton();
  JLabel jLabel2 = new JLabel();
  JComboBox formatComboBox = new JComboBox(new String[]{ADJACENCE, GraphML, GML, LGL, NCOL});
  JButton exportButton = new JButton();
  JPanel jPanel2 = new JPanel();
  JButton cancelButton = new JButton();
  JLabel jLabel3 = new JLabel();

  public ExportGraph(JFrame frame, Graph<String, Edge> graph) {
    super(frame,"Export current graph",true);
    this.frame = frame;
    G = graph;
    try {
      jbInit();
      pack();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setText("File");
    browseButton.setText("BROWSE");
    browseButton.addActionListener(new ExportInteractome_browseButton_actionAdapter(this));
    jLabel2.setText("Graph fomat");
    exportButton.setText("EXPORT");
    exportButton.addActionListener(new ExportInteractome_exportButton_actionAdapter(this));
    cancelButton.setText("CANCEL");
    cancelButton.addActionListener(new ExportInteractome_cancelButton_actionAdapter(this));
    jLabel3.setIcon(MainFrame.imageExport);
    fileTextField.setText("current_network");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1,         new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    jPanel1.add(fileTextField,          new GridBagConstraints(2, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
    jPanel1.add(browseButton,          new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
    jPanel1.add(jLabel2,        new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(formatComboBox,        new GridBagConstraints(3, 1, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 10), 0, 0));
    jPanel1.add(jLabel3,       new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(exportButton, null);
    jPanel2.add(cancelButton, null);
  }

  void browseButton_actionPerformed(ActionEvent e) {
    FileDialog chooser = new FileDialog(frame, "Export", FileDialog.SAVE);
    chooser.setVisible(true);
    if (chooser.getFile() != null) {
      fileTextField.setText(chooser.getDirectory() + chooser.getFile());
    }
  }

  void exportButton_actionPerformed(ActionEvent e) {
    try {
      String filename = fileTextField.getText();
      if (filename.charAt(filename.length() - 5) != '.') {
        if (formatComboBox.getSelectedItem().toString().equals(GraphML)) {
          if (!filename.endsWith(".xml")) filename += ".xml";
          writeGraphML(filename);
        } else if (formatComboBox.getSelectedItem().toString().equals(GML)) {
        	if (!filename.endsWith(".gml")) filename += ".gml";
          writeGML(filename);
        } else if (formatComboBox.getSelectedItem().toString().equals(LGL)) {
        	if (!filename.endsWith(".lgl")) filename += ".lgl";
          writeLGL(filename);
        } else if (formatComboBox.getSelectedItem().toString().equals(ADJACENCE)) {
        	if (!filename.endsWith(".txt")) filename += ".txt";
          writeAdjacenceMatrix(filename);
        } else if (formatComboBox.getSelectedItem().toString().equals(NCOL)) {
        	if (!filename.endsWith(".ncol")) filename += ".ncol";
          writeNCol(filename);
        }
      }
    }catch (Exception ex){
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage(), "Exporting graph",
                                    JOptionPane.ERROR_MESSAGE);
    }
    dispose();
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    dispose();
  }

  void writeGraphML(String filename) throws Exception {
    File output = new File(filename);
    FileWriter fw = new FileWriter(output);
    BufferedWriter w = new BufferedWriter(fw);
    String tab="  ";
    w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    w.newLine();
    w.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"");
    w.newLine();
    w.write(tab+tab+"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    w.newLine();
    w.write(tab+tab+"xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns");
    w.newLine();
    w.write(tab+tab+" http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">");
    w.newLine();
    w.write(tab+"<key id=\"d1\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>");
    w.newLine();
    w.write(tab+"<graph id=\""+filename+"\" edgedefault=\"undirected\">");
    w.newLine();
    for (String v : G.getVertices()){
      w.write(tab+tab+"<node id=\""+v+"\"/>");
      w.newLine();
    }
    for (Edge edge : G.getEdges()){
      String v1 = G.getEndpoints(edge).getFirst();
      String v2 = G.getEndpoints(edge).getSecond();
      w.write(tab+tab+"<edge source=\""+v1+"\" target=\""+v2+"\"/>");
      w.newLine();
      w.write(tab+tab+tab+"<data key=\"d1\">"+(""+edge.getWeight()).replace(',', '.')+"</data>");
      w.newLine();
    }
    w.write(tab+"</graph>");
    w.newLine();
    w.write("</graphml>");
    w.newLine();
    w.close();
    fw.close();
  }

  void writeGML(String filename) throws Exception {
    File output = new File(filename);
    FileWriter fw = new FileWriter(output);
    BufferedWriter w = new BufferedWriter(fw);
    Map<String, Integer> labels = new HashMap<String, Integer>();
    Set<String> vertices = new TreeSet<String>(G.getVertices());
    int i=0;
    for (String v : vertices){
      labels.put(v,i);
      i++;
    }
    String tab="  ";
    w.write("graph [");
    w.newLine();
    for (String label : vertices){
      w.write(tab+"node [");
      w.newLine();
      w.write(tab+tab+"id "+ labels.get(label));
      w.newLine();
      w.write(tab+tab+"label \"" + label + "\"");
      w.newLine();
      w.write(tab+"]");
      w.newLine();
    }
    for (Edge edge : G.getEdges()){
      String v1 = G.getEndpoints(edge).getFirst();
      String v2 = G.getEndpoints(edge).getSecond();
      w.write(tab+"edge [");
      w.newLine();
      w.write(tab+tab+"label \"" + (""+edge.getWeight()).replace(',', '.') + "\"");
      w.newLine();
      w.write(tab+tab+"source "+ labels.get(v1));
      w.newLine();
      w.write(tab+tab+"target "+ labels.get(v2));
      w.newLine();
      w.write(tab+"]");
      w.newLine();
    }
    w.write("]");
    w.newLine();
    w.close();
    fw.close();
  }

  void writeAdjacenceMatrix(String filename) throws Exception {
    File output = new File(filename);
    FileWriter fw = new FileWriter(output);
    BufferedWriter w = new BufferedWriter(fw);
    double[][] A = new double[G.getVertexCount()][G.getVertexCount()];
    for (int i=0 ; i < A.length ; i++){
      for (int j=0 ; j < A[i].length ; j++){
        A[i][j] = 0;
      }
    }
    Map<String, Integer> labels = new HashMap<String, Integer>();
    Set<String> vertices = new TreeSet<String>(G.getVertices());
    int i=0;
    for (String v : vertices){
      labels.put(v,i);
      i++;
    }
    for (String v : G.getVertices()){
      for (Edge e : G.getIncidentEdges(v)){
        i = labels.get(v);
        String neighbor = G.getOpposite(v, e);
        int j = labels.get(neighbor);
        A[i][j] = e.getWeight();
      }
    }
    String tab = "\t";
    for (String v : vertices){
      w.write(tab + v);
    }
    w.newLine();
    i=0;
    for (String v : vertices){
      w.write(v);
      for (int j=0 ; j < A[i].length ; j++){
        w.write(tab + (""+A[i][j]).replace(',', '.'));
      }
      w.newLine();
      i++;
    }
    w.close();
    fw.close();
  }

  void writeLGL(String filename) throws Exception {
    File output = new File(filename);
    FileWriter fw = new FileWriter(output);
    BufferedWriter w = new BufferedWriter(fw);
    Set<String> done = new HashSet<String>();
    for (Edge edge : G.getEdges()){
      String v = G.getEndpoints(edge).getFirst();
      if (done.add(v)){
        w.write("#" + v);
        w.newLine();
        for (Edge e : G.getIncidentEdges(v)) {
        	String neighbor = G.getOpposite(v, e);
          w.write(neighbor + "\t" + (""+e.getWeight()).replace(',', '.'));
          w.newLine();
        }
      }
    }
    w.close();
    fw.close();
  }

  void writeNCol(String filename) throws Exception {
    File output = new File(filename);
    FileWriter fw = new FileWriter(output);
    BufferedWriter w = new BufferedWriter(fw);
    for (Edge edge : G.getEdges()){
      String v1 = G.getEndpoints(edge).getFirst();
      String v2 = G.getEndpoints(edge).getSecond();
      w.write(v1 + "\t" + v2 + "\t" + (""+edge.getWeight()).replace(',', '.'));
      w.newLine();
    }
    w.close();
    fw.close();
  }
}

class ExportInteractome_browseButton_actionAdapter implements java.awt.event.ActionListener {
  ExportGraph adaptee;

  ExportInteractome_browseButton_actionAdapter(ExportGraph adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.browseButton_actionPerformed(e);
  }
}

class ExportInteractome_exportButton_actionAdapter implements java.awt.event.ActionListener {
  ExportGraph adaptee;

  ExportInteractome_exportButton_actionAdapter(ExportGraph adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.exportButton_actionPerformed(e);
  }
}

class ExportInteractome_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  ExportGraph adaptee;

  ExportInteractome_cancelButton_actionAdapter(ExportGraph adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}
