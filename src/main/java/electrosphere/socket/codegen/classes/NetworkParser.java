package electrosphere.socket.codegen.classes;

import electrosphere.socket.codegen.Main;
import electrosphere.socket.codegen.model.ConfigFile;
import electrosphere.socket.codegen.utils.Utilities;

/*

This handles the conversion of socket bytes to a LinkedList of Bytes

*/

public class NetworkParser extends SourceGenerator {

    ConfigFile config;
    
    public NetworkParser(ConfigFile config){
        this.config = config;
    }
    
    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.raw;\n\n";
        
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".net.message.NetworkMessage;\n";
        
        //content
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkParser.txt"));
        
        return fullFile;
    }
    
}
