package electrosphere.socket.codegen.classes;

import electrosphere.socket.codegen.Main;
import electrosphere.socket.codegen.model.Category;
import electrosphere.socket.codegen.model.ConfigFile;
import electrosphere.socket.codegen.model.MessageType;
import electrosphere.socket.codegen.utils.Utilities;

/*

This contains constants that the other classes use to distinguish different message types/sizes

*/

public class TypeBytes extends SourceGenerator {
    
    ConfigFile config;
    
    public TypeBytes(ConfigFile config){
        this.config = config;
    }

    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.message;\n\n";
        
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
                fullFile = fullFile + "    public static final byte " + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + "_SIZE = " + incrementer + ";\n";
            }
        }
        
        //outro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/TypeBytesSecondPart.txt"));
        
        //return
        return fullFile;
    }
    
}
