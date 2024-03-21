import java.util.*;

public class FirstDraft {
   public static void main (String[] args) {
       Positions positions = new Positions();
       Map<Byte,Character> displayMap = buildDisplayMap();
       /*positions.printBoard(displayMap);
       positions.makeMove(0b11110000000000010010001101101110);
       positions.printBoard(displayMap);
       positions.unMakeMove(0b11110000000000010010001101101110);
       positions.printBoard(displayMap);
       System.out.println(0b11110000000000010010001101101110);
       System.out.println(positions.encodeMove(55,35));*/
       Scanner console = new Scanner(System.in);
       ArrayList<Integer> moves = new ArrayList<>();
       while(true) {
           positions.printBoard(displayMap);
           System.out.print("0 move 1 unmove");
           if (console.nextInt() == 1) {
               positions.unMakeMove(moves.get(moves.size()-1));
               moves.remove(moves.get(moves.size()-1));
           } else {
               int from = console.nextInt();
               int to = console.nextInt();
               int move = positions.encodeMove(to,from);
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