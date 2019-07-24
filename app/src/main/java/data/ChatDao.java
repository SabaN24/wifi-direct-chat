package data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ChatDao {

    @Query("SELECT * FROM chat")
    List<ChatEntity> getAllChats();

    @Query("SELECT * FROM chat WHERE id LIKE :id")
    ChatEntity getChatById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateChat(ChatEntity chat);

    @Query("DELETE FROM chat where id = :id")
    void deleteChat(int id);

}
