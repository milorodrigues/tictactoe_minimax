package minimaxtictactoe;

import java.util.Iterator;

public class Minimax {
	
	public boolean begins;
	public char character;
	public int number;
	
	private Graph graph;
	
	public Minimax(boolean begins) {
		this.begins = begins;
		
		if (begins) {
			this.character = 'X'; this.number = 1;
		}
		else {
			this.character = 'O'; this.number = 2;
		}
	}
	
	public void execTurn() {
		
		System.out.println("Waiting for opponent...");
		
		graph = new Graph();
		graph.createNode(Main.board.state); graph.setNodeValue(0, Integer.MIN_VALUE);
		
		long startTime = System.nanoTime();
		
		search(0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		
		Iterator<Integer> it; it = graph.nodes.get(0).neighbors.iterator();
		Integer bestChild = null; Integer bestValue = Integer.MIN_VALUE;
		while (it.hasNext()) {
			int child = it.next();
			if (graph.getNodeValue(child) > bestValue) {
				bestChild = child; bestValue = graph.getNodeValue(child);
			}
		}
		int nextMove = -1;
		String state = graph.getNodeStatebyId(0);
		String childState = graph.getNodeStatebyId(bestChild);
		for (int i=0;i<9;i++) {
			if (state.charAt(i) != childState.charAt(i)) { nextMove = i; break; }
		}
		
		if (nextMove >= 0) {
			Main.board.setCell(nextMove, character);
			Main.board.printBoard();
			
			if(timeElapsed/1000000 > 0) System.out.println("\n" + "search() time elapsed: " + timeElapsed/1000000 + " miliseconds");
			else System.out.println("\n" + "search() time elapsed: " + timeElapsed + " nanoseconds");
			
			char status = Main.board.checkVictory();
			if (status == '-') {
				Main.player.execTurn();
			} else if (status == 'X') {
				System.out.println("Player X wins!");
			} else if (status == 'O') {
				System.out.println("Player O wins!");
			} else if (status == 'T') {
				System.out.println("Tie!");
			}
			
		} else {
			System.out.println("No next move possible. Please restart game.");
		}
		
	}
	
	public int search(int v, boolean max, int a, int b) {
		
		String state = graph.getNodeStatebyId(v);
		
		if (isLeaf(v)) {
			graph.setNodeValue(v, evaluateLeaf(v));
			return graph.getNodeValue(v);
		}
		
		for (int i=0;i<9;i++) {
			if (state.charAt(i) == '-') {
				String newstate = state;
				if (max) newstate = state.substring(0, i) + this.character + state.substring(i+1);
				else newstate = state.substring(0, i) + Main.player.character + state.substring(i+1);
				
				int child = graph.createChild(v, newstate);
				int childValue = search(child, !max, a, b);
				
				if (max) {
					if (graph.getNodeValue(v) == null || childValue > graph.getNodeValue(v)) {
						graph.setNodeValue(v, childValue);
						if (graph.getNodeValue(v) > a) { a = graph.getNodeValue(v); }
						if (a >= b) return a;
					}
				} else {
					if (graph.getNodeValue(v) == null || childValue < graph.getNodeValue(v)) {
						graph.setNodeValue(v, childValue);
						if (graph.getNodeValue(v) < b) { b = graph.getNodeValue(v); }
						if (a >= b) return b;
					}
				}
								
			}
		}
		
		return graph.getNodeValue(v);
		
	}
	
	public boolean isLeaf(int v) {
		
		boolean leaf = false;
		String state = graph.getNodeStatebyId(v);
		
		//checking for row victory
		for (int i=0;i<9;i+=3) {
			if (state.charAt(i) == state.charAt(i+1) && state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) != '-')
				leaf = true;
		}
		//checking for column victory
		for (int i=0;i<3;i++) {
			if (state.charAt(i) == state.charAt(i+3) && state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) != '-')
				leaf = true;
		}
		//checking for diagonals
		if (state.charAt(0) == state.charAt(4) && state.charAt(4) == state.charAt(8) && state.charAt(0) != '-')
			leaf = true;
		else if (state.charAt(2) == state.charAt(4) && state.charAt(4) == state.charAt(6) && state.charAt(2) != '-')
			leaf = true;
				
		//checking for tie
		boolean tie = true;
		for (int i=0;i<9;i++) {
			if (state.charAt(i) == '-') {
				tie = false;
				break;
			}
		}
		if (tie) leaf = true;
		
		return leaf;
		
	}
	
	public int evaluateLeaf(int v) {
		
		String state = graph.getNodeStatebyId(v);
		int value = 0;

		//Rule 1: full line = 100
		
		//checking rows
		for (int i=0;i<9;i+=3) {
			if (state.charAt(i) == state.charAt(i+1) && state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) == this.character) {
				value += 100;
				for (int j=0;j<9;j++) {
					if (state.charAt(j) == '-') value++;
				}
			}
			if (state.charAt(i) == state.charAt(i+1) && state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) == Main.player.character) {
				value -= 100;
				for (int j=0;j<9;j++) {
					if (state.charAt(j) == '-') value--;
				}
			}
		}
		//checking columns
		for (int i=0;i<3;i++) {
			if (state.charAt(i) == state.charAt(i+3) && state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) == this.character) {
				value += 100;
				for (int j=0;j<9;j++) {
					if (state.charAt(j) == '-') value++;
				}
			}
			if (state.charAt(i) == state.charAt(i+3) && state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) == Main.player.character) {
				value -= 100;
				for (int j=0;j<9;j++) {
					if (state.charAt(j) == '-') value--;
				}
			}
		}
		//checking diagonals
		if ((state.charAt(0) == state.charAt(4) && state.charAt(4) == state.charAt(8) && state.charAt(0) == this.character) ||
				(state.charAt(2) == state.charAt(4) && state.charAt(4) == state.charAt(6) && state.charAt(2) == this.character)) {
			value += 100;
			for (int j=0;j<9;j++) {
				if (state.charAt(j) == '-') value++;
			}
		}
		if ((state.charAt(0) == state.charAt(4) && state.charAt(4) == state.charAt(8) && state.charAt(0) == Main.player.character) ||
				(state.charAt(2) == state.charAt(4) && state.charAt(4) == state.charAt(6) && state.charAt(2) == Main.player.character)) {
			value -= 100;
			for (int j=0;j<9;j++) {
				if (state.charAt(j) == '-') value--;
			}
		}
		
		if (value != 0) return value;
		
		//Rule 2: 1 empty + 2 claimed = 30
		
		//checking rows
		for (int i=0;i<9;i+=3) {
			if ((state.charAt(i) == state.charAt(i+1) && state.charAt(i+2) == '-' && state.charAt(i) == this.character) ||
					(state.charAt(i) == state.charAt(i+2) && state.charAt(i+1) == '-' && state.charAt(i) == this.character) ||
					(state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) == '-' && state.charAt(i+1) == this.character)) {
				value += 30;
			}
			if ((state.charAt(i) == state.charAt(i+1) && state.charAt(i+2) == '-' && state.charAt(i) == Main.player.character) ||
					(state.charAt(i) == state.charAt(i+2) && state.charAt(i+1) == '-' && state.charAt(i) == Main.player.character) ||
					(state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) == '-' && state.charAt(i+1) == Main.player.character)) {
				value -= 30;
			}
		}
		//checking columns
		for (int i=0;i<3;i++) {
			if ((state.charAt(i) == state.charAt(i+3) && state.charAt(i+6) == '-' && state.charAt(i) == this.character) ||
					(state.charAt(i) == state.charAt(i+6) && state.charAt(i+3) == '-' && state.charAt(i) == this.character) ||
					(state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) == '-' && state.charAt(i+3) == this.character)) {
				value += 30;
			}
			if ((state.charAt(i) == state.charAt(i+3) && state.charAt(i+6) == '-' && state.charAt(i) == Main.player.character) ||
					(state.charAt(i) == state.charAt(i+6) && state.charAt(i+3) == '-' && state.charAt(i) == Main.player.character) ||
					(state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) == '-' && state.charAt(i+3) == Main.player.character)) {
				value -= 30;
			}
		}
		//checking diagonals
		if ((state.charAt(0) == state.charAt(4) && state.charAt(8) == '-' && state.charAt(0) == this.character) ||
				(state.charAt(0) == state.charAt(8) && state.charAt(4) == '-' && state.charAt(0) == this.character) ||
				(state.charAt(4) == state.charAt(8) && state.charAt(2) == '-' && state.charAt(4) == this.character) ||
				(state.charAt(2) == state.charAt(4) && state.charAt(6) == '-' && state.charAt(2) == this.character) ||
				(state.charAt(2) == state.charAt(6) && state.charAt(4) == '-' && state.charAt(2) == this.character) ||
				(state.charAt(4) == state.charAt(6) && state.charAt(2) == '-' && state.charAt(4) == this.character)) {
			value += 30;
		}
		if ((state.charAt(0) == state.charAt(4) && state.charAt(8) == '-' && state.charAt(0) == Main.player.character) ||
				(state.charAt(0) == state.charAt(8) && state.charAt(4) == '-' && state.charAt(0) == Main.player.character) ||
				(state.charAt(4) == state.charAt(8) && state.charAt(2) == '-' && state.charAt(4) == Main.player.character) ||
				(state.charAt(2) == state.charAt(4) && state.charAt(6) == '-' && state.charAt(2) == Main.player.character) ||
				(state.charAt(2) == state.charAt(6) && state.charAt(4) == '-' && state.charAt(2) == Main.player.character) ||
				(state.charAt(4) == state.charAt(6) && state.charAt(2) == '-' && state.charAt(4) == Main.player.character)) {
			value -= 30;
		}
		
		//Rule 3: 2 empty + 1 claimed = 5
		
		//checking rows
		for (int i=0;i<9;i+=3) {
			if ((state.charAt(i) == state.charAt(i+1) && state.charAt(i+2) == this.character && state.charAt(i) == '-') ||
					(state.charAt(i) == state.charAt(i+2) && state.charAt(i+1) == this.character && state.charAt(i) == '-') ||
					(state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) == this.character && state.charAt(i+1) == '-')) {
				value += 5;
			}
			if ((state.charAt(i) == state.charAt(i+1) && state.charAt(i+2) == Main.player.character && state.charAt(i) == '-') ||
					(state.charAt(i) == state.charAt(i+2) && state.charAt(i+1) == Main.player.character && state.charAt(i) == '-') ||
					(state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) == Main.player.character && state.charAt(i+1) == '-')) {
				value -= 5;
			}
		}
		//checking columns
		for (int i=0;i<3;i++) {
			if ((state.charAt(i) == state.charAt(i+3) && state.charAt(i+6) == this.character && state.charAt(i) == '-') ||
					(state.charAt(i) == state.charAt(i+6) && state.charAt(i+3) == this.character && state.charAt(i) == '-') ||
					(state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) == this.character && state.charAt(i+3) == '-')) {
				value += 5;
			}
			if ((state.charAt(i) == state.charAt(i+3) && state.charAt(i+6) == Main.player.character && state.charAt(i) == '-') ||
					(state.charAt(i) == state.charAt(i+6) && state.charAt(i+3) == Main.player.character && state.charAt(i) == '-') ||
					(state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) == Main.player.character && state.charAt(i+3) == '-')) {
				value -= 5;
			}
		}
		//checking diagonals
		if ((state.charAt(0) == state.charAt(4) && state.charAt(8) == this.character && state.charAt(0) == '-') ||
				(state.charAt(0) == state.charAt(8) && state.charAt(4) == this.character && state.charAt(0) == '-') ||
				(state.charAt(4) == state.charAt(8) && state.charAt(2) == this.character && state.charAt(4) == '-') ||
				(state.charAt(2) == state.charAt(4) && state.charAt(6) == this.character && state.charAt(2) == '-') ||
				(state.charAt(2) == state.charAt(6) && state.charAt(4) == this.character && state.charAt(2) == '-') ||
				(state.charAt(4) == state.charAt(6) && state.charAt(2) == this.character && state.charAt(4) == '-')) {
			value += 5;
		}
		if ((state.charAt(0) == state.charAt(4) && state.charAt(8) == Main.player.character && state.charAt(0) == '-') ||
				(state.charAt(0) == state.charAt(8) && state.charAt(4) == Main.player.character && state.charAt(0) == '-') ||
				(state.charAt(4) == state.charAt(8) && state.charAt(2) == Main.player.character && state.charAt(4) == '-') ||
				(state.charAt(2) == state.charAt(4) && state.charAt(6) == Main.player.character && state.charAt(2) == '-') ||
				(state.charAt(2) == state.charAt(6) && state.charAt(4) == Main.player.character && state.charAt(2) == '-') ||
				(state.charAt(4) == state.charAt(6) && state.charAt(2) == Main.player.character && state.charAt(4) == '-')) {
			value -= 5;
		}
		
		
		//checking for tie
		if (value == 0) {
			boolean tie = true;
			for (int i=0;i<9;i++) {
				if (state.charAt(i) == '-') {
					tie = false;
					break;
				}
			}
			if (tie) value += 50;
		}
		
		return value;	
	}

}
