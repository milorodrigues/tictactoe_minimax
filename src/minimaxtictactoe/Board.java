package minimaxtictactoe;

import java.util.Arrays;

@SuppressWarnings("unused")

public class Board {
	
	//A configuração do tabuleiro é mantida no objeto como uma string.
	//Na string, - denota uma célula vazia, X ou O denotam uma célula preenchida de acordo.
	public String state = "---------";
	
	public Board() {
	}
	
	//Métodos utilitários para imprimir e atualizar o tabuleiro, conferir células individuais e checar se houve vitória.
	//Os nomes dos métodos são autoexplicativos.
	
	public void printBoard() {
		System.out.println();
		for (int i=0;i<3;i++) {
			System.out.print(" " + printCell(i) + " ");
			if (i < 2) {
				System.out.print("|");
			} else {
				System.out.print("\n");
			}
		}
		System.out.println("- - - - - -");
		for (int i=0;i<3;i++) {
			System.out.print(" " + printCell(i+3) + " ");
			if (i < 2) {
				System.out.print("|");
			} else {
				System.out.print("\n");
			}
		}
		System.out.println("- - - - - -");
		for (int i=0;i<3;i++) {
			System.out.print(" " + printCell(i+6) + " ");
			if (i < 2) {
				System.out.print("|");
			} else {
				System.out.print("\n");
			}
		}
		System.out.println();
	}
	
	public char printCell(int cell) {
		if (state.charAt(cell) == '-') return ' ';
		else return state.charAt(cell);
	}
	
	public void setCell(int cell, char player) {
		state = state.substring(0, cell) + player + state.substring(cell+1);
	}
	
	public boolean cellIsFilled(int cell) {
		if (state.charAt(cell) == '-') return false;
		else return true;
	}
	
	public char checkVictory() {
		
		char victory = '-';
		
		// -: unfinished game
		// X: X wins
		// O: O wins
		// T: tie
		
		//checking for rows
		for (int i=0;i<9;i+=3) {
			if (state.charAt(i) == state.charAt(i+1) && state.charAt(i+1) == state.charAt(i+2)) victory = state.charAt(i);
		}
		//checking for columns
		for (int i=0;i<3;i++) {
			if (state.charAt(i) == state.charAt(i+3) && state.charAt(i+3) == state.charAt(i+6)) victory = state.charAt(i);
		}
		//checking for diagonals
		if (state.charAt(0) == state.charAt(4) && state.charAt(4) == state.charAt(8)) victory = state.charAt(0);
		else if (state.charAt(2) == state.charAt(4) && state.charAt(4) == state.charAt(6)) victory = state.charAt(2);
		
		//checking for tie
		if (victory == '-') {
			for (int i=0;i<9;i++) {
				if (state.charAt(i) == '-') break;
				if (i == 8) victory = 'T';
			}
		}
		return victory;
	}

}
