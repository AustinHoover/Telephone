package electrosphere.socket.codegen.classes;

import electrosphere.socket.codegen.model.Category;
import electrosphere.socket.codegen.model.ConfigFile;
import electrosphere.socket.codegen.model.Data;
import electrosphere.socket.codegen.model.MessageType;
import java.util.HashMap;

/*

Represents a specific category of message that we will be parsing for

A very dense class, it contains:

variables representing the contents of the category
getters and setters for the above
an enum for different message subtypes
a checker function for whether a given byte stream can parse an instance of this class
functions for parsing specific message subtypes from a byte stream
functions for instantiation instances of this class, serialized, with specific message subtypes
a serialization function to take an instantiation of this into a series of bytes

*/

public class TypedMessage extends SourceGenerator {
    
    ConfigFile config;
    Category cat;
    
    public TypedMessage(ConfigFile config, Category cat){
        this.config = config;
        this.cat = cat;
    }

    @Override
    public String generateClassSource() {
        //construct map of whether data is fixed size
        HashMap<String,Boolean> fixedSizeVariableMap = new HashMap<String,Boolean>();
        for(Data data : cat.getData()){
            switch(data.getType()){
                case "FIXED_INT":
                    fixedSizeVariableMap.put(data.getName(), true);
                    break;
                case "FIXED_FLOAT":
                    fixedSizeVariableMap.put(data.getName(), true);
                    break;
                case "FIXED_LONG":
                    fixedSizeVariableMap.put(data.getName(), true);
                    break;
                case "VAR_STRING":
                    fixedSizeVariableMap.put(data.getName(), false);
                    break;
            }
        }
        //construct map of whether the message type is variable size or not
        HashMap<MessageType,Boolean> fixedSizeMessageMap = new HashMap<MessageType,Boolean>();
        for(MessageType type : cat.getMessageTypes()){
            boolean isFixedSize = true;
            for(String varName : type.getData()){
                if(!fixedSizeVariableMap.get(varName)){
                    isFixedSize = false;
                }
            }
            fixedSizeMessageMap.put(type,isFixedSize);
        }
        
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.message;\n\n";
        
        //imports
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".util.ByteStreamUtils;\n";
        fullFile = fullFile + "import java.util.LinkedList;\n";
        fullFile = fullFile + "import java.util.List;\n\n";
        
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
                case "FIXED_LONG":
                    fullFile = fullFile + "    long " + variable.getName() + ";\n";
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
        
        //getter and setter for message subtype
        fullFile = fullFile + "    public " + cat.getCategoryName() + "MessageType getMessageSubtype(){\n";
        fullFile = fullFile + "        return this.messageType;\n";
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
                case "FIXED_LONG":
                    //getter
                    fullFile = fullFile + "    public long get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(long " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "VAR_STRING":
                    //getter
                    fullFile = fullFile + "    public String get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(String " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
            }
        }
        
        //strip packet header
        fullFile = fullFile + "    static void stripPacketHeader(List<Byte> byteStream){\n";
        fullFile = fullFile + "        byteStream.remove(0);\n";
        fullFile = fullFile + "        byteStream.remove(0);\n";
        fullFile = fullFile + "    }\n\n";
        
        //parse check function
        fullFile = fullFile + "    public static boolean canParseMessage(List<Byte> byteStream, byte secondByte){\n";
        fullFile = fullFile + "        switch(secondByte){\n";
        for(MessageType type : cat.getMessageTypes()){
            if(fixedSizeMessageMap.get(type)){
                fullFile = fullFile + getFixedSizeParseCheck(cat,type);
            } else {
                fullFile = fullFile + getVariableSizeParseCheck(cat,type);
            }
        }
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "        return false;\n";
        fullFile = fullFile + "    }\n\n";
        
        //parse and construct functions
        for(MessageType type : cat.getMessageTypes()){
            //get all data types
            HashMap<String,String> typeMap = new HashMap();
            for(Data variable : cat.getData()){
                typeMap.put(variable.getName(), variable.getType());
            }
            //parse function
            if(fixedSizeMessageMap.get(type)){
                fullFile = fullFile + getParseFunction(cat,type,typeMap);
            } else {
                fullFile = fullFile + getParseCheckTypeFunction(cat,type,typeMap);
                fullFile = fullFile + getParseFunction(cat,type,typeMap);
            }
            
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
                    case "FIXED_LONG":
                        fullFile = fullFile + "long " + data + ",";
                        break;
                    case "VAR_STRING":
                        fullFile = fullFile + "String " + data + ",";
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
        fullFile = fullFile + "        byte[] intValues = new byte[8];\n";
        fullFile = fullFile + "        byte[] stringBytes;\n";
        fullFile = fullFile + "        switch(this.messageType){\n";
        for(MessageType type : cat.getMessageTypes()){
            //get all data types
            HashMap<String,String> typeMap = new HashMap();
            for(Data variable : cat.getData()){
                typeMap.put(variable.getName(), variable.getType());
            }
            fullFile = fullFile + "            case " + type.getMessageName().toUpperCase() + ":\n";
            String packetSizeCalculation = "2"; // 2 for packet header
            for(String variable : type.getData()){
                switch(typeMap.get(variable)){
                    case "FIXED_INT":
                        packetSizeCalculation = packetSizeCalculation + "+4";
                        break;
                    case "FIXED_FLOAT":
                        packetSizeCalculation = packetSizeCalculation + "+4";
                        break;
                    case "FIXED_LONG":
                        packetSizeCalculation = packetSizeCalculation + "+8";
                        break;
                    case "VAR_STRING":
                        packetSizeCalculation = packetSizeCalculation + "+4+" + variable + ".length()"; // 4 for integer header
                        break;
                }
            }
            fullFile = fullFile + "                rawBytes = new byte[" + packetSizeCalculation + "];\n";
            fullFile = fullFile + "                //message header\n";
            fullFile = fullFile + "                rawBytes[0] = TypeBytes.MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + ";\n";
            fullFile = fullFile + "                //entity messaage header\n";
            fullFile = fullFile + "                rawBytes[1] = TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ";\n";
            int offset = 2;
            String offsetFunctions = "";
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_INT":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeIntToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 4;
                        break;
                    case "FIXED_FLOAT":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeFloatToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }";
                        offset = offset + 4;
                        break;
                    case "FIXED_LONG":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeLongToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 8; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 8;
                        break;
                    case "VAR_STRING":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeIntToBytes(" + data + ".length());\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 4;
                        fullFile = fullFile + "                stringBytes = " + data + ".getBytes();\n";
                        fullFile = fullFile + "                for(int i = 0; i < " + data + ".length(); i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = stringBytes[i];\n";
                        fullFile = fullFile + "                }\n";
                        offsetFunctions = offsetFunctions + "+" + data + ".length()";
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
        
        return fullFile;
    }
    
    static String getFixedSizeParseCheck(Category cat, MessageType type){
        String rVal = "";
        rVal = rVal + "            case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
        rVal = rVal + "                if(byteStream.size() >= TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + "_SIZE){\n";
        rVal = rVal + "                    return true;\n";
        rVal = rVal + "                } else {\n";
        rVal = rVal + "                    return false;\n";
        rVal = rVal + "                }\n";
        return rVal;
    }
    
    static String getVariableSizeParseCheck(Category cat, MessageType type){
        String rVal = "";
        rVal = rVal + "            case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
        rVal = rVal + "                return " + cat.getCategoryName() + "Message.canParse" + type.getMessageName() + "Message(byteStream);\n";
        return rVal;
    }
    
    static String getParseFunction(Category cat, MessageType type, HashMap<String,String> typeMap){
        String rVal = "";
        rVal = rVal + "    public static " + cat.getCategoryName() + "Message parse" + type.getMessageName() + "Message(List<Byte> byteStream){\n";
        rVal = rVal + "        " + cat.getCategoryName() + "Message rVal = new " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType." + type.getMessageName().toUpperCase() + ");\n";
        rVal = rVal + "        stripPacketHeader(byteStream);\n";
        for(String data : type.getData()){
            switch(typeMap.get(data)){
                case "FIXED_INT":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popIntFromByteQueue(byteStream));\n";
                    break;
                case "FIXED_FLOAT":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popFloatFromByteQueue(byteStream));\n";
                    break;
                case "FIXED_LONG":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popLongFromByteQueue(byteStream));\n";
                    break;
                case "VAR_STRING":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popStringFromByteQueue(byteStream));\n";
                    break;
            }
        }
        rVal = rVal + "        return rVal;\n";
        rVal = rVal + "    }\n\n";
        return rVal;
    }
    
    static String getParseCheckTypeFunction(Category cat, MessageType type, HashMap<String,String> typeMap){
        String rVal = "";
        rVal = rVal + "    public static boolean canParse" + type.getMessageName() + "Message(List<Byte> byteStream){\n";
        rVal = rVal + "        int currentStreamLength = byteStream.size();\n";
        rVal = rVal + "        List<Byte> temporaryByteQueue = new LinkedList();\n";
        int currentLength = 2;
        for(String currentData : type.getData()){
            switch(typeMap.get(currentData)){
                case "FIXED_INT":
                    currentLength = currentLength + 4; // size of int
                    rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n";
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "FIXED_FLOAT":
                    currentLength = currentLength + 4; // size of float
                    rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n";
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "FIXED_LONG":
                    currentLength = currentLength + 8; // size of long
                    rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n";
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "VAR_STRING":
                    rVal = rVal + "        int " + currentData + "Size = 0;\n";
                    rVal = rVal + "        if(currentStreamLength < " + (currentLength + 4) + "){\n"; // have to account for integer header
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        } else {\n";
                    rVal = rVal + "            temporaryByteQueue.add(byteStream.get(" + currentLength + " + 0));\n";
                    rVal = rVal + "            temporaryByteQueue.add(byteStream.get(" + currentLength + " + 1));\n";
                    rVal = rVal + "            temporaryByteQueue.add(byteStream.get(" + currentLength + " + 2));\n";
                    rVal = rVal + "            temporaryByteQueue.add(byteStream.get(" + currentLength + " + 3));\n";
                    rVal = rVal + "            " + currentData + "Size = ByteStreamUtils.popIntFromByteQueue(temporaryByteQueue);\n";
                    rVal = rVal + "        }\n";
                    rVal = rVal + "        if(currentStreamLength < " + (currentLength + 4) + " + " + currentData + "Size){\n";
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
            }
        }
        rVal = rVal + "        return true;\n";
        rVal = rVal + "    }\n\n";
        return rVal;
    }
    
}
