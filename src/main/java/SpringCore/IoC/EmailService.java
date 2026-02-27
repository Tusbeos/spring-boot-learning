package SpringCore.IoC;

//import org.springframework.stereotype.Component;
//
//@Component
public class EmailService implements MessageService {

    public void sendMessage(String message) {
        System.out.println("Sending email: " + message);
    }
}
