import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) {
        //for each square, byte digits represent __,___,___,enpassant/castling ability(16s),color(8s),3xpiece
        //0 = empty
        //1 = pawn
        //2 = knight
        //3 = bishop
        //4 = rook (or 20)
        //5 = queen
        //6 = king (or 22)
        //7 = idk
        byte[] board = new byte[] {20,2,3,5,22,3,2,20,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,9,9,9,9,9,9,28,10,11,13,30,11,10,28};
    }
}