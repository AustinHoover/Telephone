package electrosphere.socket.codegen;

import com.google.gson.Gson;
import electrosphere.socket.codegen.model.Category;
import electrosphere.socket.codegen.model.ConfigFile;
import electrosphere.socket.codegen.model.Data;
import electrosphere.socket.codegen.model.MessageType;
import electrosphere.socket.codegen.utils.Utilities;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class Main {
    public static void main(String args[]){
        File f = new File("./template.json");
        Gson gson = new Gson();
        try {
            ConfigFile config = gson.fromJson(Files.newBufferedReader(f.toPath()), ConfigFile.class);
            
            for(Category cat : config.getCategories()){
                for(MessageType msg : cat.getMessageTypes()){
                    for(String data : msg.getData()){
                        System.out.println(cat.getCategoryName() + " - " + msg.getMessageName() + " - " + data);
                    }
                }
            }
            recursiveDeletePath(config.getOutputPath());
            writeByteStreamUtils(config);
            writeTypeBytesClass(config);
            writeNetworkParserClass(config);
            writeNetworkMessageClass(config);
            for(Category cat : config.getCategories()){
                createMessageClassForCategory(config,cat);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    static void recursiveDeletePath(String path){
        File f = new File(path);
        if(f.isDirectory()){
            for(String child : f.list()){
                recursiveDeletePath(path + "/" + child);
            }
            try {
                Files.delete(f.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Files.delete(f.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    static void writeByteStreamUtils(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/util/";
        String fullOutputPath = fullOutputDirectory + "ByteStreamUtils.java";
        //package header
        String fullFile = "package " + config.getPackageName() + ";\n\n";
        
        //content
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/ByteStreamUtils.txt"));
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), fullFile.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    static void writeTypeBytesClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "TypeBytes.java";
        
        //package header
        String fullFile = "package " + config.getPackageName() + ";\n\n";
        
        //intro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/TypeBytesFirstPart.txt"));
        
        //add type bytes for categories
        fullFile = fullFile + "/*\nMessage categories\n*/\n";
        int incrementer = 0;
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "    public static final byte MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + " = " + incrementer + ";\n";
            incrementer++;
        }
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "    /*\n";
            fullFile = fullFile + "    " + cat.getCategoryName() + " subcategories\n";
            fullFile = fullFile + "    */\n";
            incrementer = 0;
            for(MessageType type : cat.getMessageTypes()){
                fullFile = fullFile + "    public static final byte " + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + " = " + incrementer + ";\n";
                incrementer++;
            }
            fullFile = fullFile + "    /*\n";
            fullFile = fullFile + "    " + cat.getCategoryName() + " packet sizes\n";
            fullFile = fullFile + "    */\n";
            for(MessageType type : cat.getMessageTypes()){
                fullFile = fullFile + "    public static final byte " + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + " = " + incrementer + ";\n";
            }
        }
        
        //outro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/TypeBytesSecondPart.txt"));
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), fullFile.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    static void writeNetworkParserClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/raw/";
        String fullOutputPath = fullOutputDirectory + "NetworkParser.java";
        
        //package header
        String fullFile = "package " + config.getPackageName() + ";\n\n";
        
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".util.ByteStreamUtils;\n";
        
        //content
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkParser.txt"));
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), fullFile.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    static void writeNetworkMessageClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "NetworkMessage.java";
        
        //package header
        String fullFile = "package " + config.getPackageName() + ";\n\n";
        
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".util.ByteStreamUtils;\n";
        
        //intro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkMessageFirstPart.txt"));
        
        //construct enum
        for(Category cat : config.getCategories()){
            fullFile = fullFile + cat.getCategoryName() + "_MESSAGE,\n";
        }
        
        //second part of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkMessageSecondPart.txt"));
        
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "                case TypeBytes.MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + ":\n";
            fullFile = fullFile + "                    secondByte = byteStream.get(1);\n";
            fullFile = fullFile + "                    switch(secondByte){\n";
            for(MessageType type : cat.getMessageTypes()){
                fullFile = fullFile + "                    case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
                fullFile = fullFile + "                        if(" + cat.getCategoryName() + "Message.canParseMessage(byteStream,secondByte)){\n";
                fullFile = fullFile + "                            rVal = " + cat.getCategoryName() + "Message.parse" + type.getMessageName() + "Message(byteStream);\n";
                fullFile = fullFile + "                        }\n";
                fullFile = fullFile + "                        break;\n";
            }
            fullFile = fullFile + "                }\n";
            fullFile = fullFile + "                break;\n";
        }
        
        //third part of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkMessageThirdPart.txt"));
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), fullFile.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    static void createMessageClassForCategory(ConfigFile config, Category cat){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "" + cat.getCategoryName() + "Message.java";
        
        //package header
        String fullFile = "package " + config.getPackageName() + ";\n\n";
        
        //imports
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".util.ByteStreamUtils;\n";
        fullFile = fullFile + "import java.util.LinkedList;\n\n";
        
        //class name
        fullFile = fullFile + "public class " + cat.getCategoryName() + "Message extends NetworkMessage {\n\n";
        
        //message type enum
        fullFile = fullFile + "    public enum " + cat.getCategoryName() + "MessageType {\n";
        for(MessageType type : cat.getMessageTypes()){
            fullFile = fullFile + "        " + type.getMessageName().toUpperCase() + ",\n";
        }
        fullFile = fullFile + "    }\n\n";
        
        //variables
        fullFile = fullFile + "    " + cat.getCategoryName() + "MessageType messageType;\n";
        for(Data variable : cat.getData()){
            switch(variable.getType()){
                case "FIXED_INT":
                    fullFile = fullFile + "    int " + variable.getName() + ";\n";
                    break;
                case "FIXED_FLOAT":
                    fullFile = fullFile + "    float " + variable.getName() + ";\n";
                    break;
                case "VAR_STRING":
                    fullFile = fullFile + "    String " + variable.getName() + ";\n";
                    break;
            }
        }
        fullFile = fullFile + "\n";
        
        //constructor
        fullFile = fullFile + "    " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType messageType){\n";
        fullFile = fullFile + "        this.type = MessageType." + cat.getCategoryName().toUpperCase() + "_MESSAGE;\n";
        fullFile = fullFile + "        this.messageType = messageType;\n";
        fullFile = fullFile + "    }\n\n";
        
        //getters and setters for each data
        for(Data variable : cat.getData()){
            switch(variable.getType()){
                case "FIXED_INT":
                    //getter
                    fullFile = fullFile + "    public int get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(int " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "FIXED_FLOAT":
                    //getter
                    fullFile = fullFile + "    public float get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(float " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "VAR_STRING":
                    //TODO
                    break;
            }
        }
        
        //strip packet header
        fullFile = fullFile + "    static void stripPacketHeader(LinkedList<Byte> byteStream){\n";
        fullFile = fullFile + "        byteStream.remove(0);\n";
        fullFile = fullFile + "        byteStream.remove(0);\n";
        fullFile = fullFile + "    }\n\n";
        
        //parse check function
        fullFile = fullFile + "    public static boolean canParseMessage(LinkedList<Byte> byteStream, byte secondByte){\n";
        fullFile = fullFile + "        boolean rVal = false;\n";
        fullFile = fullFile + "        switch(secondByte){\n";
        for(MessageType type : cat.getMessageTypes()){
            fullFile = fullFile + "            case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
            fullFile = fullFile + "                if(byteStream.size() >= TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_" + type.getMessageName().toUpperCase() + "_SIZE){\n";
            fullFile = fullFile + "                    return true;\n";
            fullFile = fullFile + "                } else {\n";
            fullFile = fullFile + "                    return false;\n";
            fullFile = fullFile + "                }\n";
        }
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "    }\n\n";
        
        //parse and construct functions
        for(MessageType type : cat.getMessageTypes()){
            //get all data types
            HashMap<String,String> typeMap = new HashMap();
            for(Data variable : cat.getData()){
                typeMap.put(variable.getName(), variable.getType());
            }
            //parse function
            fullFile = fullFile + "    public static " + cat.getCategoryName() + "Message parse" + type.getMessageName() + "Message(LinkedList<Byte> byteStream){\n";
            fullFile = fullFile + "        " + cat.getCategoryName() + "Message rVal = new " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType." + type.getMessageName().toUpperCase() + ");\n";
            fullFile = fullFile + "        stripPacketHeader(byteStream);\n";
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_INT":
                        fullFile = fullFile + "        rVal.set" + data + "(ByteStreamUtils.popIntFromByteQueue(byteStream));\n";
                        break;
                    case "FIXED_FLOAT":
                        fullFile = fullFile + "        rVal.set" + data + "(ByteStreamUtils.popFloatFromByteQueue(byteStream));\n";
                        break;
                    case "VAR_STRING":
                        //TODO
                        break;
                }
            }
            fullFile = fullFile + "        return rVal;\n";
            fullFile = fullFile + "    }\n\n";
            
            //construct function
            fullFile = fullFile + "    public static " + cat.getCategoryName() + "Message construct" + type.getMessageName() + "Message(";
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_INT":
                        fullFile = fullFile + "int " + data + ",";
                        break;
                    case "FIXED_FLOAT":
                        fullFile = fullFile + "float " + data + ",";
                        break;
                    case "VAR_STRING":
                        //TODO
                        break;
                }
            }
            //chop off last comma
            fullFile = fullFile.substring(0, fullFile.length() - 1);
            fullFile = fullFile + "){\n";
            fullFile = fullFile + "        " + cat.getCategoryName() + "Message rVal = new " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType." + type.getMessageName().toUpperCase() + ");\n";
            for(String data : type.getData()){
                fullFile = fullFile + "        rVal.set" + data + "(" + data + ");\n";
            }
            fullFile = fullFile + "        rVal.serialize();\n";
            fullFile = fullFile + "        return rVal;\n";
            fullFile = fullFile + "    }\n\n";
        }
        
        
        //serialize function
        fullFile = fullFile + "    @Override\n";
        fullFile = fullFile + "    void serialize(){\n";
        fullFile = fullFile + "        byte[] intValues = new byte[4];\n";
        fullFile = fullFile + "        switch(this.messageType){\n";
        for(MessageType type : cat.getMessageTypes()){
            //get all data types
            HashMap<String,String> typeMap = new HashMap();
            for(Data variable : cat.getData()){
                typeMap.put(variable.getName(), variable.getType());
            }
            fullFile = fullFile + "            case " + type.getMessageName().toUpperCase() + ":\n";
            fullFile = fullFile + "                rawBytes = new byte[TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_" + type.getMessageName().toUpperCase() + "_SIZE];\n";
            fullFile = fullFile + "                //message header\n";
            fullFile = fullFile + "                rawBytes[0] = TypeBytes.MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + ";\n";
            fullFile = fullFile + "                //entity messaage header\n";
            fullFile = fullFile + "                rawBytes[1] = TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ";\n";
            int offset = 2;
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_INT":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeIntToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 4;
                        break;
                    case "FIXED_FLOAT":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeFloatToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }";
                        offset = offset + 4;
                        break;
                    case "VAR_STRING":
                        //TODO
                        break;
                }
            }
            fullFile = fullFile + "                break;\n";
        }
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "        serialized = true;\n";
        fullFile = fullFile + "    }\n\n";
        
        //end class
        fullFile = fullFile + "}\n";
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), fullFile.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
