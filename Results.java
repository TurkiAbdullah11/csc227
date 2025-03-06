public class Results { // Results class to hold the averages
    double avgWaitingTime;
    double avgTurnaroundTime;

    public Results(double avgWaitingTime, double avgTurnaroundTime) {
        this.avgWaitingTime = avgWaitingTime;
        this.avgTurnaroundTime = avgTurnaroundTime;
    }

    public double getAvgWaitingTime() {
        return avgWaitingTime;
    }

    public double getAvgTurnaroundTime() {
        return avgTurnaroundTime;
    }
}