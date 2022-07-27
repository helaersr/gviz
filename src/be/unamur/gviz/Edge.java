package be.unamur.gviz;

public class Edge {
	private double weight;
	
	public Edge(double weight){
		this.weight = weight;
	}
	
	public double getWeight(){
		return weight;
	}
	
	public String toString(){
		return Tools.displayDouble(Tools.round(weight,2));
	}
}
