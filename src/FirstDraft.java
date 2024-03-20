import java.util.*;

public class FirstDraft {
   public static void main (String[] args) {
       byte boardState = (byte) 0b11111000;
       System.out.println(boardState);
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