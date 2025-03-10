
public class Process {
private int id;
private int burstTime;
private int priority;
private int memoryRequired;
private int waitingTime;
private int turnaroundTime;
private State state;
Process(int id , int burstTime, int priority,int memoryRequired){
	this.id=id;
	this.burstTime=burstTime;
	this.priority=priority;
	this.memoryRequired=memoryRequired;
}
public String toString() {
    return "Process ID: " + id + ", Burst Time: " + burstTime +
           ", Priority: " + priority + ", Memory: " + memoryRequired;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getBurstTime() {
	return burstTime;
}
public void setBurstTime(int burstTime) {
	this.burstTime = burstTime;
}
public int getPriority() {
	return priority;
}
public void setPriority(int priority) {
	this.priority = priority;
}
public int getMemoryRequired() {
	return memoryRequired;
}
public void setMemoryRequired(int memoryRequired) {
	this.memoryRequired = memoryRequired;
}
public int getWaitingTime() {
	return waitingTime;
}
public void setWaitingTime(int waitingTime) {
	this.waitingTime = waitingTime;
}
public int getTurnaroundTime() {
	return turnaroundTime;
}
public void setTurnaroundTime(int turnaroundTime) {
	this.turnaroundTime = turnaroundTime;
}
public State getState() {
		return state;
	}
public void setState(State state) {
		this.state = state;
	}
}
