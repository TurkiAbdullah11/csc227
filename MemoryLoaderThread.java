import java.util.Queue;

public class MemoryLoaderThread extends Thread {
    private MemoryLoader ml;
    private Queue<Process> jobQ;
    private volatile boolean running = true;
    
    // constructor
    public MemoryLoaderThread(MemoryLoader ml, Queue<Process> jobQ) {
        this.ml = ml;
        this.jobQ = jobQ;
    }

    public void stopThread() {
        running = false;
    }
    
    // run method
    @Override
    public void run() {
        System.out.println("Memory loader thread started!");
        
        while (running) {
            if (!jobQ.isEmpty()) {
                boolean loaded = ml.reloadReadyQueue(jobQ);
                
                if (loaded) {
                    System.out.println("\nLoaded some processes to ready queue:");
                    ml.printReadyQueue();
                    System.out.println("=====================");
                }
            }
            
            if (jobQ.isEmpty() && ml.readyQueue.isEmpty()) {
                System.out.println("No more jobs to load!");
                stopThread();
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
      }
}