package minimaxtictactoe;

import java.util.Scanner;

public class Main {
	
	public static Scanner scan = new Scanner(System.in);
	public static Board board = new Board();
	
	public static Player player;
	public static Minimax minimax;

	public static void main(String[] args) {
		
		System.out.println("Welcome to Tic Tac Toe!"); 
		
		boolean welcome = welcome();
		player = new Player(welcome);
		minimax = new Minimax(!welcome);
		
		board.printBoard();
		
		if (welcome) player.execTurn();
		else minimax.execTurn();
		
		scan.close();

	}
	
	protected static boolean welcome() {
		
		System.out.println("Pick X or O (player X plays first!): ");
		
		String input; input = scan.next();
		
		if (input.charAt(0) == 'x' || input.charAt(0) == 'X') {
			System.out.println("You're player X.");
			return true;
		}
		else if (input.charAt(0) == 'o' || input.charAt(0) == 'O') {
			System.out.println("You're player O.");
			return false;
		}
		else {
			System.out.println("Invalid input! Please input X or O.");
			return welcome();
		}
		
	}

}
