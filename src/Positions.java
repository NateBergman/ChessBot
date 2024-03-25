import java.util.*;
public class Positions {
    byte[] board;
    byte boardState;
    int[] knightMoves;
    int[] kingMoves;
    int[] diagonalMoves;
    int[] horizontalMoves;
    public Positions() {
        board = new byte[] {7,7,7,7,7,7,7,7,7,7, //looks upside down in this view
                7,7,7,7,7,7,7,7,7,7,
                7,4,2,3,5,6,3,2,4,7,
                7,1,1,1,1,1,1,1,1,7,
                7,0,0,0,0,0,0,0,0,7,
                7,0,0,0,0,0,0,0,0,7,
                7,0,0,0,0,0,0,0,0,7,
                7,0,0,0,0,0,0,0,0,7,
                7,9,9,9,9,9,9,9,9,7,
                7,12,10,11,13,14,11,10,12,7,
                7,7,7,7,7,7,7,7,7,7,
                7,7,7,7,7,7,7,7,7,7};
        boardState = (byte) 0b11110000;
        knightMoves = new int[] {21,19,12,8,-8,-12,-19,-21};
        kingMoves = new int[] {11,10,9,1,-1,-9,-10,-11};
        diagonalMoves = new int[] {11,9,-9,-11};
        horizontalMoves = new int[] {1,-1,10,-10};
        /*Collections.addAll(whitePieces,21,22,23,24,25,26,27,28,31,32,33,34,35,36,37,38);
        Collections.addAll(blackPieces,81,82,83,84,85,86,87,88,91,92,93,94,95,96,97,98); not using for now - too much to keep track of */
    }
    public boolean isAttacked(int square, boolean whiteControl) {
        int[] knight = new int[] {21,19,12,8,-8,-12,-19,-21};
        for (int i : knight) {
            byte piece = board[i + square];
            if ((whiteControl && piece == 10) || (!whiteControl && piece == 2)) {
                return true;
            }
        }
        int[] king = new int[] {11,10,9,1,-1,-9,-10,-11};
        for (int i : king) {
            byte piece = board[i + square];
            if ((whiteControl && piece == 14) || (!whiteControl && piece == 6)) {
                return true;
            }
        }
        if (whiteControl && (board[square + 11] == 9 || board[square + 9] == 9) || (!whiteControl && (board[square - 11] == 1 || board[square - 9] == 1))) {
            return true;
        }
        int[] diagonals = new int[] {-11,-9,9,11};
        for (int i : diagonals) {
            for (int j = square + i; true; j += i) {
                byte piece = board[j];
                if (piece != 0) {
                    if ((whiteControl && (piece == 11 || piece == 13)) || (!whiteControl && (piece == 3 || piece == 5))) {
                        return true;
                    }
                    break;
                }
            }
        }
        int[] horizontals = new int[] {-11,-9,9,11};
        for (int i : horizontals) {
            for (int j = square + i; true; j += i) {
                byte piece = board[j];
                if (piece != 0) {
                    if ((whiteControl && (piece == 12 || piece == 13)) || (!whiteControl && (piece == 4 || piece == 5))) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
    public ArrayList<Integer> getMoves(boolean black) { //just does white for now
        ArrayList<Integer> moves = new ArrayList<>();
        for (int p = 21; p < 99; p++) { //currently does square based searching, piece lists are faster but more complex
            byte piece = board[p];
            if (piece == 0 || piece > 6) {
                //do nothing (skips future tests)
            } else if (piece == 2) { //knight
                for (int i : knightMoves) {
                    int j = p + i;
                    byte target = board[j];
                    if (target == 0 || target > 8) {
                        moves.add((boardState<<23) + (target<<19) + ((target & 0b1000)<<14) + (p<<8) + (j<<1));
                    }
                }
            } else if (piece == 1) { //pawn
                if (p/10 == 8) { //promotions for fwd and captures if currently on 7th rank
                    int i = p + 10;
                    if (board[i] == 0) {
                        moves.add((boardState << 23) + 0b1000000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + 0b1001000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + 0b1010000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + 0b1011000000000000000 + (p << 8) + (i << 1));
                    }
                    i = p + 9;
                    if (board[i] > 8) {
                        byte target = board[i];
                        moves.add((boardState << 23) + (target << 19) + 0b1100000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + (target << 19) + 0b1101000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + (target << 19) + 0b1110000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + (target << 19) + 0b1111000000000000000 + (p << 8) + (i << 1));
                    }
                    i = p + 11;
                    if (board[i] > 8) {
                        byte target = board[i];
                        moves.add((boardState << 23) + (target << 19) + 0b1100000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + (target << 19) + 0b1101000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + (target << 19) + 0b1110000000000000000 + (p << 8) + (i << 1));
                        moves.add((boardState << 23) + (target << 19) + 0b1111000000000000000 + (p << 8) + (i << 1));

                    }
                } else {
                    if (board[p + 10] == 0) {//normal moves forward
                        moves.add((boardState << 23) + (p << 8) + ((p + 10) << 1));
                        if (p/10 == 3 && board[p+20] == 0) { //pushes
                            moves.add((boardState << 23) + 0b1000000000000000 + (p << 8) + ((p + 20) << 1));
                        }
                    }
                    if (board[p + 9] > 8) { //captures diagonal and stuff
                        moves.add((boardState << 23) + (board[p + 9] << 19) + (1 << 17) + (p << 8) + ((p + 9) << 1));
                    }
                    if (board[p + 11] > 8) {
                        moves.add((boardState << 23) + (board[p + 11] << 19) + (1 << 17) + (p << 8) + ((p + 11) << 1));
                    }
                    if (p / 10 == 6) { //en passants
                        if (p % 10 == (boardState & 0b1111) + 1) {
                            moves.add((boardState << 23) + (0b101 << 15) + (p << 8) + ((p + 9) << 1));
                        } else if (p % 10 == (boardState & 0b1111) - 1) {
                            moves.add((boardState << 23) + (0b101 << 15) + (p << 8) + ((p + 11) << 1));
                        }
                    }
                }
            } else if (piece == 6) { //king
                for (int i : kingMoves) {
                    int j = p + i;
                    byte target = board[j];
                    if (target == 0 || target > 8) {
                        moves.add((boardState<<23) + (target<<19) + ((target & 0b1000)<<14) + (p<<8) + (j<<1));
                    }
                }
                if ((boardState & 0b00010000) == 0b00010000 && board[26] == 0 && board[27] == 0 && !isAttacked(25,true) && !isAttacked(26,true)) {
                    moves.add((boardState<<23) + 0b10001100100110110); //short castle - checks for destination are in eval/actual making
                }
                if ((boardState & 0b00100000) == 0b00100000 && board[24] == 0 && board[23] == 0 && board[22] == 0 && !isAttacked(25,true) && !isAttacked(24,true)) {
                    moves.add((boardState<<23) + 0b11001100100101110); //long castle
                }
            } else {
                if (piece == 3 || piece == 5) { //diagonal bishop/queen +11s, +9s, -11s, -9s
                    for (int x : diagonalMoves) {
                        for (int i = p + x; true; i += x) {
                            byte target = board[i];
                            if (target == 0) {
                                moves.add((boardState << 23) + (p << 8) + (i << 1));
                            } else {
                                if (target > 8) {
                                    moves.add((boardState << 23) + (target << 19) + (1 << 17) + (p << 8) + (i << 1));
                                }
                                break;
                            }
                        }
                    }
                }
                if (piece == 4 || piece == 5) { //horizontal rook/queen +1s, -1s, +10s, -10s
                    for (int x : horizontalMoves) {
                        for (int i = p + x; true; i += x) {
                            byte target = board[i];
                            if (target == 0) {
                                moves.add((boardState << 23) + (p << 8) + (i << 1));
                            } else {
                                if (target > 8) {
                                    moves.add((boardState << 23) + (target << 19) + (1 << 17) + (p << 8) + (i << 1));
                                }
                                break;
                            }
                        }
                    }
                }
            }

        }
        return moves;
    }
    public void makeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        boardState = (byte) (boardState & 0b11110000); //resets en passantables
        if ((move>>18 & 1) == 1) { //if promotion, place correct piece
            board[to] = (byte) (((move>>15) & 0b11) + 2 + (8 * (move & 0b1))); //first part gets type, second half color
        } else {
            board[to] = board[from]; //otherwise piece in resulting square is same as initial
            if ((move>>15 & 0b111) == 0b101) { //en passant
                board[to - 10 + 20*(move & 0b1)] = 0; //btw (move & 0b1) gives you 0 if white move and 1 if black move
            } else if ((move>>16 & 1) == 1) { //castling
                if ((move>>15 & 1) == 1) { //long castle
                    board[to+1] = board[to-2];
                    board[to-2] = 0;
                } else { //short castle
                    board[to-1] = board[to+1];
                    board[to+1] = 0;
                }
            } else if ((move>>15 & 1) == 1) { //pawn pushing makes this column en passantable
                boardState += to%10;
            }
        }
        board[from] = 0; //space piece is leaving is always empty

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
    public void unMakeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        if ((move>>18 & 1) == 1) { //if promotion, need to unpromote
            board[from] = (byte)(1 + 8 * (move & 0b1)); //same color pawn as side moving
        } else {
            board[from] = board[to]; //otherwise just move the piece back
            if ((move>>15 & 0b111) == 0b101) { //en passant: also restore opp pawn
                board[to - 10 + 20*(move & 1)] = (byte)(9 - 8 * (move & 1)); //put opp color pawn in appropriate space
            } else if ((move>>16 & 1) == 1) { //undoing castling
                if ((move>>15 & 1) == 1) { //long castle
                    board[to-2] = board[to+1];
                    board[to+1] = 0;
                } else { //short castle
                    board[to+1] = board[to-1];
                    board[to-1] = 0;
                }
            }
        }
        board[to] = (byte)(move>>19 & 0b1111); //captured piece goes back on to
        boardState = (byte)(move>>23); //reverts board state (castle, ep) to previous
    }
    public int encodeMove(int to, int from) {
        int move = 0;
        move += (board[from]>>3) & 1; //is the move white or black
        move += to<<1;
        move += from<<8;
        move += board[to]<<19; //captured piece
        move += boardState<<23; //prior board state
        if (board[to] != 0) {
            move += 1<<17; //capture flag
        }
        if ((board[from] & 0b111) == 1) { //if a pawn (they have a lot of special moves)
            if (to > 90 || to < 30) { //promote
                move += 1<<18;
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
    public void printBoard(Map<Byte,Character> displayMap) {
        for (int y = 90; y > 19; y -= 10) {
            for (int x = 1; x < 9; x++) {
                System.out.print(displayMap.get(board[y + x]) + "|");
            }
            System.out.println("\n------------------------------------------");
        }
        System.out.println(boardState);
    }
}