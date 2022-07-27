package be.unamur.gviz;

import java.util.Collection;
import java.util.EnumMap;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import be.unamur.gviz.Annotation.Field;
import be.unamur.gviz.Annotation.PathWaySource;
import be.unamur.gviz.Database.Id;

public class NodeInfoPanel extends JTextPane {
	private final static String LIST="list";
	private final static String DISPLAYED="displayed";
	private final static String HIDDEN="hidden";
	private final static String THRESHOLD="thresold";
	private final static String GENE_INFO="gene information";
	
	private boolean detailsDisplayedVertices = false;
	private boolean detailsHiddenVertices = false;
	private boolean detailsThresholdVertices = false;
	private boolean detailsGeneInformation = false;
	private EnumMap<Id, Boolean> detailsIdentifiers = new EnumMap<Id, Boolean>(Id.class);
	private EnumMap<Field, Boolean> detailsFields = new EnumMap<Field, Boolean>(Field.class);
	private EnumMap<PathWaySource, Boolean> detailsPathways = new EnumMap<PathWaySource, Boolean>(PathWaySource.class);
	private String currentDisplayedVertex = "";
	
	private final MainFrame mainFrame;
	private final VertexSelection parent;
	
	private HyperlinkListener showGeneHyperLinkListener;
  private HyperlinkListener showListHyperLinkListener;
  private boolean showGeneListenerActive = false;
  private boolean showListListenerActive = false;

	public NodeInfoPanel(MainFrame m, VertexSelection p){
		super();
		this.mainFrame = m;
		this.parent = p; 
  	for (Id v : Id.values()){
  		detailsIdentifiers.put(v, false);
  	}
  	for (Field v : Field.values()){
  		detailsFields.put(v, false);
  	}
  	for (PathWaySource v : PathWaySource.values()){
  		detailsPathways.put(v, false);
  	}
		try {
			showGeneHyperLinkListener = new HyperlinkListener(){
				public void hyperlinkUpdate(HyperlinkEvent e){
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
						currentDisplayedVertex = e.getDescription();
						showCurrentSelectedVertex();
					}
				}
			};
			showListHyperLinkListener = new HyperlinkListener(){
				public void hyperlinkUpdate(HyperlinkEvent e){
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
						String hyperlink = e.getDescription();
						if (hyperlink.equals(DISPLAYED)){
							detailsDisplayedVertices = !detailsDisplayedVertices;
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(HIDDEN)){
							detailsHiddenVertices = !detailsHiddenVertices;
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(THRESHOLD)){
							detailsThresholdVertices = !detailsThresholdVertices;
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(GENE_INFO)){
							detailsGeneInformation = !detailsGeneInformation;
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Id.PROBE_SET.toString())){
							boolean b = detailsIdentifiers.get(Id.PROBE_SET);
							detailsIdentifiers.put(Id.PROBE_SET, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Id.ENTREZ.toString())){
							boolean b = detailsIdentifiers.get(Id.ENTREZ);
							detailsIdentifiers.put(Id.ENTREZ, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Id.ENSEMBL.toString())){
							boolean b = detailsIdentifiers.get(Id.ENSEMBL);
							detailsIdentifiers.put(Id.ENSEMBL, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Id.GENE_SYMBOL.toString())){
							boolean b = detailsIdentifiers.get(Id.GENE_SYMBOL);
							detailsIdentifiers.put(Id.GENE_SYMBOL, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Id.UNIGENE.toString())){
							boolean b = detailsIdentifiers.get(Id.UNIGENE);
							detailsIdentifiers.put(Id.UNIGENE, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Id.SWISS_PROT.toString())){
							boolean b = detailsIdentifiers.get(Id.SWISS_PROT);
							detailsIdentifiers.put(Id.SWISS_PROT, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Id.OMIM.toString())){
							boolean b = detailsIdentifiers.get(Id.OMIM);
							detailsIdentifiers.put(Id.OMIM, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Field.INTERPRO.toString())){
							boolean b = detailsFields.get(Field.INTERPRO);
							detailsFields.put(Field.INTERPRO, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Field.KEGG_ENZYME.toString())){
							boolean b = detailsFields.get(Field.KEGG_ENZYME);
							detailsFields.put(Field.KEGG_ENZYME, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Field.KEGG_ID.toString())){
							boolean b = detailsFields.get(Field.KEGG_ID);
							detailsFields.put(Field.KEGG_ID, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Field.BP.toString())){
							boolean b = detailsFields.get(Field.BP);
							detailsFields.put(Field.BP, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Field.CC.toString())){
							boolean b = detailsFields.get(Field.CC);
							detailsFields.put(Field.CC, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Field.MF.toString())){
							boolean b = detailsFields.get(Field.MF);
							detailsFields.put(Field.MF, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(Field.PUBMED.toString())){
							boolean b = detailsFields.get(Field.PUBMED);
							detailsFields.put(Field.PUBMED, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(PathWaySource.Kegg.toString())){
							boolean b = detailsPathways.get(PathWaySource.Kegg);
							detailsPathways.put(PathWaySource.Kegg, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(PathWaySource.GenMAPP.toString())){
							boolean b = detailsPathways.get(PathWaySource.GenMAPP);
							detailsPathways.put(PathWaySource.GenMAPP, !b);
							showCurrentSelectedVertex();
						}else if (hyperlink.equals(LIST)){
							setGeneList(parent.getSelectedVertices());
						}else{
							Tools.openURL(e.getURL().toString());                      	
						}
					}
				}
			};
	    setEditorKit(new HTMLEditorKit());
	    setEditable(false);	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public NodeInfoPanel(MainFrame m, VertexSelection parent, String text){
		this(m, parent);
		setText(text);
	}
	
  public void setGeneList(Collection<String> genes){
  	if (showListListenerActive) {
  		removeHyperlinkListener(showListHyperLinkListener);
  		showListListenerActive = false;
  	}
  	if (!showGeneListenerActive) {
  		addHyperlinkListener(showGeneHyperLinkListener);
  		showGeneListenerActive = true;
  	}
    String text = "";
    for (String gene : genes){
      text += "<a href=\""+gene+"\">"+gene+"</a><br>";
    }
    setText(text);
    setCaretPosition(0);  	
  }

  public void setSelectedVertex(String vertex){
		currentDisplayedVertex = vertex;
		showCurrentSelectedVertex(); 
  }
  
  public void showCurrentSelectedVertex(){
  	String vertex = currentDisplayedVertex;
  	if (showGeneListenerActive) {
  		removeHyperlinkListener(showGeneHyperLinkListener);
  		showGeneListenerActive = false;
  	}
  	if (!showListListenerActive) {
  		addHyperlinkListener(showListHyperLinkListener);
  		showListListenerActive = true;
  	}
  	StringBuilder displayedVertices = new StringBuilder();
  	StringBuilder hiddenVertices = new StringBuilder();
  	StringBuilder thresholdVertices = new StringBuilder();
  	int displayedCount=0, hiddenCount=0, thresholdCount=0;
  	for (String neighbor : mainFrame.dataset.getInteractions(vertex)){
			if (mainFrame.dataset.getWeight(vertex, neighbor) < mainFrame.EDGE_WEIGHT_THRESHOLD){
				thresholdVertices.append(neighbor+", ");
				thresholdCount++;
			}else{
				if (mainFrame.graph.containsVertex(neighbor)){
  				displayedVertices.append(neighbor+", ");
  				displayedCount++;
  			}else{
  				hiddenVertices.append(neighbor+", ");
  				hiddenCount++;
  			}	
			}
  	} 	
  	if (displayedVertices.length() > 0) displayedVertices.setLength(displayedVertices.length()-2);
  	if (hiddenVertices.length() > 0) hiddenVertices.setLength(hiddenVertices.length()-2);
  	if (thresholdVertices.length() > 0) thresholdVertices.setLength(thresholdVertices.length()-2);
  	displayedVertices.append("<br>");
  	hiddenVertices.append("<br>");
  	thresholdVertices.append("<br>");
  	StringBuilder text = new StringBuilder("<b>"+vertex+"</b> [<a href=\""+LIST+"\">List</a>]<br>");
  	text.append("<i>Probe set is used as reference</i>");
  	text.append("<p>");
  	text.append("<b>Other hub members</b> (" + mainFrame.dataset.getHubSize(vertex) + ") :<br>");
  	text.append("[<a href=\""+DISPLAYED+"\">"+((detailsDisplayedVertices)?"-":"+")+"</a>] <b><i>Displayed</i></b> (" + displayedCount + ")");
  	text.append("<br>");
  	if (detailsDisplayedVertices) text.append(displayedVertices.toString());
  	text.append("[<a href=\""+HIDDEN+"\">"+((detailsHiddenVertices)?"-":"+")+"</a>] <b><i>Hidden</i></b> (" + hiddenCount + ")");
  	text.append("<br>");
  	if (detailsHiddenVertices) text.append(hiddenVertices.toString());
  	text.append("[<a href=\""+THRESHOLD+"\">"+((detailsThresholdVertices)?"-":"+")+"</a>] <b><i>Under threshold</i></b> (" + thresholdCount + ")");
  	text.append("<br>");
  	if (detailsThresholdVertices) text.append(thresholdVertices.toString());
  	text.append("<p>");
  	try{
  		Annotation annotation = mainFrame.dataset.getAnnotation(vertex);
  		text.append("<b>Probe set identifiers</b> :<br>");
			text.append("[<a href=\""+Id.PROBE_SET.toString()+"\">"+((detailsIdentifiers.get(Id.PROBE_SET))?"-":"+")+"</a>] <b><i>"+Id.PROBE_SET.toString()+"</i></b> (" + annotation.getCount(Id.PROBE_SET) + ")");
			text.append("<br>");
			if (detailsIdentifiers.get(Id.PROBE_SET)) text.append(annotation.getURL(Id.PROBE_SET) + "<br>");
	  	text.append("<p>");
			text.append("[<a href=\""+GENE_INFO+"\">"+((detailsGeneInformation)?"-":"+")+"</a>] <b>Gene information</b> :");
			text.append("<br>");
			if (detailsGeneInformation){ 
				text.append(annotation.description + "<br>");
				text.append("<i>Chromosome location</i> : " + annotation.chromosomeLocation + "<br>");
			}
	  	text.append("<p>");
  		text.append("<b>Gene identifiers</b> :<br>");
			text.append("[<a href=\""+Id.GENE_SYMBOL.toString()+"\">"+((detailsIdentifiers.get(Id.GENE_SYMBOL))?"-":"+")+"</a>] <b><i>"+Id.GENE_SYMBOL.toString()+"</i></b> (" + annotation.getCount(Id.GENE_SYMBOL) + ")");
			text.append("<br>");
			if (detailsIdentifiers.get(Id.GENE_SYMBOL)) text.append(annotation.getURL(Id.GENE_SYMBOL) + "<br>");
			text.append("[<a href=\""+Id.ENTREZ.toString()+"\">"+((detailsIdentifiers.get(Id.ENTREZ))?"-":"+")+"</a>] <b><i>"+Id.ENTREZ.toString()+"</i></b> (" + annotation.getCount(Id.ENTREZ) + ")");
			text.append("<br>");
			if (detailsIdentifiers.get(Id.ENTREZ)) text.append(annotation.getURL(Id.ENTREZ) + "<br>");
			text.append("[<a href=\""+Field.KEGG_ID+"\">"+((detailsFields.get(Field.KEGG_ID))?"-":"+")+"</a>] <b><i>"+Field.KEGG_ID.getName()+"</i></b> (" + annotation.getCount(Field.KEGG_ID) + ")");
			text.append("<br>");
			if (detailsFields.get(Field.KEGG_ID)) text.append(annotation.getURL(Field.KEGG_ID) + "<br>");
			text.append("[<a href=\""+Id.ENSEMBL.toString()+"\">"+((detailsIdentifiers.get(Id.ENSEMBL))?"-":"+")+"</a>] <b><i>"+Id.ENSEMBL.toString()+"</i></b> (" + annotation.getCount(Id.ENSEMBL) + ")");
			text.append("<br>");
			if (detailsIdentifiers.get(Id.ENSEMBL)) text.append(annotation.getURL(Id.ENSEMBL) + "<br>");
			text.append("[<a href=\""+Id.UNIGENE.toString()+"\">"+((detailsIdentifiers.get(Id.UNIGENE))?"-":"+")+"</a>] <b><i>"+Id.UNIGENE.toString()+"</i></b> (" + annotation.getCount(Id.UNIGENE) + ")");
			text.append("<br>");
			if (detailsIdentifiers.get(Id.UNIGENE)) text.append(annotation.getURL(Id.UNIGENE) + "<br>");
	  	text.append("<p>");
  		text.append("<b>Gene Ontology</b> :<br>");
  		for (Field field : Field.values()){
  			switch(field){
  			case BP:
  			case CC:
  			case MF:
  				text.append("[<a href=\""+field+"\">"+((detailsFields.get(field))?"-":"+")+"</a>] <b><i>"+field.getName()+"</i></b> (" + annotation.getCount(field) + ")");
  				text.append("<br>");
  				if (detailsFields.get(field)) text.append(annotation.getURL(field) + "<br>");
  				break;
  			default:
  				break;
  			}
  		}
	  	text.append("<p>");
  		text.append("<b>Protein identifiers</b> :<br>");
			text.append("[<a href=\""+Id.SWISS_PROT.toString()+"\">"+((detailsIdentifiers.get(Id.SWISS_PROT))?"-":"+")+"</a>] [Sequence] <b><i>"+Id.SWISS_PROT.toString()+"</i></b> (" + annotation.getCount(Id.SWISS_PROT) + ")");
			text.append("<br>");
			if (detailsIdentifiers.get(Id.SWISS_PROT)) text.append(annotation.getURL(Id.SWISS_PROT) + "<br>");
			text.append("[<a href=\""+Field.INTERPRO+"\">"+((detailsFields.get(Field.INTERPRO))?"-":"+")+"</a>] [Domain] <b><i>"+Field.INTERPRO.getName()+"</i></b> (" + annotation.getCount(Field.INTERPRO) + ")");
			text.append("<br>");
			if (detailsFields.get(Field.INTERPRO)) text.append(annotation.getURL(Field.INTERPRO) + "<br>");
	  	text.append("<p>");
  		text.append("<b>Literature identifiers</b> :<br>");
			text.append("[<a href=\""+Field.PUBMED+"\">"+((detailsFields.get(Field.PUBMED))?"-":"+")+"</a>] <b>"+Field.PUBMED.getName()+"</b> (" + annotation.getCount(Field.PUBMED) + ")");
			text.append("<br>");
			if (detailsFields.get(Field.PUBMED)) text.append(annotation.getURL(Field.PUBMED) + "<br>");
	  	text.append("<p>");
  		text.append("<b>Disease identifiers</b> :<br>");
			text.append("[<a href=\""+Id.OMIM.toString()+"\">"+((detailsIdentifiers.get(Id.OMIM))?"-":"+")+"</a>] <b><i>"+Id.OMIM.toString()+"</i></b> (" + annotation.getCount(Id.OMIM) + ")");
			text.append("<br>");
			if (detailsIdentifiers.get(Id.OMIM)) text.append(annotation.getURL(Id.OMIM) + "<br>");
	  	text.append("<p>");
  		text.append("<b>Chemical Reaction identifiers</b> :<br>");
			text.append("[<a href=\""+Field.KEGG_ENZYME+"\">"+((detailsFields.get(Field.KEGG_ENZYME))?"-":"+")+"</a>] <b>"+Field.KEGG_ENZYME.getName()+"</b> (" + annotation.getCount(Field.KEGG_ENZYME) + ")");
			text.append("<br>");
			if (detailsFields.get(Field.KEGG_ENZYME)) text.append(annotation.getURL(Field.KEGG_ENZYME) + "<br>");
			text.append("<p>");
  		text.append("<b>Metabolic pathways</b> :<br>");
			text.append("[<a href=\""+PathWaySource.Kegg+"\">"+((detailsPathways.get(PathWaySource.Kegg))?"-":"+")+"</a>] <b><i>"+PathWaySource.Kegg.name()+"</i></b> (" + annotation.getCount(PathWaySource.Kegg) + ")");
			text.append("<br>");
			if (detailsPathways.get(PathWaySource.Kegg)) text.append(annotation.getURL(PathWaySource.Kegg) + "<br>");  			
			text.append("<p>");
  		text.append("<b>All pathways</b> :<br>");
			text.append("[<a href=\""+PathWaySource.GenMAPP+"\">"+((detailsPathways.get(PathWaySource.GenMAPP))?"-":"+")+"</a>] <b><i>"+PathWaySource.GenMAPP.name()+"</i></b> (" + annotation.getCount(PathWaySource.GenMAPP) + ")");
			text.append("<br>");
			if (detailsPathways.get(PathWaySource.GenMAPP)) text.append(annotation.getURL(PathWaySource.GenMAPP) + "<br>");  			
  	}catch (Exception e){
  		e.printStackTrace();
  		text.append("\n Java exception : "+e.getCause() + " (" + e.getMessage() + ")");
  		for (StackTraceElement el : e.getStackTrace()){
  			text.append("\tat " + el.toString());
  		}  		
  	}
  	text.append("<p>");
  	setText(text.toString());    
  	setCaretPosition(0);
  }
  
	
}
