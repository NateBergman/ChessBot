import java.util.Arrays;

public class ArrayNegativeSwapper {
    public static void main (String[] args) {
        int[] array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 82, 82, 82, 82, 82, 82, 82, 82, 0, 0, 180, 216, 143, 177, 150, 208, 116, 71, 0, 0, 76, 89, 108, 113, 147, 138, 107, 62, 0, 0, 68, 95, 88, 103, 105, 94, 99, 59, 0, 0, 55, 80, 77, 94, 99, 88, 92, 57, 0, 0, 56, 78, 78, 72, 85, 85, 115, 70, 0, 0, 47, 81, 62, 59, 67, 106, 120, 60, 0, 0, 82, 82, 82, 82, 82, 82, 82, 82, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < array.length; i++) {
            array[i] = -array[i];
        }
        System.out.print(Arrays.toString(array));
    }
}
