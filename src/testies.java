public class testies {
    public static void main(String[] args){
        new Thread(new Runnable() {
            public void run() {
                while (true){
                System.out.println("hello");
                }
            }
        }).start();

        
        while (true){
            System.out.println("hello?");
        }
    }
}