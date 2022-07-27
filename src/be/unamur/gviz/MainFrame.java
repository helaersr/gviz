package be.unamur.gviz;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PluggableRenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import edu.uci.ics.jung.visualization.util.Animator;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

public class MainFrame extends JFrame implements VertexSelection {

	public MainFrame thisFrame = this;
	
	public int MODE = GraphTools.MODE_TRANSFORM;
	public boolean HUB = false;
	public boolean SCALE = true;
	public boolean THICKNESS = true;
	public double EDGE_WEIGHT_THRESHOLD = 0.5;
	public int VERTEX_BASE_SIZE = 20;
	
	public Dataset dataset;
	
	public GraphTools tools;
	public GraphZoomScrollPane graphScrollPane;
  public AggregateLayout<String,  Edge> layout;
  public PickedState<String> psV, pathEndsV;
  public PickedState<Edge> psE, pathEndsE;
  public Graph<String,  Edge> graph;
  public VisualizationViewer<String,  Edge> vv;
  private VertexShapeFactory<String> shapeFactory;
  
  DefaultModalGraphMouse<String,  Edge> graphMouse = new DefaultModalGraphMouse<String,  Edge>();
  ScalingControl scaler = new CrossoverScalingControl();

  LeftToolBar leftToolBar;
  RightToolBar rightToolBar;
  JToolBar topToolBar = new JToolBar();
  JButton openButton = new JButton();
  JButton zoomPlusButton = new JButton();
  JButton zoomMinusButton = new JButton();
  JToggleButton hubFilterToggleButton = new JToggleButton();
  JToggleButton edgesLabelToggleButton = new JToggleButton();
  JToggleButton selectionLabelToggleButton = new JToggleButton();
  JButton layoutBox = new JButton();
  JPopupMenu layoutMenu = new JPopupMenu();
  public JComboBox layoutComboBox = new JComboBox(new Class[] {KKLayout.class,
      FRLayout.class, CircleLayout.class, SpringLayout2.class, ISOMLayout.class});
  JButton resetButton = new JButton();
  JButton stopButton = new JButton();
  JToggleButton verticesLabelButton = new JToggleButton();
  JToggleButton vertexHubSizeButton = new JToggleButton();
  JToggleButton edgeThicknessButton = new JToggleButton();
  JButton modeBox = new JButton();
  JPopupMenu modeMenu = new JPopupMenu();
  JButton removeNonHighlightedButton = new JButton();
  JButton statisticsButton = new JButton();
  JButton exportButton = new JButton();
  JButton exportPath = new JButton();
  JButton printButton = new JButton();
  JButton aboutButton = new JButton();
  JButton searchButton = new JButton();
  JButton edgeWeightThresholdButton = new JButton();
  SearchBox searchBox = null;

  private final JDialog progressDialog = new JDialog(this,false);
  private final JProgressBar progressBar = new JProgressBar();
  
  public static ImageIcon imageOpenFile = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/openFile.png"));
  public static ImageIcon imageZoomIn = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/zoom_in.png"));
  public static ImageIcon imageZoomOut = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/zoom_out.png"));
  public static ImageIcon imagePrint = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/printer.png"));
  public static ImageIcon imagePathBehaviour = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour.png"));
  public static ImageIcon imagePathBehaviourMask = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour_masks.png"));
  public static ImageIcon imagePathBehaviourCluster = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour_clusters.png"));
  public static ImageIcon imagePathBehaviourBoth = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour_masks_clusters.png"));
  public static ImageIcon imagePathBehaviourArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour_arrow.png"));
  public static ImageIcon imagePathBehaviourMaskArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour_masks_arrow.png"));
  public static ImageIcon imagePathBehaviourClusterArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour_clusters_arrow.png"));
  public static ImageIcon imagePathBehaviourBothArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/behaviour_masks_clusters_arrow.png"));
  public static ImageIcon imageHub = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/hub.png"));
  public static ImageIcon imageHubGrey = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/hub_grey.png"));
  public static ImageIcon imageVertexSize = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/vertex_size.png"));
  public static ImageIcon imageVertexSizeGrey = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/vertex_size_grey.png"));
  public static ImageIcon imageLabelEdge = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/label_edge.png"));
  public static ImageIcon imageLabelEdgeGrey = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/label_edge_grey.png"));
  public static ImageIcon imageLabelVertex = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/label_node.png"));
  public static ImageIcon imageLabelVertexGrey = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/label_node_grey.png"));
  public static ImageIcon imageLabelSelection = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/label_selection.png"));
  public static ImageIcon imageLabelSelectionGrey = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/label_selection_grey.png"));
  public static ImageIcon imageModeTransform = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/move_cursor.png"));
  public static ImageIcon imageModeTransformArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/move_cursor_arrow.png"));
  public static ImageIcon imageModePick = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/pick_cursor.png"));
  public static ImageIcon imageModePickArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/pick_cursor_arrow.png"));
  public static ImageIcon imageModePath = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/path_cursor.png"));
  public static ImageIcon imageModePathArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/path_cursor_arrow.png"));
  public static ImageIcon imageLayoutKK = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_KK.png"));
  public static ImageIcon imageLayoutFR = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_FR.png"));
  public static ImageIcon imageLayoutCircle = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_circle.png"));
  public static ImageIcon imageLayoutSpring = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_spring.png"));
  public static ImageIcon imageLayoutIsom = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_ISOM.png"));
  public static ImageIcon imageLayoutKKArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_KK_arrow.png"));
  public static ImageIcon imageLayoutFRArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_FR_arrow.png"));
  public static ImageIcon imageLayoutCircleArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_circle_arrow.png"));
  public static ImageIcon imageLayoutSpringArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_spring_arrow.png"));
  public static ImageIcon imageLayoutIsomArrow = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_ISOM_arrow.png"));
  public static ImageIcon imageLayoutStop = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/stop_layout.png"));
  public static ImageIcon imageLayoutReset = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/reset_layout.png"));
  public static ImageIcon imageRemoveNonHighlighted = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/remove_non_highlighted.png"));
  public static ImageIcon imageStatistics = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/statistics.png"));
  public static ImageIcon imageExport = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/export.png"));
  public static ImageIcon imagePathExport = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/path_export.png"));
  public static ImageIcon imageEdgeThickness = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/edge_size.png"));
  public static ImageIcon imageEdgeThicknessGrey = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/edge_size_grey.png"));
  public static ImageIcon imageAbout = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/about.png"));
  public static ImageIcon imageSearch = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/search.png"));
  public static ImageIcon imageKegg = new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/kegg.png"));

  @SuppressWarnings("unchecked")
	public MainFrame(){
  	enableEvents(AWTEvent.WINDOW_EVENT_MASK);
  	setIconImage(Toolkit.getDefaultToolkit().getImage(be.unamur.gviz.gViz.class.getResource("resources/layout_KK.png")));
  	setTitle("gViz");
  	tools = new GraphTools();
  	graph = new UndirectedSparseGraph<String,  Edge>();
  	graph.addVertex("0");
  	layout = new AggregateLayout<String, Edge>(new ISOMLayout<String,  Edge>(graph));
  	layout.setSize(new Dimension(3000, 3000));
  	psV = new MultiPickedState<String>();
  	psE = new MultiPickedState<Edge>();
  	pathEndsV = new MultiPickedState<String>();
  	pathEndsE = new MultiPickedState<Edge>();
  	vv = new VisualizationViewer<String,  Edge>(layout);
  	vv.setBackground(Color.WHITE);
  	vv.setPickSupport(new ShapePickSupport<String,  Edge>(vv));
  	vv.setPickedEdgeState(psE);
  	vv.setPickedVertexState(psV);
    Transformer<Edge,String> stringer = new Transformer<Edge,String>(){
      public String transform(Edge e) {
          return "Edge:"+graph.getEndpoints(e).toString()+ " - " + e.getWeight();
      }
    };
    vv.setEdgeToolTipTransformer(stringer);
  	vv.setVertexToolTipTransformer(new ToStringLabeller<String>());
  	//vv.setToolTipText("<html><center>Use the mouse wheel to zoom<p> In 'transform' mode click and Drag the mouse to pan<p>and shift-click and Drag to Rotate<p>In 'pick' mode ctrl-click to select and center</center></html>");
  	graphScrollPane = new GraphZoomScrollPane(vv);
  	graphMouse.setZoomAtMouse(true);
  	vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener()); //hit 't' or 'p' to change mode
		initializeGraph();
		leftToolBar = new LeftToolBar(this, graphMouse); 
  	modeBox.addItemListener( ( (DefaultModalGraphMouse<String,  Edge>)vv.getGraphMouse()).getModeListener());
  	rightToolBar = new RightToolBar(this); 
  	jbInit();
  	initModeMenu();
  	initLayoutMenu();
  	tools.cluster(this, layout, 0);
  	clearGraph();
  	pack();
//  	int K = 10;
//  	graph.addVertex("1");
//  	for (int i=2 ; i < K ; i++){
//  		graph.addVertex(""+i);
//  		graph.addEdge(new Edge(i*0.2), ""+(1), ""+i);
//  	}
  }
  
  public void loadGraph(File file){
  	try{
  		clearGraph();
  		if (dataset != null){
  			dataset = null;
  		}
  		dataset = new Dataset(this, file);
  	}catch (Exception ex){
  		ex.printStackTrace();
  		JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage(), "Importing graph",
  				JOptionPane.ERROR_MESSAGE);
  	}		
  }
  
  public void clearGraph(){
  	while (graph.getEdgeCount() > 0){
  		graph.removeEdge(graph.getEdges().iterator().next());
  	}
  	while (graph.getVertexCount() > 0){
  		graph.removeVertex(graph.getVertices().iterator().next());
  	}
  }
  
  public void initializeGraph(){
    Transformer<String, Integer> vertexSizeTransformer = new Transformer<String, Integer>(){
    	public Integer transform(String v){
        if (SCALE) {
        	if (!graph.containsVertex(v)) 
        		System.out.println("Pas de " + v);
          return (int) (graph.inDegree(v) * 1.5) + VERTEX_BASE_SIZE;
        } else {
          return VERTEX_BASE_SIZE;
        }    		
    	}
    };
    Transformer<String, Float> vertexAspectRatioTransformer = new Transformer<String, Float>(){
    	public Float transform(String v){
    		return 1.0f;
    	}
    };    
    shapeFactory = new VertexShapeFactory<String>(vertexSizeTransformer, vertexAspectRatioTransformer);
    Transformer<String, Shape> vertexShapeTransformer = new Transformer<String, Shape>(){
    	public Shape transform(String v){
        if (pathEndsV.isPicked(v) || tools.shortPathVertices.contains(v)){
          return shapeFactory.getRegularStar(v, 5);
        }else{
          return shapeFactory.getEllipse(v);
        }    		
    	}
    };
		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<String,Paint>() {
			public Paint transform(String v) {
        if (psV.isPicked(v)){
          return new Color(255, 0, 0, GraphTools.transparency);
        }else{
        	Color k = tools.vertexPaints.get(v);
        	if (k != null) {
        		return k;
        	}
        	return new Color(134, 206, 189, GraphTools.transparency); /*Mantis green*/
        }				
			}
		});
		vv.getRenderContext().setVertexDrawPaintTransformer(new Transformer<String,Paint>() {
			public Paint transform(String v) {
        if (psV.isPicked(v)) {
          return Color.RED;
       }else if (rightToolBar.neighborIsPicked(psV, v, 1)) {
         return Color.RED;
       }else if (pathEndsV.isPicked(v)){
         return Color.CYAN;
       }else if (tools.shortPathVertices.contains(v)){
         return GraphTools.COLOR_SHORTEST_PATH;
       }else {
         return Color.BLACK;
       }
			}
		});
		vv.getRenderContext().setVertexStrokeTransformer(new Transformer<String, Stroke>(){
      protected Stroke heavy = new BasicStroke(6);
      protected Stroke medium = new BasicStroke(3);
      protected Stroke light = new BasicStroke(1);
      protected Stroke none = new BasicStroke(0);
      
      public Stroke transform(String v){
        Color c = tools.vertexPaints.get(v);
        if (psV.isPicked(v)) {
          if (c == GraphTools.SILVER_GRAY_TRANSPARENT) {
            return medium;
          } else {
            return heavy;
          }
        } else if (rightToolBar.neighborIsPicked(psV, v, 1)) {
          if (c == GraphTools.SILVER_GRAY_TRANSPARENT) {
            return light;
          } else {
            return medium;
          }
        } else if (pathEndsV.isPicked(v)){
          return heavy;
        } else {
          if (c == GraphTools.SILVER_GRAY_TRANSPARENT) {
            return none;
          } else {
            return light;
          }
        }				
			}
		});
		vv.getRenderContext().setVertexShapeTransformer(vertexShapeTransformer);
    vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer<Edge, Paint>(){
    	public Paint transform(Edge e){
    		Color c = Color.BLACK;
    		if (THICKNESS){
    			int w = 200 - (int)(e.getWeight()*100);
    			if (w < 0) w = 0;
    			c = new Color(w,w,w);
    		}
    		if (tools.isOnShortestPath(graph, e)){
    			if (tools.isRemovedFromCluster(e)) {
    				return GraphTools.COLOR_SHORTEST_PATH_GRAYED;
    			} else {
    				return GraphTools.COLOR_SHORTEST_PATH;
    			}
    		}else if (tools.isOnPathway(graph, e)){
    			if (tools.isRemovedFromCluster(e)) {
    				return GraphTools.COLOR_PATHWAY_GRAYED;
    			} else {
    				return GraphTools.COLOR_PATHWAY;
    			}    			
    		}else{
    			return c;
    		}
    	}
    });
    vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<Edge,Stroke>() {
    	protected final Stroke THIN = new BasicStroke(0);
    	protected final Stroke NORMAL = new BasicStroke(1);
    	protected final Stroke THICK = new BasicStroke(2);
    	//protected final Stroke DOTTED = PluggableRenderContext.DOTTED;
    	protected final Stroke DASHED = PluggableRenderContext.DASHED;

    	public Stroke transform(Edge e) {
    		if (tools.isRemovedFromCluster(e)) {
    			return DASHED;
    		}else if (THICKNESS){
    			if (e.getWeight() < 0.5){
    				return THIN;
    			}else if (e.getWeight() < 1){
    				return NORMAL;
    			}else if (e.getWeight() < 1.5){
    				return THICK;
    			}else{
    				return new BasicStroke(Math.round(e.getWeight()*2));
    			}
    		}else{
    			return NORMAL;
    		}
    	}
    });
  }
  
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
    	System.exit(0);
    }
  }

  private void jbInit(){
  	progressDialog.setLayout(new BorderLayout());
  	progressDialog.add(progressBar, BorderLayout.CENTER);
  	progressDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(be.unamur.gviz.gViz.class.getResource("resources/layout_KK.png")));
  	progressDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
  	progressDialog.setResizable(false);
  	progressDialog.setUndecorated(true);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  	progressDialog.setSize((int)screenSize.getWidth()/2, (int)screenSize.getHeight()/10);
    Dimension windowSize = progressDialog.getSize();
    progressDialog.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));  	
  	setLayout(new BorderLayout());
    topToolBar.setRollover(true);
    topToolBar.setFloatable(false);
    openButton.setBorder(BorderFactory.createRaisedBevelBorder());
    openButton.setMaximumSize(new Dimension(56, 56));
    openButton.setMinimumSize(new Dimension(56, 56));
    openButton.setToolTipText("Open adjacence matrix");
    openButton.setIcon(imageOpenFile);
    openButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	new Thread(new Runnable(){
      		public void run(){
            FileDialog chooser = new FileDialog(thisFrame, "Open adjacence matrix", FileDialog.LOAD) ;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize() ;
            Dimension windowSize = chooser.getSize() ;
            chooser.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2),
            		Math.max(0, (screenSize.height - windowSize.height) / 2)) ;
            chooser.setVisible(true) ;
            if (chooser.getFile() != null) {
            	File file = new File(chooser.getDirectory() + chooser.getFile()) ;
            	loadGraph(file);
            }
      		}
      	}).start();
      }      
    });
    openButton.setBorder(null);
    openButton.setBorderPainted(false);
    openButton.setContentAreaFilled(false);
    openButton.setMargin(new Insets(0, 0, 0, 0));
    openButton.setBorder(BorderFactory.createRaisedBevelBorder());
    hubFilterToggleButton.setMaximumSize(new Dimension(56, 56));
    hubFilterToggleButton.setMinimumSize(new Dimension(56, 56));
    hubFilterToggleButton.setToolTipText("Filter nodes list on number of hub members");
    hubFilterToggleButton.setIcon(imageHubGrey);
    hubFilterToggleButton.addItemListener(new ItemListener(){
    	public void itemStateChanged(ItemEvent e){
      	new Thread(new Runnable(){
      		public void run(){
            HUB = hubFilterToggleButton.isSelected();
            if (HUB) {
              leftToolBar.addHubFilter();
            } else {
              leftToolBar.removeHubFilter();
            }
      		}
      	}).start();
    	}
    });
    hubFilterToggleButton.setBorder(null);
    hubFilterToggleButton.setBorderPainted(false);
    hubFilterToggleButton.setContentAreaFilled(false);
    hubFilterToggleButton.setMargin(new Insets(0, 0, 0, 0));
    hubFilterToggleButton.setSelectedIcon(imageHub);
    hubFilterToggleButton.setRolloverIcon(imageHub);
    hubFilterToggleButton.setRolloverEnabled(true);
    edgesLabelToggleButton.setBorder(BorderFactory.createRaisedBevelBorder());
    edgesLabelToggleButton.setMaximumSize(new Dimension(56, 56));
    edgesLabelToggleButton.setMinimumSize(new Dimension(56, 56));
    edgesLabelToggleButton.setSelected(false);
    edgesLabelToggleButton.setToolTipText("Show edges labels");
    edgesLabelToggleButton.setIcon(imageLabelEdgeGrey);
    edgesLabelToggleButton.addItemListener(new ItemListener(){
    	public void itemStateChanged(ItemEvent e){
      	new Thread(new Runnable(){
      		public void run(){
        		if (edgesLabelToggleButton.isSelected()){
        			vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());
        		}else{
        			vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Edge, String>(){
        				public String transform(Edge e){
        					return null;
        				}
        			});
        		}
        		vv.validate();
            vv.repaint(); 
      		}
      	}).start();
    	}
    });
    edgesLabelToggleButton.setBorder(null);
    edgesLabelToggleButton.setBorderPainted(false);
    edgesLabelToggleButton.setContentAreaFilled(false);
    edgesLabelToggleButton.setMargin(new Insets(0, 0, 0, 0));
    edgesLabelToggleButton.setSelectedIcon(imageLabelEdge);
    edgesLabelToggleButton.setRolloverIcon(imageLabelEdge);
    edgesLabelToggleButton.setRolloverEnabled(true);
    selectionLabelToggleButton.setBorder(BorderFactory.createRaisedBevelBorder());
    selectionLabelToggleButton.setMaximumSize(new Dimension(56, 56));
    selectionLabelToggleButton.setMinimumSize(new Dimension(56, 56));
    selectionLabelToggleButton.setSelected(false);
    selectionLabelToggleButton.setToolTipText("Show selection labels");
    selectionLabelToggleButton.setIcon(imageLabelSelectionGrey);
    selectionLabelToggleButton.addItemListener(new ItemListener(){
    	public void itemStateChanged(ItemEvent e){
      	new Thread(new Runnable(){
      		public void run(){
        		if (selectionLabelToggleButton.isSelected()){
        			verticesLabelButton.setSelected(false);
        			vv.getRenderContext().setVertexLabelTransformer(new Transformer<String, String>(){
        				public String transform(String v){
        					if (psV.isPicked(v)) {
        						return v;
        					}else if (rightToolBar.neighborIsPicked(psV, v, 1)) {
        						return v;
        					}else if (pathEndsV.isPicked(v)){
        						return v;
        					}else if (tools.shortPathVertices.contains(v)){
        						return v;
        					}else {
        						return null;
        					}
        				}
        			});
        		}else{
        			vv.getRenderContext().setVertexLabelTransformer(new Transformer<String, String>(){
        				public String transform(String v){
        					return null;
        				}
        			});
        		}
        		vv.validate();
        		vv.repaint(); 
      		}
      	}).start();
    	}
    });
    selectionLabelToggleButton.setBorder(null);
    selectionLabelToggleButton.setBorderPainted(false);
    selectionLabelToggleButton.setContentAreaFilled(false);
    selectionLabelToggleButton.setMargin(new Insets(0, 0, 0, 0));
    selectionLabelToggleButton.setSelectedIcon(imageLabelSelection);
    selectionLabelToggleButton.setRolloverIcon(imageLabelSelection);
    selectionLabelToggleButton.setRolloverEnabled(true);
    verticesLabelButton.setBorder(BorderFactory.createRaisedBevelBorder());
    verticesLabelButton.setMaximumSize(new Dimension(56, 56));
    verticesLabelButton.setMinimumSize(new Dimension(56, 56));
    verticesLabelButton.setPreferredSize(new Dimension(56, 56));
    verticesLabelButton.setSelected(false);
    verticesLabelButton.setToolTipText("Show vertices labels");
    verticesLabelButton.setIcon(imageLabelVertexGrey);
    verticesLabelButton.setBorder(null);
    verticesLabelButton.setBorderPainted(false);
    verticesLabelButton.setContentAreaFilled(false);
    verticesLabelButton.setMargin(new Insets(0, 0, 0, 0));
    verticesLabelButton.setSelectedIcon(imageLabelVertex);
    verticesLabelButton.setRolloverIcon(imageLabelVertex);
    verticesLabelButton.setRolloverEnabled(true);
    verticesLabelButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
      	new Thread(new Runnable(){
      		public void run(){
        		if (verticesLabelButton.isSelected()){
        			selectionLabelToggleButton.setSelected(false);
        			vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        		}else{
        			vv.getRenderContext().setVertexLabelTransformer(new Transformer<String, String>(){
        				public String transform(String v){
        					return null;
        				}
        			});
        		}
            vv.validate();
            vv.repaint();
      		}
      	}).start();
      }
    });
    vertexHubSizeButton.setBorder(BorderFactory.createRaisedBevelBorder());
    vertexHubSizeButton.setMaximumSize(new Dimension(56, 56));
    vertexHubSizeButton.setMinimumSize(new Dimension(56, 56));
    vertexHubSizeButton.setPreferredSize(new Dimension(56, 56));
    vertexHubSizeButton.setSelected(true);
    vertexHubSizeButton.setToolTipText("Vertices size relative to hub size");
    vertexHubSizeButton.setIcon(imageVertexSizeGrey);
    vertexHubSizeButton.setBorder(null);
    vertexHubSizeButton.setBorderPainted(false);
    vertexHubSizeButton.setContentAreaFilled(false);
    vertexHubSizeButton.setMargin(new Insets(0, 0, 0, 0));
    vertexHubSizeButton.setSelectedIcon(imageVertexSize);
    vertexHubSizeButton.setRolloverIcon(imageVertexSize);
    vertexHubSizeButton.setRolloverEnabled(true);
    vertexHubSizeButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
      	new Thread(new Runnable(){
      		public void run(){
            SCALE = vertexHubSizeButton.isSelected();
            vv.repaint();
      		}
      	}).start();
      }
    });
    edgeThicknessButton.setBorder(BorderFactory.createRaisedBevelBorder());
    edgeThicknessButton.setMaximumSize(new Dimension(56, 56));
    edgeThicknessButton.setMinimumSize(new Dimension(56, 56));
    edgeThicknessButton.setPreferredSize(new Dimension(56, 56));
    edgeThicknessButton.setSelected(true);
    edgeThicknessButton.setToolTipText("Edges thickness relative to weight");
    edgeThicknessButton.setIcon(imageEdgeThicknessGrey);
    edgeThicknessButton.setBorder(null);
    edgeThicknessButton.setBorderPainted(false);
    edgeThicknessButton.setContentAreaFilled(false);
    edgeThicknessButton.setMargin(new Insets(0, 0, 0, 0));
    edgeThicknessButton.setSelectedIcon(imageEdgeThickness);
    edgeThicknessButton.setRolloverIcon(imageEdgeThickness);
    edgeThicknessButton.setRolloverEnabled(true);
    edgeThicknessButton.addItemListener(new ItemListener() {
    	public void itemStateChanged(ItemEvent e) {
      	new Thread(new Runnable(){
      		public void run(){
        		THICKNESS = edgeThicknessButton.isSelected();
        		vv.repaint();
      		}
      	}).start();
    	}
    });
    modeBox.setBorder(BorderFactory.createRaisedBevelBorder());
    modeBox.setMaximumSize(new Dimension(56, 56));
    modeBox.setMinimumSize(new Dimension(56, 56));
    modeBox.setPreferredSize(new Dimension(56, 56));
    modeBox.setToolTipText("Mouse mode");
    modeBox.setBorder(null);
    modeBox.setBorderPainted(false);
    modeBox.setContentAreaFilled(false);
    modeBox.setMargin(new Insets(0, 0, 0, 0));
    modeBox.setIcon(imageModeTransformArrow);
    layoutBox.setBorder(BorderFactory.createRaisedBevelBorder());
    layoutBox.setMaximumSize(new Dimension(56, 56));
    layoutBox.setMinimumSize(new Dimension(56, 56));
    layoutBox.setPreferredSize(new Dimension(56, 56));
    layoutBox.setToolTipText("Network layout");
    layoutBox.setBorder(null);
    layoutBox.setBorderPainted(false);
    layoutBox.setContentAreaFilled(false);
    layoutBox.setMargin(new Insets(0, 0, 0, 0));
    layoutComboBox.setVisible(false);
    layoutComboBox.setRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        String valueString = value.toString();
        valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
        return super.getListCellRendererComponent(list, valueString, index,
                                                  isSelected, cellHasFocus);
      }
    });
    layoutComboBox.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
      	new Thread(new Runnable(){
      		public void run(){
        		applySelectedLayout();
      		}
      	}).start();
    	}
    });
    layoutBox.setIcon(imageLayoutFRArrow);
    layoutComboBox.setSelectedItem(FRLayout.class);
    stopButton.setBorder(BorderFactory.createRaisedBevelBorder());
    stopButton.setMaximumSize(new Dimension(56, 56));
    stopButton.setMinimumSize(new Dimension(56, 56));
    stopButton.setToolTipText("Stop layout iterations");
    stopButton.setIcon(imageLayoutStop);
    stopButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
      	new Thread(new Runnable(){
      		public void run(){
    				Layout<String,Edge> layout = vv.getGraphLayout();
    				layout.initialize();
    				Relaxer relaxer = vv.getModel().getRelaxer();
    				if(relaxer != null) {
    					relaxer.stop();
    				} 
      		}
      	}).start();
    	}
    });
    stopButton.setBorder(null);
    stopButton.setBorderPainted(false);
    stopButton.setContentAreaFilled(false);
    stopButton.setMargin(new Insets(0, 0, 0, 0));
    resetButton.setBorder(BorderFactory.createRaisedBevelBorder());
    resetButton.setMaximumSize(new Dimension(56, 56));
    resetButton.setMinimumSize(new Dimension(56, 56));
    resetButton.setToolTipText("Reset layout");
    resetButton.setIcon(imageLayoutReset);
    resetButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
      	new Thread(new Runnable(){
      		public void run(){
    				Layout<String,Edge> layout = vv.getGraphLayout();
    				layout.initialize();
    				Relaxer relaxer = vv.getModel().getRelaxer();
    				if(relaxer != null) {
    					relaxer.stop();
    					relaxer.prerelax();
    					relaxer.relax();
    				}
      		}
      	}).start();
    	}
    });
    resetButton.setBorder(null);
    resetButton.setBorderPainted(false);
    resetButton.setContentAreaFilled(false);
    resetButton.setMargin(new Insets(0, 0, 0, 0));
    removeNonHighlightedButton.setToolTipText("Remove non-highlighted elements");
    removeNonHighlightedButton.setBorder(BorderFactory.createRaisedBevelBorder());
    removeNonHighlightedButton.setMaximumSize(new Dimension(56, 56));
    removeNonHighlightedButton.setMinimumSize(new Dimension(56, 56));
    removeNonHighlightedButton.setIcon(imageRemoveNonHighlighted);
    removeNonHighlightedButton.setBorder(null);
    removeNonHighlightedButton.setBorderPainted(false);
    removeNonHighlightedButton.setContentAreaFilled(false);
    removeNonHighlightedButton.setMargin(new Insets(0, 0, 0, 0));
    removeNonHighlightedButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    		new Thread(new Runnable(){
    			public void run(){
    				leftToolBar.removeNonHighlighted();
    			}
    		}).start();        
      }
    });
    statisticsButton.setBorder(BorderFactory.createRaisedBevelBorder());
    statisticsButton.setMaximumSize(new Dimension(56, 56));
    statisticsButton.setMinimumSize(new Dimension(56, 56));
    statisticsButton.setToolTipText("Statistics");
    statisticsButton.setIcon(imageStatistics);
    statisticsButton.setBorder(null);
    statisticsButton.setBorderPainted(false);
    statisticsButton.setContentAreaFilled(false);
    statisticsButton.setMargin(new Insets(0, 0, 0, 0));
    statisticsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	new Thread(new Runnable(){
      		public void run(){
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            GraphStatFrame graphStatFrame = new GraphStatFrame(thisFrame);
            graphStatFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(be.unamur.gviz.gViz.class.getResource("resources/statistics.png")));
            graphStatFrame.setSize(800, 600);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension windowSize = graphStatFrame.getSize();
            graphStatFrame.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2),
                                   Math.max(0, (screenSize.height - windowSize.height) / 2));
            //graphStatFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            setCursor(Cursor.getDefaultCursor());
            graphStatFrame.setVisible(true);
      		}
      	}).start();
      }
    });
    exportButton.setBorder(BorderFactory.createRaisedBevelBorder());
    exportButton.setMaximumSize(new Dimension(56, 56));
    exportButton.setMinimumSize(new Dimension(56, 56));
    exportButton.setToolTipText("Full export of the view");
    exportButton.setIcon(imageExport);
    exportButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	new Thread(new Runnable(){
      		public void run(){
            JFrame exportFrame = new JFrame();
            exportFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(be.unamur.gviz.gViz.class.getResource("resources/export.png")));
            ExportGraph dlg = new ExportGraph(exportFrame, graph);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension windowSize = dlg.getSize();
            dlg.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2),
                            Math.max(0, (screenSize.height - windowSize.height) / 2));
            dlg.setVisible(true); 
      		}
      	}).start();
      }      
    });
    exportButton.setBorder(null);
    exportButton.setBorderPainted(false);
    exportButton.setContentAreaFilled(false);
    exportButton.setMargin(new Insets(0, 0, 0, 0));
    exportButton.setBorder(BorderFactory.createRaisedBevelBorder());
    exportPath.setMaximumSize(new Dimension(56, 56));
    exportPath.setMinimumSize(new Dimension(56, 56));
    exportPath.setToolTipText("Export shortest path");
    exportPath.setIcon(imagePathExport);
    exportPath.setBorder(null);
    exportPath.setBorderPainted(false);
    exportPath.setContentAreaFilled(false);
    exportPath.setMargin(new Insets(0, 0, 0, 0));
    exportPath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	new Thread(new Runnable(){
      		public void run(){
            if (leftToolBar.hasShortestPath()){
              tools.exportShortestPath(dataset);
            }else{
              JOptionPane.showMessageDialog(null, "You must first select the end points of your path", "Export shortest path",
                                            JOptionPane.ERROR_MESSAGE, imagePathExport);
            }
      		}
      	}).start();
      }
    });
    printButton.setBorder(BorderFactory.createRaisedBevelBorder());
    printButton.setMaximumSize(new Dimension(56, 56));
    printButton.setMinimumSize(new Dimension(56, 56));
    printButton.setToolTipText("Save view as PNG");
    printButton.setIcon(imagePrint);
    printButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
      	new Thread(new Runnable(){
      		public void run(){
        		GraphTools.writeJPEGImage(thisFrame);
      		}
      	}).start();
    	}
    });
    printButton.setBorder(null);
    printButton.setBorderPainted(false);
    printButton.setContentAreaFilled(false);
    printButton.setMargin(new Insets(0, 0, 0, 0));
    aboutButton.setBorder(BorderFactory.createRaisedBevelBorder());
    aboutButton.setMaximumSize(new Dimension(56, 56));
    aboutButton.setMinimumSize(new Dimension(56, 56));
    aboutButton.setToolTipText("About gViz");
    aboutButton.setIcon(imageAbout);
    aboutButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
      	new Thread(new Runnable(){
      		public void run(){
          	JFrame aboutFrame = new JFrame();
            aboutFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(be.unamur.gviz.gViz.class.getResource("resources/about.png")));
            AboutBox dlg = new AboutBox(aboutFrame);
            dlg.setModal(true);
            dlg.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension windowSize = dlg.getSize();
            dlg.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2),
                            Math.max(0, (screenSize.height - windowSize.height) / 2));
            dlg.setVisible(true);
      		}
      	}).start();
    	}
    });
    aboutButton.setBorder(null);
    aboutButton.setBorderPainted(false);
    aboutButton.setContentAreaFilled(false);
    aboutButton.setMargin(new Insets(0, 0, 0, 0));
    searchButton.setBorder(BorderFactory.createRaisedBevelBorder());
    searchButton.setMaximumSize(new Dimension(56, 56));
    searchButton.setMinimumSize(new Dimension(56, 56));
    searchButton.setToolTipText("Search for a gene or probe set");
    searchButton.setIcon(imageSearch);
    searchButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		new Thread(new Runnable(){
    			public void run(){
    				if (dataset == null){
              JOptionPane.showMessageDialog(null, "You must first open a dataset", "Search",
                  JOptionPane.ERROR_MESSAGE, imageSearch);    					
    				}else{
    					if (searchBox == null){
    						searchBox = new SearchBox(thisFrame);
    						searchBox.setIconImage(Toolkit.getDefaultToolkit().getImage(be.unamur.gviz.gViz.class.getResource("resources/search.png")));
    						searchBox.setSize(new Dimension(600, 800));
    						Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    						Dimension windowSize = searchBox.getSize();
    						searchBox.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2),
    								Math.max(0, (screenSize.height - windowSize.height) / 2));
    					}
    					searchBox.setVisible(true);
    					searchBox.toFront();
    				}
    			}
    		}).start();
    	}
    });
    searchButton.setBorder(null);
    searchButton.setBorderPainted(false);
    searchButton.setContentAreaFilled(false);
    searchButton.setMargin(new Insets(0, 0, 0, 0));
    edgeWeightThresholdButton.setText("<html><center><B><FONT face=\"Arial\" size=2>EDGE WEIGHT<br>THRESHOLD<br>"+EDGE_WEIGHT_THRESHOLD+"</FONT></B></center></html>");
    edgeWeightThresholdButton.setBorder(BorderFactory.createRaisedBevelBorder());
    edgeWeightThresholdButton.setMaximumSize(new Dimension(76, 56));
    edgeWeightThresholdButton.setMinimumSize(new Dimension(76, 56));
    edgeWeightThresholdButton.setToolTipText("change edge weight threshold, edges with lower values won't be added to the graph");
    edgeWeightThresholdButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
      	new Thread(new Runnable(){
      		public void run(){
        		String s = JOptionPane.showInputDialog(null, "New edge weight threshold : ", "Change edge weight threshold", JOptionPane.QUESTION_MESSAGE, imageEdgeThickness, null, (Object)(""+EDGE_WEIGHT_THRESHOLD)).toString();
        		double threshold = Double.parseDouble(s);
        		EDGE_WEIGHT_THRESHOLD = threshold;
        		if (dataset != null){
        			try {
        				dataset.updateDataset(EDGE_WEIGHT_THRESHOLD);
        			}catch(Exception ex){
        				ex.printStackTrace();
        				JOptionPane.showMessageDialog(null, "Threshold must be a real number", "Change edge weight threshold",
        						JOptionPane.ERROR_MESSAGE, imageLayoutStop);
        			}
        		}else{
        			edgeWeightThresholdButton.setText("<html><center><B><FONT face=\"Arial\" size=2>EDGE WEIGHT<br>THRESHOLD<br>"+EDGE_WEIGHT_THRESHOLD+"</FONT></B></center></html>");        			
        		}
      		}
      	}).start();
    	}
    });
    edgeWeightThresholdButton.setBorder(null);
    edgeWeightThresholdButton.setBorderPainted(false);
    edgeWeightThresholdButton.setContentAreaFilled(false);
    edgeWeightThresholdButton.setMargin(new Insets(0, 0, 0, 0));
    zoomPlusButton.setBorder(BorderFactory.createRaisedBevelBorder());
    zoomPlusButton.setMaximumSize(new Dimension(56, 56));
    zoomPlusButton.setMinimumSize(new Dimension(56, 56));
    zoomPlusButton.setToolTipText("Zoom in");
    zoomPlusButton.setIcon(imageZoomIn);
    zoomPlusButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		new Thread(new Runnable(){
    			public void run(){
    				scaler.scale(vv, 1.1f, vv.getCenter());
    			}
    		}).start();
    	}
    });
    zoomPlusButton.setBorder(null);
    zoomPlusButton.setBorderPainted(false);
    zoomPlusButton.setContentAreaFilled(false);
    zoomPlusButton.setMargin(new Insets(0, 0, 0, 0));
    zoomMinusButton.setBorder(BorderFactory.createRaisedBevelBorder());
    zoomMinusButton.setMaximumSize(new Dimension(56, 56));
    zoomMinusButton.setMinimumSize(new Dimension(56, 56));
    zoomMinusButton.setToolTipText("Zoom out");
    zoomMinusButton.setIcon(imageZoomOut);
    zoomMinusButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		new Thread(new Runnable(){
    			public void run(){
        		scaler.scale(vv, 1 / 1.1f, vv.getCenter());
    			}
    		}).start();
    	}
    });
    zoomMinusButton.setBorder(null);
    zoomMinusButton.setBorderPainted(false);
    zoomMinusButton.setContentAreaFilled(false);
    zoomMinusButton.setMargin(new Insets(0, 0, 0, 0));
    topToolBar.add(openButton, null);
    topToolBar.add(zoomPlusButton, null);
    topToolBar.add(zoomMinusButton, null);
    topToolBar.addSeparator();
    topToolBar.add(layoutBox, null);
    topToolBar.add(stopButton, null);
    topToolBar.add(resetButton, null);
    topToolBar.addSeparator();
    topToolBar.add(edgeWeightThresholdButton, null);
    topToolBar.add(hubFilterToggleButton, null);
    topToolBar.addSeparator();
    topToolBar.add(edgesLabelToggleButton, null);
    topToolBar.add(verticesLabelButton, null);
    topToolBar.add(selectionLabelToggleButton, null);
    topToolBar.add(edgeThicknessButton, null);
    topToolBar.add(vertexHubSizeButton, null);
    topToolBar.add(modeBox, null);
    topToolBar.add(removeNonHighlightedButton, null);
    topToolBar.addSeparator();
    topToolBar.add(searchButton, null);
    topToolBar.add(statisticsButton, null);
    topToolBar.add(exportButton, null);
    topToolBar.add(exportPath, null);
    topToolBar.add(printButton, null);
    topToolBar.add(aboutButton, null);
    add(topToolBar, BorderLayout.NORTH);
    add(leftToolBar, BorderLayout.WEST);
    add(rightToolBar, BorderLayout.EAST);
    add(graphScrollPane, BorderLayout.CENTER);
  }

  private void initModeMenu(){
    JMenu mode = graphMouse.getModeMenu();
    JMenuItem itemTransform = mode.getItem(0);
    JMenuItem itemPick = mode.getItem(1);
    itemTransform.setIcon(imageModeTransform);
    itemTransform.setText(null);
    itemTransform.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        modeBox.setIcon(imageModeTransformArrow);
        MODE = GraphTools.MODE_TRANSFORM;
        vv.setPickedEdgeState(psE);
        vv.setPickedVertexState(psV);
      }
    });
    modeMenu.add(itemTransform);
    itemPick.setIcon(imageModePick);
    itemPick.setText(null);
    itemPick.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        modeBox.setIcon(imageModePickArrow);
        MODE = GraphTools.MODE_PICK;
        vv.setPickedEdgeState(psE);
        vv.setPickedVertexState(psV);
      }
    });
    modeMenu.add(itemPick);
    JRadioButtonMenuItem itemPath = new JRadioButtonMenuItem(imageModePath);
    itemPath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        modeBox.setIcon(imageModePathArrow);
        graphMouse.setMode(Mode.PICKING);
        MODE = GraphTools.MODE_PATH;
        vv.setPickedEdgeState(pathEndsE);
        vv.setPickedVertexState(pathEndsV);
      }
    });
    modeMenu.add(itemPath);
    ButtonGroup radio = new ButtonGroup();
    radio.add(itemTransform);
    radio.add(itemPick);
    radio.add(itemPath);
    modeBox.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        modeMenu.show(e.getComponent(), e.getX(), e.getY());
      }

      public void mouseEntered(MouseEvent e) {}

      public void mouseExited(MouseEvent e) {}

      public void mousePressed(MouseEvent e) {}

      public void mouseReleased(MouseEvent e) {}
    });
  }

  private void initLayoutMenu(){
    JRadioButtonMenuItem itemLayoutKK = new JRadioButtonMenuItem(imageLayoutKK);
    itemLayoutKK.setText("Kamada-Kawai");
    itemLayoutKK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutBox.setIcon(imageLayoutKKArrow);
        layoutComboBox.setSelectedItem(KKLayout.class);
      }
    });
    layoutMenu.add(itemLayoutKK);
    JRadioButtonMenuItem itemLayoutFR = new JRadioButtonMenuItem(imageLayoutFR);
    itemLayoutFR.setText("Fruchterman-Reingold");
    itemLayoutFR.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutBox.setIcon(imageLayoutFRArrow);
        layoutComboBox.setSelectedItem(FRLayout.class);
      }
    });
    layoutMenu.add(itemLayoutFR);
    JRadioButtonMenuItem itemLayoutCircle = new JRadioButtonMenuItem(imageLayoutCircle);
    itemLayoutCircle.setText("Circle");
    itemLayoutCircle.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutBox.setIcon(imageLayoutCircleArrow);
        layoutComboBox.setSelectedItem(CircleLayout.class);
      }
    });
    layoutMenu.add(itemLayoutCircle);
    JRadioButtonMenuItem itemLayoutSpring = new JRadioButtonMenuItem(imageLayoutSpring);
    itemLayoutSpring.setText("Force-directed");
    itemLayoutSpring.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutBox.setIcon(imageLayoutSpringArrow);
        layoutComboBox.setSelectedItem(SpringLayout2.class);
      }
    });
    layoutMenu.add(itemLayoutSpring);
    JRadioButtonMenuItem itemLayoutIsom = new JRadioButtonMenuItem(imageLayoutIsom);
    itemLayoutIsom.setText("Meyer's \"Self-Organizing Map\"");
    itemLayoutIsom.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutBox.setIcon(imageLayoutIsomArrow);
        layoutComboBox.setSelectedItem(ISOMLayout.class);
      }
    });
    layoutMenu.add(itemLayoutIsom);
    ButtonGroup radio = new ButtonGroup();
    radio.add(itemLayoutKK);
    radio.add(itemLayoutFR);
    radio.add(itemLayoutCircle);
    radio.add(itemLayoutSpring);
    radio.add(itemLayoutIsom);
    itemLayoutFR.setSelected(true);
    layoutBox.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        layoutMenu.show(e.getComponent(), e.getX(), e.getY());
      }

      public void mouseEntered(MouseEvent e) {}

      public void mouseExited(MouseEvent e) {}

      public void mousePressed(MouseEvent e) {}

      public void mouseReleased(MouseEvent e) {}
    });
  }
  
  @SuppressWarnings("unchecked")
  private void applySelectedLayout(){
    Object[] constructorArgs = {graph};
    Class<? extends Layout<String,Double>> layoutC = (Class<? extends Layout<String,Double>>) layoutComboBox.getSelectedItem();
    //Class lay = layoutC;
    try {
      Constructor<? extends Layout<String,Double>> constructor = layoutC.getConstructor(new Class[] {Graph.class});
      Object o = constructor.newInstance(constructorArgs);
      layout = new AggregateLayout<String, Edge>((Layout<String,Edge>) o);
      layout.setInitializer(vv.getGraphLayout());
      layout.setSize(vv.getSize());
      LayoutTransition<String,Edge> lt = new LayoutTransition<String,Edge>(vv, vv.getGraphLayout(), layout);
      Animator animator = new Animator(lt);
      animator.start();
      vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
      vv.repaint();
    }
    catch (Exception e) {
      e.printStackTrace();
    }  	
  }
    
  public Collection<String> getSelectedVertices(){
    Set<String> genes = new TreeSet<String>(psV.getPicked());
    return genes;  	
  }
  
  public void refreshGraph(){
    leftToolBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    rightToolBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    graphScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    tools.groupAndRecolor(vv, layout, leftToolBar.groupClusters.isSelected(), leftToolBar.groupMasked.isSelected());
    tools.buildShortestPath(graph);
    if (tools.shortPathVertices.size() > 0){
      leftToolBar.pathTitleBorder.setTitle("Shortest path ("+(tools.shortPathVertices.size()-1)+")");
    }else{
      leftToolBar.pathTitleBorder.setTitle("Shortest path");
    }
    leftToolBar.pathPanel.repaint();
    vv.validate();
    vv.repaint();
    leftToolBar.setCursor(Cursor.getDefaultCursor());
    rightToolBar.setCursor(Cursor.getDefaultCursor());
    graphScrollPane.setCursor(Cursor.getDefaultCursor());
  }

  public void setProgressVisible(final String title, final boolean isVisible, final boolean indeterminate){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if (isVisible){
		  		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		  		progressBar.setString(title);
		  		progressBar.setStringPainted(true);
		  		progressBar.setIndeterminate(indeterminate);
		  		progressDialog.setTitle(title);
		  		progressDialog.setVisible(true);
		    	progressDialog.toFront();
		    	progressDialog.requestFocus();    	
		  	}else{
		  		setCursor(Cursor.getDefaultCursor());
		  		progressBar.setIndeterminate(indeterminate);
		  		progressDialog.setVisible(false);  		
		  	}
			}
		});
  }
  
  public void setProgressValue(int value){
  	progressBar.setValue(value);
  }
  
}
