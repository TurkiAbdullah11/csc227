public class Main {
    public static void main(String[] args) {

        JobReader j = new JobReader();
        j.read("Ourjob.txt");
        j.printJobs();

        System.out.println("====================================================");

        MemoryLoader m = new MemoryLoader();
        m.loadToMemory(j.jobQueue);
        m.printReadyQueue();

        System.out.println("====================================================");

        j.printJobs();
    }
}
