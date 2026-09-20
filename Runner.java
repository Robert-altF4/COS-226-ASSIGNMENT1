import java.util.concurrent.atomic.AtomicLong;
import java.util.Arrays;

/*Optional Helper Runner Class*/
public class Runner 
{

    public final int numberOfThreads;
    public final int iterations;
    public final Auction auction;
    public final Lock lock;

    private final AtomicLong totalWaitingTime = new AtomicLong(0);
    private final int[] bidsWon;

    public Runner(int numberOfThreads,int iterations,Auction auction,Lock lock) 
    {
        this.numberOfThreads = numberOfThreads;
        this.iterations = iterations;
        this.auction = auction;
        this.lock = lock;
        this.bidsWon = new int[numberOfThreads];
    }

    public void run() throws InterruptedException 
    {
        Thread[] threads = new Thread[numberOfThreads];

        for(int i = 0; i < numberOfThreads; i++) 
        {
            final int bidderId = i;

            threads[i] = new Thread(() -> {
                bidder(bidderId);
            });
        }

        long startTime = System.nanoTime();

        for(Thread thread : threads) 
        {
            thread.start();
        }

        for(Thread thread : threads) 
        {
            thread.join();
        }

        long endTime = System.nanoTime();

        reportResults(endTime - startTime);
    }

    /*Defines the behaviour of an individual bidder. Note you have to decide how to incorporate your lock.*/
    public void bidder(int bidderId) 
    {
        for (int i = 0; i < iterations; i++) {
            long waitStart = System.nanoTime();
            lock.lock();
            long waitTime = System.nanoTime() - waitStart;
            
            totalWaitingTime.addAndGet(waitTime);
            
            try {
                double currentBid = auction.getHighestBid();
                double newBid = currentBid + 10.0; 
                auction.placeBid(bidderId, newBid);
                bidsWon[bidderId]++;
            } finally {
                lock.unlock(); 
            }
        }
       
    }

    /*Optional Helper: Records and reports the results of the experiment.*/
    public void reportResults(long executionTime) 
    {
        System.out.println("Total Execution Time (ns): " + executionTime);
        System.out.println("Final Highest Bid: R " + auction.getHighestBid());
        System.out.println("Winning Bidder ID: " + auction.getHighestBidder());
        
        // calc total successful bids placed
        int totalSuccessfulBids = 0;
        for (int bids : bidsWon) {
            totalSuccessfulBids += bids;
        }
        System.out.println("Total Bids Successfully Placed: " + totalSuccessfulBids);
        
        long totalAcquisitions = (long) numberOfThreads * iterations;
        long avgWaitTime = totalWaitingTime.get() / totalAcquisitions;
        System.out.println("Average Wait Time per Lock (ns): " + avgWaitTime);
        
        System.out.println("Bids Won by Each Bidder: " + Arrays.toString(bidsWon));
    }
}