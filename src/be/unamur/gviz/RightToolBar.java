package be.unamur.gviz;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.picking.PickedInfo;

import java.awt.geom.Point2D;

/**
 * <p>Title: gViz</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Laboratory of Evolutionary Genetics (ULB)</p>
 * @author Raphael Helaers
 * @version 1.0
 */

class RightToolBar  extends JToolBar {
	private final MainFrame mainFrame;
  
  final int barWidth = 230;
  static boolean isPicking = false;
  JSlider deepnessSlider = new JSlider();
  TitledBorder highlightTitleBorder;
  JScrollPane displayedGenesScrollPane = new JScrollPane();
  DefaultListModel geneListModel = new DefaultListModel();
  JList displayedGenesList = new JList(geneListModel);
  TitledBorder geneListTitledBorder;
  TitledBorder titledBorder4;
  JScrollPane infoScrollPane = new JScrollPane();
  NodeInfoPanel geneInfo;
  TitledBorder titledBorder1;
  JSplitPane genesSplitPane = new JSplitPane();
  JPanel genePanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel saveSelectionPanel = new JPanel();
  JButton saveSelectionButton = new JButton("Save selection");
  JComboBox savedSelectionsBox = new JComboBox(new String[]{"None"});
  Map<String, Set<String>> savedSelections = new HashMap<String, Set<String>>();
  int selectionIndex = 0;
  
  public RightToolBar(MainFrame m) {
  	this.mainFrame = m;
  	geneInfo = new NodeInfoPanel(mainFrame, mainFrame);
  	savedSelections.put("None", new TreeSet<String>());
    mainFrame.psV.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent e){
        if (!isPicking){
          isPicking = true;
          int[] selection = new int[mainFrame.psV.getPicked().size()];
          Set<String> genes = new HashSet<String>();
          int i=0;
          for (String gene : mainFrame.psV.getPicked()){
            genes.add(gene);
            selection[i] = geneListModel.indexOf(gene);
            i++;
          }
          displayedGenesList.setSelectedIndices(selection);
          if (genes.size() == 1){
          	geneInfo.setSelectedVertex(genes.iterator().next()); 
          }else if (genes.size() > 0){
          	geneInfo.setGeneList(genes); 
          }else{
          	geneInfo.setText("");
          }
          highlightTitleBorder.setTitle("Highlight deepness ("+mainFrame.psV.getPicked().size()+"+"+getHighlightedNumber()+")");
          deepnessSlider.repaint();
          isPicking = false;
        }
      }
    });
//    MainFrame.renderer.setVertexLabelCentering(true);
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  void fillGeneList(Set<String> list) {
    geneListModel.removeAllElements();
    for (String gene : list) {
      geneListModel.addElement(gene);
    }
    geneListTitledBorder.setTitle("Displayed nodes (" + list.size() + ")");
    displayedGenesScrollPane.repaint();
  }

  private int getHighlightedNumber(){
    int count = 0;
    for (String v : mainFrame.graph.getVertices()){
      if (!mainFrame.psV.isPicked(v) && neighborIsPicked(mainFrame.psV, v, 1)) count++;
    }
    return count;
  }

  public boolean neighborIsPicked(PickedInfo<String> pi, String vertex, int deepness) {
    if (deepness <= deepnessSlider.getValue()) {
      for (String w : mainFrame.graph.getSuccessors(vertex)) {
        if (pi.isPicked(w) || neighborIsPicked(pi, w, deepness + 1)) {
          return true;
        }
      }
    }
    return false;
  }

  private void jbInit() throws Exception {
    highlightTitleBorder = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151)),"Highlight deepness");
    geneListTitledBorder = new TitledBorder(BorderFactory.createEtchedBorder(
        Color.white, new Color(165, 163, 151)), "Displayed nodes (0)");
    titledBorder4 = new TitledBorder(BorderFactory.createEtchedBorder(Color.
        white, new Color(165, 163, 151)), "Selected node info");
//    titledBorder1 = new TitledBorder(BorderFactory.createEmptyBorder(),"INTERACTOME HIGHLIGHTING");
    this.setOrientation(JToolBar.VERTICAL);
    this.setFloatable(false);
//    this.setBorder(titledBorder1);
    deepnessSlider.setMajorTickSpacing(1);
    deepnessSlider.setMaximum(5);
    deepnessSlider.setMinimum(0);
    deepnessSlider.setMinorTickSpacing(0);
    deepnessSlider.setPaintLabels(true);
    deepnessSlider.setPaintTicks(true);
    deepnessSlider.setBorder(highlightTitleBorder);
    deepnessSlider.setPreferredSize(new Dimension(barWidth, 74));
    deepnessSlider.setValue(1);
    deepnessSlider.setSnapToTicks(true);
    deepnessSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
          if (!isPicking){
            isPicking = true;
            mainFrame.psV.clear();
            for (int i = 0; i < displayedGenesList.getSelectedValues().length; i++) {
              String gene = displayedGenesList.getSelectedValues()[i].toString();
              mainFrame.psV.pick(gene, true);
            }
          mainFrame.vv.validate();
          mainFrame.vv.repaint();
          highlightTitleBorder.setTitle("Highlight deepness ("+mainFrame.psV.getPicked().size()+"+"+getHighlightedNumber()+")");
          isPicking = false;
        }
      }
      }
    });
    displayedGenesScrollPane.setVerticalScrollBarPolicy(JScrollPane.
        VERTICAL_SCROLLBAR_ALWAYS);
    displayedGenesScrollPane.setBorder(geneListTitledBorder);
    displayedGenesList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          if (!isPicking){
          	pickSelectedGenes();
        }
      }
    }
  });
    infoScrollPane.setBorder(titledBorder4);
    infoScrollPane.getViewport().add(geneInfo);
    genesSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    genesSplitPane.setBorder(null);
    genePanel.setLayout(gridBagLayout1);
    genesSplitPane.add(displayedGenesScrollPane, JSplitPane.TOP);
    genesSplitPane.add(infoScrollPane, JSplitPane.BOTTOM);
    displayedGenesScrollPane.getViewport().add(displayedGenesList, null);
    genesSplitPane.setResizeWeight(0.1);
    genePanel.setPreferredSize(new Dimension(barWidth, 250));
    genePanel.add(genesSplitPane,   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    saveSelectionPanel.setLayout(new GridBagLayout());
    final GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.insets = new Insets(5, 5, 5, 5);
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    saveSelectionPanel.add(saveSelectionButton, gridBagConstraints);
    saveSelectionButton.addActionListener(new ActionListener() {
    	public void actionPerformed(final ActionEvent arg0) {
    		Set<String> selection = new TreeSet<String>();
    		for (Object o : displayedGenesList.getSelectedValues()){
    			selection.add(o.toString());
    		}
    		selectionIndex++;
    		String key = "Selection "+selectionIndex;
    		savedSelections.put(key, selection);
    		savedSelectionsBox.addItem(key);
    		isPicking = true;
    		savedSelectionsBox.setSelectedItem(key);
    		isPicking = false;
    	}
    });
    final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
    gridBagConstraints_1.weightx = 1;
    gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
    gridBagConstraints_1.gridy = 0;
    gridBagConstraints_1.gridx = 1;
    saveSelectionPanel.add(savedSelectionsBox, gridBagConstraints_1);
    saveSelectionPanel.setPreferredSize(new Dimension(barWidth, 30));
    saveSelectionPanel.setMaximumSize(new Dimension(barWidth, 30));
    savedSelectionsBox.addItemListener(new ItemListener() {
    	public void itemStateChanged(final ItemEvent arg0) {
    		if (!isPicking && arg0.getStateChange() == ItemEvent.SELECTED){
    			isPicking = true;
    			Set<String> selection = savedSelections.get(savedSelectionsBox.getSelectedItem().toString());
    			int[] selectedIndices = new int[selection.size()];
    			int i=0;
    			for (String gene : selection){
    				selectedIndices[i] = geneListModel.indexOf(gene);
    				i++;
    			}
    			displayedGenesList.setSelectedIndices(selectedIndices);
    			isPicking = false;
    			pickSelectedGenes();
    		}
    	}
    });
    this.add(deepnessSlider, null);
    this.add(saveSelectionPanel, null);
    this.add(genePanel, null);
  }

  private void pickSelectedGenes(){
  	isPicking = true;
  	mainFrame.psV.clear();
  	for (int i = 0; i < displayedGenesList.getSelectedValues().length; i++) {
  		String gene = displayedGenesList.getSelectedValues()[i].toString();
  		mainFrame.psV.pick(gene, true);
  	}
  	//Center the screen on the first vertex of the selection and display gene info
  	if (displayedGenesList.getSelectedValues().length > 0){
  		geneInfo.setSelectedVertex(displayedGenesList.getSelectedValue().toString());
  		Layout<String, Edge> layout = mainFrame.vv.getGraphLayout();
  		Point2D q = layout.transform(displayedGenesList.getSelectedValue().toString());
  		Point2D lvc = mainFrame.vv.getRenderContext().getMultiLayerTransformer().inverseTransform(mainFrame.vv.getCenter());
  		final double dx = (lvc.getX() - q.getX()) / 10;
  		final double dy = (lvc.getY() - q.getY()) / 10;

  		Runnable animator = new Runnable() {

  			public void run() {
  				for (int i = 0; i < 10; i++) {
  					mainFrame.vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
  					try {
  						Thread.sleep(100);
  					} catch (InterruptedException ex) {
  					}
  				}
  			}
  		};
  		Thread thread = new Thread(animator);
  		thread.start();

  	}else{
  		geneInfo.setText("");
  	}
  	isPicking = false;  	
  }
}
