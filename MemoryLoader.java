import java.util.*;

public class MemoryLoader {
    Queue<Process> readyQueue = new LinkedList<>();
    private final int maxSize = 2048;
    private int currentSize = 0;

    // load Processes into ReadyQueue
    public Queue<Process> loadToMemory(Queue<Process> jobQueue) {
        Queue<Process> tempQueue = new LinkedList<>(jobQueue);
        for (Process process : tempQueue) {
            if (process.getMemoryRequired() + currentSize <= maxSize) {
                SystemCall.loadMemory(process,jobQueue,readyQueue);
                currentSize += process.getMemoryRequired();

                SystemCall.setProcessState(process,State.READY); // NEW → READY when loading process into readyQueue
            } else {
                //System.out.println("Not enough memory for process " + process.getId());
            }
        }
        return readyQueue;
    }

    public void printReadyQueue() {
        System.out.println("[ READY QUEUE ] ; Used memory: " + currentSize + " / 2048 MB");
        for (Process process : readyQueue) {
            System.out.println(process);
        }
    }
}
