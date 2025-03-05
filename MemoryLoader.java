import java.util.*;

public class MemoryLoader {
    Queue<Process> readyQueue = new LinkedList<>();
    private final int maxSize = 2048;
    private int currentSize = 0;

    // Add this new method to reset and reload processes
    public boolean reloadReadyQueue(Queue<Process> jobQueue) {
        // Clear ready queue and reset current size
        readyQueue.clear();
        currentSize = 0;
        
        // Try to load new processes
        boolean loadedAny = false;
        Queue<Process> tempQueue = new LinkedList<>(jobQueue);
        
        for (Process process : tempQueue) {
            if (process.getMemoryRequired() + currentSize <= maxSize) {
                SystemCall.loadMemory(process, jobQueue, readyQueue);
                currentSize += process.getMemoryRequired();
                SystemCall.setProcessState(process, State.READY);
                loadedAny = true;
            }
        }
        
        return loadedAny; // Return true if any processes were loaded
    }

    // Modify the existing loadToMemory method to return boolean
    public boolean loadToMemory(Queue<Process> jobQueue) {
        return reloadReadyQueue(jobQueue);
    }

    public void removeProcess(Process process) {
        if (process != null) {
            currentSize -= process.getMemoryRequired();
        }
    }

    public void printReadyQueue() {
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
