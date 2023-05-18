import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Memory {
    HashMap<String, String> memory = new HashMap<String, String>();

    void unloadProcess(Process process) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("Process" + memory.keySet().toArray()[process.memoryStart]));

        for (int i = process.memoryStart; i < process.memoryEnd; i++){
            String key = (String)memory.keySet().toArray()[i];
            writer.write(memory.keySet().toArray()[i] + " " + memory.get(key));
        }

        writer.close();
    }
    void loadProcess(Process process){
        
    }
    void runProcess(Object object){

    }
}
