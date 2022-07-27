package be.unamur.gviz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import be.unamur.gviz.Database.Id;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;

public class Dataset {
	private double LOADED_WEIGHT_THRESHOLD = 0.3;
	private final Map<String, Integer> labelToId = new HashMap<String, Integer>();
	private final Map<Integer, Set<String>> idToLabel = new HashMap<Integer, Set<String>>();
	private final String[] probeSetIds;
	private final String[] vertices;
	private SparseDoubleMatrix1D[] adjacence;
	private final int vnum;
	private final File source; 
	private final MainFrame frame;
	private final Id identifiers;
	
	public Dataset(MainFrame mainFrame, File file) throws Exception {
		frame = mainFrame;
		source = file;
		identifiers = Id.valueOfName(JOptionPane.showInputDialog(frame, "Which identifiers do you want to use within gViz ?", 
				"Identifier conversion", JOptionPane.QUESTION_MESSAGE, 
				new ImageIcon(be.unamur.gviz.gViz.class.getResource("resources/layout_KK.png")), 
				Id.values(), Id.PROBE_SET).toString());
		FileReader fr = new FileReader(source);
		BufferedReader br = new BufferedReader(fr);
		String line;
		//First line 
		line = br.readLine();
		labelToId.clear();
		probeSetIds = line.trim().split("\t");
		for (int i=0 ; i < probeSetIds.length ; i++){
			probeSetIds[i] = probeSetIds[i].replace('"', ' ').trim();
		}
		if (identifiers != Id.PROBE_SET){
			frame.setProgressVisible("Converting " + Id.PROBE_SET.toString() + " to " + identifiers.toString(), true, true);
			Map<String, Set<String>> probeToId = gViz.DB.convert(Arrays.asList(probeSetIds), Id.PROBE_SET, identifiers);
			for (int i=0 ; i < probeSetIds.length ; i++){
				Set<String> ids = probeToId.get(probeSetIds[i]); 
				idToLabel.put(i, ids);
				for (String id : ids){
					labelToId.put(id, i);
				}
			}
			frame.setProgressVisible("", false, false);
		}else{
			for (int i=0 ; i < probeSetIds.length ; i++){
				Set<String> set = new HashSet<String>();
				set.add(probeSetIds[i]);
				idToLabel.put(i, set);
				labelToId.put(probeSetIds[i], i);
			}
		}
		vertices = getVerticesAsList().toArray(new String[0]);
		vnum = probeSetIds.length;
		adjacence = new SparseDoubleMatrix1D[vnum];
		new DatasetReader(this, mainFrame.EDGE_WEIGHT_THRESHOLD, fr, br).execute();
	}
	
  public class DatasetReader extends SwingWorker<Void, Void>{
  	private final FileReader fr;
  	private final BufferedReader br;
  	private final Dataset dataset;
  	public DatasetReader(Dataset d, double threshold, FileReader fr, BufferedReader br){
  		LOADED_WEIGHT_THRESHOLD = threshold;
  		this.fr = fr;
  		this.br = br;
  		this.dataset = d;
  		addPropertyChangeListener(new PropertyChangeListener(){
  			public void propertyChange(PropertyChangeEvent e){
          if("progress".equals(e.getPropertyName())) {
          	frame.setProgressValue((Integer) e.getNewValue());
          }
  			}
  		});
  	}
  	protected Void doInBackground() throws Exception {
  		frame.setProgressVisible("Loading dataset", true, false);
  		String line;
  		int id=0, p=0;
  		while ((line = br.readLine()) != null){
  			if (line.length() > 0){
  				setProgress((++p)*100/vnum);
  				String[] current = line.split("\t");
  				adjacence[id] = new SparseDoubleMatrix1D(id+1);
  				for (int i=0 ; i <= id ; i++){
  					double val = Double.parseDouble(current[i+1]);
  					if (val >= LOADED_WEIGHT_THRESHOLD) adjacence[id].setQuick(i, val);
    			}  					
  				id++;
  			}
  		}
  		return null;
  	}
  	protected void done(){
  		frame.setProgressVisible("", false, false);
  		frame.leftToolBar.setGeneList(dataset);
  		if (frame.searchBox != null){
  			frame.searchBox.setVisible(false);
  			frame.searchBox = null;
  		}
  		frame.leftToolBar.entrezLabel.setVisible(identifiers != Id.ENTREZ);
  		frame.leftToolBar.pathwayDescriptionBox.setVisible(identifiers == Id.ENTREZ);
  		frame.leftToolBar.pathwayIdBox.setVisible(identifiers == Id.ENTREZ);
  		try{
  			br.close();
  			fr.close();
  		}catch (Exception e){
  			e.printStackTrace();
  		}
  	}
  }
	
  public class DatasetUpdater extends SwingWorker<Void, Void>{
  	private final double newThreshold;
  	private final FileReader fr;
  	private final BufferedReader br;
  	private final Dataset dataset;
  	public DatasetUpdater(Dataset d, double newThreshold) throws Exception {
  		this.newThreshold = newThreshold;
  		this.dataset = d;
  		fr = new FileReader(source);
  		br = new BufferedReader(fr);
  		br.readLine();
  		addPropertyChangeListener(new PropertyChangeListener(){
  			public void propertyChange(PropertyChangeEvent e){
  				if("progress".equals(e.getPropertyName())) {
  					frame.setProgressValue((Integer) e.getNewValue());
  				}
  			}
  		});
  	}
  	protected Void doInBackground() throws Exception {
  		if (newThreshold < LOADED_WEIGHT_THRESHOLD){
  			frame.setProgressVisible("Updating dataset", true, false);
  			setProgress(0);
  			String line;
  			int id=0, p=0;
  			while ((line = br.readLine()) != null){
  				if (line.length() > 0){
  					setProgress((++p)*100/vnum);
  					String[] current = line.split("\t");
  					for (int i=0 ; i <= id ; i++){
  						double val = Double.parseDouble(current[i+1]);
  						if (val < LOADED_WEIGHT_THRESHOLD && val >= newThreshold) adjacence[id].setQuick(i, val);
  					}
  					id++;
  				}
  			}
  			LOADED_WEIGHT_THRESHOLD = newThreshold;
  		}
  		return null;
  	}
  	protected void done(){
  		frame.setProgressVisible("", false, false);
  		frame.leftToolBar.setGeneList(dataset);
  		if (frame.searchBox != null){
  			frame.searchBox.setVisible(false);
  			frame.searchBox = null;
  		}  		
  		try{
  			br.close();
  			fr.close();
  		}catch (Exception e){
  			e.printStackTrace();
  		}
  		frame.leftToolBar.showGraph();
  		frame.edgeWeightThresholdButton.setText("<html><center><B><FONT face=\"Arial\" size=2>EDGE WEIGHT<br>THRESHOLD<br>"+frame.EDGE_WEIGHT_THRESHOLD+"</FONT></B></center></html>");
  		frame.repaint();          		  		
  	}
  }
  
  public void updateDataset(double newThreshold) throws Exception {
    DatasetUpdater updater = new DatasetUpdater(this, newThreshold);
    updater.execute();
  }
  
  public Id getIdentifiersType(){
  	return identifiers;
  }
  
	public List<String> getVerticesAsList(){
		List<String> list = new ArrayList<String>();
		for (Set<String> vertex : idToLabel.values())
			list.addAll(vertex);
		return list;
	}
	
	public final String[] getVertices(){
		return vertices;
	}
	
	public double getWeight(String vertex1, String vertex2){
		int id1 = labelToId.get(vertex1);
		int id2 = labelToId.get(vertex2);
		return (id1 < id2) ? adjacence[id2].getQuick(id1) : adjacence[id1].getQuick(id2);
  }
  
  public int getHubSize(String vertex) {
  	if (!labelToId.containsKey(vertex)) return 0;
  	int id = labelToId.get(vertex);
  	int size = 0;
  	for (int i=0 ; i < id ; i++){
  		if (adjacence[id].getQuick(i) > 0) size++;
  	}
  	for (int i=id ; i < vnum ; i++){
  		if (adjacence[i].getQuick(id) > 0) size++;
  	}
    return size;
  }

  public Set<String> getInteractions(String vertex){
  	Set<String> interactions = new TreeSet<String>();
  	if (!labelToId.containsKey(vertex)) return interactions;
  	int id = labelToId.get(vertex);
  	for (int i=0 ; i < id ; i++){
  		if (adjacence[id].getQuick(i) > 0) interactions.addAll(idToLabel.get(i));
  	}
  	for (int i=id ; i < vnum ; i++){
  		if (adjacence[i].getQuick(id) > 0) interactions.addAll(idToLabel.get(i));
  	}
    return interactions;
  }
  
  public String getOtherHubMembers(String vertex) {
    String hub = "";
    for(String s : getInteractions(vertex)) {
      hub += s + ", ";
    }
    return (hub.length() == 0) ? hub : hub.substring(0, hub.length()-2);   
  }

  public Annotation getAnnotation(String vertex) throws Exception {
  	return new Annotation(probeSetIds[labelToId.get(vertex)]);
  }
  
 }
