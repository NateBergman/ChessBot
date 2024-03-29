import java.util.Arrays;

public class PSTMaker {
    public static void main (String[] args) {
        int[] pesto = {
                0,   0,   0,   0,   0,   0,  0,   0,
                98, 134,  61,  95,  68, 126, 34, -11,
                -6,   7,  26,  31,  65,  56, 25, -20,
                -14,  13,   6,  21,  23,  12, 17, -23,
                -27,  -2,  -5,  12,  17,   6, 10, -25,
                -26,  -4,  -4, -10,   3,   3, 33, -12,
                -35,  -1, -20, -23, -15,  24, 38, -22,
                0,   0,   0,   0,   0,   0,  0,   0,
        };
        int offset = 0;
        int[] result = new int[120];
        for (int y = 7; y < -1; y--) {
            for (int x = 0; x < 8; x++) {
                result[91 - 10 * y + x] = pesto[8 * y + x] + offset;
            }
        }
        System.out.print(Arrays.toString(result));
    }
}
