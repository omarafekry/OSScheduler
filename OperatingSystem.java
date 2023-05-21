import java.util.LinkedList;
import java.io.*;

public class OperatingSystem {
    LinkedList<Process> readyQueue = new LinkedList<Process>();;
    Memory memory = new Memory();
    int programCount = 1;
    boolean doneReadingPrograms = false;

    public static void main(String[] args){
        OperatingSystem os = new OperatingSystem();
        os.run();
    }

    public void run(){
        while(true){

            if (!doneReadingPrograms) loadProgramToMemory("Program_" + programCount + ".txt");
            if (readyQueue.isEmpty()) return;

            Process process = readyQueue.removeFirst();
            memory.prepareProcess(process);

            //run process here and set isDone property

            if (!process.isDone) readyQueue.addLast(process);
            else memory.removeProcess(process);
        }
    }

    public void loadProgramToMemory(String filename){
        File programFile = new File(filename);
        if (!programFile.exists()){
            doneReadingPrograms = true;
            return;
        }
        Process process = memory.addProcess(programFile, programCount);
        programCount++;
        readyQueue.addLast(process);
    }

    
}