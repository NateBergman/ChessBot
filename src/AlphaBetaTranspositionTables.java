import java.util.*;
public class AlphaBetaTranspositionTables {
    static byte[] board;
    static byte boardState;
    static Set<Integer>[] pieceLists = new Set[]{new HashSet<>(), new HashSet<>()};

    static boolean[] moveGenSlide = {false,false,true,true,true,false};
    static int[][] moveGenOffset = {{},{-21, -19,-12, -8, 8, 12, 19, 21},{-11,  -9,  9, 11},{-10,  -1,  1, 10},{-11, -10, -9, -1, 1,  9, 10, 11},{-11, -10, -9, -1, 1,  9, 10, 11}};

    //piece square tables
    static int phase = 0;
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

    static long[][] hashIndex;
    static Map<Long,HashEntry> transpositionTable;

    static final int SEARCH_DEPTH = 4;

    static final int TIME_PER_MOVE = 5000; //how long we take per move in milliseconds

    //to-dos: quiescence search (with SEE, pruning, and tt hashing), attack maps, check extentions, time-based iterative deepending, move ordering, aspiration windows
    //better eval (pawn structure, mobility, king safety, mop up endgame)
    //handle draws
    //eventually do forward pruning/reductions (lmr, delta, futility, null move)
    //bitboard move gen
    //lastly opening/endgame tablebases/books
    public static void main(String[] args) {
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

        seedHashIndex();
        transpositionTable = new HashMap<>();

        //Map<Byte,Character> displayMap = buildDisplayMap();
        Map<Byte,Character> displayMap = laptopDisplayMap();

        Scanner console = new Scanner(System.in);
        ArrayList<Integer> gameMoves = new ArrayList<>();
        while (true) {
            printBoard(displayMap);
            System.out.print("0 for manual move, 1 to undo move, 2 for white engine, 3 for black engine");
            int x = console.nextInt();
            if (x == 1) {
                unMakeMove(gameMoves.get(gameMoves.size() - 1));
                gameMoves.remove(gameMoves.size() - 1);
            } else if (x == 0) {
                System.out.print("From : ");
                int from = console.nextInt();
                System.out.print("To : ");
                int to = console.nextInt();
                int move = encodeMove(to,from);
                makeMove(move);
                gameMoves.add(move);
            } else if (x == 2) {
                int move = iterativeDeepening(SEARCH_DEPTH, true);
                makeMove(move);
                gameMoves.add(move);
            } else if (x == 3) {
                int move = iterativeDeepening(SEARCH_DEPTH, false);
                makeMove(move);
                gameMoves.add(move);
            }
        }
    }
    public static void printBoard(Map<Byte,Character> displayMap) {
        for (int y = 90; y > 19; y -= 10) {
            for (int x = 1; x < 9; x++) {
                System.out.print(displayMap.get(board[y + x]) + "|");
            }
            System.out.println("\n------------------------------------------");
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
    public static ArrayList<Integer> getWhiteMoves() {
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
        MoveSorter sorter = new MoveSorter(board);
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
        return moves;
    }
    public static boolean isAttacked (int square, boolean white) {
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
    public static boolean makeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        int moveColor = move & 1;
        if (board[to] % 8 == 6) { //taking the king
            return true;
        }
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
        if ((move & 0b11110000000000000000000) != 0) {
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
    }
    public static double evaluatePosition() {
        double score = 0;
        for (int i : pieceLists[0]) {
            score += ((whiteOpening[board[i]-1][i]) * (24.0 - phase) / 24.0) + ((whiteEndgame[board[i]-1][i]) * phase / 24.0);
        }
        for (int i : pieceLists[1]) { //24 phase points total
            score -= ((blackOpening[board[i]-9][i]) * (24.0 - phase) / 24.0) + ((blackEndgame[board[i]-9][i]) * phase / 24.0);
        }
        return score;
    }
    public static int startSearch (int depth, boolean whiteMove, double alpha, double beta) {
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
                    transpositionTable.put(hash,new HashEntry(moves,99,true,9999.0));
                    return m;
                }
                double score = search(depth - 1, false,alpha,beta);
                if (score > alpha) {
                    alpha = score;
                    moves.add(0,moves.remove(i));
                }
                unMakeMove(m);
            }
            if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                transpositionTable.put(hash,new HashEntry(moves,depth,true,alpha));
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
                    transpositionTable.put(hash,new HashEntry(moves,99,true,-9999.0));
                    return m;
                }
                double score = search(depth - 1, true,alpha,beta);
                if (score < beta) {
                    beta = score;
                    moves.add(0,moves.remove(i));
                }
                unMakeMove(m);
            }
            if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                transpositionTable.put(hash,new HashEntry(moves,depth,true,beta));
            }
            return moves.get(0);
        }
    }
    public static double search (int depth, boolean whiteMove, double alpha, double beta) {
        boolean foundGoodMove = false;
        long hash = getHashIndex(whiteMove);
        if (depth < 1) { //quiescence search for captures and final eval
            ArrayList<Integer> moves;
            if (transpositionTable.containsKey(hash)) {
                HashEntry h = transpositionTable.get(hash);
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
            if (moves.size() == 0) {
                double score = evaluatePosition();
                transpositionTable.put(hash,new HashEntry(moves,0,foundGoodMove,score));
                return score;
            }
            if (whiteMove) {
                alpha = Math.max(evaluatePosition(),alpha); //need to find out a way for counting it as a full search still if nothing exceeds no capture
                int length = moves.size();
                for (int i = 0; i < length; i++) {
                    int m = moves.get(i);
                    if (makeMove(m)) {
                        moves.add(0,moves.remove(i));
                        transpositionTable.put(hash,new HashEntry(moves,99,true,9999.0));
                        return 9999.0;
                    }
                    double score = search(0, false, alpha, beta);
                    if (score >= beta) {
                        unMakeMove(m);
                        moves.add(0,moves.remove(i));
                        if (!transpositionTable.containsKey(hash)) {
                            transpositionTable.put(hash,new HashEntry(moves,0,false,score));
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
                    transpositionTable.put(hash,new HashEntry(moves,0,foundGoodMove,alpha));
                }
                return alpha;
            }
            //black moves
            beta = Math.min(evaluatePosition(),beta); //need to find out a way for counting it as a full search still if nothing exceeds no capture
            int length = moves.size();
            for (int i = 0; i < length; i++) {
                int m = moves.get(i);
                if (makeMove(m)) {
                    moves.add(0,moves.remove(i));
                    transpositionTable.put(hash,new HashEntry(moves,99,true,-9999.0));
                    return -9999.0;
                }
                double score = search(0, true, alpha, beta);
                if (score <= alpha) {
                    unMakeMove(m);
                    moves.add(0,moves.remove(i));
                    if (!transpositionTable.containsKey(hash)) {
                        transpositionTable.put(hash,new HashEntry(moves,0,false,score));
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
                transpositionTable.put(hash,new HashEntry(moves,0,foundGoodMove,beta));
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
                        transpositionTable.put(hash,new HashEntry(moves,99,true,9999.0));
                        return 9999.0;
                    }
                    double score = search(depth - 1, false, alpha, beta);
                    if (score >= beta) {
                        unMakeMove(m);
                        moves.add(0,moves.remove(i));
                        if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                            transpositionTable.put(hash,new HashEntry(moves,depth,false,score));
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
                if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                    transpositionTable.put(hash,new HashEntry(moves,depth,foundGoodMove,alpha));
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
                        transpositionTable.put(hash,new HashEntry(moves,99,true,-9999.0));
                        return -9999.0;
                    }
                    double score = search(depth - 1, true,alpha,beta);
                    if (score <= alpha) {
                        unMakeMove(m);
                        if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                            moves.add(0,moves.remove(i));
                            transpositionTable.put(hash,new HashEntry(moves,depth,false,score));
                        }
                        return score;
                    }
                    if (score < beta) {
                        beta = score;
                        foundGoodMove = true;
                        moves.add(0,moves.remove(i));
                    }
                    unMakeMove(m);
                }
                if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                    transpositionTable.put(hash,new HashEntry(moves,depth,foundGoodMove,beta));
                }
                return beta;
            }
        }
    }
    public static int iterativeDeepening(int depth, boolean whiteMove) {
        int move = 0;
        for (int n = 2; n <= depth; n += 1) {
            move = startSearch(n,whiteMove,-99999.0,99999.0); //assumes (pretty safely) that score will end up being between these bounds
        } //if it's not between them everything breaks but at that point we have a bigger problem with the eval
        return move;
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
}