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
		
		//Cria uma árvore com o estado atual do tabuleiro como raiz.
		//Cada nó tem como filhos os movimentos possíveis a partir da configuração daquele nó.
		
		graph = new Graph();
		graph.createNode(Main.board.state); graph.setNodeValue(0, Integer.MIN_VALUE);
		
		long startTime = System.nanoTime();
		
		search(0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		
		//Após rodar a busca, confere os filhos da raiz para escolher o movimento que leve ao maior valor possível.
		//Encontrando o melhor estado filho, compara o tabuleiro da raiz e do filho em questão para saber em qual célula deve jogar.
		
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
			
			//Checa fim de jogo e termina a execução caso o jogo tenha acabado
			
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
		
		//Essa função retorna o valor do nó v.
		//Se max = true, a função se comporta como max. Se false, se comporta como min.
		//int a se refere a alfa, int b se refere a beta.
		
		String state = graph.getNodeStatebyId(v);
		
		if (isLeaf(v)) {
			// Caso seja folha, calcula o valor do nó e retorna.
			graph.setNodeValue(v, evaluateLeaf(v));
			return graph.getNodeValue(v);
		}
		
		//Loop para gerar os até 9 possíveis filhos de v e avaliá-los.
		for (int i=0;i<9;i++) {
			if (state.charAt(i) == '-') {
				//Se há um - na string de estado (ou seja, uma célula vazia), pode-se gerar um estado filho jogando naquela célula vazia.
				//Gera o estado seguinte a partir do estado atual e calcula seu valor recursivamente.
				String newstate = state;
				if (max) newstate = state.substring(0, i) + this.character + state.substring(i+1);
				else newstate = state.substring(0, i) + Main.player.character + state.substring(i+1);
				
				int child = graph.createChild(v, newstate);
				int childValue = search(child, !max, a, b);
				
				//Aqui, após a recursão, toda a sub-árvore enraizada no filho correspondente a essa iteração do nó já foi avaliada.
				
				if (max) {
					if (graph.getNodeValue(v) == null || childValue > graph.getNodeValue(v)) {
						//Caso max, procura o filho de maior valor para descobrir o valor de v.
						graph.setNodeValue(v, childValue);
						/*Atualiza alfa. alfa >= beta indica que não é necessário gerar os outros filhos pois não será possível aumentar o valor.
						Então já retorna o valor atual de v.*/
						if (graph.getNodeValue(v) > a) { a = graph.getNodeValue(v); }
						if (a >= b) return a;
					}
				} else {
					if (graph.getNodeValue(v) == null || childValue < graph.getNodeValue(v)) {
						//Caso min, procura o filho de menor valor para descobrir o valor de v.
						graph.setNodeValue(v, childValue);
						/*Atualiza alfa. alfa >= beta indica que não é necessário gerar os outros filhos pois não será possível diminuir o valor.
						Então já retorna o valor atual de v.*/
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
		
		//leaf indica se o nó v é folha ou não. Começa assumindo que não é folha.
		
		boolean leaf = false;
		String state = graph.getNodeStatebyId(v);
		
		//Se o estado contém uma vitória na vertical, é folha
		for (int i=0;i<9;i+=3) {
			if (state.charAt(i) == state.charAt(i+1) && state.charAt(i+1) == state.charAt(i+2) && state.charAt(i) != '-')
				leaf = true;
		}
		//Se o estado contém uma vitória na horizontal, é folha
		if (!leaf) {
			for (int i=0;i<3;i++) {		
				if (state.charAt(i) == state.charAt(i+3) && state.charAt(i+3) == state.charAt(i+6) && state.charAt(i) != '-')
					leaf = true;
			}
		}
		//Se o estado contém uma vitória na diagonal, é folha
		if (!leaf) {
			if (state.charAt(0) == state.charAt(4) && state.charAt(4) == state.charAt(8) && state.charAt(0) != '-')
				leaf = true;
			else if (state.charAt(2) == state.charAt(4) && state.charAt(4) == state.charAt(6) && state.charAt(2) != '-')
				leaf = true;
		}
				
		//Se o estado não contém vitórias mas está totalmente preenchido, é empate, logo é folha
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
		
		//Calcula o valor do estado v. Note que a função foi escrita espeficamente para calcular o valor de uma folha, apesar de funcionar para qualquer nó.
		
		String state = graph.getNodeStatebyId(v);
		int value = 0;

		//Regra 1: Linha completa(vitória) vale 100 pontos, mais 1 ponto para cada célula vazia
		
		//Aplicando regra 1 às linhas
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
		//Aplicando regra 1 às colunas
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
		//Aplicando regra 1 às diagonais
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
		
		//Se houve vitória, o resto dos pontos não é calculado.
		//Isso é para evitar que o agente tente adiar a própria vitória para acumular mais pontos.
		if (value != 0) return value;
		
		//As regras 2 e 3 existem para encorajar o agente a criar um fork, ou seja, uma configuração na qual ele ganha no turno seguinte independente do movimento do oponente.
		
		//Regra 2: Linha formada por dois símbolos iguais e um vazio vale 30 pontos.
		
		//Aplicando regra 2 às linhas
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
		//Aplicando regra 2 às colunas
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
		//Aplicando regra 2 às diagonais
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
		
		//Regra 3: Linha formada por um símbolo e dois vazios vale 5 pontos.
		
		//Aplicando regra 3 às linhas
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
		//Aplicando regra 3 às colunas
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
		//Aplicando regra 3 às diagonais
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
		
		//Caso valor seja zero até aqui, há a chance de ser um empate. Empate vale 50 pontos para ambos os lados.
		//Se valor não for zero, não tem como ser um empate, pois ou houve vitória ou há pelo menos um espaço vazio.
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
