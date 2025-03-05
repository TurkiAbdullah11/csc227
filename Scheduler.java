import java.util.Queue;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Scheduler {
    private MemoryLoader memoryLoader;

    public Scheduler(MemoryLoader memoryLoader) {
        this.memoryLoader = memoryLoader;
    }

    public void FIFO(Queue<Process> readyQueue) {
        System.out.println("Printing FIFO:");
        int totalTime = 0;
        
        while (!readyQueue.isEmpty()) {
            Process currentProcess = readyQueue.poll();
            memoryLoader.removeProcess(currentProcess);
            
            // Print process info before running
            System.out.println(String.format("Process ID: %d, Burst Time: %d, Priority: %d, Memory: %d → RUNNING",
                currentProcess.getId(),
                currentProcess.getBurstTime(),
                currentProcess.getPriority(),
                currentProcess.getMemoryRequired()));
                
            SystemCall.setProcessState(currentProcess, State.RUNNING);
            
            try {
                long startTime = System.nanoTime();
                Thread.sleep(currentProcess.getBurstTime());
                long executionTime = (System.nanoTime() - startTime) / 1_000_000;
                totalTime += executionTime;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            SystemCall.terminateProcess(currentProcess);
        }
        
        System.err.println("Total time taken to execute all processes: " + totalTime + " m/s.");
    }

    public void RoundRobin(Queue<Process> readyQueue, int timeQuantum) {
        System.out.println("Printing Round Robin (Time Quantum = " + timeQuantum + " ms):");
        int totalTime = 0;
        
        while (!readyQueue.isEmpty()) {
            Process currentProcess = readyQueue.poll();
            memoryLoader.removeProcess(currentProcess);
            SystemCall.setProcessState(currentProcess, State.RUNNING);
            
            int remainingBurst = currentProcess.getBurstTime();
            int executeTime = Math.min(remainingBurst, timeQuantum);
            
            // First time process runs - show full info
            if (remainingBurst == currentProcess.getInitialBurstTime()) {
                System.out.println(String.format(
                    "Process ID: %d, Burst Time: %d, Priority: %d, Memory: %d → RUNNING for %d ms%s",
                    currentProcess.getId(),
                    currentProcess.getBurstTime(),
                    currentProcess.getPriority(),
                    currentProcess.getMemoryRequired(),
                    executeTime,
                    (executeTime == remainingBurst) ? " (COMPLETED)" : ""));  // Add this check
            } else {
                boolean isCompleting = remainingBurst <= executeTime;
                System.out.println(String.format(
                    "Process ID: %d, Remaining Burst Time: %d → RUNNING for %d ms%s",
                    currentProcess.getId(),
                    remainingBurst,
                    executeTime,
                    isCompleting ? " (COMPLETED)" : ""));
            }
            
            try {
                long startTime = System.nanoTime();
                Thread.sleep(executeTime);
                long executionTime = (System.nanoTime() - startTime) / 1_000_000;
                totalTime += executionTime;
                
                remainingBurst -= executeTime;
                currentProcess.setBurstTime(remainingBurst);
                
                if (remainingBurst > 0) {
                    SystemCall.setProcessState(currentProcess, State.READY);
                    readyQueue.add(currentProcess);
                } else {
                    SystemCall.terminateProcess(currentProcess);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.err.println("Total time taken to execute all processes: " + totalTime + " m/s.");
    }

    public void PriorityQueue(Queue<Process> readyQueue) {
        System.out.println("Printing Priority Queue (Preemptive with Aging):");
        int totalTime = 0;
        final int AGING_THRESHOLD = 10;    
        final int PRIORITY_BOOST = 2;     
        final int MAX_PRIORITY = 8;      
        
        PriorityQueue<Process> priorityQueue = new PriorityQueue<>((p1, p2) -> 
            p2.getPriority() - p1.getPriority());
        
        priorityQueue.addAll(readyQueue);
        readyQueue.clear();
        
        while (!priorityQueue.isEmpty()) {
            Process currentProcess = priorityQueue.poll();
            memoryLoader.removeProcess(currentProcess);
            SystemCall.setProcessState(currentProcess, State.RUNNING);
            
            // Apply aging to waiting processes
            for (Process waitingProcess : priorityQueue) {
                waitingProcess.setWaitingTime(waitingProcess.getWaitingTime() + 1);
                
                if (waitingProcess.getWaitingTime() >= AGING_THRESHOLD) {
                    int newPriority = Math.min(waitingProcess.getPriority() + PRIORITY_BOOST, MAX_PRIORITY);
                    if (newPriority > waitingProcess.getPriority()) {
                        System.out.println("Aging: Process " + waitingProcess.getId() + 
                                        " priority increased from " + waitingProcess.getPriority() + 
                                         " to " + newPriority);
                        waitingProcess.setPriority(newPriority);
                        waitingProcess.setWaitingTime(0);
                    }
                }
            }
            
            currentProcess.setWaitingTime(0);
            
            System.out.println(String.format("Process ID: %d, Priority: %d, Burst Time: %d, Memory: %d → RUNNING",
                currentProcess.getId(),
                currentProcess.getPriority(),
                currentProcess.getBurstTime(),
                currentProcess.getMemoryRequired()));
            
            try {
                long startTime = System.nanoTime();
                Thread.sleep(currentProcess.getBurstTime()); // Run for full burst time
                long executionTime = (System.nanoTime() - startTime) / 1_000_000;
                totalTime += executionTime;
                
                SystemCall.terminateProcess(currentProcess);
                System.out.println("Process ID: " + currentProcess.getId() + " completed execution");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.err.println("Total time taken to execute all processes: " + totalTime + " m/s.");
    }
}


