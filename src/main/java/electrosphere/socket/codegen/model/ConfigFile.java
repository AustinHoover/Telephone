package electrosphere.socket.codegen.model;

import java.util.List;

public class ConfigFile {
    List<Category> categories;

    String outputPath;
    
    String packageName;

    public List<Category> getCategories() {
        return categories;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getPackageName() {
        return packageName;
    }
    
    
}
