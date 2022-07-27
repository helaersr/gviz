package be.unamur.gviz;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.algorithms.shortestpath.*;

/**
 * <p>Title: Mantis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Laboratory of Evolutionary Genetics (ULB)</p>
 * @author Raphael Helaers
 * @version 1.0
 */

public class GraphStatFrame extends JFrame {
  public final static String DEGREE = "Degree of displayed graph";
  public final static String DIAMETER = "Diameter of displayed graph";
  public final static String WEIGHTS = "Edge weights distribution";
  public final static String CLUSTER_COEFF = "Clustering coefficient of displayed graph";
  public final static String CLUSTER_SIZE = "Cluster size in displayed graph";

  MainFrame mainFrame;
  
  Set<Set<String>> fullListClusterSet = null;

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel numberPanel = new JPanel();
  JPanel histogramPanel = new JPanel();
  ButtonGroup titleButtonGroup = new ButtonGroup();
  TitledBorder titledBorder1;
  JLabel nbrClustersLabel = new JLabel();
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  GridBagLayout gridBagLayout9 = new GridBagLayout();
  JLabel nbrVertexLabel = new JLabel();
  JLabel nbrEdgesLabel = new JLabel();
  JLabel histogramLabel = new JLabel();
  Map<String, HistogramPanel> histograms = new HashMap<String, HistogramPanel>();
  JComboBox mainComboBox = new JComboBox(new String[] {DEGREE, DIAMETER, WEIGHTS, CLUSTER_COEFF, CLUSTER_SIZE});
  CardLayout cardLayout = new CardLayout();
  JPanel cardPanel = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();

  public GraphStatFrame(MainFrame mainFrame) {
    try {
      this.setTitle("Network statistics");
      this.mainFrame = mainFrame;
      jbInit();
      Graph<String, Edge> g = mainFrame.graph;
      Collection<Set<String>> cs = mainFrame.tools.clusterSet;
      nbrVertexLabel.setText(g.getVertices().size() + " node" + ( (g.getVertices().size() > 1) ? "s" : ""));
      nbrEdgesLabel.setText(g.getEdges().size() + " edge" + ( (g.getEdges().size() > 1) ? "s" : ""));
      nbrClustersLabel.setText(cs.size() + " cluster" + ( (mainFrame.tools.clusterSet.size() > 1) ? "s" : ""));
      showDegreeHistogram();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public double getAverageShortestPath(Graph<String, Edge> g) {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      DijkstraDistance<String, Edge> dijkstra = new DijkstraDistance<String, Edge>(g);
      String[] vertices = (String[]) (g.getVertices().toArray(new String[0]));
      double distance = 0, total = 0;
      for (int i = 0; i < vertices.length; i++) {
        for (int j = i + 1; j < vertices.length; j++) {
          Number n = dijkstra.getDistance(vertices[i], vertices[j]);
          if (n != null) {
            total++;
            distance += n.doubleValue();
          }
        }
      }
      setCursor(Cursor.getDefaultCursor());
      return distance / total;
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      setCursor(Cursor.getDefaultCursor());
      JOptionPane.showMessageDialog(this,
          "The JVM is running out of memory, average shortest path cannot be computed. Try to increase the JVM maximum heap size.",
                                    "Out of memory", JOptionPane.ERROR_MESSAGE, MainFrame.imageStatistics);
      return 0;
    }
  }

  void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    this.getContentPane().setLayout(borderLayout1);
    mainPanel.setLayout(gridBagLayout1);
    numberPanel.setBorder(BorderFactory.createEtchedBorder());
    numberPanel.setLayout(gridBagLayout9);
    histogramPanel.setBorder(BorderFactory.createEtchedBorder());
    histogramPanel.setLayout(gridBagLayout8);
    cardPanel.setLayout(cardLayout);
    nbrVertexLabel.setText(" node");
    nbrClustersLabel.setToolTipText("");
    nbrClustersLabel.setText(" cluster");
    nbrEdgesLabel.setText(" edge");
    mainComboBox.addItemListener(new GraphStatFrame_mainComboBox_itemAdapter(this));
    jLabel1.setText("Displayed graph :");
    jLabel2.setText("");
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(numberPanel,   new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(histogramPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    numberPanel.add(nbrClustersLabel,  new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    numberPanel.add(nbrVertexLabel,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    numberPanel.add(nbrEdgesLabel,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    numberPanel.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    numberPanel.add(jLabel2,  new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    histogramPanel.add(histogramLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    histogramPanel.add(cardPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(mainComboBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }

  void mainComboBox_itemStateChanged(ItemEvent e) {
    if (mainComboBox.getSelectedItem().toString().equals(DEGREE)) {
      showDegreeHistogram();
    } else if (mainComboBox.getSelectedItem().toString().equals(DIAMETER)) {
      showDiameterHistogram();
    } else if (mainComboBox.getSelectedItem().toString().equals(WEIGHTS)) {
      showWeightsHistogram();
    } else if (mainComboBox.getSelectedItem().toString().equals(CLUSTER_COEFF)) {
      showClusteringCoefficientHistogram();
    } else if (mainComboBox.getSelectedItem().toString().equals(CLUSTER_SIZE)) {
      showClusterSizeHistogram();
    }
  }

  private void showDegreeHistogram() {
    if (!histograms.containsKey(DEGREE)) {
      HistogramPanel histogram = new HistogramPanel(this, "#Edges", "#Nodes");
      Graph<String, Edge> g = mainFrame.graph;
      for (String v : g.getVertices()) {
        histogram.addValue(g.inDegree(v));
      }
      histograms.put(DEGREE, histogram);
      cardPanel.add(histogram, DEGREE);
    }
    HistogramPanel histogram = (HistogramPanel) histograms.get(DEGREE);
    histogramLabel.setText(Tools.round(histogram.average(), 4) + " average degree");
    cardLayout.show(cardPanel, DEGREE);
  }

  private void showDiameterHistogram() {
    if (!histograms.containsKey(DIAMETER)) {
      HistogramPanel histogram = new HistogramPanel(this, "#Egdes", "#Paths");
      Graph<String, Edge> g = mainFrame.graph;
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try {
        DijkstraDistance<String, Edge> dijkstra = new DijkstraDistance<String, Edge>(g);
        String[] vertices = (String[]) (g.getVertices().toArray(new String[0]));
        for (int i = 0; i < vertices.length; i++) {
          for (int j = i + 1; j < vertices.length; j++) {
            Number n = dijkstra.getDistance(vertices[i], vertices[j]);
            if (n != null) {
              histogram.addValue(n.intValue());
            }
          }
        }
        setCursor(Cursor.getDefaultCursor());
        histograms.put(DIAMETER, histogram);
        cardPanel.add(histogram, DIAMETER);
      } catch (OutOfMemoryError e) {
        e.printStackTrace();
        setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(this,
            "The JVM is running out of memory, average shortest path cannot be computed. Try to increase the JVM maximum heap size.",
                                      "Out of memory", JOptionPane.ERROR_MESSAGE, MainFrame.imageStatistics);
      }
    }
    HistogramPanel histogram = (HistogramPanel) histograms.get(DIAMETER);
    histogramLabel.setText("Diameter of " + Tools.round(histogram.average(), 4));
    cardLayout.show(cardPanel, DIAMETER);
  }

  private void showWeightsHistogram() {
    if (!histograms.containsKey(WEIGHTS)) {
      HistogramPanel histogram = new HistogramPanel(this, "Weight (x100)", "#Edges");
      Graph<String, Edge> g = mainFrame.graph;
      histogram.clear();
      for (Edge edge : g.getEdges()) {
      	int weight = (int)Math.round(edge.getWeight()*100);
        histogram.addValue(weight);
      }
      histograms.put(WEIGHTS, histogram);
      cardPanel.add(histogram, WEIGHTS);
    }
    HistogramPanel histogram = (HistogramPanel) histograms.get(WEIGHTS);
    histogramLabel.setText("Average weight (x100) of " + Tools.round(histogram.average(), 4));
    cardLayout.show(cardPanel, WEIGHTS);
  }

  private void showClusteringCoefficientHistogram() {
    if (!histograms.containsKey(CLUSTER_COEFF)) {
      HistogramPanel histogram = new HistogramPanel(this, "CC%", "#Nodes");
      Graph<String, Edge> g = mainFrame.graph;
      for (String v : g.getVertices()) {
        Set<String> neighbors = new HashSet<String>();
        for (String n : g.getNeighbors(v)){
          neighbors.add(n);
        }
        double coeff;
        int Kv = neighbors.size(); //Number of neighbor of v
        int Nv = 0; //Number of interactions between the neighbors of v, without interactions with v
        if (Kv > 1){
          for (String n : g.getNeighbors(v)) {
            for (String nn : g.getNeighbors(n)) {
              if (!n.equals(nn) && neighbors.contains(nn)) Nv++;
            }
          }
          Nv /= 2;
          coeff = (double)(2*Nv) / (double)(Kv*(Kv-1));
        }else{
          coeff = 0;
        }
        histogram.addValue( (int) (coeff*100));
      }
      histograms.put(CLUSTER_COEFF, histogram);
      cardPanel.add(histogram, CLUSTER_COEFF);
    }
    HistogramPanel histogram = (HistogramPanel) histograms.get(CLUSTER_COEFF);
    histogramLabel.setText("Average clustering coefficient of " + Tools.round(histogram.average(), 4) + "%");
    cardLayout.show(cardPanel, CLUSTER_COEFF);
  }

  private void showClusterSizeHistogram() {
    if (!histograms.containsKey(CLUSTER_SIZE)) {
      HistogramPanel histogram = new HistogramPanel(this, "#Nodes", "#Clusters");
      Collection<Set<String>> cs = mainFrame.tools.clusterSet;
      histogram.clear();
      for (Set<String> set : cs) {
        histogram.addValue(set.size());
      }
      histograms.put(CLUSTER_SIZE, histogram);
      cardPanel.add(histogram, CLUSTER_SIZE);
    }
    HistogramPanel histogram = (HistogramPanel) histograms.get(CLUSTER_SIZE);
    histogramLabel.setText(Tools.round(histogram.average(), 4) + " average cluster size");
    cardLayout.show(cardPanel, CLUSTER_SIZE);
  }
}

class GraphStatFrame_mainComboBox_itemAdapter implements java.awt.event.ItemListener {
  GraphStatFrame adaptee;

  GraphStatFrame_mainComboBox_itemAdapter(GraphStatFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.mainComboBox_itemStateChanged(e);
  }
}
