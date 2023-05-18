import java.util.LinkedList;
import java.util.List;

public class OperatingSystem {
    LinkedList<Process> readyQueue;
    Memory memory;
    int programCount = 1;
    List<Process> processes;
    int roundRobin = 1;
    boolean doneReadingPrograms = false;
    public OperatingSystem(){
        readyQueue = new LinkedList<Process>();
    }
    public static void main(String[] args){
        OperatingSystem os = new OperatingSystem();
        os.run();
    }
    public void run(){
        while(true){
            if (!doneReadingPrograms)
                loadProgramToMemory("Program" + programCount);
            if (readyQueue.isEmpty())
                return;
            memory.runProcess(processes.get(roundRobin));
            if (!processes.get(roundRobin).isDone){
                readyQueue.addLast(processes.get(roundRobin));
            }
            if (roundRobin < readyQueue.size())
                roundRobin++;
            
        }
    }
    public void loadProgramToMemory(String filename){

    }
}