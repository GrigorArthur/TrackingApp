package hitec.com.model;

public class MessageItem {
    private String fromUser;
    private String toUser;
    private String message;
    private String time;

    public MessageItem() {
        fromUser = "";
        toUser = "";
        message = "";
        time = "";
    }

    public void setFromUser(String value) {
        this.fromUser = value;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setToUser(String value) {
        this.toUser = value;
    }

    public String getToUser() {
        return toUser;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getMessage() {
        return message;
    }

    public void setTime(String value) {
        this.time = value;
    }

    public String getTIme() {
        return time;
    }
}
