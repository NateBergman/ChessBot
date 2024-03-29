import java.util.*;
public class SecondDraft { //uses tapered piece-square eval, no/basic pruning, and mailbox with piece lists and offset move generation
    static byte[] board;
    static byte boardState;
    static Set<Integer>[] pieceLists = new Set[]{new HashSet<>(), new HashSet<>()};
    int whiteKing;
    int blackKing;

    static int phase = 0;
    static int[] phaseCounts = {0,0,1,1,2,4,0,0,0,0,1,1,2,4,0};

    static boolean[] moveGenSlide = {false,false,true,true,true,false};
    static int[][] moveGenOffset = {{},{-21, -19,-12, -8, 8, 12, 19, 21},{-11,  -9,  9, 11},{-10,  -1,  1, 10},{-11, -10, -9, -1, 1,  9, 10, 11},{-11, -10, -9, -1, 1,  9, 10, 11}};

    int[] evalPawnOpening = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 82, 82, 82, 82, 82, 82, 82, 82, 0,
            0, 60, 120, 106, 67, 59, 62, 81, 47, 0,
            0, 70, 115, 85, 85, 72, 78, 78, 56, 0,
            0, 57, 92, 88, 99, 94, 77, 80, 55, 0,
            0, 59, 99, 94, 105, 103, 88, 95, 68, 0,
            0, 62, 107, 138, 147, 113, 108, 89, 76, 0,
            0, 71, 116, 208, 150, 177, 143, 216, 180, 0,
            0, 82, 82, 82, 82, 82, 82, 82, 82, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalPawnEndgame = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 94, 94, 94, 94, 94, 94, 94, 94, 0,
            0, 87, 96, 94, 107, 104, 102, 102, 107, 0,
            0, 86, 93, 89, 94, 95, 88, 101, 98, 0,
            0, 93, 97, 86, 87, 87, 91, 103, 107, 0,
            0, 111, 111, 98, 92, 99, 107, 118, 126, 0,
            0, 178, 176, 147, 150, 161, 179, 194, 188, 0,
            0, 281, 259, 226, 241, 228, 252, 267, 272, 0,
            0, 94, 94, 94, 94, 94, 94, 94, 94, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalKnightOpening = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 314, 318, 309, 320, 304, 279, 316, 232, 0, 0, 318, 323, 355, 336, 334, 325, 284, 308, 0, 0, 321, 362, 354, 356, 347, 349, 328, 314, 0, 0, 329, 358, 356, 365, 350, 353, 341, 324, 0, 0, 359, 355, 406, 374, 390, 356, 354, 328, 0, 0, 381, 410, 466, 421, 402, 374, 397, 290, 0, 0, 320, 344, 399, 360, 373, 409, 296, 264, 0, 0, 230, 322, 240, 398, 288, 303, 248, 170, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalKnightEndgame = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 217, 231, 263, 259, 266, 258, 230, 252, 0, 0, 237, 258, 261, 279, 276, 271, 261, 239, 0, 0, 259, 261, 278, 291, 296, 280, 278, 258, 0, 0, 263, 285, 298, 297, 306, 297, 275, 263, 0, 0, 263, 289, 292, 303, 303, 303, 284, 264, 0, 0, 240, 262, 272, 280, 290, 291, 261, 257, 0, 0, 229, 257, 256, 272, 279, 256, 273, 256, 0, 0, 182, 218, 254, 250, 253, 268, 243, 223, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalBishopOpening = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 344, 326, 353, 352, 344, 351, 362, 332, 0, 0, 366, 398, 386, 372, 365, 381, 380, 369, 0, 0, 375, 383, 392, 379, 380, 380, 380, 365, 0, 0, 369, 375, 377, 399, 391, 378, 378, 359, 0, 0, 363, 372, 402, 402, 415, 384, 370, 361, 0, 0, 363, 402, 415, 400, 405, 408, 402, 349, 0, 0, 318, 383, 424, 395, 352, 347, 381, 339, 0, 0, 357, 372, 323, 340, 328, 283, 369, 336, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalBishopEndgame = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 280, 292, 281, 288, 292, 274, 288, 274, 0, 0, 270, 282, 288, 301, 296, 290, 279, 283, 0, 0, 282, 290, 300, 310, 307, 305, 294, 285, 0, 0, 288, 294, 307, 304, 316, 310, 300, 291, 0, 0, 299, 300, 307, 311, 306, 309, 306, 294, 0, 0, 301, 297, 303, 295, 296, 297, 289, 299, 0, 0, 283, 293, 284, 294, 285, 304, 293, 289, 0, 0, 273, 280, 288, 290, 289, 286, 276, 283, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalRookOpening = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 451, 440, 484, 493, 494, 478, 464, 458, 0, 0, 406, 471, 488, 476, 468, 457, 461, 433, 0, 0, 444, 472, 477, 480, 460, 461, 452, 432, 0, 0, 454, 483, 470, 486, 476, 465, 451, 441, 0, 0, 457, 469, 512, 501, 503, 484, 466, 453, 0, 0, 493, 538, 522, 494, 513, 503, 496, 472, 0, 0, 521, 503, 544, 557, 539, 535, 509, 504, 0, 0, 520, 508, 486, 540, 528, 509, 519, 509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalRookEndgame = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 492, 516, 499, 507, 511, 515, 514, 503, 0, 0, 509, 501, 503, 503, 514, 512, 506, 506, 0, 0, 496, 504, 500, 505, 511, 507, 512, 508, 0, 0, 501, 504, 506, 507, 516, 520, 517, 515, 0, 0, 514, 511, 513, 514, 513, 525, 515, 516, 0, 0, 509, 507, 509, 516, 517, 519, 519, 519, 0, 0, 515, 520, 515, 509, 523, 525, 525, 523, 0, 0, 517, 520, 524, 524, 527, 530, 522, 525, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalQueenOpening = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 975, 994, 1000, 1010, 1035, 1016, 1007, 1024, 0, 0, 1026, 1022, 1040, 1033, 1027, 1036, 1017, 990, 0, 0, 1030, 1039, 1027, 1020, 1023, 1014, 1027, 1011, 0, 0, 1022, 1028, 1021, 1023, 1015, 1016, 999, 1016, 0, 0, 1026, 1023, 1042, 1024, 1009, 1009, 998, 998, 0, 0, 1082, 1072, 1081, 1054, 1033, 1032, 1008, 1012, 0, 0, 1079, 1053, 1082, 1009, 1026, 1020, 986, 1001, 0, 0, 1070, 1068, 1069, 1084, 1037, 1054, 1025, 997, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalQueenEndgame = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 895, 916, 904, 931, 893, 914, 908, 903, 0, 0, 904, 900, 913, 920, 920, 906, 913, 914, 0, 0, 941, 946, 953, 945, 942, 951, 909, 920, 0, 0, 959, 975, 970, 967, 983, 955, 964, 918, 0, 0, 972, 993, 976, 993, 981, 960, 958, 939, 0, 0, 945, 955, 971, 983, 985, 945, 942, 916, 0, 0, 936, 966, 961, 994, 977, 968, 956, 919, 0, 0, 956, 946, 955, 963, 963, 958, 958, 927, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalKingOpening = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 24, -28, 8, -54, 12, 36, -15, 0, 0, 8, 9, -16, -43, -64, -8, 7, 1, 0, 0, -27, -15, -30, -44, -46, -22, -14, -14, 0, 0, -51, -33, -44, -46, -39, -27, -1, -49, 0, 0, -36, -14, -25, -30, -27, -12, -20, -17, 0, 0, -22, 22, 6, -20, -16, 2, 24, -9, 0, 0, -29, -38, -4, -8, -7, -20, -1, 29, 0, 0, 13, 2, -34, -56, -15, 16, 23, 65, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    int[] evalKingEndgame = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -43, -24, -14, -28, -11, -21, -34, -53, 0, 0, -17, -5, 4, 14, 13, 4, -11, -27, 0, 0, -9, 7, 16, 23, 21, 11, -3, -19, 0, 0, -11, 9, 23, 27, 24, 21, -4, -18, 0, 0, 3, 26, 33, 26, 27, 24, 22, -8, 0, 0, 13, 44, 45, 20, 15, 23, 17, 10, 0, 0, 11, 23, 38, 17, 17, 14, 17, -12, 0, 0, -17, 4, 15, -11, -18, -18, -35, -74, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

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

        Map<Byte,Character> displayMap = buildDisplayMap();
        Scanner console = new Scanner(System.in);
        ArrayList<Integer> gameMoves = new ArrayList<>();
        while (true) {
            printBoard(displayMap);
            System.out.print("0 for white move, 1 for black move, 2 undo move, 3 manual move, 4 get best white move, 5 get best black move");
            int x = console.nextInt();
            if (x == 3) {
                System.out.print("Move index : ");
                int move = console.nextInt();
                makeMove(move);
                gameMoves.add(move);
            }
            else if (x == 2) {
                unMakeMove(gameMoves.get(gameMoves.size() - 1));
                gameMoves.remove(gameMoves.size() - 1);
            } else if (x == 1) {
                ArrayList<Integer> possibleMoves = getBlackMoves();
                int move = 0;
                while (!possibleMoves.contains(move)) {
                    System.out.print("From : ");
                    int from = console.nextInt();
                    System.out.print("To : ");
                    int to = console.nextInt();
                    move = encodeMove(to,from);
                }
                makeMove(move);
                gameMoves.add(move);
            } else {
                ArrayList<Integer> possibleMoves = getWhiteMoves();
                int move = 0;
                while (!possibleMoves.contains(move)) {
                    System.out.print("From : ");
                    int from = console.nextInt();
                    System.out.print("To : ");
                    int to = console.nextInt();
                    move = encodeMove(to,from);
                }
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
        System.out.println(boardState);
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
                    if (toPiece != 0) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }

                    toCoordinate = fromCoordinate + 11;
                    toPiece = board[toCoordinate];
                    if (toPiece != 0) {
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

                    if (fromCoordinate / 10 == 6) { //ep
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
                    if (toPiece != 0) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1001000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1010000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                        moves.add((boardState << 23) + 0b1011000000000000000 + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    toCoordinate = fromCoordinate - 11;
                    toPiece = board[toCoordinate];
                    if (toPiece != 0) {
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
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    toCoordinate = fromCoordinate - 11;
                    toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1) + 1);
                    }

                    if (fromCoordinate / 10 == 5) { //ep
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
    public static void makeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        int moveColor = move & 0b1;
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
                pieceLists[1-moveColor].add(to - 10 + 20*(moveColor));
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

        pieceLists[moveColor].add(from);
        pieceLists[moveColor].remove(to);
        if ((move & 0b11110000000000000000000) != 0) {
            pieceLists[1-moveColor].add(to);
        }
    }
}