import java.util.Arrays;

public class PSTMaker {
    public static void main (String[] args) {
        int[] pesto = {
                -74, -35, -18, -18, -11,  15,   4, -17,
                -12,  17,  14,  17,  17,  38,  23,  11,
                10,  17,  23,  15,  20,  45,  44,  13,
                -8,  22,  24,  27,  26,  33,  26,   3,
                -18,  -4,  21,  24,  27,  23,   9, -11,
                -19,  -3,  11,  21,  23,  16,   7,  -9,
                -27, -11,   4,  13,  14,   4,  -5, -17,
                -53, -34, -21, -11, -28, -14, -24, -43
        };
        int offset = 0;
        int[] result = new int[120];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                result[21 + 10 * y + x] = pesto[8 * y + x] + offset;
            }
        }
        int[] invertedResult = new int[120];
        for (int i = 0; i < 120; i++) {
            invertedResult[i] = result[119-i];
        }
        System.out.print(Arrays.toString(invertedResult));
    }
}
