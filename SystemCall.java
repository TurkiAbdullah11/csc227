import java.util.*;

public class SystemCall {

    public static Process createProcess(int id, int burstTime, int priority, int memoryRequired) {
        return new Process(id, burstTime, priority, memoryRequired);
    }

    public static void loadMemory(Process process, Queue<Process> jobQueue, Queue<Process> readyQueue) {
        if (process == null) {
            System.out.println("Error: Attempted to load a null process.");
            return;
        }

        //System.out.println("Loading Process ID " + process.getId() + " into readyQueue...");
        jobQueue.remove(process);
        JobReader.count --;
        readyQueue.add(process);
    }

    public static int getCurrentProcessID(Process process) {
        return process.getId();
    }

    public static void setProcessState(Process process, State state) {
        process.setState(state);
        //System.out.println("Process ID: " + SystemCall.getCurrentProcessID(process) + " transitioned to state: " + state);
    }
}
