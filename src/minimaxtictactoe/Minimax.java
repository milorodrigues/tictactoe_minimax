package minimaxtictactoe;

import java.util.Iterator;

public class Minimax {
	
	public char character;
	public int number;
	
	private Graph graph;
	
	public Minimax(boolean begins) {
		
		if (begins) {
			this.character = 'X'; this.number = 1;
		}
		else {
			this.character = 'O'; this.number = 2;
		}
	}
	
	public void execTurn() {
		
		System.out.println("Waiting for opponent...");
		
		//Cria uma �rvore com o estado atual do tabuleiro como raiz.
		//Cada n� tem como filhos os movimentos poss�veis a partir da configura��o daquele n�.
		
		graph = new Graph();
		graph.createNode(Main.board.state); graph.setNodeValue(0, Integer.MIN_VALUE);
		
		long startTime = System.nanoTime();
		
		search(0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		
		//Ap�s rodar a busca, confere os filhos da raiz para escolher o movimento que leve ao maior valor poss�vel.
		//Encontrando o melhor estado filho, compara o tabuleiro da raiz e do filho em quest�o para saber em qual c�lula deve jogar.
		
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
			
			//Imprime o tempo que status() levou, para fins de registro
			
			if(timeElapsed/1000000 > 0) System.out.println("\n" + "search() time elapsed: " + timeElapsed/1000000 + " miliseconds");
			else System.out.println("search() time elapsed: " + timeElapsed + " nanoseconds");
			
			//Checa fim de jogo e termina a execu��o caso o jogo tenha acabado
			
			char status = Main.board.checkVictory();
			if (status == '-') {
				Main.player.execTurn();
			} else if (status == 'X') {
				System.out.println("\nPlayer X wins!");
			} else if (status == 'O') {
				System.out.println("\nPlayer O wins!");
			} else if (status == 'T') {
				System.out.println("\nTie!");
			}
			
		} else {
			System.out.println("No next move possible. Please restart game.");
		}
		
	}
	
	public int search(int v, boolean max, int a, int b) {
		
		//Essa fun��o retorna o valor do n� v.
		//Se max = true, a fun��o se comporta como max. Se false, se comporta como min.
		//int a se refere a alfa, int b se refere a beta.
		
		String state = graph.getNodeStatebyId(v);
		
		if (isLeaf(v)) {
			// Caso seja folha, calcula o valor do n� e retorna.
			graph.setNodeValue(v, evaluateLeaf(v));
			return graph.getNodeValue(v);
		}
		
		//Loop para gerar os at� 9 poss�veis filhos de v e avali�-los.
		for (int i=0;i<9;i++) {
			if (state.charAt(i) == '-') {
				//Se h� um - na string de estado (ou seja, uma c�lula vazia), pode-se gerar um estado filho jogando naquela c�lula vazia.
				//Gera o estado seguinte a partir do estado atual e calcula seu valor recursivamente.
				String newstate = state;
				if (max) newstate = state.substring(0, i) + this.character + state.substring(i+1);
				else newstate = state.substring(0, i) + Main.player.character + state.substring(i+1);
				
				int child = graph.createChild(v, newstate);
				int childValue = search(child, !max, a, b);
				
				//Aqui, ap�s a recurs�o, toda a sub-�rvore enraizada no filho correspondente a essa itera��o do n� j� foi avaliada.
				
				if (max) {
					if (graph.getNodeValue(v) == null || childValue > graph.getNodeValue(v)) {
						//Caso max, procura o filho de maior valor para descobrir o valor de v.
						graph.setNodeValue(v, childValue);
						/*Atualiza alfa. alfa >= beta indica que n�o � necess�rio gerar os outros filhos pois n�o ser� poss�vel aumentar o valor.
						Ent�o j� retorna o valor atual de v.*/
						if (graph.getNodeValue(v) > a) { a = graph.getNodeValue(v); }
						if (a >= b) return a;
					}
				} else {
					if (graph.getNodeValue(v) == null || childValue < graph.getNodeValue(v)) {
						//Caso min, procura o filho de menor valor para descobrir o valor de v.
						graph.setNodeValue(v, childValue);
						/*Atualiza alfa. alfa >= beta indica que n�o � necess�rio gerar os outros filhos pois n�o ser� poss�vel diminuir o valor.
						Ent�o j� retorna o valor atual de v.*/
						if (graph.getNodeValue(v) < b) { b = graph.getNodeValue(v); }
						if (a >= b) return b;
					}
				}
								
			}
		}
		
		//Retorna o valor de v.
		return graph.getNodeValue(v);
		
	}
	
	public boolean isLeaf(int v) {
		
		//leaf indica se o n� v � folha ou n�o. Come�a assumindo que n�o � folha.
		
		boolean leaf = false;
		String state = graph.getNodeStatebyId(v);
		
		//Se o estado cont�m uma vit�ria na vertical, � folha
		for (int i=0;i<9;i+=3) {
			if (state.charAt(i) == state.charAt(i+1) && state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) != '-')
				leaf = true;
		}
		//Se o estado cont�m uma vit�ria na horizontal, � folha
		if (!leaf) {
			for (int i=0;i<3;i++) {		
				if (state.charAt(i) == state.charAt(i+3) && state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) != '-')
					leaf = true;
			}
		}
		//Se o estado cont�m uma vit�ria na diagonal, � folha
		if (!leaf) {
			if (state.charAt(0) == state.charAt(4) && state.charAt(4) == state.charAt(8) && state.charAt(0) != '-')
				leaf = true;
			else if (state.charAt(2) == state.charAt(4) && state.charAt(4) == state.charAt(6) && state.charAt(2) != '-')
				leaf = true;
		}
				
		//Se o estado n�o cont�m vit�rias mas est� totalmente preenchido, � empate, logo � folha
		if (!leaf) {
			boolean tie = true;
			for (int i=0;i<9;i++) {
				if (state.charAt(i) == '-') {
					tie = false;
					break;
				}
			}
			if (tie) leaf = true;
		} 
		
		return leaf;
		
	}
	
	public int evaluateLeaf(int v) {
		
		//Calcula o valor do estado v. Note que a fun��o foi escrita espeficamente para calcular o valor de uma folha, apesar de funcionar para qualquer n�.
		
		String state = graph.getNodeStatebyId(v);
		int value = 0;

		//Regra 1: Linha completa(vit�ria) vale 100 pontos, mais 1 ponto para cada c�lula vazia
		
		//Aplicando regra 1 �s linhas
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
		//Aplicando regra 1 �s colunas
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
		//Aplicando regra 1 �s diagonais
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
		
		//Se houve vit�ria, o resto dos pontos n�o � calculado.
		//Isso � para evitar que o agente tente adiar a pr�pria vit�ria para acumular mais pontos.
		if (value != 0) return value;
		
		//As regras 2 e 3 existem para encorajar o agente a criar um fork, ou seja, uma configura��o na qual ele ganha no turno seguinte independente do movimento do oponente.
		
		//Regra 2: Linha formada por dois s�mbolos iguais e um vazio vale 30 pontos.
		
		//Aplicando regra 2 �s linhas
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
		//Aplicando regra 2 �s colunas
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
		//Aplicando regra 2 �s diagonais
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
		
		//Regra 3: Linha formada por um s�mbolo e dois vazios vale 5 pontos.
		
		//Aplicando regra 3 �s linhas
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
		//Aplicando regra 3 �s colunas
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
		//Aplicando regra 3 �s diagonais
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
		
		//Caso valor seja zero at� aqui, h� a chance de ser um empate. Empate vale 50 pontos para ambos os lados.
		//Se valor n�o for zero, n�o tem como ser um empate, pois ou houve vit�ria ou h� pelo menos um espa�o vazio.
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
