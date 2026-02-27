package SpringCore.Dependency;

public class Main {
    public static void main(String[] args) {
        MessageService emailService = new EmailService();
        MessageService smsService = new SMSService();

        Client client = new Client();
//        client.setMessageService(emailService);
        client.setService(emailService);
        client.processMessage("Hello, Dependency Injection!");
    }
}
