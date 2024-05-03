import java.util.*;
public class MoveSorter implements Comparator<Integer> { //just sorting captures from most valuable captured, least valuable moved
    byte[] board;
    int[] scores;
    public MoveSorter(byte[] board) {
        this.board = board;
        scores = new int[] {0,1,3,3,5,9,20,0,0,1,3,3,5,9,20};
    }
    public int compare(Integer m1, Integer m2) { //better moves have lower integers/outputs
        //boolean capture1 = (m1 >>19 & 0b1111) != 0;
        //boolean capture2 = (m2 >>19 & 0b1111) != 0;
        //if (capture1 && capture2) { //to is 1, from is 8;
            byte to1 = board[m1 >> 1 & 0b1111111];
            byte from1 = board[m1 >> 8 & 0b1111111];
            byte to2 = board[m2 >> 1 & 0b1111111];
            byte from2 = board[m2 >> 1 & 0b1111111];
            return -4 * board[to1] + board[from1] + 4 * board[to2] + board[from2];
        //}
        //if (capture1) {
        //    return -1;
        //}
        //if (capture2) {
        //    return 1;
        //}
        //return 0;
    }
}