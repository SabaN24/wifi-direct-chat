package scenes.chat.model;

import java.util.Date;

public class MessageModel {

    private int id;

    private String text;

    private Date time;

    private boolean response;

    private int chatId;

    public MessageModel(int id, String text, Date time, boolean response, int chatId) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.response = response;
        this.chatId = chatId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

}
