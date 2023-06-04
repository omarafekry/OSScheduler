
public class Process {
    int id;
    Memory memory;
    OperatingSystem os;
    boolean isDone = false;
    public Process(int id, Memory memory, OperatingSystem os){
        this.id = id;
        this.memory = memory;
        this.os = os;
    }
    public void run() {
        int pc = memory.getProcessPC(id);
        String instruction = memory.getProcessLine(id, pc);
        System.out.println("Currently executing instruction " + pc + ": " + instruction);
        String[] words = instruction.split(" ");
        switch(words[0]){
            case "print":
                os.printData(getValue1(words));
                break;
            case "assign":
                os.writeToMemory(id, getValue1(words), getValue2(words));
                break;
            case "writeFile":
                os.writeToDisk(System.getProperty("user.dir") + "\\" + getValue1(words), getValue2(words));
                break;
            case "printFromTo":
                int value1 = Integer.parseInt(getValue1(words)), value2 = Integer.parseInt(getValue2(words));
                for (int i = value1; i <= value2; i++)
                    os.printData("" + i);
                break;
        }

        memory.setProcessPC(id, pc + 1);
        if (memory.getProcessPC(id) == memory.getProcessLength(id)){
            memory.setProcessState(id, "Finished");
            isDone = true;
        }
    }
    private String getValue2(String[] parts) {
        int value2Start = 0;
        boolean foundFirst = false;
        for (int i = 1; i < parts.length; i++) 
            if (!parts[i].equals("input") && !parts[i].equals("readFile") && !foundFirst)
                foundFirst = true;
            else if (foundFirst){
                value2Start = i;
                break;
            }
        

        switch(parts[value2Start]){
            case "input":
                return os.readInput();
            case "readFile":
                return os.readFromDisk(System.getProperty("user.dir") + "\\" + parts[value2Start+1]);
            default:
                String value = os.readFromMemory(id, parts[value2Start]);
                if (value == null) 
                    return parts[value2Start];
                return value;
        }
    }
    public String getValue1(String[] parts){
        switch(parts[1]){
            case "input":
                return os.readInput();
            case "readFile":
                return os.readFromDisk(System.getProperty("user.dir") + "\\" + parts[2]);
            default:
                String value = os.readFromMemory(id, parts[1]);
                if (value == null) 
                    return parts[1];
                return value;
        }
    }
}
