import java.util.LinkedList;
import java.util.Scanner;
import java.io.*;

public class OperatingSystem {
    LinkedList<Process> readyQueue = new LinkedList<Process>();;
    Memory memory = new Memory();
    int programCount = 1;
    boolean doneReadingPrograms = false;
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args){
        OperatingSystem os = new OperatingSystem();
        os.run();
    }

    public void run(){
        while(true){
            System.out.println();
            System.out.println();
            if (!doneReadingPrograms) loadProgramToMemory("Program_" + programCount + ".txt");
            if (readyQueue.isEmpty()) return;
            printQueue();
            Process process = readyQueue.removeFirst();
            memory.prepareProcess(process);
            System.out.println("Currently executing process " + process.id);
            //run process here and set isDone property
            for (int i = 0; i < 2 && !process.isDone; i++){
                process.run();
                memory.printMemory();
            }
            

            if (!process.isDone) readyQueue.addLast(process);
            else memory.removeProcess(process);
        }
    }
    public void printQueue(){
        int qsize = readyQueue.size();
            System.out.print("Ready Queue:");
            for (int i = 0; i < qsize; i++) {
                Process p = readyQueue.removeFirst();
                System.out.print(" " + p.id);
                readyQueue.addLast(p);
            }
            System.out.println();
    }
    public void loadProgramToMemory(String filename){
        File programFile = new File(filename);
        if (!programFile.exists()){
            doneReadingPrograms = true;
            return;
        }
        Process process = new Process(programCount, memory, this);
        memory.addProcess(programFile, process);
        programCount++;
        readyQueue.addFirst(process);
    }
    
    public String readInput(){
        String input = sc.nextLine();
        return input;
    }
    public void printData(String data){
        System.out.println(data);
    }
    public String readFromDisk(String filePath){
        Scanner sc = new Scanner(filePath);
        String line = sc.nextLine();
        sc.close();
        return line;
    }
    public void writeToDisk(String filePath, String data){
        File file = new File(filePath);
        try {
            if (!file.exists())
                file.createNewFile();
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(data);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFromMemory(int id, String var){
        return memory.getProcessVariable(id, var);
    }
    public void writeToMemory(int id, String var, String data){
        memory.setProcessVariable(id, var, data);
    }
    
}