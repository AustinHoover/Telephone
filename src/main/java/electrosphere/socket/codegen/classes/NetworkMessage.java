package electrosphere.socket.codegen.classes;

import electrosphere.socket.codegen.Main;
import electrosphere.socket.codegen.model.Category;
import electrosphere.socket.codegen.model.ConfigFile;
import electrosphere.socket.codegen.model.MessageType;
import electrosphere.socket.codegen.utils.Utilities;

/*

This is a generic message type that is used in parsing specific message types

*/

public class NetworkMessage extends SourceGenerator {

    
    ConfigFile config;
    
    public NetworkMessage(ConfigFile config){
        this.config = config;
    }
    
    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.message;\n\n";
        
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".util.ByteStreamUtils;\n";
        
        //intro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkMessageFirstPart.txt"));
        
        //construct enum
        for(Category cat : config.getCategories()){
            fullFile = fullFile + cat.getCategoryName().toUpperCase() + "_MESSAGE,\n";
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
        
        return fullFile;
    }
    
}
