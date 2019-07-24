package data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "chat")
public class ChatEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "deviceName")
    private String deviceName;

    @ColumnInfo(name = "time")
    private Long time;

    public ChatEntity(int id, String deviceName, Long time) {
        this.id = id;
        this.deviceName = deviceName;
        this.time = time;
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
