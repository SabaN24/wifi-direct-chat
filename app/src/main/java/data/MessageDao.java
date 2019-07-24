package data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM message where chat_id = id")
    List<MessageEntity> getChatMessages(int id);

    @Query("SELECT COUNT(*) FROM message where chat_id = id")
    int getChatMessagesCount(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateMessage(MessageEntity message);

    @Query("DELETE FROM message where chat_id = id")
    void deleteChatMessages(int id);

}
