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
        if ((move & 0b11110000000000000000) != 0) {
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
        if ((move & 0b11110000000000000000) != 0) {
            pieceLists[1-moveColor].add(to);
        }
    }
}