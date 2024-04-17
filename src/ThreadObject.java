public class ThreadObject implements Runnable {
    int a;
    int b;
    int c;
    public ThreadObject(int a, int b) {
        this.a = a;
        this.b = b;
    }
    public void run() {
        c = a + b;
    }
    public int getC() {
        return c;
    }
}
