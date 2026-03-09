package com.example.eventmanager.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.eventmanager.database.dao.*;
import com.example.eventmanager.database.converter.DateConverter;
import com.example.eventmanager.model.*;

@Database(
    entities = {Event.class, Guest.class, Task.class, User.class, Vendor.class, Budget.class, Location.class},
    version = 4,
    exportSchema = false
)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract EventDao eventDao();
    public abstract GuestDao guestDao();
    public abstract TaskDao taskDao();
    public abstract UserDao userDao();
    public abstract VendorDao vendorDao();
    public abstract BudgetDao budgetDao();
    public abstract LocationDao locationDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "event_manager_db"
                        )
                        .fallbackToDestructiveMigration()
                        .build();
                }
            }
        }
        return instance;
    }
}
