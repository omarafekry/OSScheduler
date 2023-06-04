import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Memory {
    final int memorySize = 40;
    final int pcbSize = 4;
    final int noOfVariables = 3;
    LinkedHashMap<String, String> memory = new LinkedHashMap<String, String>();
    LinkedHashSet<Integer> loadedProcesses = new LinkedHashSet<Integer>();
    

    private void unloadProcess(){
        int id = loadedProcesses.iterator().next();
        BufferedWriter writer = null;
        String MB = memory.get("" + id + "MB");
        int memoryStart = Integer.parseInt(MB.split(" ")[0]);
        int memoryEnd = Integer.parseInt(MB.split(" ")[1]);

        try{
            writer = new BufferedWriter(new FileWriter("Process" + id + ".txt"));
            String key = (String)memory.keySet().toArray()[0];
            writer.write(memory.keySet().toArray()[0] + "=" + memory.get(key));
            
            for (int i = memoryStart + 1; i <= memoryEnd; i++){
                key = (String)memory.keySet().toArray()[i];
                writer.write("\n" + memory.keySet().toArray()[i] + "=" + memory.get(key));
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        int numberOfLines = memoryEnd - memoryStart - (pcbSize + noOfVariables) + 1;

        memory.remove("" + id + "PID");
        memory.remove("" + id + "State");
        memory.remove("" + id + "PC");
        memory.remove("" + id + "MB");
        memory.remove("" + id + "VAR1");
        memory.remove("" + id + "VAR2");
        memory.remove("" + id + "VAR3");
        for (int i = 0; i < numberOfLines; i++)
            memory.remove("" + id + "Line" + i);

        loadedProcesses.remove((Integer) id);

        //decrease memory boundaries of all other processes
        for (Integer pid : loadedProcesses) {
            String value = memory.get(pid + "MB");
            int newStart = Integer.parseInt(value.split(" ")[0]) - (noOfVariables + pcbSize + numberOfLines);
            int newEnd = Integer.parseInt(value.split(" ")[1]) - (noOfVariables + pcbSize + numberOfLines);
            memory.put(pid + "MB", newStart + " " + newEnd);
        }

        System.out.println("Unloaded Process " + id + " onto disk");
    }

    private void loadProcess(int id){
        File processFile = new File("Process" + id + ".txt");
        long numberOfLines = 0;
        FileInputStream fis = null;
        try {
            numberOfLines = Files.lines(processFile.toPath()).count();
            fis = new FileInputStream(processFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (numberOfLines > memorySize - (pcbSize + noOfVariables)) //process cannot fit in memory
            throw new IndexOutOfBoundsException("Not enough space in memory");

        while (memorySize - memory.size() < numberOfLines + pcbSize + noOfVariables) // not enough space remaining
            unloadProcess();
        
        Scanner sc = new Scanner(fis);
        int memoryStart = memory.size();
        for (int i = 0; i < numberOfLines; i++){
            String line = sc.nextLine();
            String key = line.split("=")[0];
            String value = "";
            if (line.split("=").length > 1)
                value = line.split("=")[1];
            memory.put(key, value);
        }
        int memoryEnd = memory.size() - 1;

        //update memory boundaries
        memory.put(id + "MB", memoryStart + " " + memoryEnd);

        sc.close();

        loadedProcesses.add(id);
        System.out.println("Loaded Process " + id + " into memory");
    }

    public void prepareProcess(Process process){
        if (!loadedProcesses.contains(process.id))
            loadProcess(process.id);
    }
    
    public Process addProcess(File programFile, Process process){
        int id = process.id;
        long numberOfLines = 0;
        FileInputStream fis = null;
        try {
            numberOfLines = Files.lines(programFile.toPath()).count();
            fis = new FileInputStream(programFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (numberOfLines > memorySize - (pcbSize + noOfVariables)) //process cannot fit in memory
            throw new IndexOutOfBoundsException("Not enough space in memory");

        while (memorySize - memory.size() < numberOfLines + pcbSize + noOfVariables) // not enough space remaining
            unloadProcess();
        
        int nextLineInMemory = memory.size();
        memory.put("" + id + "PID", "" + id);
        memory.put("" + id + "State", "Ready");
        memory.put("" + id + "PC", "" + 0);
        memory.put("" + id + "MB", nextLineInMemory + " " + (nextLineInMemory + pcbSize + noOfVariables + numberOfLines - 1));
        memory.put("" + id + "VAR1", "");
        memory.put("" + id + "VAR2", "");
        memory.put("" + id + "VAR3", "");

        Scanner sc = new Scanner(fis);
        for (int i = 0; i < numberOfLines; i++) 
            memory.put("" + id + "Line" + i, sc.nextLine());
        sc.close();

        loadedProcesses.add(id);
        System.out.println("Added Process " + id + " to memory");
        return process;

    }
    
    public void removeProcess(Process process) {
        if (!loadedProcesses.contains(process.id)){
            new File("Process" + process.id + ".txt").delete();
            return;
        }
        
        String MB = memory.get("" + process.id + "MB");
        int memoryStart = Integer.parseInt(MB.split(" ")[0]);
        int memoryEnd = Integer.parseInt(MB.split(" ")[1]);
        int numberOfLines = memoryEnd - memoryStart - (pcbSize + noOfVariables) + 1;
        int id = process.id;

        memory.remove("" + id + "PID");
        memory.remove("" + id + "State");
        memory.remove("" + id + "PC");
        memory.remove("" + id + "MB");
        memory.remove("" + id + "VAR1");
        memory.remove("" + id + "VAR2");
        memory.remove("" + id + "VAR3");
        for (int i = 0; i < numberOfLines; i++) 
            memory.remove("" + id + "Line" + i);

        loadedProcesses.remove((Integer)id);
    }

    public String getProcessState(int id){
        return memory.get("" + id + "State");
    }

    public void setProcessState(int id, String state){
        memory.replace("" + id + "State", state);
    }
    
    public int getProcessLength(int id){
        String MB = memory.get("" + id + "MB");
        int memoryStart = Integer.parseInt(MB.split(" ")[0]);
        int memoryEnd = Integer.parseInt(MB.split(" ")[1]);
        return memoryEnd - memoryStart - (pcbSize + noOfVariables) + 1;
    }

    public String getProcessLine(int id, int line){
        return memory.get("" + id + "Line" + line);
    }

    public int getProcessPC(int id){
        return Integer.parseInt(memory.get("" + id + "PC"));
    }

    public void setProcessPC(int id, int pc){
        memory.replace("" + id + "PC", "" + pc);
    }

    public void setProcessVariable(int id, String variable, String value){
        for (int i = 1; i <= 3; i++) {
            if (memory.get("" + id + "VAR" + i).equals("")){ //empty variable slot
                memory.put("" + id + "VAR" + i, variable + " " + value);
                break;
            }
            else if (memory.get("" + id + "VAR" + i).split(" ")[0].equals(variable)) //found variable name
                memory.replace("" + id + "VAR" + i, variable + " " + value);
        }
    }

    public String getProcessVariable(int id, String variable){
        for (int i = 1; i <= 3; i++) {
            if (memory.get("" + id + "VAR" + i).split(" ")[0].equals(variable)) //found variable name
                return memory.get("" + id + "VAR" + i).split(" ")[1];
        }
        return null;
    }
    
    public void printMemory(){
        Iterator<String> it = memory.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            String key = it.next();
            System.out.println(i + ": " + key + " = " + memory.get(key));
        }
    }
}