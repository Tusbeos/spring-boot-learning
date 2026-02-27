package SpringCore.Dependency;

public class SMSService implements MessageService {
    public void sendMessage(String message) {
        System.out.println("Sending SMS: " + message);
    }
}
