import java.util.*;
public class BitBoardFirstDraft { //also using 0x88
    //board representation changing it so pieces are 0 empty and 1-6 white 7-12 black
    static byte[] board;
    static byte boardState;

    static long[] pieceBitboards = new long[] {0,0b1111111100000000,0b1000010,0b100100,0b10000001,0b1000,0b10000,
            0b11111111000000000000000000000000000000000000000000000000L,0b100001000000000000000000000000000000000000000000000000000000000L,
            0b10010000000000000000000000000000000000000000000000000000000000L,0b1000000100000000000000000000000000000000000000000000000000000000L,
            0b100000000000000000000000000000000000000000000000000000000000L,0b1000000000000000000000000000000000000000000000000000000000000L
    };

    //useful constants
    static final long NOTAFILE = 0b1111111011111110111111101111111011111110111111101111111011111110L;
    static final long NOTABFILE = 0b1111110011111100111111001111110011111100111111001111110011111100L;
    static final long NOTHFILE = 0b0111111101111111011111110111111101111111011111110111111101111111L;
    static final long NOTGHFILE = 0b00111111001111110011111100111111001111110011111100111111001111111L;
    public static void main (String[] args) {

    }
}