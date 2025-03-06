import java.util.*;

public class Scheduler {
    private MemoryLoader memoryLoader;

    public Scheduler(MemoryLoader memoryLoader) {
        this.memoryLoader = memoryLoader;
    }

    public void FIFO(Queue<Process> readyQueue) {
        int currentTime = 0;
        long totalTime = 0; // Total execution time for all processes
        List<Process> processes = new ArrayList<>();
        List<GanttEntry> ganttChart = new ArrayList<>();

        
        while (!readyQueue.isEmpty()) {
            Process currentProcess = readyQueue.poll();
            memoryLoader.removeProcess(currentProcess);
            SystemCall.setProcessState(currentProcess, State.RUNNING);

            int startTime = currentTime;
            int endTime = startTime + currentProcess.getBurstTime();

            try {
                long startExecutionTime = System.nanoTime();
                Thread.sleep(currentProcess.getBurstTime());
                long executionTime = (System.nanoTime() - startExecutionTime) / 1_000_000;
                totalTime += executionTime;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            currentProcess.setWaitingTime(startTime);
            currentProcess.setTurnaroundTime(endTime);

            // Add to Gantt chart
            ganttChart.add(new GanttEntry(currentProcess.getId(), startTime, endTime));
            currentTime = endTime;
            // Add to processes list for statistics
            processes.add(currentProcess);

            SystemCall.terminateProcess(currentProcess);
        }

        printOutput("First-Come-First-Serve (FCFS)", processes, ganttChart);

        //System.err.println("Total time taken to execute all processes: " + totalTime + " m/s.");
    }

    public void RoundRobin(Queue<Process> readyQueue, int timeQuantum) {
        //System.out.println("Printing Round Robin (Time Quantum = " + timeQuantum + " ms):");
        int currentTime = 0;
        int totalTime = 0;
        List<Process> processes = new ArrayList<>();
        List<GanttEntry> ganttChart = new ArrayList<>();
        
        while (!readyQueue.isEmpty()) {
            Process currentProcess = readyQueue.poll();
            memoryLoader.removeProcess(currentProcess);
            SystemCall.setProcessState(currentProcess, State.RUNNING);

            int remainingBurst = currentProcess.getBurstTime();
            int executeTime = Math.min(remainingBurst, timeQuantum);

            int startTime = currentTime;
            int endTime = startTime + executeTime;

            // First time process runs - show full info
//            if (remainingBurst == currentProcess.getInitialBurstTime()) {
//                System.out.println(String.format(
//                    "Process ID: %d, Burst Time: %d, Priority: %d, Memory: %d → RUNNING for %d ms%s",
//                    currentProcess.getId(),
//                    currentProcess.getBurstTime(),
//                    currentProcess.getPriority(),
//                    currentProcess.getMemoryRequired(),
//                    executeTime,
//                    (executeTime == remainingBurst) ? " (COMPLETED)" : ""));  // Add this check
//            } else {
//                boolean isCompleting = remainingBurst <= executeTime;
//                System.out.println(String.format(
//                    "Process ID: %d, Remaining Burst Time: %d → RUNNING for %d ms%s",
//                    currentProcess.getId(),
//                    remainingBurst,
//                    executeTime,
//                    isCompleting ? " (COMPLETED)" : ""));
//            }
            
            try {
                long startExecutionTime  = System.nanoTime();
                Thread.sleep(executeTime);
                long executionTime = (System.nanoTime() - startExecutionTime ) / 1_000_000;
                totalTime += executionTime;
                
                remainingBurst -= executeTime;
                currentProcess.setBurstTime(remainingBurst);

                currentProcess.setWaitingTime(startTime);
                currentProcess.setTurnaroundTime(endTime);

                ganttChart.add(new GanttEntry(currentProcess.getId(), startTime, endTime));
                currentTime = endTime;

                processes.add(currentProcess);

                if (remainingBurst > 0) {
                    SystemCall.setProcessState(currentProcess, State.READY);
                    readyQueue.add(currentProcess);
                } else {
                    // Add to processes list for statistics
                    processes.add(currentProcess);

                    SystemCall.terminateProcess(currentProcess);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        printOutput("Round Robin (Quantum = 7ms)", processes, ganttChart);

        //System.err.println("Total time taken to execute all processes: " + totalTime + " m/s.");
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

    public void printOutput(String algorithmName, List<Process> processes, List<GanttEntry> ganttChart) {
        System.out.println("========================================");
        System.out.println("     " + algorithmName);
        System.out.println("========================================");

        // Print process execution order
        System.out.print("Process Execution Order:\n");
        Iterator<GanttEntry> iterator = ganttChart.iterator();
        while (iterator.hasNext()) {
            GanttEntry entry = iterator.next();
            System.out.print("P" + entry.getProcessId());
            if (iterator.hasNext()) {
                System.out.print(" -> ");
            }
        }
        System.out.println(); // Move to the next line after printing all processes

        // Print execution timeline
        System.out.println("Execution Timeline:");
        System.out.println("Time | Process | Start Burst | Stop Burst");
        System.out.println("-----------------------------------------");
        for (GanttEntry entry : ganttChart) {
            System.out.printf("%-4d | P%-6d | %-11d | %-9d\n",
                    entry.getStartTime(), entry.getProcessId(), entry.getStartTime(), entry.getEndTime());
        }

        printGanttChart(ganttChart);

        // Print average waiting and turnaround times
        double avgWaitingTime = calculateAverageWaitingTime(processes);
        double avgTurnaroundTime = calculateAverageTurnaroundTime(processes);
        System.out.printf("Average Waiting Time: %.1f ms\n", avgWaitingTime);
        System.out.printf("Average Turnaround Time: %.1f ms\n", avgTurnaroundTime);

        System.out.println("========================================");
    }

    public void printGanttChart(List<GanttEntry> ganttChart) {
        if (ganttChart.isEmpty()) {
            System.out.println("No processes executed.");
            return;
        }

        System.out.println("Gantt Chart:");

        // Print the top line (process blocks)
        System.out.print("|");
        for (GanttEntry entry : ganttChart) {
            System.out.printf(" P%-2d |", entry.getProcessId());
        }
        System.out.println();
        int endTime = 0;
        System.out.print(endTime);
        for (GanttEntry entry : ganttChart) {
            int digits = String.valueOf(endTime).length();
            endTime = entry.getEndTime();
            int spacing = 6 - digits;
            System.out.printf("%" + (spacing + String.valueOf(endTime).length()) + "d", endTime);
        }
        System.out.println();
    }

    private double calculateAverageWaitingTime(List<Process> processes) {
        double totalWaitingTime = 0;
        for (Process process : processes) {
            totalWaitingTime += process.getWaitingTime();
        }
        return totalWaitingTime / processes.size();
    }

    private double calculateAverageTurnaroundTime(List<Process> processes) {
        double totalTurnaroundTime = 0;
        for (Process process : processes) {
            totalTurnaroundTime += process.getTurnaroundTime();
        }
        return totalTurnaroundTime / processes.size();
    }
}


