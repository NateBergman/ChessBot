import java.util.*;
import java.io.*;
public class OpeningBookGenerator {
    static byte[] board;
    static byte boardState;
    static long[][] hashIndex;
    public static void main (String[] args) throws FileNotFoundException {
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
        seedHashIndex();
        Scanner console = new Scanner(System.in);
        PrintStream output = new PrintStream(new File("src/openingBookV1"));
        Map<Byte,Character> displayMap = laptopDisplayMap();
        ArrayList<Integer> gameMoves = new ArrayList<>();
        while(true) {
            printBoard(displayMap);
            System.out.println("move1 unmove2 writebestmove3");
            int x = console.nextInt();
            if (x == 2) {
                unMakeMove(gameMoves.get(gameMoves.size() - 1));
                gameMoves.remove(gameMoves.size() - 1);
            } else {
                System.out.print("From : ");
                int from = console.nextInt();
                System.out.print("To : ");
                int to = console.nextInt();
                int move = encodeMove(to,from);

                if (x == 3) {
                    System.out.println("0 for white's move, 1 for black's");
                    output.println(getHashIndex(console.nextInt() == 0) + " " + move);
                }

                makeMove(move);
                gameMoves.add(move);
            }
        }
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
    public static void makeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        int moveColor = move & 1;
        boardState = (byte) (boardState & 0b11110000); //resets en passantables
        if ((move>>18 & 1) == 1) { //if promotion, place correct piece
            board[to] = (byte) (((move>>15) & 0b11) + 2 + (8 * (moveColor))); //first part gets type, second half color
        } else {
            board[to] = board[from]; //otherwise piece in resulting square is same as initial
            if ((move>>15 & 0b111) == 0b101) { //en passant
                board[to - 10 + 20*(moveColor)] = 0; //btw (move & 0b1) gives you 0 if white move and 1 if black move
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
    public static void printBoard(Map<Byte,Character> displayMap) {
        for (int y = 90; y > 19; y -= 10) {
            for (int x = 1; x < 9; x++) {
                System.out.print(displayMap.get(board[y + x]) + "|");
            }
            System.out.println("\n------------------------------------------");
        }
    }
}
