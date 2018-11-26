package minimaxtictactoe;

import java.util.ArrayList;

public class Node {
	
	public int id;
	public ArrayList<Integer> neighbors;
	
	public Integer parent = null;
	
	public int alpha;
	public int beta;
	private Integer value = null;
	
	private String state;
	
	public Node(int id, String state) {
		this.id = id;
		this.state = state;
		
		neighbors = new ArrayList<Integer>(0);
	}
	
	public int getId() {
		return id;
	}
	
	public String getState() {
		return state;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void addNeighbor(int n) {
		neighbors.add(n);
	}

}
