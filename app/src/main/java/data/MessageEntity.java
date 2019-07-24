package data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "message",
        indices = {@Index("chat_id")},
        foreignKeys = @ForeignKey(entity = ChatEntity.class,
                parentColumns = "id",
                childColumns = "chat_id",
                onDelete = ForeignKey.CASCADE))
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "response")
    private boolean response;

    @ColumnInfo(name = "chat_id")
    private int chatId;

    public MessageEntity(int id, String text, Long time, boolean response, int chatId) {
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
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
