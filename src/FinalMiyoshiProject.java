import java.util.*;
import java.io.*;
public class FinalMiyoshiProject {
    //board setup
    static byte[] board;
    static byte boardState;
    static Set<Integer>[] pieceLists = new Set[]{new HashSet<>(), new HashSet<>()};
    static int[] kingPositions = {25,95};
    static MoveSorter sorter;
    //move generation constants for each piece
    static boolean[] moveGenSlide = {false,false,true,true,true,false};
    static int[][] moveGenOffset = {{},{-21, -19,-12, -8, 8, 12, 19, 21},{-11,  -9,  9, 11},{-10,  -1,  1, 10},{-11, -10, -9, -1, 1,  9, 10, 11},{-11, -10, -9, -1, 1,  9, 10, 11}};

    //scoring initialization - includes phase for mid/endgame scoring and pst's
    static int phase = 0;
    static int searchNumber = 0;
    static int[] phaseCounts = {0,0,1,1,2,4,0,0,0,0,1,1,2,4,0};
    static int[] WPO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 82, 82, 82, 82, 82, 82, 82, 82, 0, 0, 47, 81, 62, 59, 67, 106, 120, 60, 0, 0, 56, 78, 78, 72, 85, 85, 115, 70, 0, 0, 55, 80, 77, 94, 99, 88, 92, 57, 0, 0, 68, 95, 88, 103, 105, 94, 99, 59, 0, 0, 76, 89, 108, 113, 147, 138, 107, 62, 0, 0, 180, 216, 143, 177, 150, 208, 116, 71, 0, 0, 82, 82, 82, 82, 82, 82, 82, 82, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BPO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 82, 82, 82, 82, 82, 82, 82, 82, 0, 0, 180, 216, 143, 177, 150, 208, 116, 71, 0, 0, 76, 89, 108, 113, 147, 138, 107, 62, 0, 0, 68, 95, 88, 103, 105, 94, 99, 59, 0, 0, 55, 80, 77, 94, 99, 88, 92, 57, 0, 0, 56, 78, 78, 72, 85, 85, 115, 70, 0, 0, 47, 81, 62, 59, 67, 106, 120, 60, 0, 0, 82, 82, 82, 82, 82, 82, 82, 82, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WPE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 94, 94, 94, 94, 94, 94, 94, 94, 0, 0, 107, 102, 102, 104, 107, 94, 96, 87, 0, 0, 98, 101, 88, 95, 94, 89, 93, 86, 0, 0, 107, 103, 91, 87, 87, 86, 97, 93, 0, 0, 126, 118, 107, 99, 92, 98, 111, 111, 0, 0, 188, 194, 179, 161, 150, 147, 176, 178, 0, 0, 272, 267, 252, 228, 241, 226, 259, 281, 0, 0, 94, 94, 94, 94, 94, 94, 94, 94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BPE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 94, 94, 94, 94, 94, 94, 94, 94, 0, 0, 272, 267, 252, 228, 241, 226, 259, 281, 0, 0, 188, 194, 179, 161, 150, 147, 176, 178, 0, 0, 126, 118, 107, 99, 92, 98, 111, 111, 0, 0, 107, 103, 91, 87, 87, 86, 97, 93, 0, 0, 98, 101, 88, 95, 94, 89, 93, 86, 0, 0, 107, 102, 102, 104, 107, 94, 96, 87, 0, 0, 94, 94, 94, 94, 94, 94, 94, 94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WNO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 232, 316, 279, 304, 320, 309, 318, 314, 0, 0, 308, 284, 325, 334, 336, 355, 323, 318, 0, 0, 314, 328, 349, 347, 356, 354, 362, 321, 0, 0, 324, 341, 353, 350, 365, 356, 358, 329, 0, 0, 328, 354, 356, 390, 374, 406, 355, 359, 0, 0, 290, 397, 374, 402, 421, 466, 410, 381, 0, 0, 264, 296, 409, 373, 360, 399, 344, 320, 0, 0, 170, 248, 303, 288, 398, 240, 322, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BNO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 170, 248, 303, 288, 398, 240, 322, 230, 0, 0, 264, 296, 409, 373, 360, 399, 344, 320, 0, 0, 290, 397, 374, 402, 421, 466, 410, 381, 0, 0, 328, 354, 356, 390, 374, 406, 355, 359, 0, 0, 324, 341, 353, 350, 365, 356, 358, 329, 0, 0, 314, 328, 349, 347, 356, 354, 362, 321, 0, 0, 308, 284, 325, 334, 336, 355, 323, 318, 0, 0, 232, 316, 279, 304, 320, 309, 318, 314, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WNE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 252, 230, 258, 266, 259, 263, 231, 217, 0, 0, 239, 261, 271, 276, 279, 261, 258, 237, 0, 0, 258, 278, 280, 296, 291, 278, 261, 259, 0, 0, 263, 275, 297, 306, 297, 298, 285, 263, 0, 0, 264, 284, 303, 303, 303, 292, 289, 263, 0, 0, 257, 261, 291, 290, 280, 272, 262, 240, 0, 0, 256, 273, 256, 279, 272, 256, 257, 229, 0, 0, 223, 243, 268, 253, 250, 254, 218, 182, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BNE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 223, 243, 268, 253, 250, 254, 218, 182, 0, 0, 256, 273, 256, 279, 272, 256, 257, 229, 0, 0, 257, 261, 291, 290, 280, 272, 262, 240, 0, 0, 264, 284, 303, 303, 303, 292, 289, 263, 0, 0, 263, 275, 297, 306, 297, 298, 285, 263, 0, 0, 258, 278, 280, 296, 291, 278, 261, 259, 0, 0, 239, 261, 271, 276, 279, 261, 258, 237, 0, 0, 252, 230, 258, 266, 259, 263, 231, 217, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WBO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 332, 362, 351, 344, 352, 353, 326, 344, 0, 0, 369, 380, 381, 365, 372, 386, 398, 366, 0, 0, 365, 380, 380, 380, 379, 392, 383, 375, 0, 0, 359, 378, 378, 391, 399, 377, 375, 369, 0, 0, 361, 370, 384, 415, 402, 402, 372, 363, 0, 0, 349, 402, 408, 405, 400, 415, 402, 363, 0, 0, 339, 381, 347, 352, 395, 424, 383, 318, 0, 0, 336, 369, 283, 328, 340, 323, 372, 357, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BBO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 336, 369, 283, 328, 340, 323, 372, 357, 0, 0, 339, 381, 347, 352, 395, 424, 383, 318, 0, 0, 349, 402, 408, 405, 400, 415, 402, 363, 0, 0, 361, 370, 384, 415, 402, 402, 372, 363, 0, 0, 359, 378, 378, 391, 399, 377, 375, 369, 0, 0, 365, 380, 380, 380, 379, 392, 383, 375, 0, 0, 369, 380, 381, 365, 372, 386, 398, 366, 0, 0, 332, 362, 351, 344, 352, 353, 326, 344, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WBE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 274, 288, 274, 292, 288, 281, 292, 280, 0, 0, 283, 279, 290, 296, 301, 288, 282, 270, 0, 0, 285, 294, 305, 307, 310, 300, 290, 282, 0, 0, 291, 300, 310, 316, 304, 307, 294, 288, 0, 0, 294, 306, 309, 306, 311, 307, 300, 299, 0, 0, 299, 289, 297, 296, 295, 303, 297, 301, 0, 0, 289, 293, 304, 285, 294, 284, 293, 283, 0, 0, 283, 276, 286, 289, 290, 288, 280, 273, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BBE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 283, 276, 286, 289, 290, 288, 280, 273, 0, 0, 289, 293, 304, 285, 294, 284, 293, 283, 0, 0, 299, 289, 297, 296, 295, 303, 297, 301, 0, 0, 294, 306, 309, 306, 311, 307, 300, 299, 0, 0, 291, 300, 310, 316, 304, 307, 294, 288, 0, 0, 285, 294, 305, 307, 310, 300, 290, 282, 0, 0, 283, 279, 290, 296, 301, 288, 282, 270, 0, 0, 274, 288, 274, 292, 288, 281, 292, 280, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WRO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 458, 464, 478, 494, 493, 484, 440, 451, 0, 0, 433, 461, 457, 468, 476, 488, 471, 406, 0, 0, 432, 452, 461, 460, 480, 477, 472, 444, 0, 0, 441, 451, 465, 476, 486, 470, 483, 454, 0, 0, 453, 466, 484, 503, 501, 512, 469, 457, 0, 0, 472, 496, 503, 513, 494, 522, 538, 493, 0, 0, 504, 509, 535, 539, 557, 544, 503, 521, 0, 0, 509, 519, 509, 528, 540, 486, 508, 520, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BRO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 509, 519, 509, 528, 540, 486, 508, 520, 0, 0, 504, 509, 535, 539, 557, 544, 503, 521, 0, 0, 472, 496, 503, 513, 494, 522, 538, 493, 0, 0, 453, 466, 484, 503, 501, 512, 469, 457, 0, 0, 441, 451, 465, 476, 486, 470, 483, 454, 0, 0, 432, 452, 461, 460, 480, 477, 472, 444, 0, 0, 433, 461, 457, 468, 476, 488, 471, 406, 0, 0, 458, 464, 478, 494, 493, 484, 440, 451, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WRE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 503, 514, 515, 511, 507, 499, 516, 492, 0, 0, 506, 506, 512, 514, 503, 503, 501, 509, 0, 0, 508, 512, 507, 511, 505, 500, 504, 496, 0, 0, 515, 517, 520, 516, 507, 506, 504, 501, 0, 0, 516, 515, 525, 513, 514, 513, 511, 514, 0, 0, 519, 519, 519, 517, 516, 509, 507, 509, 0, 0, 523, 525, 525, 523, 509, 515, 520, 515, 0, 0, 525, 522, 530, 527, 524, 524, 520, 517, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BRE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 525, 522, 530, 527, 524, 524, 520, 517, 0, 0, 523, 525, 525, 523, 509, 515, 520, 515, 0, 0, 519, 519, 519, 517, 516, 509, 507, 509, 0, 0, 516, 515, 525, 513, 514, 513, 511, 514, 0, 0, 515, 517, 520, 516, 507, 506, 504, 501, 0, 0, 508, 512, 507, 511, 505, 500, 504, 496, 0, 0, 506, 506, 512, 514, 503, 503, 501, 509, 0, 0, 503, 514, 515, 511, 507, 499, 516, 492, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WQO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1024, 1007, 1016, 1035, 1010, 1000, 994, 975, 0, 0, 990, 1017, 1036, 1027, 1033, 1040, 1022, 1026, 0, 0, 1011, 1027, 1014, 1023, 1020, 1027, 1039, 1030, 0, 0, 1016, 999, 1016, 1015, 1023, 1021, 1028, 1022, 0, 0, 998, 998, 1009, 1009, 1024, 1042, 1023, 1026, 0, 0, 1012, 1008, 1032, 1033, 1054, 1081, 1072, 1082, 0, 0, 1001, 986, 1020, 1026, 1009, 1082, 1053, 1079, 0, 0, 997, 1025, 1054, 1037, 1084, 1069, 1068, 1070, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BQO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 997, 1025, 1054, 1037, 1084, 1069, 1068, 1070, 0, 0, 1001, 986, 1020, 1026, 1009, 1082, 1053, 1079, 0, 0, 1012, 1008, 1032, 1033, 1054, 1081, 1072, 1082, 0, 0, 998, 998, 1009, 1009, 1024, 1042, 1023, 1026, 0, 0, 1016, 999, 1016, 1015, 1023, 1021, 1028, 1022, 0, 0, 1011, 1027, 1014, 1023, 1020, 1027, 1039, 1030, 0, 0, 990, 1017, 1036, 1027, 1033, 1040, 1022, 1026, 0, 0, 1024, 1007, 1016, 1035, 1010, 1000, 994, 975, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WQE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 903, 908, 914, 893, 931, 904, 916, 895, 0, 0, 914, 913, 906, 920, 920, 913, 900, 904, 0, 0, 920, 909, 951, 942, 945, 953, 946, 941, 0, 0, 918, 964, 955, 983, 967, 970, 975, 959, 0, 0, 939, 958, 960, 981, 993, 976, 993, 972, 0, 0, 916, 942, 945, 985, 983, 971, 955, 945, 0, 0, 919, 956, 968, 977, 994, 961, 966, 936, 0, 0, 927, 958, 958, 963, 963, 955, 946, 956, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BQE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 927, 958, 958, 963, 963, 955, 946, 956, 0, 0, 919, 956, 968, 977, 994, 961, 966, 936, 0, 0, 916, 942, 945, 985, 983, 971, 955, 945, 0, 0, 939, 958, 960, 981, 993, 976, 993, 972, 0, 0, 918, 964, 955, 983, 967, 970, 975, 959, 0, 0, 920, 909, 951, 942, 945, 953, 946, 941, 0, 0, 914, 913, 906, 920, 920, 913, 900, 904, 0, 0, 903, 908, 914, 893, 931, 904, 916, 895, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WKO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -15, 36, 12, -54, 8, -28, 24, 14, 0, 0, 1, 7, -8, -64, -43, -16, 9, 8, 0, 0, -14, -14, -22, -46, -44, -30, -15, -27, 0, 0, -49, -1, -27, -39, -46, -44, -33, -51, 0, 0, -17, -20, -12, -27, -30, -25, -14, -36, 0, 0, -9, 24, 2, -16, -20, 6, 22, -22, 0, 0, 29, -1, -20, -7, -8, -4, -38, -29, 0, 0, -65, 23, 16, -15, -56, -34, 2, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BKO = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -65, 23, 16, -15, -56, -34, 2, 13, 0, 0, 29, -1, -20, -7, -8, -4, -38, -29, 0, 0, -9, 24, 2, -16, -20, 6, 22, -22, 0, 0, -17, -20, -12, -27, -30, -25, -14, -36, 0, 0, -49, -1, -27, -39, -46, -44, -33, -51, 0, 0, -14, -14, -22, -46, -44, -30, -15, -27, 0, 0, 1, 7, -8, -64, -43, -16, 9, 8, 0, 0, -15, 36, 12, -54, 8, -28, 24, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] WKE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -53, -34, -21, -11, -28, -14, -24, -43, 0, 0, -27, -11, 4, 13, 14, 4, -5, -17, 0, 0, -19, -3, 11, 21, 23, 16, 7, -9, 0, 0, -18, -4, 21, 24, 27, 23, 9, -11, 0, 0, -8, 22, 24, 27, 26, 33, 26, 3, 0, 0, 10, 17, 23, 15, 20, 45, 44, 13, 0, 0, -12, 17, 14, 17, 17, 38, 23, 11, 0, 0, -74, -35, -18, -18, -11, 15, 4, -17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] BKE = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -74, -35, -18, -18, -11, 15, 4, -17, 0, 0, -12, 17, 14, 17, 17, 38, 23, 11, 0, 0, 10, 17, 23, 15, 20, 45, 44, 13, 0, 0, -8, 22, 24, 27, 26, 33, 26, 3, 0, 0, -18, -4, 21, 24, 27, 23, 9, -11, 0, 0, -19, -3, 11, 21, 23, 16, 7, -9, 0, 0, -27, -11, 4, 13, 14, 4, -5, -17, 0, 0, -53, -34, -21, -11, -28, -14, -24, -43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[][] whiteOpening = {WPO,WNO,WBO,WRO,WQO,WKO};
    static int[][] blackOpening = {BPO,BNO,BBO,BRO,BQO,BKO};
    static int[][] whiteEndgame = {WPE,WNE,WBE,WRE,WQE,WKE};
    static int[][] blackEndgame = {BPE,BNE,BBE,BRE,BQE,BKE};
    static int[] mobilityOpening = {0,4,3,2,1,0};
    static int[] mobilityEndgame = {0,4,3,4,2,0};
    static int[] attackerValue = {1,2,2,3,5,0};
    static int[] attackTable = {
            0,  0,   1,   2,   3,   5,   7,   9,  12,  15,
            18,  22,  26,  30,  35,  39,  44,  50,  56,  62,
            68,  75,  82,  85,  89,  97, 105, 113, 122, 131,
            140, 150, 169, 180, 191, 202, 213, 225, 237, 248,
            260, 272, 283, 295, 307, 319, 330, 342, 354, 366,
            377, 389, 401, 412, 424, 436, 448, 459, 471, 483,
            494, 500, 500, 500, 500, 500, 500, 500, 500, 500,
            500, 500, 500, 500, 500, 500, 500, 500, 500, 500,
            500, 500, 500, 500, 500, 500, 500, 500, 500, 500,
            500, 500, 500, 500, 500, 500, 500, 500, 500, 500
    };
    static boolean[][] nearKing = new boolean[120][120];
    //transposition and repetition tables
    static long[][] hashIndex;
    static Set<Long> repetitionHashTable;
    static Map<Long,HashEntry> transpositionTable;
    static final int MAXTTSIZE = 5000000;
    static final int PANICTTCLEARTRIGGER = 12000000;
    static final int PANICTTCLEARSIZE = 8000000;
    static final int TTBACKDEPTH = 4;
    //Constants for checking time/tt storage every ___ nodes
    static final int NODESPERCHECK = 2048;
    static int nodeCount = 0;
    //important constants returned by scoring function
    static final int DRAW = 0;
    static final int WIN = 9999;
    static final int TAKEKING = 999999;
    static final int WIDEALPHABETA = 99999;
    static final int OUTOFTIME = 10000000;
    //time/depth control
    static final int SEARCH_DEPTH = 10; //search ends upon hitting a certain depth (doesn't waste time if clear best/only/book move)
    static final int TIME_CONTROL = 180000; //currently set up to play 5 + 3 rapid (5 mins + 3 sec increment)
    static final int INCREMENT = 0;
    static int timeLeft = TIME_CONTROL;

    static final boolean WHITEBOT = false;

    // TODOS:

    // NEEDED:
    // take backs/check for legal moves
    // check extentions (fixes quiescence stalemate problem)
    // improve time management
    // move ordering (hash (best moves previously, stored in tt), captures w/ mvv/lva, killer moves/history heuristic, others),
    // tune eval! maybe simplify for speed and add mop up endgame
    // better tt clearing (irreversible moves), (currently have problem if >beta in one search is <alpha in another)
    // opening book
    // limited quiescence (so it doesn't take forever!)
    // incremental update of pst, hash, material, etc. to save time

    // OPTIONAL / AFTER GRADUATION:
    // forward pruning/reductions (lmr, delta, futility, null move)
    // aspiration windows and pv search (narrow window for all non-pv)
    // bitboard move gen and attack maps
    // SEE
    // endgame tablebases
    // "pondering" (thinking during opponent's time)

    public static void main(String[] args) throws FileNotFoundException { //starts by initializing everything
        board = new byte[]{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, //looks upside down in this view, 7s are borders
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                7, 4, 2, 3, 5, 6, 3, 2, 4, 7,
                7, 1, 1, 1, 1, 1, 1, 1, 1, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 9, 9, 9, 9, 9, 9, 9, 9, 7,
                7, 12, 10, 11, 13, 14, 11, 10, 12, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
        boardState = (byte) 0b11110000;
        Collections.addAll(pieceLists[0],21, 22, 23, 24, 25, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38);
        Collections.addAll(pieceLists[1], 81, 82, 83, 84, 85, 86, 87, 88, 91, 92, 93, 94, 95, 96, 97, 98);
        sorter = new MoveSorter(board);
        seedHashIndex();
        transpositionTable = new HashMap<>();
        repetitionHashTable = new HashSet<>();
        buildNearKingTable();
        // normal display map is nicer because it has the actual pieces, but they don't work on my laptop
        //Map<Byte,Character> displayMap = buildDisplayMap();
        Map<Byte,Character> displayMap = laptopDisplayMap();

        ArrayList<Integer> gameMoves = new ArrayList<>();
        getOpenings("src/OpeningBookV1");

        if (WHITEBOT) {
            while (true) {
                printBoard(displayMap);
                int move = iterativeDeepening(SEARCH_DEPTH, true, allocateTime());
                makeMove(move);
                gameMoves.add(move);
                printBoard(displayMap);

                clearTranspositionTableOrderIn(MAXTTSIZE);
                searchNumber++;

                move = playerInputToMove();
                makeMove(move);
                gameMoves.add(move);
            }
        }

        printBoard(displayMap);
        while (true) {
            int move = playerInputToMove();
            makeMove(move);
            gameMoves.add(move);
            printBoard(displayMap);

            move = iterativeDeepening(SEARCH_DEPTH, false, allocateTime());
            makeMove(move);
            gameMoves.add(move);
            printBoard(displayMap);

            clearTranspositionTableOrderIn(MAXTTSIZE);
            searchNumber++;
        }
    }
    public static void printBoard(Map<Byte,Character> displayMap) {
        System.out.println("\nEngine has " + timeLeft / 60000 + ":" + (timeLeft / 1000) % 60 + " left on its clock\n");
        for (int y = 90; y > 19; y -= 10) {
            for (int x = 1; x < 9; x++) {
                System.out.print(displayMap.get(board[y + x]) + "|");
            }
            System.out.println("\n------------------------------------------");
        }
    }
    public static void buildNearKingTable() {
        for (int i = 20; i < 100; i +=10) {
            for (int j = 1; j < 9; j++) {
                int kingCoordinate = i + j;
                int[] coordinatesToTry = {kingCoordinate,kingCoordinate+1,kingCoordinate-1,kingCoordinate+9,kingCoordinate+10,kingCoordinate+11,kingCoordinate+19,kingCoordinate+20,kingCoordinate+21,kingCoordinate-9,kingCoordinate-10,kingCoordinate-11,kingCoordinate-19,kingCoordinate-20,kingCoordinate-21};
                for (int c : coordinatesToTry) {
                    if (board[c] != 7) {
                        nearKing[kingCoordinate][c] = true;
                    }
                }
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
    public static Map<Byte,Character> laptopDisplayMap () {
        Map<Byte,Character> display = new HashMap<>();
        display.put((byte) 0,' ');
        display.put((byte) 1,'P');
        display.put((byte) 9, 'p');
        display.put((byte) 2, 'N');
        display.put((byte) 10, 'n');
        display.put((byte) 3, 'B');
        display.put((byte) 11, 'b');
        display.put((byte) 4, 'R');
        display.put((byte) 12, 'r');
        display.put((byte) 5, 'Q');
        display.put((byte) 13, 'q');
        display.put((byte) 6, 'K');
        display.put((byte) 14, 'k');
        return display;
    }
    public static void seedHashIndex() {
        Random r = new Random(1);
        hashIndex = new long[13][64]; //1-12 are pieces on the board, 0 is for special stuff like side to move, castling, en passant (0 is side to move, 1-8 en passant, 9-23 castling rights)
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 64; j++) {
                hashIndex[i][j] = r.nextLong();
            }
        }
    }
    public static long getHashIndex(boolean whiteMove) {//what do we do with TT? at start if already searched this position: at higher depth previously, use that result, else do best move first. then if current depth is higher update table
        long hash = 0;
        for (int y = 2; y < 10; y++) {
            for (int x = 1; x < 9; x++) {
                hash = hash ^ hashIndex[board[10*y+x] % 8 + (board[10*y+x]>>>3) * 6][x-1 + (y-2) * 8];
            }
        }
        if (!whiteMove) {
            hash = hashIndex[0][0] ^ hash;
        }
        if ((boardState & 0b1111) != 0) {
            hash = hash ^ hashIndex[0][boardState & 0b1111];
        }
        if ((boardState & 0b11110000) != 0) {
            hash = hash ^ hashIndex[0][8 + ((boardState & 0b11110000)>>>4)];
        }
        return hash;
    }
    //memory management
    public static void clearTranspositionTableOrderIn(int goalSize) { //first in, first out
        int cutoff = searchNumber - TTBACKDEPTH;
        Set<Long> keys = transpositionTable.keySet(); //gets down to goal size exactly - will end up removing some but not all from a certain order
        int count = 0;
        int requiredCount = transpositionTable.size() - goalSize;
        while (count < requiredCount) {
            Iterator<Long> itr = keys.iterator();
            while (itr.hasNext() && count < requiredCount) {
                HashEntry h = transpositionTable.get(itr.next());
                if (h.orderIn <= cutoff) {
                    itr.remove();
                    count++;
                }
            }
            cutoff ++;
        }
        //System.out.println("Removed " + count + " entries");
    }
    public static void getOpenings(String fileName) throws FileNotFoundException {
        Scanner openingReader = new Scanner(new File(fileName)); //format is:  "position bestMove"
        while (openingReader.hasNext()) { //all have an orderIn of 0 because we can clear them as soon as we get out of book
            long key = openingReader.nextLong();
            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(openingReader.nextInt());
            transpositionTable.put(key,new HashEntry(moves,99,true,0,0));
        }
    }
    public static int playerInputToMove() { //gathers player input and translates it to my move notation
        Scanner console = new Scanner(System.in);
        int from = 0;
        int to = 0;
        boolean confirmed = false;
        while (!confirmed) {
            System.out.print("Move piece from column : ");
            from = console.next().charAt(0) - 'a' + 1;
            System.out.print("and row : ");
            from += console.nextInt() * 10 + 10;
            System.out.print("To column : ");
            to = console.next().charAt(0) - 'a' + 1;
            System.out.print("and row : ");
            to += console.nextInt() * 10 + 10;
            System.out.print("1 to confirm, 0 to reinput");
            confirmed = console.nextInt() == 1;
        }
        return encodeMove(to,from);
    }
    public static int encodeMove(int to, int from) {
        int move = 0;
        move += (board[from]>>3) & 1; //is the move white or black
        move += to<<1;
        move += from<<8;
        move += board[to]<<19; //captured piece
        move += boardState<<23; //prior board state
        if ((board[from] & 0b111) == 1) { //if a pawn (they have a lot of special moves)
            if (to > 90 || to < 30) { //promote
                move += 1<<18;
                Scanner console = new Scanner(System.in);
                System.out.println("Promote to what? 0 knight, 1 bishop, 2 rook, 3 queen :");
                move += console.nextInt() << 15;
            } else if ((to > 50 && from < 40) || (to < 70 && from > 80)) { //push
                move += 1<<15;
            }
            else if ((boardState & 0b1111) + 70 - (30 * ((board[from]>>3) & 1)) == to) { //ep
                move += 0b101<<15;
            }
        } else if ((board[from] & 0b111) == 6 && from%10 == 5) { //castling
            if (to%10 == 3) {
                move += 0b11<<15;
            } else if (to%10 == 7) {
                move += 0b1<<16;
            }
        }
        return move;
    }
    public static int getNullMove(boolean whiteMove) {
        if (whiteMove) {
            return boardState << 23;
        }
        return (boardState << 23) + 1;
    }
    public static boolean makeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        int moveColor = move & 1;
        if (board[to] % 8 == 6) { //taking the king
            return true;
        }
        repetitionHashTable.add(getHashIndex(moveColor == 0));
        boardState = (byte) (boardState & 0b11110000); //resets en passantables
        if ((move>>18 & 1) == 1) { //if promotion, place correct piece
            board[to] = (byte) (((move>>15) & 0b11) + 2 + (8 * (moveColor))); //first part gets type, second half color
        } else {
            board[to] = board[from]; //otherwise piece in resulting square is same as initial
            if ((move>>15 & 0b111) == 0b101) { //en passant
                board[to - 10 + 20*(moveColor)] = 0; //btw (move & 0b1) gives you 0 if white move and 1 if black move
                pieceLists[1-moveColor].remove(to - 10 + 20*(moveColor));
            } else if ((move>>16 & 1) == 1) { //castling
                if ((move>>15 & 1) == 1) { //long castle
                    board[to+1] = board[to-2];
                    board[to-2] = 0;
                    pieceLists[moveColor].add(to + 1);
                    pieceLists[moveColor].remove(to - 2);
                } else { //short castle
                    board[to-1] = board[to+1];
                    board[to+1] = 0;
                    pieceLists[moveColor].add(to - 1);
                    pieceLists[moveColor].remove(to + 1);
                }
            } else if ((move>>15 & 1) == 1) { //pawn pushing makes this column en passantable
                boardState += to%10;
            }
        }
        board[from] = 0; //space piece is leaving is always empty

        phase += phaseCounts[move>>19 & 0b1111];

        pieceLists[moveColor].remove(from);
        pieceLists[moveColor].add(to);
        if ((move & 0b11110000000000000000000) != 0) { //captures
            pieceLists[1-moveColor].remove(to);
        }

        if(from == 21 || from == 25 || to == 21) {//castling rights are lost if pieces move off 21,25,28,91,95,or98 or opp captures those rooks
            boardState = (byte)(boardState & 0b11011111);
        }
        if(from == 28 || from == 25 || to == 28) {
            boardState = (byte)(boardState & 0b11101111);
        }
        if(from == 91 || from == 95 || to == 91) {
            boardState = (byte)(boardState & 0b01111111);
        }
        if(from == 98 || from == 95 || to == 98) {
            boardState = (byte)(boardState & 0b10111111);
        }

        if (board[to] == 6) {
            kingPositions[0] = to;
        } else if (board[to] == 14) {
            kingPositions[1] = to;
        }
        return false;
    }
    public static void unMakeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        int moveColor = move & 0b1;
        if ((move>>18 & 1) == 1) { //if promotion, need to unpromote
            board[from] = (byte)(1 + 8 * (moveColor)); //same color pawn as side moving
        } else {
            board[from] = board[to]; //otherwise just move the piece back
            if ((move>>15 & 0b111) == 0b101) { //en passant: also restore opp pawn
                board[to - 10 + 20*(moveColor)] = (byte)(9 - 8 * (moveColor)); //put opp color pawn in appropriate space
                pieceLists[1-moveColor].add(to - 10 + 20 * (moveColor));
            } else if ((move>>16 & 1) == 1) { //undoing castling
                if ((move>>15 & 1) == 1) { //long castle
                    board[to-2] = board[to+1];
                    board[to+1] = 0;
                    pieceLists[moveColor].remove(to + 1);
                    pieceLists[moveColor].add(to - 2);
                } else { //short castle
                    board[to+1] = board[to-1];
                    board[to-1] = 0;
                    pieceLists[moveColor].remove(to - 1);
                    pieceLists[moveColor].add(to + 1);
                }
            }
        }
        board[to] = (byte)(move>>19 & 0b1111); //captured piece goes back on to
        boardState = (byte)(move>>23); //reverts board state (castle, ep) to previous
        phase -= phaseCounts[move>>19 & 0b1111];

        pieceLists[moveColor].add(from);
        pieceLists[moveColor].remove(to);
        if ((move & 0b11110000000000000000000) != 0) {
            pieceLists[1-moveColor].add(to);
        }
        if (board[from] == 6) {
            kingPositions[0] = from;
        } else if (board[from] == 14) {
            kingPositions[1] = from;
        }
        repetitionHashTable.remove(getHashIndex(moveColor == 0));
    }
    public static ArrayList<Integer> getWhiteMoves() { //move generation and all it's simplified derivatives
        ArrayList<Integer> moves = new ArrayList<>();
        for (int fromCoordinate : pieceLists[0]) {
            int fromPiece = board[fromCoordinate] - 1;
            if (fromPiece == 0) { //pawn
                if (fromCoordinate/10 == 8) { //promotions
                    int toCoordinate = fromCoordinate + 10;
                    int toPiece = board[toCoordinate];
                    if (toPiece == 0) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1001000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1010000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1011000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1));
                    }

                    toCoordinate = fromCoordinate + 9;
                    toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }

                    toCoordinate = fromCoordinate + 11;
                    toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }
                }
                else {
                    int toCoordinate = fromCoordinate + 10;
                    if (board[toCoordinate] == 0) {//normal moves forward
                        moves.add((boardState << 23) + (fromCoordinate << 8) + ((toCoordinate) << 1));
                        toCoordinate += 10;
                        if (fromCoordinate/10 == 3 && board[toCoordinate] == 0) { //pushes
                            moves.add((boardState << 23) + 0b1000000000000000 + (fromCoordinate << 8) + ((toCoordinate) << 1));
                        }
                    }

                    toCoordinate = fromCoordinate + 9;
                    int toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }

                    toCoordinate = fromCoordinate + 11;
                    toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }

                    if (fromCoordinate / 10 == 6 && (boardState & 0b1111) != 0) { //ep
                        if (fromCoordinate % 10 == (boardState & 0b1111) + 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate + 9) << 1));
                        } else if (fromCoordinate % 10 == (boardState & 0b1111) - 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate + 11) << 1));
                        }
                    }
                }
            } else {
                int[] offsets = moveGenOffset[fromPiece];
                boolean slide = moveGenSlide[fromPiece];
                for (int o : offsets) {
                    int toCoordinate = fromCoordinate + o;
                    do {
                        byte toPiece = board[toCoordinate];
                        if (toPiece > 8) {
                            moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1) + (toPiece << 19));
                            break;
                        }
                        if (toPiece != 0) {
                            break;
                        }
                        moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1));
                        toCoordinate += o;
                    } while (slide);
                }
            }
        }
        if ((boardState & 0b00010000) == 0b00010000 && board[26] == 0 && board[27] == 0 && !isAttacked(25,true) && !isAttacked(26,true)) {
            moves.add((boardState<<23) + 0b10001100100110110); //short castle - checks for destination are in eval/actual making
        }
        if ((boardState & 0b00100000) == 0b00100000 && board[24] == 0 && board[23] == 0 && board[22] == 0 && !isAttacked(25,true) && !isAttacked(24,true)) {
            moves.add((boardState<<23) + 0b11001100100101110); //long castle
        }
        return moves;
    }
    public static ArrayList<Integer> getBlackMoves() {
        ArrayList<Integer> moves = new ArrayList<>();
        for (int fromCoordinate : pieceLists[1]) {
            int fromPiece = board[fromCoordinate] - 9;
            if (fromPiece == 0) { //pawn
                if (fromCoordinate/10 == 3) { //promotions
                    int toCoordinate = fromCoordinate - 10;
                    int toPiece = board[toCoordinate];
                    if (toPiece == 0) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1001000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1010000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1011000000000000000 + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    toCoordinate = fromCoordinate - 9;
                    toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    toCoordinate = fromCoordinate - 11;
                    toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }
                }
                else {
                    int toCoordinate = fromCoordinate - 10;
                    if (board[toCoordinate] == 0) {//normal moves forward
                        moves.add((boardState << 23) + (fromCoordinate << 8) + ((toCoordinate) << 1) + 1);
                        toCoordinate -= 10;
                        if (fromCoordinate/10 == 8 && board[toCoordinate] == 0) { //pushes
                            moves.add((boardState << 23) + 0b1000000000000000 + (fromCoordinate << 8) + ((toCoordinate) << 1) + 1);
                        }
                    }

                    toCoordinate = fromCoordinate - 9;
                    int toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    toCoordinate = fromCoordinate - 11;
                    toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    if (fromCoordinate / 10 == 5  && (boardState & 0b1111) != 0) { //ep
                        if (fromCoordinate % 10 == (boardState & 0b1111) + 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate - 11) << 1) + 1);
                        } else if (fromCoordinate % 10 == (boardState & 0b1111) - 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate - 9) << 1) + 1);
                        }
                    }
                }
            } else {
                int[] offsets = moveGenOffset[fromPiece];
                boolean slide = moveGenSlide[fromPiece];
                for (int o : offsets) {
                    int toCoordinate = fromCoordinate + o;
                    do {
                        byte toPiece = board[toCoordinate];
                        if (toPiece < 7 && toPiece != 0) {
                            moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1) + (toPiece << 19) + 1);
                            break;
                        }
                        if (toPiece != 0) {
                            break;
                        }
                        moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        toCoordinate += o;
                    } while (slide);
                }
            }
        }
        if ((boardState & 0b01000000) == 0b01000000 && board[96] == 0 && board[97] == 0 && !isAttacked(95,false) && !isAttacked(96,false)) {
            moves.add((boardState<<23) + 0b10101111111000011); //short castle
        }
        if ((boardState & 0b10000000) == 0b10000000 && board[94] == 0 && board[93] == 0 && board[92] == 0 && !isAttacked(95,false) && !isAttacked(94,false)) {
            moves.add((boardState<<23) + 0b11101111110111011); //long castle
        }
        return moves;
    }
    public static ArrayList<Integer> getWhiteCaptures() { //what it sounds like, used for quiescence search
        ArrayList<Integer> moves = new ArrayList<>();
        for (int fromCoordinate : pieceLists[0]) {
            int fromPiece = board[fromCoordinate] - 1;
            if (fromPiece == 0) { //pawn
                if (fromCoordinate/10 == 8) { //promotions
                    int toCoordinate = fromCoordinate + 9;
                    int toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }

                    toCoordinate = fromCoordinate + 11;
                    toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }
                }
                else {
                    int toCoordinate = fromCoordinate + 9;
                    int toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }
                    toCoordinate = fromCoordinate + 11;
                    toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }

                    if (fromCoordinate / 10 == 6 && (boardState & 0b1111) != 0) { //ep
                        if (fromCoordinate % 10 == (boardState & 0b1111) + 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate + 9) << 1));
                        } else if (fromCoordinate % 10 == (boardState & 0b1111) - 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate + 11) << 1));
                        }
                    }
                }
            } else {
                int[] offsets = moveGenOffset[fromPiece];
                boolean slide = moveGenSlide[fromPiece];
                for (int o : offsets) {
                    int toCoordinate = fromCoordinate + o;
                    do {
                        byte toPiece = board[toCoordinate];
                        if (toPiece > 8) {
                            moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1) + (toPiece << 19));
                            break;
                        }
                        if (toPiece != 0) {
                            break;
                        }
                        toCoordinate += o;
                    } while (slide);
                }
            }
        }
        moves.sort(sorter);
        return moves;
    }
    public static ArrayList<Integer> getBlackCaptures() {
        ArrayList<Integer> moves = new ArrayList<>();
        for (int fromCoordinate : pieceLists[1]) {
            int fromPiece = board[fromCoordinate] - 9;
            if (fromPiece == 0) { //pawn
                if (fromCoordinate/10 == 3) { //promotions
                    int toCoordinate = fromCoordinate - 9;
                    int toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    toCoordinate = fromCoordinate - 11;
                    toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }
                }
                else {
                    int toCoordinate = fromCoordinate - 9;
                    int toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    toCoordinate = fromCoordinate - 11;
                    toPiece = board[toCoordinate];
                    if (toPiece != 0 && toPiece < 7) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    if (fromCoordinate / 10 == 5  && (boardState & 0b1111) != 0) { //ep
                        if (fromCoordinate % 10 == (boardState & 0b1111) + 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate - 11) << 1) + 1);
                        } else if (fromCoordinate % 10 == (boardState & 0b1111) - 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate - 9) << 1) + 1);
                        }
                    }
                }
            } else {
                int[] offsets = moveGenOffset[fromPiece];
                boolean slide = moveGenSlide[fromPiece];
                for (int o : offsets) {
                    int toCoordinate = fromCoordinate + o;
                    do {
                        byte toPiece = board[toCoordinate];
                        if (toPiece < 7 && toPiece != 0) {
                            moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1) + (toPiece << 19) + 1);
                            break;
                        }
                        if (toPiece != 0) {
                            break;
                        }
                        toCoordinate += o;
                    } while (slide);
                }
            }
        }
        moves.sort(sorter);
        return moves;
    }
    public static int getMobility (int square, boolean white, int[] attacks, int kingSquare) { //faster generation for evaluation, doesn't get actual moves (just count)
        int count = 0; //also helps with king safety by tracking what pieces attack each king's area
        boolean attacking = false;
        if (white) {
            int fromPiece = board[square] - 1;
            int[] offsets = moveGenOffset[fromPiece];
            boolean slide = moveGenSlide[fromPiece];
            for (int o : offsets) {
                int toCoordinate = square + o;
                do {
                    byte toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        count++;
                        if (nearKing[kingSquare][toCoordinate] && !attacking) {
                            attacks[0] += attackerValue[fromPiece];
                            attacks[1]++;
                        }
                        break;
                    }
                    if (toPiece != 0) {
                        if (nearKing[kingSquare][toCoordinate] && !attacking) {
                            attacks[0] += attackerValue[fromPiece];
                            attacks[1]++;
                        }
                        break;
                    }
                    count++;
                    if (nearKing[kingSquare][toCoordinate] && !attacking) {
                        attacks[0] += attackerValue[fromPiece];
                        attacks[1]++;
                        attacking = true;
                    }
                    toCoordinate += o;
                } while (slide);
            }
        } else {
            int fromPiece = board[square] - 9;
            int[] offsets = moveGenOffset[fromPiece];
            boolean slide = moveGenSlide[fromPiece];
            for (int o : offsets) {
                int toCoordinate = square + o;
                do {
                    byte toPiece = board[toCoordinate];
                    if (toPiece < 7 && toPiece != 0) {
                        count++;
                        if (nearKing[kingSquare][toCoordinate] && !attacking) {
                            attacks[2] += attackerValue[fromPiece];
                            attacks[3]++;
                        }
                        break;
                    }
                    if (toPiece != 0) {
                        if (nearKing[kingSquare][toCoordinate] && !attacking) {
                            attacks[2] += attackerValue[fromPiece];
                            attacks[3]++;
                        }
                        break;
                    }
                    count++;
                    if (nearKing[kingSquare][toCoordinate] && !attacking) {
                        attacks[2] += attackerValue[fromPiece];
                        attacks[3]++;
                        attacking = true;
                    }
                    toCoordinate += o;
                } while (slide);
            }
        }
        return count;
    }
    public static boolean isAttacked (int square, boolean white) { //used mostly for checks and castling
        for (int i = 1; i < 6; i++) {
            int[] offsets = moveGenOffset[i];
            boolean slide = moveGenSlide[i];
            for (int o : offsets) {
                int x = square + o;
                do {
                    byte p = board[x];
                    if (p != 0) {
                        if ((p - 9 == i && white) || (p - 1 == i && !white)) {
                            return true;
                        }
                        break;
                    }
                    x += o;
                } while (slide);
            }
        }
        return ((white && (board[square + 9] == 9 || board[square + 11] == 9))) || (!white && (board[square - 9] == 1 || board[square - 11] == 1));
    }
    public static int evaluatePosition() {
        //to add: king safety, mop up endgame, update pst to be more general (not pesto). works with lazy eval
        //add more sophisticated material counts (for inequalities), simpler psts, and piece specific heuristics (bonus for canons w/ bishop/rook/queen)
        int mgScore = 0;
        int egScore = 0;
        int[] attacks = new int[4]; //goes white attack value, white attacker count, black attack value, black attacker count

        for (int i : pieceLists[0]) {
            byte piece = (byte) (board[i] - 1);
            mgScore += whiteOpening[piece][i];
            egScore += whiteEndgame[piece][i];
            if (piece == 0) {
                int score = evaluatePawn(i,true,attacks,kingPositions[1]);
                mgScore += score;
                egScore += score;
            } else {
                int score = getMobility(i,true,attacks,kingPositions[1]);
                mgScore += score * mobilityOpening[piece];
                egScore += score * mobilityEndgame[piece];
            }
        }
        for (int i : pieceLists[1]) { //24 phase points total
            byte piece = (byte) (board[i] - 9);
            mgScore -= blackOpening[piece][i];
            egScore -= blackEndgame[piece][i];
            if (piece == 0) {
                int score = evaluatePawn(i,false, attacks, kingPositions[0]);
                mgScore -= score;
                egScore -= score;
            } else {
                int score = getMobility(i,false, attacks, kingPositions[0]);
                mgScore -= score * mobilityOpening[piece];
                egScore -= score * mobilityEndgame[piece];
            }
        }

        if (attacks[1] > 1) {
            mgScore += attackTable[attacks[0]];
        }
        if (attacks[3] > 1) {
            egScore += attackTable[attacks[2]];
        }
        //pawn shields
        int shieldRankTwo = 10;
        int shieldRankThree = 5;
        if (kingPositions[0] == 26 || kingPositions[0] == 27 || kingPositions[0] == 28) { //white short castled
            if (board[26] == 1) {
                mgScore += shieldRankTwo;
            } else if (board[36] == 1) {
                mgScore += shieldRankThree;
            }
            if (board[27] == 1) {
                mgScore += shieldRankTwo;
            } else if (board[37] == 1) {
                mgScore += shieldRankThree;
            }
            if (board[28] == 1) {
                mgScore += shieldRankTwo;
            } else if (board[38] == 1) {
                mgScore += shieldRankThree;
            }
        } else if (kingPositions[0] == 21 || kingPositions[0] == 22 || kingPositions[0] == 23) { //white long castled
            if (board[21] == 1) {
                mgScore += shieldRankTwo;
            } else if (board[31] == 1) {
                mgScore += shieldRankThree;
            }
            if (board[22] == 1) {
                mgScore += shieldRankTwo;
            } else if (board[32] == 1) {
                mgScore += shieldRankThree;
            }
            if (board[23] == 1) {
                mgScore += shieldRankTwo;
            } else if (board[33] == 1) {
                mgScore += shieldRankThree;
            }
        }
        if (kingPositions[1] == 96 || kingPositions[1] == 97 || kingPositions[1] == 98) { //black short castled
            if (board[86] == 1) {
                mgScore -= shieldRankTwo;
            } else if (board[76] == 1) {
                mgScore -= shieldRankThree;
            }
            if (board[87] == 1) {
                mgScore -= shieldRankTwo;
            } else if (board[77] == 1) {
                mgScore -= shieldRankThree;
            }
            if (board[88] == 1) {
                mgScore -= shieldRankTwo;
            } else if (board[78] == 1) {
                mgScore -= shieldRankThree;
            }
        } else if (kingPositions[1] == 91 || kingPositions[1] == 92 || kingPositions[1] == 93) { //black long castled
            if (board[81] == 1) {
                mgScore -= shieldRankTwo;
            } else if (board[71] == 1) {
                mgScore -= shieldRankThree;
            }
            if (board[82] == 1) {
                mgScore -= shieldRankTwo;
            } else if (board[72] == 1) {
                mgScore -= shieldRankThree;
            }
            if (board[83] == 1) {
                mgScore -= shieldRankTwo;
            } else if (board[73] == 1) {
                mgScore -= shieldRankThree;
            }
        }

        return (int)((mgScore * (24.0 - phase) / 24.0) + (egScore * phase / 24.0));
    }
    public static int evaluatePawn(int sq, boolean white, int[] attacks, int kingPosition) {
        int score = 0;

        boolean opposed = false; //for semi open file stuff
        boolean weak = true;
        boolean isolated = true;
        boolean passed = true;

        int doubledPenalty = -20;
        int weakPenalty = -5; //higher if on semi-open file
        int weakSemiOpenPenalty = -5; //stacks with weak and isloated
        int isolatedPenalty = -10; //stacks with weak and semi open
        int passedBonus = 25;
        int supportedBonus = 5; //adjacent pawn at level or right behind
        int supportedPassed = 5;
        //other ideas: scale passed via position on pst, calculate for blocked/can't advance/attacked target square, mobility points

        int stepFwd;
        if (white) {
            stepFwd = 10;
        } else {
            stepFwd = -10;
        }
        byte piece = board[sq];
        int nextSq = sq + stepFwd;

        while (true) { //searching forward
            byte target = board[nextSq];
            if (target == 7) {
                break;
            }
            if (target == piece) { //doubled pawns
                score += doubledPenalty;
                passed = false;
            } else if (target % 8 == 1){ //other colors pawn
                opposed = true;
                passed = false;
            }

            target = board[nextSq - 1];
            if (target == piece) {
                isolated = false;
            } else if (target % 8 == 1) {
                passed = false;
            }

            target = board[nextSq + 1];
            if (target == piece) {
                isolated = false;
            } else if (target % 8 == 1) {
                passed = false;
            }

            nextSq += stepFwd;
        }

        if (board[sq + 1] == piece || board[sq - 1] == piece || board[sq - stepFwd - 1] == piece || board[sq - stepFwd + 1] == piece) {
            score += supportedBonus;
            if (passed) {
                score += supportedPassed;
            }
            isolated = false;
            weak = false;
        }

        nextSq = sq - stepFwd;
        while(board[nextSq] != 7) {
            if (board[nextSq - 1] == piece || board[nextSq + 1] == piece) {
                isolated = false;
                weak = false;
                break;
            }
            nextSq -= stepFwd;
        }

        if (weak) {
            score += weakPenalty;
            if (!opposed) {
                score += weakSemiOpenPenalty;
            }
            if (isolated) {
                score += isolatedPenalty;
            }
        }

        if (passed) {
            score += passedBonus;
        }

        if (white) { //attack points for pawn storms
            if (nearKing[kingPosition][sq] || nearKing[kingPosition][sq + 10]) {
                attacks[0] += attackerValue[0];
                attacks[1]++;
            }
        } else {
            if (nearKing[kingPosition][sq] || nearKing[kingPosition][sq - 10]) {
                attacks[2] += attackerValue[0];
                attacks[3]++;
            }
        }

        return score;
    }
    public static int startSearch (int depth, boolean whiteMove, int alpha, int beta, long endTime) {
        long hash = getHashIndex(whiteMove);
        if (whiteMove) {
            ArrayList<Integer> moves;
            if(transpositionTable.containsKey(hash)) {
                HashEntry h = transpositionTable.get(hash);
                if (!h.isQuiescence()) {
                    moves = new ArrayList<>(h.getMoves());
                } else {
                    moves = getWhiteMoves();
                }
                if (h.getDepth() >= depth && h.getFullSearch()) {
                    return moves.get(0);
                }
            } else {
                moves = getWhiteMoves();
            }
            int length = moves.size();
            for (int i = 0; i < length; i++) {
                int m = moves.get(i);
                if (makeMove(m)) {
                    moves.add(0,moves.remove(i));
                    transpositionTable.put(hash,new HashEntry(99,true,TAKEKING,searchNumber));
                    return m;
                }
                int score = search(depth - 1, false,alpha,beta,endTime);
                if (score == OUTOFTIME) {
                    unMakeMove(m);
                    return moves.get(0);
                }
                if (score > alpha) {
                    alpha = score;
                    moves.add(0,moves.remove(i));
                }
                unMakeMove(m);
                /*if (System.currentTimeMillis() > endTime) {
                    return moves.get(0);
                }*/
            }
            if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                transpositionTable.put(hash,new HashEntry(moves,depth,true,alpha,searchNumber));
            }
            return moves.get(0);
        } else {
            ArrayList<Integer> moves;
            if(transpositionTable.containsKey(hash)) {
                HashEntry h = transpositionTable.get(hash);
                if (!h.isQuiescence()) {
                    moves = new ArrayList<>(h.getMoves());
                } else {
                    moves = getBlackMoves();
                }
                if (h.getDepth() >= depth && h.getFullSearch()) {
                    return moves.get(0);
                }
            } else {
                moves = getBlackMoves();
            }
            int length = moves.size();
            for (int i = 0; i < length; i++) {
                int m = moves.get(i);
                if (makeMove(m)) {
                    moves.add(0,moves.remove(i));
                    transpositionTable.put(hash,new HashEntry(99,true,-TAKEKING,searchNumber));
                    return m;
                }
                int score = search(depth - 1, true,alpha,beta,endTime);
                if (score == OUTOFTIME) {
                    unMakeMove(m);
                    return moves.get(0);
                }
                if (score < beta) {
                    beta = score;
                    moves.add(0,moves.remove(i));
                }
                unMakeMove(m);
                /*if (System.currentTimeMillis() > endTime) { //commented out because it's extremely rare we'll hit out of time in the root node
                    return moves.get(0);
                }*/
            }
            if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                transpositionTable.put(hash,new HashEntry(moves,depth,true,beta,searchNumber));
            }
            return moves.get(0);
        }
    }
    public static int search (int depth, boolean whiteMove, int alpha, int beta, long endTime) {
        nodeCount++;
        if (nodeCount == NODESPERCHECK) { //check if search is out of time/tt overflow risk (housekeeping)
            nodeCount = 0;
            if (System.currentTimeMillis() > endTime) {
                return OUTOFTIME;
            }
            if (transpositionTable.size() > PANICTTCLEARTRIGGER) {
                clearTranspositionTableOrderIn(PANICTTCLEARSIZE);
            }
        }

        boolean foundGoodMove = false;
        long hash = getHashIndex(whiteMove);
        if (repetitionHashTable.contains(hash)) {
            return DRAW;
        }
        if (depth < 1) { //quiescence search for captures and final eval
            ArrayList<Integer> moves;
            if (transpositionTable.containsKey(hash)) {
                HashEntry h = transpositionTable.get(hash);
                if (h.isCheckmate()) {
                    return h.getCheckmateScore(depth);
                }
                if (h.getFullSearch() || h.getScore() >= beta || h.getScore() <= alpha) {
                    return h.getScore();
                }
                if (h.isQuiescence()) {
                    moves = new ArrayList<>(h.getMoves());
                } else if (whiteMove) {
                    moves = getWhiteCaptures();
                } else {
                    moves = getBlackCaptures();
                }
            }
            else if (whiteMove) {
                moves = getWhiteCaptures();
            } else {
                moves = getBlackCaptures();
            }
            if (moves.isEmpty()) {
                int score = evaluatePosition();
                transpositionTable.put(hash,new HashEntry(0,true,score,searchNumber));
                return score;
            }
            if (whiteMove) {
                alpha = Math.max(evaluatePosition(),alpha); //need to find out a way for counting it as a full search still if nothing exceeds no capture
                if (alpha >= beta) { //if this position is too good for us even without making a move (bad for opponent), don't have to search moves
                    if (!transpositionTable.containsKey(hash)) {
                        transpositionTable.put(hash,new HashEntry(moves,0,false,alpha,searchNumber));
                    }
                    return alpha;
                }
                int length = moves.size();
                for (int i = 0; i < length; i++) {
                    int m = moves.get(i);
                    if (makeMove(m)) {
                        moves.add(0,moves.remove(i));
                        transpositionTable.put(hash,new HashEntry(99,true,TAKEKING,searchNumber));
                        return TAKEKING;
                    }
                    int score = search(0, false, alpha, beta,endTime);
                    if (score == OUTOFTIME) {
                        unMakeMove(m);
                        return OUTOFTIME;
                    }
                    if (score >= beta) {
                        unMakeMove(m);
                        moves.add(0,moves.remove(i));
                        if (!transpositionTable.containsKey(hash)) {
                            transpositionTable.put(hash,new HashEntry(moves,0,false,score,searchNumber));
                        }
                        return score;
                    }
                    if (score > alpha) {
                        moves.add(0,moves.remove(i));
                        alpha = score;
                        foundGoodMove = true;
                    }
                    unMakeMove(m);
                }
                if (!transpositionTable.containsKey(hash)) {
                    transpositionTable.put(hash,new HashEntry(moves,0,foundGoodMove,alpha,searchNumber));
                }
                return alpha;
            }
            //black moves
            beta = Math.min(evaluatePosition(),beta); //need to find out a way for counting it as a full search still if nothing exceeds no capture
            if (beta <= alpha) { //if this position is too good for us even without making a move (bad for opponent), don't have to search moves
                if (!transpositionTable.containsKey(hash)) {
                    transpositionTable.put(hash,new HashEntry(moves,0,false,beta,searchNumber));
                }
                return beta;
            }
            int length = moves.size();
            for (int i = 0; i < length; i++) {
                int m = moves.get(i);
                if (makeMove(m)) {
                    moves.add(0,moves.remove(i));
                    transpositionTable.put(hash,new HashEntry(99,true,-TAKEKING,searchNumber));
                    return -TAKEKING;
                }
                int score = search(0, true, alpha, beta,endTime);
                if (score == OUTOFTIME) {
                    unMakeMove(m);
                    return OUTOFTIME;
                }
                if (score <= alpha) {
                    unMakeMove(m);
                    moves.add(0,moves.remove(i));
                    if (!transpositionTable.containsKey(hash)) {
                        transpositionTable.put(hash,new HashEntry(moves,0,false,score,searchNumber));
                    }
                    return score;
                }
                if (score < beta) {
                    moves.add(0,moves.remove(i));
                    beta = score;
                    foundGoodMove = true;
                }
                unMakeMove(m);
            }
            if (!transpositionTable.containsKey(hash)) {
                transpositionTable.put(hash,new HashEntry(moves,0,foundGoodMove,beta,searchNumber));
            }
            return beta;
        } else {
            if (whiteMove) {
                ArrayList<Integer> moves;
                if(transpositionTable.containsKey(hash)) {
                    HashEntry h = transpositionTable.get(hash);
                    if ((h.getDepth() >= depth) && (h.getFullSearch() || h.getScore() >= beta || h.getScore() <= alpha)) {
                        return h.getScore();
                    }
                    if (!h.isQuiescence()) {
                        moves = new ArrayList<>(h.getMoves());
                    } else {
                        moves = getWhiteMoves();
                    }
                } else {
                    moves = getWhiteMoves();
                }
                int length = moves.size(); //timesave b/ we don't have to look up move size every time
                for (int i = 0; i < length; i++) {
                    int m = moves.get(i);
                    if (makeMove(m)) { //checkmates (found by capturing king)
                        moves.add(0,moves.remove(i)); //puts this move first because it's best so far
                        transpositionTable.put(hash,new HashEntry(99,true,TAKEKING,searchNumber));
                        return TAKEKING;
                    }
                    int score = search(depth - 1, false, alpha, beta,endTime);
                    if (score == OUTOFTIME) {
                        unMakeMove(m);
                        return OUTOFTIME;
                    }
                    if (score >= beta) {
                        unMakeMove(m);
                        moves.add(0,moves.remove(i));
                        if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                            transpositionTable.put(hash,new HashEntry(moves,depth,false,score,searchNumber));
                        }
                        return score;
                    }
                    if (score > alpha) {
                        moves.add(0,moves.remove(i));
                        alpha = score;
                        foundGoodMove = true;
                    } else if (score == -TAKEKING) { //if not a legal move, we can remove it. non-legal moves have no chance of raising alpha because losing your king sucks
                        moves.remove(i);
                        i--;
                        length--;
                    }
                    unMakeMove(m);
                }
                if (moves.isEmpty()) { //no legal moves - either checkmate or stalemate
                    if (isAttacked(kingPositions[0],true)) {
                        transpositionTable.put(hash, new HashEntry(99,true,-WIN,searchNumber));
                        return -WIN - depth;
                    }
                    transpositionTable.put(hash, new HashEntry(99,true,0,searchNumber));
                    return 0;
                }
                if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                    transpositionTable.put(hash,new HashEntry(moves,depth,foundGoodMove,alpha,searchNumber));
                }
                return alpha;
            } else {
                ArrayList<Integer> moves;
                if(transpositionTable.containsKey(hash)) {
                    HashEntry h = transpositionTable.get(hash);
                    if ((h.getDepth() >= depth) && (h.getFullSearch() || h.getScore() <= alpha || h.getScore() >= beta)) {
                        return h.getScore();
                    }
                    if (!h.isQuiescence()) {
                        moves = new ArrayList<>(h.getMoves());
                    } else {
                        moves = getBlackMoves();
                    }
                } else {
                    moves = getBlackMoves();
                }
                int length = moves.size();
                for (int i = 0; i < length; i++) {
                    int m = moves.get(i);
                    if (makeMove(m)) {
                        moves.add(0,moves.remove(i));
                        transpositionTable.put(hash,new HashEntry(99,true,-TAKEKING,searchNumber));
                        return -TAKEKING;
                    }
                    int score = search(depth - 1, true,alpha,beta,endTime);
                    if (score == OUTOFTIME) {
                        unMakeMove(m);
                        return OUTOFTIME;
                    }
                    if (score <= alpha) {
                        unMakeMove(m);
                        if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                            moves.add(0,moves.remove(i));
                            transpositionTable.put(hash,new HashEntry(moves,depth,false,score,searchNumber));
                        }
                        return score;
                    }
                    if (score < beta) {
                        beta = score;
                        foundGoodMove = true;
                        moves.add(0,moves.remove(i));
                    } else if (score == TAKEKING) { //if not a legal move, we can remove it. non-legal moves have no chance of raising alpha because losing your king sucks
                        moves.remove(i);
                        i--;
                        length--;
                    }
                    unMakeMove(m);
                }
                if (moves.isEmpty()) { //no legal moves - either checkmate or stalemate
                    if (isAttacked(kingPositions[1],true)) {
                        transpositionTable.put(hash, new HashEntry(99,true,WIN,searchNumber));
                        return WIN + depth;
                    }
                    transpositionTable.put(hash, new HashEntry(99,true,0,searchNumber));
                    return 0;
                }
                if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                    transpositionTable.put(hash,new HashEntry(moves,depth,foundGoodMove,beta,searchNumber));
                }
                return beta;
            }
        }
    }
    public static int iterativeDeepening(int depth, boolean whiteMove, long timeAllocated) { //depth or time cutoff (whichever it hits first)
        int move = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeAllocated;
        long midCutoffTime = startTime + timeAllocated / 2; //only goes to next depth if over half our time is left
        for (int n = 2; System.currentTimeMillis() < midCutoffTime && n <= depth; n++) {
            nodeCount = 0;
            move = startSearch(n,whiteMove,-WIDEALPHABETA,WIDEALPHABETA,endTime);
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        timeLeft -= elapsedTime;
        timeLeft += INCREMENT;
        return move;
    }
    public static int allocateTime() {
        return INCREMENT * 3 / 4 + timeLeft / 25;
    }
}