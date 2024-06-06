import java.util.*;
import java.io.*;
public class FinalMiyoshiProject {
    //board setup
    static byte[] board;
    static byte boardState;
    static Set<Integer>[] pieceLists = new Set[]{new HashSet<>(), new HashSet<>()};
    static int[] kingPositions = {25,95};
    //move generation constants for each piece
    static boolean[] moveGenSlide = {false,false,true,true,true,false};
    static int[][] moveGenOffset = {{},{-21, -19,-12, -8, 8, 12, 19, 21},{-11,  -9,  9, 11},{-10,  -1,  1, 10},{-11, -10, -9, -1, 1,  9, 10, 11},{-11, -10, -9, -1, 1,  9, 10, 11}};

    //scoring initialization - includes phase for mid/endgame scoring and pst's
    static int phase = 0;
    static int searchNumber = 0;
    static int pstScoreMid = 0;
    static int pstScoreEnd = 0;
    static int[] phaseCounts = {0,0,1,1,2,4,0,0,0,0,1,1,2,4,0};
    static int[] PawnOpeningPST = { //taken from PESTO: a set of computer-optimized values online
            0,   0,   0,   0,   0,   0,  0,   0,
            98, 134,  61,  95,  68, 126, 34, -11,
            -6,   7,  26,  31,  65,  56, 25, -20,
            -14,  13,   6,  21,  23,  12, 17, -23,
            -27,  -2,  -5,  12,  17,   6, 10, -25,
            -26,  -4,  -4, -10,   3,   3, 33, -12,
            -35,  -1, -20, -23, -15,  24, 38, -22,
            0,   0,   0,   0,   0,   0,  0,   0,
    };
    static int[] PawnEndgamePST = {
            0,   0,   0,   0,   0,   0,   0,   0,
            178, 173, 158, 134, 147, 132, 165, 187,
            94, 100,  85,  67,  56,  53,  82,  84,
            32,  24,  13,   5,  -2,   4,  17,  17,
            13,   9,  -3,  -7,  -7,  -8,   3,  -1,
            4,   7,  -6,   1,   0,  -5,  -1,  -8,
            13,   8,   8,  10,  13,   0,   2,  -7,
            0,   0,   0,   0,   0,   0,   0,   0,
    };
    static int[] KnightOpeningPST = {
            -167, -89, -34, -49,  61, -97, -15, -107,
            -73, -41,  72,  36,  23,  62,   7,  -17,
            -47,  60,  37,  65,  84, 129,  73,   44,
            -9,  17,  19,  53,  37,  69,  18,   22,
            -13,   4,  16,  13,  28,  19,  21,   -8,
            -23,  -9,  12,  10,  19,  17,  25,  -16,
            -29, -53, -12,  -3,  -1,  18, -14,  -19,
            -105, -21, -58, -33, -17, -28, -19,  -23,
    };
    static int[] KnightEndgamePST = {
            -58, -38, -13, -28, -31, -27, -63, -99,
            -25,  -8, -25,  -2,  -9, -25, -24, -52,
            -24, -20,  10,   9,  -1,  -9, -19, -41,
            -17,   3,  22,  22,  22,  11,   8, -18,
            -18,  -6,  16,  25,  16,  17,   4, -18,
            -23,  -3,  -1,  15,  10,  -3, -20, -22,
            -42, -20, -10,  -5,  -2, -20, -23, -44,
            -29, -51, -23, -15, -22, -18, -50, -64,
    };
    static int[] BishopOpeningPST = {
            -29,   4, -82, -37, -25, -42,   7,  -8,
            -26,  16, -18, -13,  30,  59,  18, -47,
            -16,  37,  43,  40,  35,  50,  37,  -2,
            -4,   5,  19,  50,  37,  37,   7,  -2,
            -6,  13,  13,  26,  34,  12,  10,   4,
            0,  15,  15,  15,  14,  27,  18,  10,
            4,  15,  16,   0,   7,  21,  33,   1,
            -33,  -3, -14, -21, -13, -12, -39, -21,
    };
    static int[] BishopEndgamePST = {
            -14, -21, -11,  -8, -7,  -9, -17, -24,
            -8,  -4,   7, -12, -3, -13,  -4, -14,
            2,  -8,   0,  -1, -2,   6,   0,   4,
            -3,   9,  12,   9, 14,  10,   3,   2,
            -6,   3,  13,  19,  7,  10,  -3,  -9,
            -12,  -3,   8,  10, 13,   3,  -7, -15,
            -14, -18,  -7,  -1,  4,  -9, -15, -27,
            -23,  -9, -23,  -5, -9, -16,  -5, -17,
    };
    static int[] RookOpeningPST = {
            32,  42,  32,  51, 63,  9,  31,  43,
            27,  32,  58,  62, 80, 67,  26,  44,
            -5,  19,  26,  36, 17, 45,  61,  16,
            -24, -11,   7,  26, 24, 35,  -8, -20,
            -36, -26, -12,  -1,  9, -7,   6, -23,
            -45, -25, -16, -17,  3,  0,  -5, -33,
            -44, -16, -20,  -9, -1, 11,  -6, -71,
            -19, -13,   1,  17, 16,  7, -37, -26,
    };
    static int[] RookEndgamePST = {
            13, 10, 18, 15, 12,  12,   8,   5,
            11, 13, 13, 11, -3,   3,   8,   3,
            7,  7,  7,  5,  4,  -3,  -5,  -3,
            4,  3, 13,  1,  2,   1,  -1,   2,
            3,  5,  8,  4, -5,  -6,  -8, -11,
            -4,  0, -5, -1, -7, -12,  -8, -16,
            -6, -6,  0,  2, -9,  -9, -11,  -3,
            -9,  2,  3, -1, -5, -13,   4, -20,
    };
    static int[] QueenOpeningPST = {
            -28,   0,  29,  12,  59,  44,  43,  45,
            -24, -39,  -5,   1, -16,  57,  28,  54,
            -13, -17,   7,   8,  29,  56,  47,  57,
            -27, -27, -16, -16,  -1,  17,  -2,   1,
            -9, -26,  -9, -10,  -2,  -4,   3,  -3,
            -14,   2, -11,  -2,  -5,   2,  14,   5,
            -35,  -8,  11,   2,   8,  15,  -3,   1,
            -1, -18,  -9,  10, -15, -25, -31, -50,
    };
    static int[] QueenEndgamePST = {
            -9,  22,  22,  27,  27,  19,  10,  20,
            -17,  20,  32,  41,  58,  25,  30,   0,
            -20,   6,   9,  49,  47,  35,  19,   9,
            3,  22,  24,  45,  57,  40,  57,  36,
            -18,  28,  19,  47,  31,  34,  39,  23,
            -16, -27,  15,   6,   9,  17,  10,   5,
            -22, -23, -30, -16, -16, -23, -36, -32,
            -33, -28, -22, -43,  -5, -32, -20, -41,
    };
    static int[] KingOpeningPST = {
            -65,  23,  16, -15, -56, -34,   2,  13,
            29,  -1, -20,  -7,  -8,  -4, -38, -29,
            -9,  24,   2, -16, -20,   6,  22, -22,
            -17, -20, -12, -27, -30, -25, -14, -36,
            -49,  -1, -27, -39, -46, -44, -33, -51,
            -14, -14, -22, -46, -44, -30, -15, -27,
            1,   7,  -8, -64, -43, -16,   9,   8,
            -15,  36,  12, -54,   8, -28,  24,  14,
    };
    static int[] KingEndgamePST = {
            -74, -35, -18, -18, -11,  15,   4, -17,
            -12,  17,  14,  17,  17,  38,  23,  11,
            10,  17,  23,  15,  20,  45,  44,  13,
            -8,  22,  24,  27,  26,  33,  26,   3,
            -18,  -4,  21,  24,  27,  23,   9, -11,
            -19,  -3,  11,  21,  23,  16,   7,  -9,
            -27, -11,   4,  13,  14,   4,  -5, -17,
            -53, -34, -21, -11, -28, -14, -24, -43
    };
    static int[][] openingPST = {PawnOpeningPST,KnightOpeningPST,BishopOpeningPST,RookOpeningPST,QueenOpeningPST,KingOpeningPST}; //stored like this just for makePST convenience
    static int[][] endgamePST = {PawnEndgamePST,KnightEndgamePST,BishopEndgamePST,RookEndgamePST,QueenEndgamePST,KingEndgamePST}; //not referenced anywhere else
    static int[] openingMaterial = {82,337,365,477,1025,0};
    static int[] endgameMaterial = {94,281,297,512,936,0};
    static final double endgameMaterialModifier = 1.0;
    static int[][] openingGeneralPST = {{},new int[120],new int[120],new int[120],new int[120],new int[120],new int[120],{},{},new int[120],new int[120],new int[120],new int[120],new int[120],new int[120]};
    static int[][] endgameGeneralPST = {{},new int[120],new int[120],new int[120],new int[120],new int[120],new int[120],{},{},new int[120],new int[120],new int[120],new int[120],new int[120],new int[120]};

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
    static final int INCREMENT = 0000;
    static int timeLeft = TIME_CONTROL;

    static final boolean WHITEBOT = false; //color opponent plays
    public static void main(String[] args) throws FileNotFoundException { //starts by initializing everything
        board = new byte[]{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, //looks upside down in this view, 7s are borders
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7, //adding borders speeds up move generation because we don't have to check for "off the board"
                7, 4, 2, 3, 5, 6, 3, 2, 4, 7, //extra rows on top/bottom are for knight jumps
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
        makePSTS();
        seedHashIndex();
        transpositionTable = new HashMap<>();
        repetitionHashTable = new HashSet<>();
        buildNearKingTable();
        //Map<Byte,Character> displayMap = buildDisplayMap();
        Map<Byte,Character> displayMap = laptopDisplayMap();
        getOpenings("src/OpeningBookV1");

        if (WHITEBOT) { //player plays black, bot plays white
            while (true) { //gameplay loop
                printBoard(displayMap);
                int move = iterativeDeepening(SEARCH_DEPTH, true, allocateTime());
                makeMove(move);
                printBoard(displayMap);

                clearTranspositionTableOrderIn(MAXTTSIZE);
                searchNumber++;

                move = playerInputToMove();
                makeMove(move);
            }
        }
        //player plays white, bot plays black
        printBoard(displayMap);
        while (true) { //gameplay loop
            int move = playerInputToMove();
            makeMove(move);
            printBoard(displayMap);

            move = iterativeDeepening(SEARCH_DEPTH, false, allocateTime());
            makeMove(move);
            printBoard(displayMap);

            clearTranspositionTableOrderIn(MAXTTSIZE);
            searchNumber++;
        }
    }
    public static void makePSTS () { //converts PSTs written like they would be on a chessboard to my 120 square system, flips for colors, etc.
        for (int p = 0; p < 6; p++) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    openingGeneralPST[p + 1][91 - 10 * r + c] = openingPST[p][8 * r + c] + openingMaterial[p];
                    endgameGeneralPST[p + 1][91 - 10 * r + c] = (int)(endgamePST[p][8 * r + c] + endgameMaterial[p] * endgameMaterialModifier);
                    openingGeneralPST[p + 9][21 + 10 * r + c] = -openingPST[p][8 * r + c] - openingMaterial[p];
                    endgameGeneralPST[p + 9][21 + 10 * r + c] = (int)(-endgamePST[p][8 * r + c] - endgameMaterial[p] * endgameMaterialModifier);
                }
            }
        }
    }
    public static void printBoard(Map<Byte,Character> displayMap) { //self explanatory
        System.out.println("\nEngine has " + timeLeft / 60000 + ":" + (timeLeft / 1000) % 60 + " left on its clock\n");
        for (int y = 90; y > 19; y -= 10) {
            for (int x = 1; x < 9; x++) {
                System.out.print(displayMap.get(board[y + x]) + "|");
            }
            System.out.println("\n------------------------------------------");
        }
    }
    public static void buildNearKingTable() { //makes an array where for each square all "near" squares on the board are listed - helps with determining if a king is in danger
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
    public static Map<Byte,Character> buildDisplayMap () { //converts pieces, which are stored in bytes, to symbols for display
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
    public static Map<Byte,Character> laptopDisplayMap () { //my laptop doesn't show the pieces correctly so I have another scheme with letters
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
    public static void seedHashIndex() { //transposition table refers to positions by "hashes" - longs representing a position - to save memory
        Random r = new Random(1); //each aspect of a position is given it's own 64-bit long; "randomly" generated but not really because the seed is always 1 at program start
        hashIndex = new long[13][64]; //1-12 are pieces on the board, 0 is for special stuff like side to move, castling, en passant (0 is side to move, 1-8 en passant, 9-23 castling rights)
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 64; j++) {
                hashIndex[i][j] = r.nextLong();
            }
        }
    }
    public static long getHashIndex(boolean whiteMove) {
        long hash = 0; //to refer to a position, we just XOR each aspect of the position together
        for (int y = 2; y < 10; y++) { //isn't a perfect system, but with 64 bits the probability of having a conflict is really small
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
    public static void clearTranspositionTableOrderIn(int goalSize) { //memory managment for the transposition table so we don't overflow/get slow
        int cutoff = searchNumber - TTBACKDEPTH; //uses a simple first in, first out system looking at search #
        Set<Long> keys = transpositionTable.keySet(); //gets down to goal size exactly - will end up removing some but not all from a certain #
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
    }
    public static void getOpenings(String fileName) throws FileNotFoundException { //opening book is simple - we read from a file and store the results as "best moves" in TT
        Scanner openingReader = new Scanner(new File(fileName)); //format is:  "position bestMove"
        while (openingReader.hasNext()) { //all have an orderIn of 0 because we can clear them as soon as we get out of book
            long key = openingReader.nextLong();
            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(openingReader.nextInt());
            transpositionTable.put(key,new HashEntry(moves,99,true,0,0)); //depth is high because we always trust opening book
        }
    }
    public static int playerInputToMove() { //gathers player input and translates it to my move notation
        Scanner console = new Scanner(System.in); //notably doesn't check for legal moves - the point of this is to test the bot, not maintain enforced servers
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
    public static int encodeMove(int to, int from) { //I operate on moves as 32-bit integers (faster/less memory than a class) and so this turns player input into them
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
    public static int getNullMove(boolean whiteMove) { //potentially useful function for pruning/evaluation where we see what would happen if a side "passed" their turn
        if (whiteMove) {
            return boardState << 23;
        }
        return (boardState << 23) + 1;
    }
    public static boolean makeMove(int move) { //all the stuff that happens when we move a piece - a lot of stuff to be incrementally updated
        int to = (move>>1) & 0b1111111; //unpacks integer
        int from = (move>>8) & 0b1111111;
        int moveColor = move & 1; //0 for white, 1 for black
        if (board[to] % 8 == 6) { //if we would take the king, we don't actually have to move - just record we did for search
            return true;
        }
        repetitionHashTable.add(getHashIndex(moveColor == 0));
        boardState = (byte) (boardState & 0b11110000); //resets en passantables

        pstScoreMid -= openingGeneralPST[board[from]][from]; //incrementally update PSTs - faster than recalculating at eval
        pstScoreEnd -= endgameGeneralPST[board[from]][from];

        if ((move>>18 & 1) == 1) { //promotion
            board[to] = (byte) (((move>>15) & 0b11) + 2 + (8 * (moveColor)));
        } else {
            board[to] = board[from]; //otherwise piece in resulting square is same as initial
            if ((move>>15 & 0b111) == 0b101) { //en passant
                board[to - 10 + 20*(moveColor)] = 0;
                pieceLists[1-moveColor].remove(to - 10 + 20*(moveColor));
                pstScoreMid -= openingGeneralPST[9 - 8 * moveColor][to - 10 + 20*(moveColor)];
                pstScoreEnd -= endgameGeneralPST[9 - 8 * moveColor][to - 10 + 20*(moveColor)];
            } else if ((move>>16 & 1) == 1) { //castling - need to move and update rooks as well
                if ((move>>15 & 1) == 1) { //long castle
                    board[to+1] = board[to-2];
                    board[to-2] = 0;
                    pieceLists[moveColor].add(to + 1);
                    pieceLists[moveColor].remove(to - 2);
                    pstScoreMid += openingGeneralPST[4 + 8 * moveColor][to + 1] - openingGeneralPST[4 + 8 * moveColor][to - 2];
                    pstScoreEnd += endgameGeneralPST[4 + 8 * moveColor][to + 1] - endgameGeneralPST[4 + 8 * moveColor][to - 2];
                } else { //short castle
                    board[to-1] = board[to+1];
                    board[to+1] = 0;
                    pieceLists[moveColor].add(to - 1);
                    pieceLists[moveColor].remove(to + 1);
                    pstScoreMid += openingGeneralPST[4 + 8 * moveColor][to - 1] - openingGeneralPST[4 + 8 * moveColor][to + 1];
                    pstScoreEnd += endgameGeneralPST[4 + 8 * moveColor][to - 1] - endgameGeneralPST[4 + 8 * moveColor][to + 1];
                }
            } else if ((move>>15 & 1) == 1) { //pawn pushing makes this column en passantable
                boardState += to%10;
            }
        }
        board[from] = 0; //space piece is leaving is always empty

        pstScoreMid += openingGeneralPST[board[to]][to]; //little hack: adding value here accounts for promotion automatically
        pstScoreEnd += endgameGeneralPST[board[to]][to];

        pieceLists[moveColor].remove(from);
        pieceLists[moveColor].add(to);

        if ((move & 0b11110000000000000000000) != 0) { //captures
            pieceLists[1-moveColor].remove(to);
            phase += phaseCounts[move>>19 & 0b1111];
            pstScoreMid -= openingGeneralPST[move>>19 & 0b1111][to];
            pstScoreEnd -= endgameGeneralPST[move>>19 & 0b1111][to];
        }

        if(from == 21 || from == 25 || to == 21) { //castling rights are lost if pieces move off starting squares
            boardState = (byte)(boardState & 0b11011111); //or if opponent captures a rook
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
    public static void unMakeMove(int move) { //undoing a move - important for search. Similar to makeMove
        int to = (move>>1) & 0b1111111; //unpacks integer
        int from = (move>>8) & 0b1111111;
        int moveColor = move & 0b1;

        pstScoreMid -= openingGeneralPST[board[to]][to];
        pstScoreEnd -= endgameGeneralPST[board[to]][to];

        if ((move>>18 & 1) == 1) { //unpromoting
            board[from] = (byte)(1 + 8 * (moveColor));
        } else {
            board[from] = board[to];
            if ((move>>15 & 0b111) == 0b101) { //en passant: also restore opp pawn
                board[to - 10 + 20*(moveColor)] = (byte)(9 - 8 * (moveColor)); //put opp color pawn in appropriate space
                pieceLists[1-moveColor].add(to - 10 + 20 * (moveColor));
                pstScoreMid += openingGeneralPST[9 - 8 * moveColor][to - 10 + 20*(moveColor)];
                pstScoreEnd += endgameGeneralPST[9 - 8 * moveColor][to - 10 + 20*(moveColor)];
            } else if ((move>>16 & 1) == 1) { //undoing castling
                if ((move>>15 & 1) == 1) { //long castle
                    board[to-2] = board[to+1];
                    board[to+1] = 0;
                    pieceLists[moveColor].remove(to + 1);
                    pieceLists[moveColor].add(to - 2);
                    pstScoreMid += openingGeneralPST[4 + 8 * moveColor][to - 2] - openingGeneralPST[4 + 8 * moveColor][to + 1];
                    pstScoreEnd += endgameGeneralPST[4 + 8 * moveColor][to - 2] - endgameGeneralPST[4 + 8 * moveColor][to + 1];
                } else { //short castle
                    board[to+1] = board[to-1];
                    board[to-1] = 0;
                    pieceLists[moveColor].remove(to - 1);
                    pieceLists[moveColor].add(to + 1);
                    pstScoreMid += openingGeneralPST[4 + 8 * moveColor][to + 1] - openingGeneralPST[4 + 8 * moveColor][to - 1];
                    pstScoreEnd += endgameGeneralPST[4 + 8 * moveColor][to + 1] - endgameGeneralPST[4 + 8 * moveColor][to - 1];
                }
            }
        }
        board[to] = (byte)(move>>19 & 0b1111); //captured piece goes back on to
        boardState = (byte)(move>>23); //reverts board state (castle, ep) to previous
        phase -= phaseCounts[move>>19 & 0b1111];

        pstScoreMid += openingGeneralPST[board[from]][from];
        pstScoreEnd += endgameGeneralPST[board[from]][from];
        pieceLists[moveColor].add(from);
        pieceLists[moveColor].remove(to);

        if ((move & 0b11110000000000000000000) != 0) {
            pieceLists[1-moveColor].add(to);
            pstScoreMid += openingGeneralPST[move>>19 & 0b1111][to];
            pstScoreEnd += endgameGeneralPST[move>>19 & 0b1111][to];
        }
        if (board[from] == 6) {
            kingPositions[0] = from;
        } else if (board[from] == 14) {
            kingPositions[1] = from;
        }
        repetitionHashTable.remove(getHashIndex(moveColor == 0));
    }
    public static ArrayList<Integer> getWhiteMoves() { //generates possible moves
        ArrayList<Integer> moves = new ArrayList<>();
        for (int fromCoordinate : pieceLists[0]) { //main benefit of piece lists - have to check <16 squares vs 64/120
            int fromPiece = board[fromCoordinate] - 1;
            if (fromPiece == 0) { //pawn (actually so painful to do)
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
                        if (fromCoordinate/10 == 3 && board[toCoordinate] == 0) { //double pushes
                            moves.add((boardState << 23) + 0b1000000000000000 + (fromCoordinate << 8) + ((toCoordinate) << 1));
                        }
                    }
                    toCoordinate = fromCoordinate + 9; //captures
                    int toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }
                    toCoordinate = fromCoordinate + 11;
                    toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (toPiece << 19) + (fromCoordinate << 8) + (toCoordinate << 1));
                    }
                    if (fromCoordinate / 10 == 6 && (boardState & 0b1111) != 0) { //en passant
                        if (fromCoordinate % 10 == (boardState & 0b1111) + 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate + 9) << 1));
                        } else if (fromCoordinate % 10 == (boardState & 0b1111) - 1) {
                            moves.add((boardState << 23) + 0b101000000000000000 + (fromCoordinate << 8) + ((fromCoordinate + 11) << 1));
                        }
                    }
                }
            } else { //everything else is easy
                int[] offsets = moveGenOffset[fromPiece]; //pieces all have a set of directions they can go
                boolean slide = moveGenSlide[fromPiece]; //some can keep going until they hit something, other's just go one square
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
            moves.add((boardState<<23) + 0b10001100100110110); //short castle - checks for destination are in eval/actual making so we just have to do path/start
        }
        if ((boardState & 0b00100000) == 0b00100000 && board[24] == 0 && board[23] == 0 && board[22] == 0 && !isAttacked(25,true) && !isAttacked(24,true)) {
            moves.add((boardState<<23) + 0b11001100100101110); //long castle
        }
        return moves;
    }
    public static ArrayList<Integer> getBlackMoves() { //same as white moves but enough if statements/pawn offsets/castling changes I made a new method
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
    public static ArrayList<Integer> getWhiteCaptures() { //includes only promotions and captures
        ArrayList<Integer> moves = new ArrayList<>(); //same process as before, but only add it under certain criteria
        for (int fromCoordinate : pieceLists[0]) { //faster than getting all moves and removing non-captures
            int fromPiece = board[fromCoordinate] - 1; //used in quiescence search to handle tactics/unstable positions (where material changes)
            if (fromPiece == 0) {
                if (fromCoordinate/10 == 8) {
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
        return moves;
    }
    public static ArrayList<Integer> getBlackCaptures() { //you know the drill
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
    public static boolean isAttacked (int square, boolean white) { //used for testing castling legality and stalemate detection
        for (int i = 1; i < 6; i++) { //iterates for each piece type and sees if any of that type are attacking the square
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
    public static int getMobility (int square, boolean white, int[] attacks, int kingSquare) { //faster generation for evaluation, doesn't get actual moves (just count)
        int count = 0; //also helps with king safety by tracking what pieces attack each king's area
        int attackCnt = 0;
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
                        if (nearKing[kingSquare][toCoordinate]) {
                            attackCnt++;
                        }
                        break;
                    }
                    if (toPiece != 0) {
                        if (nearKing[kingSquare][toCoordinate]) {
                            attackCnt++;
                        }
                        break;
                    }
                    count++;
                    if (nearKing[kingSquare][toCoordinate]) {
                        attackCnt++;
                    }
                    toCoordinate += o;
                } while (slide);
            }
            if (attackCnt > 0) {
                attacks[0] += attackerValue[fromPiece] * attackCnt;
                attacks[1]++;
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
                        if (nearKing[kingSquare][toCoordinate]) {
                            attackCnt++;
                        }
                        break;
                    }
                    if (toPiece != 0) {
                        if (nearKing[kingSquare][toCoordinate]) {
                            attackCnt++;
                        }
                        break;
                    }
                    count++;
                    if (nearKing[kingSquare][toCoordinate]) {
                        attackCnt++;
                    }
                    toCoordinate += o;
                } while (slide);
            }
            if (attackCnt > 0) {
                attacks[2] += attackerValue[fromPiece] * attackCnt;
                attacks[3]++;
            }
        }
        return count;
    }
    public static int getKnightMobility(int square, boolean white, int[] attacks, int kingSquare) { //for knights I've found it more effective to only count squares not attacked by enemy pawns
        int count = 0; //otherwise you get "bad" knights that are in normally good positions and have a lot of useless moves.
        int attackCnt = 0;
        int[] offsets = moveGenOffset[1];
        if (white) {
            for (int o : offsets) {
                int toCoordinate = square + o;
                byte toPiece = board[toCoordinate];
                if ((toPiece > 8 || toPiece == 0) && board[toCoordinate + 9] != 9 && board[toCoordinate + 11] != 9) {
                    count++;
                    if (nearKing[kingSquare][toCoordinate]) {
                        attackCnt++;
                    }
                }
            }
            if (attackCnt > 0) {
                attacks[0] += attackerValue[1] * attackCnt;
                attacks[1]++;
            }
        } else {
            for (int o : offsets) {
                int toCoordinate = square + o;
                byte toPiece = board[toCoordinate];
                if (toPiece < 7 && board[toCoordinate - 9] != 1 && board[toCoordinate - 11] != 1) {
                    count++;
                    if (nearKing[kingSquare][toCoordinate]) {
                        attackCnt++;
                    }
                }
            }
            if (attackCnt > 0) {
                attacks[2] += attackerValue[1] * attackCnt;
                attacks[3]++;
            }
        }
        return count - 4;
    }
    public static int evaluatePosition(boolean whiteMove) { //general evaluation function
        int mgScore = pstScoreMid;
        int egScore = pstScoreEnd;
        int[] attacks = new int[4]; //goes white attack value, white attacker count, black attack value, black attacker count

        for (int i : pieceLists[0]) { //evaluate each piece
            byte piece = (byte) (board[i] - 1);
            if (piece == 0) {
                int score = evaluatePawn(i, true, attacks, kingPositions[1]);
                mgScore += score;
                egScore += score;
            } else if (piece == 1) {
                int score = getKnightMobility(i,true,attacks,kingPositions[1]);
                mgScore += score * mobilityOpening[1];
                egScore += score * mobilityEndgame[1];
            } else {
                int score = getMobility(i,true,attacks,kingPositions[1]);
                mgScore += score * mobilityOpening[piece];
                egScore += score * mobilityEndgame[piece];
            }
        }
        for (int i : pieceLists[1]) {
            byte piece = (byte) (board[i] - 9);
            if (piece == 0) {
                int score = evaluatePawn(i,false, attacks, kingPositions[0]);
                mgScore -= score;
                egScore -= score;
            } else if (piece == 1) {
                int score = getKnightMobility(i,false,attacks,kingPositions[0]);
                mgScore -= score * mobilityOpening[1];
                egScore -= score * mobilityEndgame[1];
            }else {
                int score = getMobility(i,false, attacks, kingPositions[0]);
                mgScore -= score * mobilityOpening[piece];
                egScore -= score * mobilityEndgame[piece];
            }
        }
        //king safety stuff
        if (attacks[1] > 1) { //attacks aren't linear - only threatening past a certain threshold/amount of attackers, but danger levels off after there
            mgScore += attackTable[attacks[0]];
        }
        if (attacks[3] > 1) {
            mgScore -= attackTable[attacks[2]];
        }
        mgScore += pawnShield();
        //tempo bonus - smoothes out adjacent search depths b/ side to move improves their position in stair-steps
        int tempoBonus = 10;
        if (whiteMove) {
            mgScore += tempoBonus;
            egScore += tempoBonus;
        } else {
            mgScore -= tempoBonus;
            egScore -= tempoBonus;
        }
        //linear interpolation between early/late game
        return (int)((mgScore * (24.0 - phase) / 24.0) + (egScore * phase / 24.0));
    }
    public static int pawnShield() { //points for pawns shielding your king - changes based on king position so can't do PST
        int score = 0;
        int shieldRankTwo = 10;
        int shieldRankThree = 5;
        if (kingPositions[0] % 10 > 5) { //white short castled
            if (board[26] == 1) {
                score += shieldRankTwo;
            } else if (board[36] == 1) {
                score += shieldRankThree;
            }
            if (board[27] == 1) {
                score += shieldRankTwo;
            } else if (board[37] == 1) {
                score += shieldRankThree;
            }
            if (board[28] == 1) {
                score += shieldRankTwo;
            } else if (board[38] == 1) {
                score += shieldRankThree;
            }
        } else if (kingPositions[0] % 10 < 4) { //white long castled
            if (board[21] == 1) {
                score += shieldRankTwo;
            } else if (board[31] == 1) {
                score += shieldRankThree;
            }
            if (board[22] == 1) {
                score += shieldRankTwo;
            } else if (board[32] == 1) {
                score += shieldRankThree;
            }
            if (board[23] == 1) {
                score += shieldRankTwo;
            } else if (board[33] == 1) {
                score += shieldRankThree;
            }
        }
        if (kingPositions[1] % 10 > 6) { //black short castled
            if (board[86] == 1) {
                score -= shieldRankTwo;
            } else if (board[76] == 1) {
                score -= shieldRankThree;
            }
            if (board[87] == 1) {
                score -= shieldRankTwo;
            } else if (board[77] == 1) {
                score -= shieldRankThree;
            }
            if (board[88] == 1) {
                score -= shieldRankTwo;
            } else if (board[78] == 1) {
                score -= shieldRankThree;
            }
        } else if (kingPositions[1] % 10 < 4) { //black long castled
            if (board[81] == 1) {
                score -= shieldRankTwo;
            } else if (board[71] == 1) {
                score -= shieldRankThree;
            }
            if (board[82] == 1) {
                score -= shieldRankTwo;
            } else if (board[72] == 1) {
                score -= shieldRankThree;
            }
            if (board[83] == 1) {
                score -= shieldRankTwo;
            } else if (board[73] == 1) {
                score -= shieldRankThree;
            }
        }
        return score;
    }
    public static int evaluatePawn(int sq, boolean white, int[] attacks, int kingPosition) { //pawn structure evaluation
        int score = 0; //considers pawns in relation to all others
        boolean opposed = false; //PSTs consider pieces in isolation so they overlook this
        boolean weak = true;
        boolean isolated = true;
        boolean passed = true;

        int doubledPenalty = -20; //these all stack, doubled is self-explanatory
        int weakPenalty = -5; //no pawns behind/level to support it
        int weakSemiOpenPenalty = -5; //can be attacked easier
        int isolatedPenalty = -10; //so like a doubled, unsupported, isolated pawn on a semi-open file is really bad
        int passedBonus = 25; //no enemy pawns in front/adjacent
        int supportedBonus = 5; //adjacent pawn at level or right behind
        int supportedPassed = 10; //stacks with above

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

            target = board[nextSq - 1]; //adjacent files (for passed detection)
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
        while(board[nextSq] != 7) { //searches backwards to catch weak pawns (no defenders)
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

        if (white) { //attack points for pawn storms near enemy king
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
    //search stuff
    public static int startSearch (int depth, boolean whiteMove, int alpha, int beta, long endTime) { //returns best move
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
            }
            if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                transpositionTable.put(hash,new HashEntry(moves,depth,true,beta,searchNumber));
            }
            return moves.get(0);
        }
    }
    public static int search (int depth, boolean whiteMove, int alpha, int beta, long endTime) {
        nodeCount++;
        if (nodeCount == NODESPERCHECK) { //check if search is out of time/tt memory overflow risk (housekeeping)
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
        if (repetitionHashTable.contains(hash)) { //program counts a single repetition as a draw, because if best play from both sides gives us a draw neither is going to want to take a worse position and will likely keep repeating moves
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
                int score = evaluatePosition(whiteMove);
                transpositionTable.put(hash,new HashEntry(0,true,score,searchNumber));
                return score;
            }
            if (whiteMove) {
                alpha = Math.max(evaluatePosition(true),alpha); //need to find out a way for counting it as a full search still if nothing exceeds no capture
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
            beta = Math.min(evaluatePosition(false),beta); //need to find out a way for counting it as a full search still if nothing exceeds no capture
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
        } else { //main (non-quiescence) search
            if (whiteMove) {
                ArrayList<Integer> moves;
                if(transpositionTable.containsKey(hash)) {
                    HashEntry h = transpositionTable.get(hash);
                    if ((h.getDepth() >= depth) && (h.getFullSearch() || h.getScore() >= beta || h.getScore() <= alpha)) {
                        return h.getScore(); //if we've already searched this position to a greater/equal depth just use that
                    } //only if it's a full (non-cutoff) search or it's outside curent bounds though
                    if (!h.isQuiescence()) { //if not but we've already generated full movelist we can use those rather than re-generating to save time
                        moves = new ArrayList<>(h.getMoves());
                    } else {
                        moves = getWhiteMoves();
                    }
                } else {
                    moves = getWhiteMoves();
                }
                int length = moves.size(); //timesave b/ we don't have to look up move size every time through the for
                for (int i = 0; i < length; i++) {
                    int m = moves.get(i);
                    if (makeMove(m)) { //checkmates (found by capturing king)
                        moves.add(0,moves.remove(i)); //we order it so best moves are first because in future searches we want to search best first
                        transpositionTable.put(hash,new HashEntry(99,true,TAKEKING,searchNumber));
                        return TAKEKING;
                    }
                    int score = search(depth - 1, false, alpha, beta,endTime);
                    if (score == OUTOFTIME) { //if we're out of time we cutoff searching immediately and get inconclusive results for this branch
                        unMakeMove(m);
                        return OUTOFTIME;
                    }
                    if (score >= beta) { //this means opponent had better moves they could have made previously so this is not PV
                        unMakeMove(m);
                        moves.add(0,moves.remove(i));
                        if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                            transpositionTable.put(hash,new HashEntry(moves,depth,false,score,searchNumber));
                        }
                        return score;
                    }
                    if (score > alpha) { //represents best score so far
                        moves.add(0,moves.remove(i));
                        alpha = score;
                        foundGoodMove = true; //used to store whether we failed low or not in transposition table
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
                    transpositionTable.put(hash, new HashEntry(99,true,DRAW,searchNumber));
                    return DRAW;
                }
                if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash).getDepth() < depth) {
                    transpositionTable.put(hash,new HashEntry(moves,depth,foundGoodMove,alpha,searchNumber));
                }
                return alpha;
            } else { //same but for black, swap alpha and beta because black is trying to get a lower score (not higher like white)
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
                    } else if (score == TAKEKING) {
                        moves.remove(i);
                        i--;
                        length--;
                    }
                    unMakeMove(m);
                }
                if (moves.isEmpty()) {
                    if (isAttacked(kingPositions[1],true)) {
                        transpositionTable.put(hash, new HashEntry(99,true,WIN,searchNumber));
                        return WIN + depth;
                    }
                    transpositionTable.put(hash, new HashEntry(99,true,DRAW,searchNumber));
                    return DRAW;
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
        for (int n = 2; System.currentTimeMillis() < midCutoffTime && n <= depth; n++) { //depth cutoff is so we don't have to waste time going to infinite depth if we have an opening book/only one move
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