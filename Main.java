public class Main 
{

    public static void main(String[] args) throws InterruptedException 
    {
        int[] threadCounts = {2, 4, 8, 16}; /*Change*/
        int iterations = 200;
        int testRuns = 3; 

        System.out.println("Lock performance experiment\n");

        for (int numberOfThreads : threadCounts) 
        {
            System.out.println("--- Testing with " + numberOfThreads + " Threads ---");
            
            for (int run = 1; run <= testRuns; run++) 
            {
                System.out.println("[Run " + run + "]");
                
                Auction auction =new Auction(AuctionUtils.generateItemName());
                //Lock lock = new Lock(); /*Add your lock here*/

                Lock lock = new MCS();

                System.out.println("Starting auction for: " + auction.getItemName());
                Runner runner = new Runner(numberOfThreads,iterations,auction,lock);
                runner.run();
                System.out.println();
            }
        }
    }
}