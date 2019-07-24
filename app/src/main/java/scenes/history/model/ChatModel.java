package scenes.history.model;

import java.util.Date;

public class ChatModel {

    private int id;

    private String deviceName;

    private Date time;

    private int messagesCount;

    public ChatModel(int id, String deviceName, Date time, int messagesCount) {
        this.id = id;
        this.deviceName = deviceName;
        this.time = time;
        this.messagesCount = messagesCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(int messagesCount) {
        this.messagesCount = messagesCount;
    }
}
