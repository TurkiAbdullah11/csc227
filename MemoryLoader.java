import java.util.*;

public class MemoryLoader {
    Queue<Process> readyQueue = new LinkedList<>();  
    private final int maxSize = 2048;
    private int currentSize = 0;

    public synchronized boolean reloadReadyQueue(Queue<Process> jobQueue) {
      
        boolean loadedAny = false;
        
        
        List<Process> processes = new ArrayList<>(jobQueue);
        
        for (Process process : processes) {
            if (process.getMemoryRequired() + currentSize <= maxSize) {
                SystemCall.loadMemory(process, jobQueue, readyQueue);
                currentSize += process.getMemoryRequired();
                SystemCall.setProcessState(process, State.READY);
                loadedAny = true;
            }
        }
        
        return loadedAny; 
    }


    public synchronized boolean loadToMemory(Queue<Process> jobQueue) {
        return reloadReadyQueue(jobQueue);
    }

    public synchronized void removeProcess(Process process) {
        if (process != null) {
            currentSize -= process.getMemoryRequired();
        }
    }

    public synchronized void printReadyQueue() {
        if (readyQueue.isEmpty()) {
            System.out.println("[ READY QUEUE ] ; Used memory: " + currentSize + " / 2048 MB (Empty)");
        } else {
            System.out.println("[ READY QUEUE ] ; Used memory: " + currentSize + " / 2048 MB");
            for (Process process : readyQueue) {
                System.out.println(process);
            }
        }
    }
}