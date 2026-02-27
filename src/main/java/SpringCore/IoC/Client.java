package SpringCore.IoC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Client  {
    @Autowired
    private MessageService messageService;
    public void processMessage(String message) {
        messageService.sendMessage(message);
    }
}
