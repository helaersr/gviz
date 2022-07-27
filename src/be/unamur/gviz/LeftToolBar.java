package be.unamur.gviz;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.control.*;


public class LeftToolBar extends JToolBar {
  final String COMMANDSTRING = "Edges removed for clustering: ";
  final int barWidth = 220;
  final private Map<Integer,Map<String, Set<String>>> clusterSet = new TreeMap<Integer,Map<String, Set<String>>>();
  
  //Shortest path
  static boolean pathPicking = false;
  public static String COMPLETE_BEHAVIOUR = "Complete network";
  public static String CLUSTER_BEHAVIOUR = "Consider clusters limits";
  public static String MASK_BEHAVIOUR = "Ignore masked nodes and edges";
  public static String BOTH_BEHAVIOUR = "Consider clusters limits and ignore masked nodes and edges";
  public static String CURRENT_PATH_BEHAVIOUR = COMPLETE_BEHAVIOUR;
  public final static String NO_PATH_A = "Select path end";
  public final static String NO_PATH_B = "Select path other end";
  public static String PATH_A_GENE = NO_PATH_A;
  public static String PATH_B_GENE = NO_PATH_B;
  
  //KEGG Pathways
  static boolean synchPathwayBoxes = false;
  public final static String NO_PATHWAY_ID = "---";
  public final static String NO_PATHWAY_DESC = "---";
  public static String PATHWAY_ID = NO_PATHWAY_ID;
  public static String PATHWAY_DESC = NO_PATHWAY_DESC;
  private final Map<String, String> pathwayIdToDesc = new TreeMap<String, String>();
  private final Map<String, String> pathwayDescToId = new TreeMap<String, String>();

  //Masks
  public static String NO_MASK = "No mask";
  public String CURRENT_MASK = NO_MASK;

  public final static int GREATER = 0;
  public final static int SMALLER = 1;
  public final static int EQUAL = 2;
  public final String[] OPERATORS = new String[] {
      ">", "<", "="};
  int operator = SMALLER;
  int threshold = 1;

  private Dataset dataset;
  Set<String> drawnVertices = new TreeSet<String>();
  Vector<Set<String>> filters = new Vector<Set<String>>();

  private int currentFilterYPosition = 0;
  private HubFilter hubFilter = new HubFilter(false);

  boolean refreshGraphWhenSelecting = true;
  
  MainFrame mainFrame;
  
  JSlider deepnessJSlider = new JSlider();
  JSlider transparencySlider = new JSlider();
  JSlider nodeSizeSlider = new JSlider();
  TitledBorder dislayedDeepnessBorder;
  JSlider edgeBetweennessSlider = new JSlider();
  TitledBorder titledBorder4;
  TitledBorder clusterBorder;
  JToggleButton groupClusters = new JToggleButton();
  JTabbedPane networkTabbedPane = new JTabbedPane();
  JPanel networkPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  TitledBorder geneListTitledBorder;
  JScrollPane geneListScrollPane = new JScrollPane();
  DefaultListModel vertexListModel = new DefaultListModel();
  JList vertexList = new JList(vertexListModel);
  FlowLayout flowLayout3 = new FlowLayout();
  FlowLayout flowLayout4 = new FlowLayout();
  FlowLayout flowLayout5 = new FlowLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JPanel filterPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  TitledBorder transparencyBorder;
  TitledBorder nodeSizeBorder;
  TitledBorder titledBorder3;
  JPanel groupingPanel = new JPanel();
  JToggleButton groupMasked = new JToggleButton();
  TitledBorder titledBorder5;
  TitledBorder titledBorder6;
  JPanel pathPanel = new JPanel();
  TitledBorder pathTitleBorder;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout10 = new GridBagLayout();
  JButton behaviourBox = new JButton();
  JPopupMenu behaviourMenu = new JPopupMenu();
  JComboBox pathBoxA = new JComboBox();
  JComboBox pathBoxB = new JComboBox();
  JButton selectAllButton = new JButton("DISPLAY FULL GRAPH");
  JPanel clusterListPanel = new JPanel();
  JSplitPane clusterSplitPane = new JSplitPane();
  DefaultListModel clusterListModel = new DefaultListModel();
  JList clusterList = new JList(clusterListModel);
  JScrollPane clusterScrollPane = new JScrollPane();
  DefaultListModel vertexOfSelectedClusterListModel = new DefaultListModel();
  JList vertexOfSelectedClusterList = new JList(vertexOfSelectedClusterListModel);
  JScrollPane geneOfClusterScrollPane = new JScrollPane();
  JButton displayFullClusterButton = new JButton();
  JPanel pathwayPanel = new JPanel();
  JButton keggButton = new JButton();
  JTextArea pathwayLabel = new JTextArea("Select a pathway to turn the common edges with your dataset in red");
  JTextArea entrezLabel = new JTextArea("Only available if you open your dataset and choose ENTREZ id's.");
  JComboBox pathwayIdBox;
  JComboBox pathwayDescriptionBox;

  public LeftToolBar(MainFrame mainframe, DefaultModalGraphMouse<String, Edge> graphMouse) {
    super();
    this.mainFrame = mainframe;
    mainFrame.pathEndsV.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent e){
        if (!pathPicking){
          pathPicking = true;
          try {
            String VA = null, VB = null;
            Set<String> picked = mainFrame.pathEndsV.getPicked();
            int pickNum = 0;
            for (Iterator<String> it = picked.iterator(); it.hasNext() && pickNum < 3; ) {
              pickNum++;
              if (pickNum == 1) {
                VA = it.next();
                PATH_A_GENE = VA;
                pathBoxA.setSelectedItem(PATH_A_GENE);
              } else if (pickNum == 2) {
                VB = it.next();
                PATH_B_GENE = VB;
                pathBoxB.setSelectedItem(PATH_B_GENE);
              }
            }
            switch (pickNum) {
              case 0:
                pathBoxA.setSelectedIndex(0);
                PATH_A_GENE = NO_PATH_A;
                pathBoxB.setSelectedIndex(0);
                PATH_B_GENE = NO_PATH_B;
                break;
              case 1:
                pathBoxB.setSelectedIndex(0);
                PATH_B_GENE = NO_PATH_B;
                break;
              case 2:
                break;
              default:
                mainFrame.pathEndsV.clear();
                mainFrame.pathEndsV.pick(VA, true);
                mainFrame.pathEndsV.pick(VB, true);
                break;
            }
            mainFrame.refreshGraph();
          } catch (Exception ex) {
            ex.printStackTrace();
            pathPicking = false;
          }
          pathPicking = false;
        }
      }
    });
    try{
    	pathwayIdToDesc.put(NO_PATHWAY_ID, NO_PATHWAY_DESC);
    	pathwayDescToId.put(NO_PATHWAY_DESC, NO_PATHWAY_ID);
    	gViz.DB.fillPathwaysMaps(pathwayIdToDesc, pathwayDescToId);
    }catch(Exception e){
    	e.printStackTrace();
    }
    try {
      jbInit();
      initBehaviourMenu();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public JList getGeneList(){
  	if (networkTabbedPane.getSelectedComponent() == geneListScrollPane){
  		return vertexList;
  	}else if (networkTabbedPane.getSelectedComponent() == clusterListPanel){
  		return vertexOfSelectedClusterList;
  	}else {
  		return vertexList;
  	}
  }
  
  public DefaultListModel getGeneListModel(){
  	if (networkTabbedPane.getSelectedComponent() == geneListScrollPane){
  		return vertexListModel;
  	}else if (networkTabbedPane.getSelectedComponent() == clusterListPanel){
  		return vertexOfSelectedClusterListModel;
  	}else {
  		return vertexListModel;
  	}
  }
  
  public void setGeneList(Dataset dataset){
  	this.dataset = dataset;
  	fillClusterList();
    applyFilters();
  }

  public void showGraph() {
    new GraphBuilding().execute();    
  }

  public class GraphBuilding extends SwingWorker<Object, Object>{
  	public GraphBuilding(){
  		addPropertyChangeListener(new PropertyChangeListener(){
  			public void propertyChange(PropertyChangeEvent e){
          if("progress".equals(e.getPropertyName())) {
          	mainFrame.setProgressValue((Integer) e.getNewValue());
          }
  			}
  		});
  	}
  	protected Object doInBackground() throws Exception {
  		mainFrame.setProgressVisible("Building graph", true, false);
    	mainFrame.clearGraph();
      try {
      	build(getGeneList().getSelectedValues(), mainFrame.graph, deepnessJSlider.getValue());
      } catch (Exception ex) {
      	System.out.println("Problem when building graph");
        ex.printStackTrace();
      }
  		return null;
  	}
  	protected void done(){
      mainFrame.rightToolBar.fillGeneList(drawnVertices);
      fillPathBoxes();
      mainFrame.layoutComboBox.setSelectedIndex(mainFrame.layoutComboBox.getSelectedIndex());
      edgeBetweennessSlider.setValue(0);
      edgeBetweennessSlider.setMaximum(mainFrame.graph.getEdgeCount());
      edgeBetweennessSlider.repaint();
      mainFrame.refreshGraph();
  		mainFrame.setProgressVisible("", false, false);
  	}
  }
  
  private void build(Object[] vertices, Graph<String,  Edge> graph, int deepness) throws Exception {
  	drawnVertices.clear();
  	Set<String> currentLevel = new HashSet<String>();
  	Set<String> nextLevel = new HashSet<String>();
  	for (Object o : vertices){
  		currentLevel.add(o.toString());
  	}
  	if (deepness == 6) deepness = Integer.MAX_VALUE;
  	for (int level = 0 ; level <= deepness && !currentLevel.isEmpty() ; level++){
  		drawnVertices.addAll(currentLevel);
  		for (String vertexA : currentLevel){
  			graph.addVertex(vertexA);
  		}
  		for (Iterator<String> i = currentLevel.iterator() ; i.hasNext() ; ){
  			String vertexA = i.next();
  			for (String vertexB : dataset.getInteractions(vertexA)) {
  				double weight = dataset.getWeight(vertexA, vertexB);
  				if (weight >= mainFrame.EDGE_WEIGHT_THRESHOLD) {
  					if (graph.containsVertex(vertexB)){
  						if(!graph.isPredecessor(vertexA, vertexB)){
  							graph.addEdge(new Edge(weight), vertexA, vertexB);
  						}
  					}else{
  						nextLevel.add(vertexB);
  					}
  				}  				
  			}
  		}
  		currentLevel = nextLevel;
  		nextLevel = new HashSet<String>();
  	}  	
  }
  
  public void removeNonHighlighted() {
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Set<String> highlight = getHighlightedVertices(mainFrame.psV.getPicked(), 0);
    int[] selection = new int[highlight.size()];
    int i = 0;
    for (String gene : highlight) {
      selection[i] = getGeneListModel().indexOf(gene);
      i++;
    }
    deepnessJSlider.setValue(0);
    refreshGraphWhenSelecting = false;
    getGeneList().setSelectedIndices(selection);
    refreshGraphWhenSelecting = true;
    showGraph();
    this.setCursor(Cursor.getDefaultCursor());
  }

  public void setSelectedGenes(Collection<String> genes){
  	for (Iterator<String> i = genes.iterator() ; i.hasNext() ; ){
  		String gene = i.next();
  		if (!getGeneListModel().contains(gene)) i.remove();
  	}
  	if (genes.size() > 0){
  		networkTabbedPane.setSelectedComponent(geneListScrollPane); 
  		int[] selection = new int[genes.size()];
  		int i = 0;
  		for (String gene : genes) {
  			selection[i] = getGeneListModel().indexOf(gene);
  			i++;
  		}
  		deepnessJSlider.setValue(0);
  		refreshGraphWhenSelecting = false;
  		getGeneList().setSelectedIndices(selection);
  		refreshGraphWhenSelecting = true;
  		showGraph();
  	}
    this.setCursor(Cursor.getDefaultCursor());  	
  }
  
  private Set<String> getHighlightedVertices(Set<String> selection, int deepness) {
    Set<String> highlight = new HashSet<String>(selection);
    if (deepness < mainFrame.rightToolBar.deepnessSlider.getValue()) {
      Set<String> nextLevel = new HashSet<String>();
      for (String gene : selection) {
        nextLevel.addAll(mainFrame.graph.getSuccessors(gene));
      }
      highlight.addAll(getHighlightedVertices(nextLevel, deepness + 1));
    }
    return highlight;
  }

  public void addHubFilter() {
    hubFilter.remove();
    hubFilter = new HubFilter();
  }

  public void removeHubFilter() {
    hubFilter.remove();
  }

	private void fillClusterList(){
    Graph<String,  Edge> g = new UndirectedSparseGraph<String,  Edge>();
    try {
    	build(dataset.getVertices(), g, 0);
    } catch (Exception ex) {
    	System.out.println("Problem when building full graph before clustering");
      ex.printStackTrace();
    }
    clusterSet.clear();
    for (Set<String> cluster : new EdgeBetweennessClusterer<String,  Edge>(0).transform(g)){
    	int size = cluster.size();
    	Map<String,Set<String>> map;
    	if (clusterSet.containsKey(size)){
    		map = clusterSet.get(size);
    	}else{
    		map = new TreeMap<String, Set<String>>();
    	}
    	map.put("Cluster with " + cluster.iterator().next(), cluster);
    	clusterSet.put(size, map);
    }
    clusterListModel.clear();
		for (Entry<Integer, Map<String,Set<String>>> e : clusterSet.entrySet()){
			for (String name : e.getValue().keySet()){
				clusterListModel.addElement("[" + e.getKey() + "] " + name);
			}
		}
		vertexOfSelectedClusterListModel.removeAllElements();
	}
	
  private void applyFilters() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    refreshGraphWhenSelecting = false;
    List<String> filteredNodeList = dataset.getVerticesAsList();
    for (Set<String> filter : filters) {
    	filteredNodeList.retainAll(filter);
    }
    vertexListModel.removeAllElements();
    vertexOfSelectedClusterListModel.removeAllElements();
		Set<String> set = new HashSet<String>();
		Object[] selection = clusterList.getSelectedValues();
    for (int i = 0; i < selection.length; i++) {
    	String[] key = selection[i].toString().substring(1).split("] ");
    	set.addAll(clusterSet.get(Integer.parseInt(key[0])).get(key[1]));
    }    
		for (Entry<Integer, Map<String,Set<String>>> e : clusterSet.entrySet()){
			for (Entry<String, Set<String>> f : e.getValue().entrySet()){
				Set<String> s = new HashSet<String>(filteredNodeList);
				s.retainAll(f.getValue());
				String element = "[" + e.getKey() + "] " + f.getKey();
				if (s.size() > 0 && !clusterListModel.contains(element)){
					boolean inserted = false;
					for (int i=0 ; i < clusterListModel.size() ; i++){
						String[] current = clusterListModel.get(i).toString().substring(1).split("] ");
						if ((e.getKey() < Integer.parseInt(current[0])) || (e.getKey() == Integer.parseInt(current[0]) && f.getKey().compareTo(current[1]) < 0)){
							clusterListModel.add(i, element);
							inserted = true;
							break;
						}
					}		
					if (!inserted) clusterListModel.addElement(element);
				}else if (s.isEmpty() && clusterListModel.contains(element)){
					clusterListModel.removeElement(element);
				}
			}
		}    
    for (String gene : new TreeSet<String>(filteredNodeList)) {
      vertexListModel.addElement(gene);
      if (set.contains(gene)) vertexOfSelectedClusterListModel.addElement(gene);
    }    
    geneListTitledBorder.setTitle("Listed nodes (" + filteredNodeList.size() +
                                  " available)");    
    networkPanel.repaint();
    updateUI();
    refreshGraphWhenSelecting = true;
    showGraph();
    setCursor(Cursor.getDefaultCursor());
  }

  private class HubFilter {
    boolean active;
    public final int GREATER = 0;
    public final int SMALLER = 1;
    public final int EQUAL = 2;
    public final String[] OPERATORS = new String[] {
        ">", "<", "="};
    int operator = GREATER;
    int threshold = 0;
    double weightThreshold = 0;
    Set<String> filter;
    JPanel filterHubPanel = new JPanel();
    JButton filterHubButton = new JButton();
    JLabel filterHubLabel = new JLabel();
    JComboBox filterHubBox = new JComboBox(OPERATORS);
    JTextField filterHubTextField = new JTextField();
    JLabel thresholdLabel = new JLabel();
    JTextField thresholdTextField = new JTextField();
    
    public HubFilter(boolean active) {
      active = false;
    }

    public HubFilter() {
      active = true;
      weightThreshold = mainFrame.EDGE_WEIGHT_THRESHOLD;
      currentFilterYPosition++;
      filterHubButton.setMaximumSize(new Dimension(30, 30));
      filterHubButton.setMinimumSize(new Dimension(30, 30));
      filterHubButton.setPreferredSize(new Dimension(30, 30));
      filterHubButton.setToolTipText("Remove this filter");
      filterHubButton.setIcon(new ImageIcon(be.unamur.gviz.MainFrame.class.getResource(
          "resources/minus_1_16.png")));
      filterHubButton.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
      filterHubButton.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
      filterHubButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          remove();
        }
      });
      filterHubLabel.setText("# hub members");
      thresholdLabel.setText("weight threshold");
      filterHubPanel.setLayout(gridBagLayout3);
      filterHubBox.setMinimumSize(new Dimension(50, 20));
      filterHubBox.setPreferredSize(new Dimension(50, 20));
      filterHubBox.setSelectedIndex(operator);
      filterHubBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          operator = filterHubBox.getSelectedIndex();
          filters.remove(filter);
          setFilter();
        }
      });
      filterHubTextField.setMinimumSize(new Dimension(40, 20));
      filterHubTextField.setPreferredSize(new Dimension(40, 20));
      filterHubTextField.setText("" + threshold);
      filterHubTextField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            threshold = Integer.parseInt(filterHubTextField.getText());
            filters.remove(filter);
            setFilter();
          } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                                          filterHubTextField.getText() +
                                          " is not an integer",
                                          "#hub members filter error",
                                          JOptionPane.ERROR_MESSAGE,
                                          MainFrame.imageHub);
          }
        }
      });
      thresholdTextField.setMinimumSize(new Dimension(40, 20));
      thresholdTextField.setPreferredSize(new Dimension(40, 20));
      thresholdTextField.setText("" + weightThreshold);
      thresholdTextField.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		try {
      			weightThreshold = Double.parseDouble(thresholdTextField.getText());
      			filters.remove(filter);
      			setFilter();
      		} catch (NumberFormatException ex) {
      			JOptionPane.showMessageDialog(null,
      					thresholdTextField.getText() +
      					" is not a real",
      					"#hub members filter error",
      					JOptionPane.ERROR_MESSAGE,
      					MainFrame.imageHub);
      		}
      	}
      });
      filterHubPanel.setBorder(BorderFactory.createEtchedBorder());
      filterPanel.add(filterHubPanel,
                      new GridBagConstraints(0, currentFilterYPosition, 1, 1,
                                             1.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(0, 0, 0, 0), 0, 0));
      filterHubPanel.add(filterHubTextField,
                         new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 2, 3), 0, 0));
      filterHubPanel.add(thresholdTextField,
      		new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
      				, GridBagConstraints.WEST,
      				GridBagConstraints.HORIZONTAL,
      				new Insets(0, 3, 2, 3), 0, 0));
      filterHubPanel.add(filterHubBox,
                         new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 3, 2, 3), 0, 0));
      filterHubPanel.add(thresholdLabel,
      		new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
      				, GridBagConstraints.WEST,
      				GridBagConstraints.NONE,
      				new Insets(0, 0, 2, 3), 0, 0));
      filterHubPanel.add(filterHubButton,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(2, 3, 0, 0), 0, 0));
      filterHubPanel.add(filterHubLabel,
                         new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(2, 3, 0, 3), 0, 0));
      setFilter();
    }

    private void setFilter() {
      filter = new HashSet<String>();
      for (String vertex : dataset.getVertices()) {
        int size = dataset.getHubSize(vertex);
        for (String neighbor : dataset.getInteractions(vertex)){
        	if (dataset.getWeight(vertex, neighbor) < weightThreshold) size--;
        }
        switch (operator) {
          case GREATER:
            if (size > threshold) {
              filter.add(vertex);
            }
            break;
          case SMALLER:
            if (size < threshold) {
              filter.add(vertex);
            }
            break;
          case EQUAL:
            if (size == threshold) {
              filter.add(vertex);
            }
            break;
        }
      }
      filters.add(filter);
      applyFilters();
    }

    public void remove() {
      if (active) {
        active = false;
        filters.remove(filter);
        filterPanel.remove(filterHubPanel);
        applyFilters();
        mainFrame.HUB = false;
        mainFrame.hubFilterToggleButton.setSelected(false);
      }
    }
  }

  public void fillPathBoxes(){
    pathPicking = true;
    pathBoxA.removeAllItems();
    pathBoxB.removeAllItems();
    pathBoxA.addItem(NO_PATH_A);
    pathBoxB.addItem(NO_PATH_B);
    for (String o : drawnVertices){
      pathBoxA.addItem(o);
      pathBoxB.addItem(o);
    }
    pathPicking = false;
  }

  public boolean hasShortestPath(){
  	return (!PATH_A_GENE.equals(NO_PATH_A) && !PATH_B_GENE.equals(NO_PATH_B));
  }
  private void jbInit() throws Exception {
    dislayedDeepnessBorder = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151)), "Displayed node deepness");
    nodeSizeBorder = new TitledBorder(BorderFactory.createEtchedBorder(Color.
        white, new Color(165, 163, 151)), "Node size");
    transparencyBorder = new TitledBorder(BorderFactory.createEtchedBorder(Color.
    		white, new Color(165, 163, 151)), "Transparency");
    clusterBorder = new TitledBorder(BorderFactory.createEtchedBorder(Color.
        white, new Color(165, 163, 151)), COMMANDSTRING + "0");
    geneListTitledBorder = new TitledBorder(BorderFactory.createEtchedBorder(
        Color.white, new Color(165, 163, 151)),
                                            "Listed nodes (" + 0 +
                                            " available)");
    titledBorder5 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"Grouping");
    titledBorder6 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"KEGG pathway");
    pathTitleBorder = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"Shortest path");
    this.setOrientation(JToolBar.VERTICAL);
    this.setFloatable(false);
    transparencySlider.setMajorTickSpacing(10);
    transparencySlider.setMaximum(255);
    transparencySlider.setMinimum(0);
    transparencySlider.setMinorTickSpacing(1);
    transparencySlider.setPaintLabels(false);
    transparencySlider.setPaintTicks(false);
    transparencySlider.setBorder(transparencyBorder);
    transparencySlider.setPreferredSize(new Dimension(barWidth, 51));
    transparencySlider.setValue(200);
    GraphTools.setTransparency(200);
    transparencySlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        GraphTools.setTransparency(source.getValue());
        mainFrame.refreshGraph();
      }
    });
    nodeSizeSlider.setMajorTickSpacing(5);
    nodeSizeSlider.setMaximum(30);
    nodeSizeSlider.setMinimum(1);
    nodeSizeSlider.setMinorTickSpacing(1);
    nodeSizeSlider.setPaintLabels(false);
    nodeSizeSlider.setPaintTicks(false);
    nodeSizeSlider.setBorder(nodeSizeBorder);
    nodeSizeSlider.setPreferredSize(new Dimension(barWidth, 51));
    nodeSizeSlider.setValue(mainFrame.VERTEX_BASE_SIZE);
    nodeSizeSlider.addChangeListener(new ChangeListener() {
    	public void stateChanged(ChangeEvent e) {
    		JSlider source = (JSlider) e.getSource();
    		mainFrame.VERTEX_BASE_SIZE = source.getValue();
    		mainFrame.refreshGraph();
    	}
    });
    deepnessJSlider.setOrientation(JSlider.HORIZONTAL);
    deepnessJSlider.setMajorTickSpacing(1);
    deepnessJSlider.setMaximum(6);
    deepnessJSlider.setMinimum(0);
    deepnessJSlider.setMinorTickSpacing(0);
    deepnessJSlider.setPaintLabels(true);
    deepnessJSlider.setPaintTicks(true);
    ((JLabel)deepnessJSlider.getLabelTable().get(6)).setText("All");
    ((JLabel)deepnessJSlider.getLabelTable().get(6)).setSize(new Dimension(12,14));
    deepnessJSlider.setBorder(dislayedDeepnessBorder);
    deepnessJSlider.setPreferredSize(new Dimension(barWidth, 74));
    deepnessJSlider.setValue(1);
    deepnessJSlider.setSnapToTicks(true);
    deepnessJSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting() && refreshGraphWhenSelecting) {
          showGraph();
        }
      }
    });
    edgeBetweennessSlider.setMajorTickSpacing(1);
    edgeBetweennessSlider.setMaximum(0);
    edgeBetweennessSlider.setMinimum(0);
    edgeBetweennessSlider.setMinorTickSpacing(1);
    edgeBetweennessSlider.setBorder(clusterBorder);
    edgeBetweennessSlider.setPreferredSize(new Dimension(barWidth, 51));
    edgeBetweennessSlider.setValue(0);
    edgeBetweennessSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
          int numEdgesToRemove = source.getValue();
          clusterBorder.setTitle(COMMANDSTRING + numEdgesToRemove);
          edgeBetweennessSlider.repaint();
          mainFrame.tools.cluster(mainFrame, mainFrame.layout, numEdgesToRemove);
        } else {
          clusterBorder.setTitle(COMMANDSTRING + source.getValue());
          edgeBetweennessSlider.repaint();
        }
      }
    });
    groupClusters.setText("CLUSTERS");
    groupClusters.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        mainFrame.refreshGraph();
      }
    });
    groupMasked.setText("MASKED");
    groupMasked.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        mainFrame.refreshGraph();
      }
    });
    groupingPanel.setBorder(titledBorder5);
    keggButton.setBorder(BorderFactory.createRaisedBevelBorder());
    keggButton.setMaximumSize(new Dimension(56, 56));
    keggButton.setMinimumSize(new Dimension(56, 56));
    keggButton.setToolTipText("See the selected pathway in KEGG");
    keggButton.setIcon(MainFrame.imageKegg);
    keggButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		new Thread(new Runnable(){
    			public void run(){
    				if (!PATHWAY_ID.equals(NO_PATHWAY_ID)) 
    					Tools.openURL("http://www.genome.jp/kegg-bin/show_pathway?"+PATHWAY_ID.replaceAll("path:", ""));
    			}
    		}).start();
    	}
    });
    keggButton.setBorder(null);
    keggButton.setBorderPainted(false);
    keggButton.setContentAreaFilled(false);
    keggButton.setMargin(new Insets(0, 0, 0, 0));  
    pathwayIdBox = new JComboBox(pathwayIdToDesc.keySet().toArray());
    pathwayIdBox.addItemListener(new ItemListener() {
    	public void itemStateChanged(ItemEvent arg0) {
    		if (!synchPathwayBoxes){
    		synchPathwayBoxes = true;
  			PATHWAY_ID = pathwayIdBox.getSelectedItem().toString();   				
  			PATHWAY_DESC = pathwayIdToDesc.get(PATHWAY_ID); 
  			pathwayDescriptionBox.setSelectedItem(PATHWAY_DESC);
  			mainFrame.tools.buildPathway(PATHWAY_ID);
  			mainFrame.refreshGraph();
    		synchPathwayBoxes = false;
    		}    		
    	}
    });
    pathwayDescriptionBox = new JComboBox(pathwayDescToId.keySet().toArray());
    pathwayDescriptionBox.addItemListener(new ItemListener() {
    	public void itemStateChanged(ItemEvent arg0) {
    		if (!synchPathwayBoxes){
    			synchPathwayBoxes = true;
    			PATHWAY_DESC = pathwayDescriptionBox.getSelectedItem().toString();
    			PATHWAY_ID = pathwayDescToId.get(PATHWAY_DESC);    				
    			pathwayIdBox.setSelectedItem(PATHWAY_ID);
    			mainFrame.tools.buildPathway(PATHWAY_ID);
    			mainFrame.refreshGraph();
    			synchPathwayBoxes = false;
    		}    		
    	}
    });
    entrezLabel.setWrapStyleWord(true);
    entrezLabel.setLineWrap(true);
    entrezLabel.setFont(UIManager.getFont("Label.font"));
    entrezLabel.setBackground(UIManager.getColor("Panel.background"));
    entrezLabel.setVisible(true);
    entrezLabel.setForeground(Color.RED);
    entrezLabel.setEditable(false);
    pathwayLabel.setWrapStyleWord(true);
    pathwayLabel.setLineWrap(true);
    pathwayLabel.setFont(UIManager.getFont("Label.font"));
    pathwayLabel.setBackground(UIManager.getColor("Panel.background"));
    pathwayLabel.setEditable(false);
    pathwayIdBox.setVisible(false);
    pathwayDescriptionBox.setVisible(false);
    pathwayPanel.setBorder(titledBorder6);
    pathwayPanel.setMaximumSize(new Dimension(2147483647, 130));
    pathwayPanel.setMinimumSize(new Dimension(168, 110));
    pathwayPanel.setPreferredSize(new Dimension(220, 130));
    pathwayPanel.setLayout(gridBagLayout10);   
    pathwayPanel.add(pathwayLabel,   new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 5, 0, 5), 0, 0));
    pathwayPanel.add(keggButton,   new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0
    		,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
    pathwayPanel.add(entrezLabel,    new GridBagConstraints(1, 1, 1, 2, 1.0, 1.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 3, 5), 0, 0));
    pathwayPanel.add(pathwayIdBox,    new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
    		,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 0, 5), 0, 0));
    pathwayPanel.add(pathwayDescriptionBox,   new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 3, 5), 0, 0));
    pathPanel.setBorder(pathTitleBorder);
    pathPanel.setMaximumSize(new Dimension(2147483647, 80));
    pathPanel.setMinimumSize(new Dimension(168, 75));
    pathPanel.setPreferredSize(new Dimension(barWidth, 85));
    pathPanel.setLayout(gridBagLayout1);
    behaviourBox.setBorder(BorderFactory.createRaisedBevelBorder());
    behaviourBox.setMaximumSize(new Dimension(56, 56));
    behaviourBox.setMinimumSize(new Dimension(56, 56));
    behaviourBox.setPreferredSize(new Dimension(56, 56));
    behaviourBox.setToolTipText("Grey behaviour");
    behaviourBox.setBorder(null);
    behaviourBox.setBorderPainted(false);
    behaviourBox.setContentAreaFilled(false);
    behaviourBox.setMargin(new Insets(0, 0, 0, 0));
    behaviourBox.setIcon(MainFrame.imagePathBehaviourArrow);
    pathBoxA.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent e){
        if (!pathPicking){
          pathPicking = true;
          if (!PATH_A_GENE.equals(NO_PATH_A)) {
            mainFrame.pathEndsV.pick(PATH_A_GENE, false);
          }
          PATH_A_GENE = pathBoxA.getSelectedItem().toString();
          if (!PATH_A_GENE.equals(NO_PATH_A)) {
            mainFrame.pathEndsV.pick(PATH_A_GENE, true);
          }
          if (!PATH_B_GENE.equals(NO_PATH_B)) {
            mainFrame.pathEndsV.pick(PATH_B_GENE, true);
          }
          mainFrame.refreshGraph();
          pathPicking = false;
        }
      }
    });
    pathBoxB.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent e){
        if (!pathPicking){
          pathPicking = true;
          if (!PATH_B_GENE.equals(NO_PATH_B)) {
            mainFrame.pathEndsV.pick(PATH_B_GENE, false);
          }
          PATH_B_GENE = pathBoxB.getSelectedItem().toString();
          if (!PATH_A_GENE.equals(NO_PATH_A)) {
            mainFrame.pathEndsV.pick(PATH_A_GENE, true);
          }
          if (!PATH_B_GENE.equals(NO_PATH_B)) {
            mainFrame.pathEndsV.pick(PATH_B_GENE, true);
          }
          mainFrame.refreshGraph();
          pathPicking = false;
        }
      }
    });
    pathPanel.add(behaviourBox,   new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
    pathPanel.add(pathBoxA,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 0, 5), 0, 0));
    pathPanel.add(pathBoxB,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 3, 5), 0, 0));
    groupingPanel.add(groupClusters, null);
    groupingPanel.add(groupMasked, null);
    networkPanel.setLayout(gridBagLayout2);
    networkPanel.setBorder(geneListTitledBorder);
    networkPanel.setPreferredSize(new Dimension(barWidth, 163));
    geneListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    geneListScrollPane.setBorder(BorderFactory.createEtchedBorder());
    vertexList.setBorder(BorderFactory.createRaisedBevelBorder());
    vertexList.setToolTipText("Select nodes to display their interaction network");
    vertexList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    vertexList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && refreshGraphWhenSelecting) {
          showGraph();
        }
      }
    });
    flowLayout5.setAlignment(FlowLayout.LEFT);
    flowLayout5.setHgap(3);
    flowLayout5.setVgap(2);
    flowLayout3.setAlignment(FlowLayout.LEFT);
    flowLayout3.setHgap(3);
    flowLayout3.setVgap(2);
    flowLayout4.setAlignment(FlowLayout.LEFT);
    flowLayout4.setHgap(3);
    flowLayout4.setVgap(2);
    filterPanel.setLayout(gridBagLayout4);
    networkTabbedPane.add("Ordered by node", geneListScrollPane);
    networkPanel.add(networkTabbedPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
    		, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
    geneListScrollPane.setViewportView(vertexList);
    clusterListPanel.setLayout(new BorderLayout());
    clusterSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    clusterList.setBorder(BorderFactory.createRaisedBevelBorder());
    clusterList.setToolTipText("Select cluster to display only nodes within");
    clusterList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    clusterList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && refreshGraphWhenSelecting) {
          applyFilters();
        }
      }
    });
    clusterScrollPane.setViewportView(clusterList);
    clusterSplitPane.setLeftComponent(clusterScrollPane);
    vertexOfSelectedClusterList.setBorder(BorderFactory.createRaisedBevelBorder());
    vertexOfSelectedClusterList.setToolTipText("Select nodes to display their interaction network");
    vertexOfSelectedClusterList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    vertexOfSelectedClusterList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && refreshGraphWhenSelecting) {
          showGraph();
        }
      }
    });
    geneOfClusterScrollPane.setViewportView(vertexOfSelectedClusterList);
    clusterSplitPane.setRightComponent(geneOfClusterScrollPane);
    clusterListPanel.add(clusterSplitPane);
    displayFullClusterButton.setText("DISPLAY FULL CLUSTER");
    displayFullClusterButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
        refreshGraphWhenSelecting = false;
        deepnessJSlider.setValue(0);
        vertexOfSelectedClusterList.setSelectionInterval(0,vertexOfSelectedClusterListModel.size()-1);
        refreshGraphWhenSelecting = true;
        showGraph();
    	}
    });
    clusterListPanel.add(displayFullClusterButton, BorderLayout.SOUTH);
    networkTabbedPane.add("Grouped by cluster", clusterListPanel);
    networkPanel.add(filterPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0), 0, 0));
    selectAllButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
        refreshGraphWhenSelecting = false;
        deepnessJSlider.setValue(0);
        networkTabbedPane.setSelectedComponent(geneListScrollPane);
        vertexList.setSelectionInterval(0,vertexListModel.size()-1);
        refreshGraphWhenSelecting = true;
        showGraph();
    	}
    });
    networkPanel.add(selectAllButton, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0), 0, 0));
    this.add(nodeSizeSlider, null);
    this.add(transparencySlider, null);
    this.add(edgeBetweennessSlider, null);
    this.add(groupingPanel, null);
    this.add(pathwayPanel, null);
    this.add(pathPanel, null);
    this.add(deepnessJSlider, null);
    this.add(networkPanel, null);
  }

  private void initBehaviourMenu(){
    JRadioButtonMenuItem itemComplete = new JRadioButtonMenuItem(MainFrame.imagePathBehaviour);
    itemComplete.setText(COMPLETE_BEHAVIOUR);
    itemComplete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        behaviourBox.setIcon(MainFrame.imagePathBehaviourArrow);
        CURRENT_PATH_BEHAVIOUR = COMPLETE_BEHAVIOUR;
        mainFrame.refreshGraph();
      }
    });
    behaviourMenu.add(itemComplete);
    JRadioButtonMenuItem itemCluster = new JRadioButtonMenuItem(MainFrame.imagePathBehaviourCluster);
    itemCluster.setText(CLUSTER_BEHAVIOUR);
    itemCluster.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        behaviourBox.setIcon(MainFrame.imagePathBehaviourClusterArrow);
        CURRENT_PATH_BEHAVIOUR = CLUSTER_BEHAVIOUR;
        mainFrame.refreshGraph();
      }
    });
    behaviourMenu.add(itemCluster);
    JRadioButtonMenuItem itemMask = new JRadioButtonMenuItem(MainFrame.imagePathBehaviourMask);
    itemMask.setText(MASK_BEHAVIOUR);
    itemMask.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        behaviourBox.setIcon(MainFrame.imagePathBehaviourMaskArrow);
        CURRENT_PATH_BEHAVIOUR = MASK_BEHAVIOUR;
        mainFrame.refreshGraph();
      }
    });
    behaviourMenu.add(itemMask);
    JRadioButtonMenuItem itemBoth = new JRadioButtonMenuItem(MainFrame.imagePathBehaviourBoth);
    itemBoth.setText(BOTH_BEHAVIOUR);
    itemBoth.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        behaviourBox.setIcon(MainFrame.imagePathBehaviourBothArrow);
        CURRENT_PATH_BEHAVIOUR = BOTH_BEHAVIOUR;
        mainFrame.refreshGraph();
      }
    });
    behaviourMenu.add(itemBoth);
    ButtonGroup radio = new ButtonGroup();
    radio.add(itemComplete);
    radio.add(itemCluster);
    radio.add(itemMask);
    radio.add(itemBoth);
    radio.add(itemBoth);
    itemComplete.setSelected(true);
    behaviourBox.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        behaviourMenu.show(e.getComponent(), e.getX(), e.getY());
      }

      public void mouseEntered(MouseEvent e) {}

      public void mouseExited(MouseEvent e) {}

      public void mousePressed(MouseEvent e) {}

      public void mouseReleased(MouseEvent e) {}
    });
  }

}

