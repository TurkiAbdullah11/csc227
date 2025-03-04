import java.io.*;
import java.util.*;

class JobReader {
    Queue<Process> jobQueue = new LinkedList<>();
    static int count = 0;
    final int maxProcess = 30;

    public Queue<Process> read(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("[")) continue;

                if (count < maxProcess) {
                    String[] parts = line.split("[:;]");
                    if (parts.length < 4) {
                        System.out.println("Invalid line: " + line);
                        continue;
                    }

                    int id = Integer.parseInt(parts[0].trim());
                    int burstTime = Integer.parseInt(parts[1].trim());
                    int priority = Integer.parseInt(parts[2].trim());
                    int memoryRequired = Integer.parseInt(parts[3].trim());

                    Process process = SystemCall.createProcess(id, burstTime, priority, memoryRequired);
                    SystemCall.setProcessState(process,State.NEW); //When creating the process it's in NEW state

                    jobQueue.add(process);
                    count++;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }
    return  jobQueue;
    }

    public void printJobs() {
        System.out.println("[ JOB QUEUE ] Number of processes: " + JobReader.count);
        for (Process process : jobQueue) {
            System.out.println(process);
        }
    }
}
