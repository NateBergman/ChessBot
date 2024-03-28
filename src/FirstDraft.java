import java.util.*;
public class FirstDraft { //really just the front-end
   public static void main (String[] args) {
       Positions positions = new Positions();
       Map<Byte,Character> displayMap = buildDisplayMap();
       Scanner console = new Scanner(System.in);
       ArrayList<Integer> moves = new ArrayList<>();
       while(true) {
           positions.printBoard(displayMap);
           System.out.print("0 white move, 1 black move, 2 undo move, 3 evaluate, 4 coded move");
           int x = console.nextInt();
           if (x == 2) {
               positions.unMakeMove(moves.get(moves.size() - 1));
               moves.remove(moves.get(moves.size() - 1));
           } else if (x == 3) {
               System.out.print("Depth: ");
               int depth = console.nextInt();
               System.out.println(positions.search(depth, depth, true));
           } else if (x == 4) {
               int move = console.nextInt();
               positions.makeMove(move);
               moves.add(move);
           } else {
               ArrayList<Integer> possibleMoves;
               if (x == 1) {
                   possibleMoves = positions.getBlackMoves();
               } else {
                   possibleMoves = positions.getWhiteMoves();
               }
               int move = 0;
               while (!possibleMoves.contains(move)) {
                   System.out.println("Enter Move:");
                   int from = console.nextInt();
                   int to = console.nextInt();
                   move = positions.encodeMove(to,from);
               }
               positions.makeMove(move);
               moves.add(move);
           }
       }
   }

    public static Map<Byte,Character> buildDisplayMap () {
        Map<Byte,Character> display = new HashMap<>();
        display.put((byte) 0,' ');
        display.put((byte) 1,'\u265F');
        display.put((byte) 9, '\u2659');
        display.put((byte) 2, '\u265E');
        display.put((byte) 10, '\u2658');
        display.put((byte) 3, '\u265D');
        display.put((byte) 11, '\u2657');
        display.put((byte) 4, '\u265C');
        display.put((byte) 12, '\u2656');
        display.put((byte) 5, '\u265B');
        display.put((byte) 13, '\u2655');
        display.put((byte) 6, '\u265A');
        display.put((byte) 14, '\u2654');
        return display;
    }
}