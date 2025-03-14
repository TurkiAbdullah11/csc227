import java.util.Queue;

public class JobReaderThread extends Thread {
    JobReader jr;
    String file;
    
    public JobReaderThread(JobReader jr, String file) {
        this.jr = jr;
        this.file = file;
    }

    @Override
    public void run() {
        System.out.println("File reader thread starting...");
        jr.read(file);
        jr.printJobs();
        System.out.println("File reader thread finished!");
    }
}