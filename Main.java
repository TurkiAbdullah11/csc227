import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        JobReader j = new JobReader();
        MemoryLoader m = new MemoryLoader();
        Scheduler scheduler = new Scheduler(m);
        Scanner scanner = new Scanner(System.in);
        
        // Load and display initial job information
        j.read("Ourjob.txt");
        j.printJobs();
        System.out.println("====================================================");
        
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
            
            // Process all jobs with selected algorithm
            while (!j.jobQueue.isEmpty() || !m.readyQueue.isEmpty()) {
                if (m.readyQueue.isEmpty() && !j.jobQueue.isEmpty()) {
                    System.out.println("\nLoading next batch of processes...");
                    m.reloadReadyQueue(j.jobQueue);
                    m.printReadyQueue();
                    System.out.println("====================================================");
                }
                
                switch (choice) {
                    case 1:
                        if (!m.readyQueue.isEmpty()) {
                            System.out.println("\n[Running FCFS Scheduler]");
                            scheduler.FIFO(m.readyQueue);
                        }
                        break;
                    case 2:
                        if (!m.readyQueue.isEmpty()) {
                            final int timeQuantum = 7;
                            System.out.println("\n[Running Round Robin Scheduler (Quantum: 7ms)]");
                            scheduler.RoundRobin(m.readyQueue, timeQuantum);
                        }
                        break;
                    case 3:
                        if (!m.readyQueue.isEmpty()) {
                            System.out.println("\n[Running Priority Scheduler]");
                            scheduler.PriorityQueue(m.readyQueue);
                        }
                        break;
                    case 4:
                        // TODO: Implement comparison of all algorithms
                        System.out.println("Feature coming soon...");
                        break;
                    case 5:
                        System.out.println("Exiting simulator...");
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please select 1-5.");
                        continue;
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
            } else {
                j.read("Ourjob.txt");
                m.loadToMemory(j.jobQueue);
            }
        }
    }
}
