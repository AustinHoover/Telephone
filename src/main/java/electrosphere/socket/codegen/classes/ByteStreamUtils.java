package electrosphere.socket.codegen.classes;

import electrosphere.socket.codegen.Main;
import electrosphere.socket.codegen.model.ConfigFile;
import electrosphere.socket.codegen.utils.Utilities;

/*

This contains utilities used to handle byte queues

*/
public class ByteStreamUtils extends SourceGenerator {

    
    ConfigFile config;
    
    public ByteStreamUtils(ConfigFile config){
        this.config = config;
    }
    
    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".util;\n\n";
        
        //content
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/ByteStreamUtils.txt"));
        
        return fullFile;
    }
    
}
