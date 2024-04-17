public class ThreadingMain {
    public static void main (String[] args) throws InterruptedException {
        ThreadObject o = new ThreadObject(1,2);
        Thread t = new Thread(o);
        t.start();




        t.join();
        int c = o.getC();
        System.out.println(c);
    }
}
