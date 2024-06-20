public class Search {
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
}
