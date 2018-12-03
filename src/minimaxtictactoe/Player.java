package minimaxtictactoe;

public class Player {
	
	public char character;
	public int number;
	public char numCh;
	
	public Player(boolean begins) {
		
		if (begins) {
			this.character = 'X'; this.number = 1;
		}
		else {
			this.character = 'O'; this.number = 2;
		}
	}
	
	public void execTurn() {
		
		//Simples, s� l� a c�lula na qual o jogador humano quer jogar e a preenche no tabuleiro
		
		System.out.print("\nIt's your turn. Pick a cell (numbered 1 to 9) to play: ");
		
		int cell = Main.scan.nextInt(); cell--;
		while (cell < 0 || cell > 8 || Main.board.cellIsFilled(cell)) {
			System.out.print("Invalid cell. Please pick a valid cell (numbered 1 to 9) to play: ");
			cell = Main.scan.nextInt(); cell--;
		}
		
		Main.board.setCell(cell, character);		
		Main.board.printBoard();
		
		//Checa fim de jogo e termina a execu��o caso o jogo tenha acabado
		
		char status = Main.board.checkVictory();
		if (status == '-') {
			Main.minimax.execTurn();
		} else if (status == 'X') {
			System.out.println("\nPlayer X wins!");
		} else if (status == 'O') {
			System.out.println("\nPlayer O wins!");
		} else if (status == 'T') {
			System.out.println("\nTie!");
		}
	}

}
