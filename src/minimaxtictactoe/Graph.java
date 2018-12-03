package minimaxtictactoe;

import java.util.LinkedList;


public class Graph {
	
	//Classe e métodos utilitários para criar e utilizar um grafo.
	
	public LinkedList<Node> nodes;
	private int nextnode;
	
	public Graph() {
		nodes = new LinkedList<Node>();
		nextnode = 0;
	}
	
	public int createNode(String state) {
		Node node = new Node(nextnode, state);
		nextnode++;
		nodes.add(node);
		
		return (nextnode-1);
	}
	
	public int createChild(Integer parent, String state) {
		createNode(state);
		createDirectedEdge(parent, nextnode-1);
		nodes.get(nextnode-1).parent = parent;
		
		return (nextnode-1);
	}
	
	public String getNodeStatebyId(int id) {
		return nodes.get(id).getState();
	}
	
	public int setNodeValue(int id, int value) {	
		Node node = nodes.get(id);
		node.setValue(value);
		nodes.set(id, node);
		return value;
	}
	
	public Integer getNodeValue(int id) {
		return nodes.get(id).getValue();
	}
	
	public void createDirectedEdge(int a, int b) {
		Node node = nodes.get(a);
		node.addNeighbor(b);
		nodes.set(a, node);
	}
	
}