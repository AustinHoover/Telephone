package electrosphere.socket.codegen.utils;

import com.google.gson.Gson;
import electrosphere.socket.codegen.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Utilities {
    
    static final int maxReadFails = 3;
    static final int READ_TIMEOUT_DURATION = 5;
    public static String readBakedResourceToString(InputStream resourceInputStream){
        String rVal = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceInputStream));
            int failCounter = 0;
            boolean reading = true;
            StringBuilder builder = new StringBuilder("");
            while(reading){
                if(reader.ready()){
                    failCounter = 0;
                    int nextValue = reader.read();
                    if(nextValue == -1){
                        reading = false;
                    } else {
                        builder.append((char)nextValue);
                    }
                } else {
                    failCounter++;
                    if(failCounter > maxReadFails){
                        reading = false;
                    } else {
                        try {
                            TimeUnit.MILLISECONDS.sleep(READ_TIMEOUT_DURATION);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            rVal = builder.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return rVal;
    }
    
    
    
    public static <T>T loadObjectFromBakedJsonFile(String fileName, Class<T> className){
        T rVal = null;
        String rawJSON = Utilities.readBakedResourceToString(Main.class.getResourceAsStream(fileName));
        Gson gson = new Gson();
        rVal = gson.fromJson(rawJSON, className);
        return rVal;
    }
    
    
}
