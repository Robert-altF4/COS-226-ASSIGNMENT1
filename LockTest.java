//test to show lock provides mutual exclusion 
public class LockTest {
    private static int counter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads = 16;
        int iterations = 10000;
        
        CLH lock = new CLH();
        Thread[] threads = new Thread[numberOfThreads];
        
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    lock.lock();
                    try {
                        counter++; 
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }
        
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        
        System.out.println("Expected count: " + (numberOfThreads * iterations));
        System.out.println("Actual count:   " + counter);
    }
}