package electrosphere.socket.codegen.model;

import java.util.List;

public class Category {
    String categoryName;
    List<MessageType> messageTypes;
    List<Data> data;

    public List<MessageType> getMessageTypes() {
        return messageTypes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Data> getData() {
        return data;
    }
    
    
}
