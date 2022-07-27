package be.unamur.gviz;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.BFSDistanceLabeler;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class GraphTools {
  public static int transparency = 200;

  public static final int MODE_TRANSFORM = 0;
  public static final int MODE_PICK = 1;
  public static final int MODE_PATH = 2;

  public final static Color SILVER_GRAY = new Color(220,220,220);
  public static Color SILVER_GRAY_TRANSPARENT = new Color(220,220,220, transparency);
  public static Color COLOR_SHORTEST_PATH = Color.CYAN;//new Color(125, 38, 205);
  public static Color COLOR_SHORTEST_PATH_GRAYED = new Color(0,200,200);//new Color(153,102,204);
  public static Color COLOR_PATHWAY = Color.RED;
  public static Color COLOR_PATHWAY_GRAYED = new Color(200,0,0);

  public Set<Set<String>> clusterSet;
  private EdgeBetweennessClusterer<String,  Edge> clusterer;
  public static Color[] similarColors;
  public Set<String> shortPathVertices = new HashSet<String>();
  public Map<String,Set<String>> pathwayVertices = new HashMap<String, Set<String>>();
  public Set<String> maskedVertices = new HashSet<String>();
	public Map<String,Color> vertexPaints = new HashMap<String,Color>();
  //public Map<Edge,Color> edgePaints = new HashMap<Edge,Color>();


  public GraphTools() {
  }

  public static void setTransparency(int transparent){
    transparency = transparent;
    SILVER_GRAY_TRANSPARENT = new Color(220,220,220, transparency);
    similarColors = new Color[]{
        new Color(134, 206, 189, transparency), /*Mantis green*/
        new Color(135, 137, 211, transparency),
        new Color(216, 134, 134, transparency),
        new Color(206, 176, 134, transparency),
        new Color(194, 204, 134, transparency),
        new Color(145, 214, 134, transparency),
        new Color(133, 178, 209, transparency),
        new Color(103, 148, 255, transparency),
        new Color(60, 220, 220, transparency),
        new Color(30, 250, 100, transparency),
        new Color(255, 160, 122, transparency) /*light salmon*/,
        new Color(125, 38, 205, transparency) /*purple3*/,
        new Color(0, 0, 255, transparency) /*blue*/,
//        new Color(255, 0, 0, transparency) /*red*/, //used for selection
        new Color(0, 255, 0, transparency) /*green*/,
        new Color(255, 192, 203, transparency) /*pink*/,
//        new Color(0, 255, 255, transparency) /*cyan*/, //use for shortest path
        new Color(255, 165, 0, transparency) /*orange*/,
        new Color(219, 219, 112, transparency) /*light grey*/,
        new Color(255, 0, 255, transparency) /*magenta*/,
        new Color(255, 255, 0, transparency) /*yellow*/,
        new Color(0,191,255, transparency) /*DeepSkyBlue*/,
        new Color(127,255,212, transparency) /*aquamarine*/,
        new Color(224,102,255, transparency) /*MediumOrchid1*/,
        new Color(139,69,19, transparency) /*SaddleBrown*/,
        new Color(0,250,154, transparency) /*MediumSpringGreen*/,
        new Color(255,127,0, transparency) /*coral*/,
        new Color(138,43,226, transparency) /*BlueViolet*/,
        new Color(133,99,99, transparency) /*Light Wood*/,
        new Color(255,215,0, transparency) /*gold*/,
        new Color(0,206,209, transparency) /*dark turquoise*/,
        new Color(187,255,255, transparency) /*PaleTurquoise1*/,
        new Color(255,127,36, transparency) /*chocolate1*/,
        new Color(255,20,147, transparency) /*DeepPink*/,
        new Color(202,255,112, transparency) /*DarkOliveGreen1*/,
        new Color(221,160,221, transparency) /*plum*/,
        new Color(46,139,87, transparency) /*SeaGreen*/,
        new Color(205,201,165, transparency) /*LemonChiffon3*/,
        new Color(127,255,0, transparency) /*chartreuse*/,
        new Color(255,62,150, transparency) /*VioletRed1*/,
        new Color(16,78,139, transparency) /*DodgerBlue4*/,
        new Color(255,48,48, transparency) /*firebrick1*/,
        new Color(139,139,0, transparency) /*yellow4*/,
        new Color(152,245,255, transparency) /*CadetBlue1*/,
        new Color(255,36,0, transparency) /*Orange Red*/
    };
  }
  
  public static Graph<String, Edge> copy(Graph<String, Edge> graph){
  	Graph<String, Edge> g = UndirectedSparseGraph.<String,Edge>getFactory().create();
  	for (String v : graph.getVertices()){
  		g.addVertex(v);
  	}
  	for (Edge e : graph.getEdges()){
  		g.addEdge(e, graph.getIncidentVertices(e));
  	}
  	return g;
  }
  
  public void buildShortestPath(Graph<String, Edge> graph){
    shortPathVertices.clear();
    if (!LeftToolBar.PATH_A_GENE.equals(LeftToolBar.NO_PATH_A) && !LeftToolBar.PATH_B_GENE.equals(LeftToolBar.NO_PATH_B)){
      try{
        Graph<String,Edge> g;
        if (LeftToolBar.CURRENT_PATH_BEHAVIOUR.equals(LeftToolBar.MASK_BEHAVIOUR) ||
            LeftToolBar.CURRENT_PATH_BEHAVIOUR.equals(LeftToolBar.BOTH_BEHAVIOUR)) {
          g = copy(graph);
          for (String v : maskedVertices){
          	g.removeVertex(v);
          }
        } else if (LeftToolBar.CURRENT_PATH_BEHAVIOUR.equals(LeftToolBar.CLUSTER_BEHAVIOUR)){
        	g = copy(graph);
        } else {
          g = graph;
        }
        String VA = LeftToolBar.PATH_A_GENE;
        String VB = LeftToolBar.PATH_B_GENE;
        if (VA == null || VB == null){
          return;
        }
        if (LeftToolBar.CURRENT_PATH_BEHAVIOUR.equals(LeftToolBar.CLUSTER_BEHAVIOUR) ||
            LeftToolBar.CURRENT_PATH_BEHAVIOUR.equals(LeftToolBar.BOTH_BEHAVIOUR)) {
          for (Set<String> cluster : clusterSet){
            if (cluster.contains(VA)){
              if (!cluster.contains(VB)) return;
            }else{
            	for (String vertex : cluster){
            		g.removeVertex(vertex);
            	}
            }
          }
        }
        BFSDistanceLabeler<String,  Edge> bdl = new BFSDistanceLabeler<String,  Edge>();
        bdl.labelDistances(g, VA);

        // grab a predecessor
        String v = VB;
        Set<String> prd = bdl.getPredecessors(v);
        shortPathVertices.add(VB);
        while (prd != null && prd.size() > 0) {          
          v = prd.iterator().next();
          shortPathVertices.add(v);
          if (v.equals(VA))return;
          prd = bdl.getPredecessors(v);
        }
      }catch (Exception ex){
        ex.printStackTrace();
      }
    }
  }

  public boolean isOnShortestPath(Graph<String, Edge> graph, Edge e) {
    String v1= graph.getEndpoints(e).getFirst()	;
    String v2= graph.getEndpoints(e).getSecond() ;
    if (v1.equals(v2)) return false;
    return shortPathVertices.contains(v1) && shortPathVertices.contains( v2 );
  }

  public void buildPathway(String pathway){
  	pathwayVertices = new HashMap<String, Set<String>>();
  	if (!pathway.equals(LeftToolBar.NO_PATHWAY_ID)){
  		try{
  			pathwayVertices = gViz.DB.fetchGenesInPathway(pathway);
  		}catch(Exception e){
  			e.printStackTrace();
  		}
  	}
  }
  
  public boolean isOnPathway(Graph<String, Edge> graph, Edge e){
    String v1= graph.getEndpoints(e).getFirst()	;
    String v2= graph.getEndpoints(e).getSecond() ;
    if (v1.equals(v2)) return false;
    if (pathwayVertices.containsKey(v1)){
    	return pathwayVertices.get(v1).contains(v2);
    }
    return false;  	
  }
  public boolean isMasked(Graph<String, Edge> graph, String vertex){
  	//TODO si implémentation des masks
  	/*
    Color c = (Color) vertex.getUserDatum(GraphTools.MANTIS_KEY);
    return (c == GraphTools.SILVER_GRAY_TRANSPARENT);
  	*/
  	return false;
  }

  public boolean isMasked(Graph<String, Edge> graph, Edge edge){
  	//TODO si implémentation des masks
    /*
  	Color c = (Color) edge.getUserDatum(GraphTools.MANTIS_KEY);
    return (c == GraphTools.SILVER_GRAY);
    */
  	return false;
  }

  /**
   * copy the visible part of the graph to a file as a jpeg image
   * @param file
   */
  public static void writeJPEGImage(MainFrame frame) {
      FileDialog chooser = new FileDialog(frame, "Export to PNG", FileDialog.SAVE);
      chooser.setVisible(true);
      if (chooser.getFile() != null) {
        File file = new File(chooser.getDirectory() + chooser.getFile() + ".png");
        try {
          BufferedImage image = new BufferedImage(frame.vv.getWidth(), frame.vv.getHeight(), BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics = image.createGraphics();
          frame.vv.paint(graphics);
          graphics.dispose();
          ImageIO.write(image, "png", file);
          //Open the image file
          Runtime run = Runtime.getRuntime();
          String osName = System.getProperty("os.name");
          String[] cmd = new String[3];
          if (osName.equals("Windows NT") || osName.equals("Windows XP") || osName.equals("Windows 2000") ||
              osName.equals("Windows Millenium")) {
            cmd[0] = "cmd.exe";
            cmd[1] = "/C";
            cmd[2] = chooser.getDirectory() + "\\" + chooser.getFile() + ".png";
          } else if (osName.equals("Windows 95") || osName.equals("Windows 98")) {
            cmd[0] = "command.com";
            cmd[1] = "/C";
            cmd[2] = chooser.getDirectory() + "\\" + chooser.getFile() + ".png";
          }else if (osName.equals("Mac OS X")) {
            cmd[0] = "open";
            cmd[1] = "";
            cmd[2] = chooser.getDirectory() + "/" + chooser.getFile() + ".png";
          }else { //Linux
            //How to ?
          }
          run.exec(cmd);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
  }

  public void exportShortestPath(Dataset dataset) {
    new ExportShortestPath(dataset);
  }

  /**
   *
   * <p>Thread that export the current interactome database to a tab separated text file
   * that can be open in Excel.</p>
   * <p>All gene information is also exported.</p>
   * <p>Copyright: Copyright (c) 2006</p>
   * <p>Company: Laboratory of Evolutionary Genetics (ULB)</p>
   * @author Raphael Helaers
   * @version 1.0
   */
  private class ExportShortestPath extends Thread {
  	Dataset dataset;
  	
    public ExportShortestPath(Dataset d) {
    	dataset = d;
      start();
    }

    public void run() {
      char sep = '\t';
      char endl = '\n';
      FileDialog chooser = new FileDialog(new Frame(), "Save as", FileDialog.SAVE);
      chooser.setVisible(true);
      if (chooser.getFile() != null) {
      	String filename = chooser.getDirectory() + chooser.getFile() + " - SP "+ LeftToolBar.PATH_A_GENE + " to " + LeftToolBar.PATH_B_GENE + ".xls";
        File file = new File(filename.replace('"', ' '));
        try {
          FileWriter fw = new FileWriter(file);
          //Headers
          fw.write("Identifier" + sep);
          fw.write("Number of direct neighbors" + sep + "Other hub members" + sep);
          fw.write("" + endl);
          for (String id : shortPathVertices) {
            fw.write(id + sep);
            Set<String> h = dataset.getInteractions(id);
            fw.write("" + h.size() + sep);
            fw.write(h.toString().substring(1, h.toString().length() - 1) + sep);
            fw.write("" + endl);
          }
          fw.close();
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("IO error:" + e.getMessage());
        }
      }
    }
  }

  public void cluster(MainFrame mainFrame, AggregateLayout<String,  Edge> layout, int numEdgesToRemove){
    mainFrame.leftToolBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    mainFrame.rightToolBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    mainFrame.graphScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Graph<String,  Edge> g = layout.getGraph();
    clusterer = new EdgeBetweennessClusterer<String,  Edge>(numEdgesToRemove);
    clusterSet = clusterer.transform(g);
    mainFrame.refreshGraph();
    mainFrame.leftToolBar.setCursor(Cursor.getDefaultCursor());
    mainFrame.rightToolBar.setCursor(Cursor.getDefaultCursor());
    mainFrame.graphScrollPane.setCursor(Cursor.getDefaultCursor());
  }

  public boolean isRemovedFromCluster(Edge e){
  	return clusterer.getEdgesRemoved().contains(e);
  }
  
  public void groupAndRecolor(VisualizationViewer<String, Edge> vv, AggregateLayout<String, Edge> layout, boolean groupClusters, boolean groupMasked) {
    //Graph<String, Edge> g = layout.getGraph();
    layout.removeAll();
    /*
    List<Edge> edges = clusterer.getEdgesRemoved();
    for (Edge e : g.getEdges()) {
      if (edges.contains(e)) {
      	edgePaints.put(e, Color.LIGHT_GRAY);
      }
      else {
      	edgePaints.put(e, Color.BLACK);
      }
    }
  	*/  
    int i = 0;
    maskedVertices.clear();
    //Set the colors of each node so that each cluster's vertices have the same color
    for (Set<String> clust : clusterSet) {
      Set<String> vertices = new HashSet<String>(clust);
      Color c = similarColors[i % similarColors.length];
      colorCluster(vertices, c);
      Set<String> masked = getMasked(vertices);
      maskedVertices.addAll(masked);
      if (groupClusters || groupMasked){
        if (groupMasked){
          vertices.removeAll(masked);
          if (masked.size() > 0) groupCluster(vv, layout, masked);
        }
        if (vertices.size() > 0) groupCluster(vv, layout, vertices);
      }
      i++;
    }
  }

  private static Set<String> getMasked(Set<String> vertices){
    Set<String> masked = new HashSet<String>();
    //TODO si implémentation des masks
    return masked;
  }

  private void colorCluster(Set<String> vertices, Color c) {
    for (String v : vertices) {
    	vertexPaints.put(v, c);
    }
  }

	private static void groupCluster(VisualizationViewer<String, Edge> vv, AggregateLayout<String,Edge> layout, Set<String> vertices) {
		if(vertices.size() < layout.getGraph().getVertexCount()) {
			Point2D center = layout.transform(vertices.iterator().next());
			Graph<String,Edge> subGraph = UndirectedSparseGraph.<String,Edge>getFactory().create();
			for(String v : vertices) {
				subGraph.addVertex(v);
			}
			Layout<String,Edge> subLayout = new CircleLayout<String,Edge>(subGraph);
			subLayout.setInitializer(vv.getGraphLayout());
			subLayout.setSize(new Dimension(40,40));

			layout.put(subLayout,center);
			vv.repaint();
		}
	}
}
