package SpringCore.IoC;

import org.springframework.stereotype.Component;

@Component
public class SMSService implements MessageService {
    public void sendMessage(String message) {
        System.out.println("Sending SMS: " + message);
    }
}
