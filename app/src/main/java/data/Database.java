package data;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import app.App;

@android.arch.persistence.room.Database(entities = {ChatEntity.class, MessageEntity.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    private static final String DATABASE_NAME = "application_database";

    private static Database INSTANCE;

    private static final Object lock = new Object();

    public abstract ChatDao chatDao();

    public abstract MessageDao messageDao();

    public static Database getInstance() {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                        App.getContext(),
                        Database.class,
                        DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }
}