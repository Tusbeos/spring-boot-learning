package SpringCore.Dependency;

public class Client implements InjectionMessage {

    private MessageService messageService;
//    public void setMessageService(MessageService messageServiceParam) {
//        this.messageService = messageServiceParam;
//    }
    public void processMessage(String message) {
        messageService.sendMessage(message);
    }
    public void  setService(MessageService messageService) {
        this.messageService = messageService;
    }
}
