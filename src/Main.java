import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) {
        char[] board = new char[] {'P','P','P','P','P','P','P','P','R','N','B','Q','K','B','N','R',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
                ,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','p','p','p','p','p','p','p','p','r','n','b','q','k','b','n','r'};
        //board state byte (handles castling and en passant):
        //En Passant:0-7 represents column, 8+ not viable
        //Castling: 16s white short, 32s white long, 64s, black short, 128s black long
        byte boardState = (byte) 240;

        Map<Character,Character> display = buildDisplayMap();
        printBoard(board,display);

        //'A' = 65
        //'a' = 97
        // ' ' = 32
    }
    public static ArrayList<int[]> getMoves(char[] board, byte boardState, boolean whiteMove) {
        if (whiteMove) {
            for (int i = 0; i < 64; i++) {
                if (board[i] != ' ' && board[i] < 97) {
                    if (board[i] == 'R' || board[i] == 'Q') {
                        //linear moves
                        //go in +1, -1, +8, -8 increments
                    }
                    if (board[i] == 'B' || board[i] == 'Q') {
                        //diagonal moves
                        //go in +7, +9, -7, and -9 increments
                    }
                    if (board[i] == 'P') {
                        //pawn moves
                        //+8 if empty
                        //+7/+9 if enemy piece or en passant is in that column and currently on rank 5
                    }
                    if (board[i] == 'K') {
                        //king moves
                        //+7,+8,+9,+1,-1,-7.-8.-9
                    }
                    if (board[i] == 'N') {
                        //knight moves:
                        //-17
                        //-15
                        //-10
                        //-6
                        //+6
                        //+10
                        //+15
                        //+17
                    }
                }
            }
        }
        return null;
    }
    public static void printBoard(char[] board) {
        for (int i = 7; i > -1; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[8*i + j]);
            }
            System.out.println();
        }
    }
    public static void printBoard (char[] board, Map<Character,Character> display) {
        for (int i = 7; i > -1; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(display.get(board[8*i + j]));
            }
            System.out.println();
        }
    }

    public static Map<Character,Character> buildDisplayMap () {
        Map<Character,Character> display = new HashMap<>();
        display.put(' ',' ');
        display.put('P','\u265F');
        display.put('p', '\u2659');
        display.put('N', '\u265E');
        display.put('n', '\u2658');
        display.put('B', '\u265D');
        display.put('b', '\u2657');
        display.put('R', '\u265C');
        display.put('r', '\u2656');
        display.put('Q', '\u265B');
        display.put('q', '\u2655');
        display.put('K', '\u265A');
        display.put('k', '\u2654');
        return display;
    }
}