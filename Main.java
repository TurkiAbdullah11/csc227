import java.util.Scanner;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        JobReader j = new JobReader();
        MemoryLoader m = new MemoryLoader();
        Scheduler scheduler = new Scheduler(m);
        Scanner scanner = new Scanner(System.in);
        
        // Display menu
        while (true) {
            System.out.println("========================================");
            System.out.println("      CPU Scheduling Simulator");
            System.out.println("========================================");
            System.out.println("Select Scheduling Algorithm:");
            System.out.println("[1] First-Come-First-Serve (FCFS)");
            System.out.println("[2] Round Robin (RR) - Quantum 7ms");
            System.out.println("[3] Priority Scheduling (Preemptive)");
            System.out.println("[4] Run All & Compare Results");
            System.out.println("[5] Exit");
            System.out.print("Choice: ");
            
            int choice = scanner.nextInt();
            
            if (choice == 5) {
                System.out.println("Exiting simulator...");
                scanner.close();
                System.exit(0);
            }
            
            // Start file reader thread instead of directly loading
            // j.read("Ourjob.txt");
            // j.printJobs();
            JobReaderThread fileThread = new JobReaderThread(j, "C:/Users/turki/Downloads/Ourjob.txt");
            fileThread.start();
            
            // Wait for file reading to finish
            try {
                fileThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("====================================================");
            
            // Start memory loader thread
            // m.loadToMemory(j.jobQueue);
            MemoryLoaderThread memThread = new MemoryLoaderThread(m, j.jobQueue);
            memThread.start();
            
            if (choice == 4) {
                // For comparing all algorithms
                scheduler.compareAllAlgorithms(j, m);
                
                // Stop the memory thread
                memThread.stopThread();
                try {
                    memThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // Process jobs with selected algorithm in main thread
                while (true) {
                    // Check if ready queue has processes
                    if (!m.readyQueue.isEmpty()) {
                        // Get the processes
                        LinkedList<Process> processes = new LinkedList<>();
                        
                        // Copy processes to a new list
                        for (Process p : m.readyQueue) {
                            processes.add(p);
                        }
                        
                        // Clear the ready queue
                        m.readyQueue.clear();
                        
                        // Run the right algorithm
                        switch (choice) {
                            case 1:
                                System.out.println("\nRunning FCFS now:");
                                scheduler.FIFO(processes);
                                break;
                            case 2:
                                System.out.println("\nRunning Round Robin now:");
                                scheduler.RoundRobin(processes, 7);
                                break;
                            case 3:
                                System.out.println("\nRunning Priority now:");
                                scheduler.PriorityQueue(processes);
                                break;
                            default:
                                System.out.println("Invalid choice");
                                break;
                        }
                    }
                    
                    // Check if we're done
                    if (j.jobQueue.isEmpty() && m.readyQueue.isEmpty()) {
                        break;
                    }
                    
                    // Wait a bit before checking again
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                // Stop the memory thread
                memThread.stopThread();
                try {
                    memThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // All jobs completed
            System.out.println("\nAll processes have been executed successfully!");
            System.out.println("[1] Return to main menu");
            System.out.println("[2] Exit");
            System.out.print("Choice: ");
            
            int completionChoice = scanner.nextInt();
            if (completionChoice == 2) {
                System.out.println("Exiting simulator...");
                scanner.close();
                System.exit(0);
            }
        }
    }
}