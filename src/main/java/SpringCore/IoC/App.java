package SpringCore.IoC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApplicationContext contex = SpringApplication.run(App.class, args);
//        EmailService emailService = contex.getBean(EmailService.class);
//        EmailService emailService = (EmailService) contex.getBean("emailService");
        SMSService smsService = contex.getBean(SMSService.class);
        Client client = contex.getBean(Client.class);
        client.processMessage("Hello, Spring IoC!");

    }
}
