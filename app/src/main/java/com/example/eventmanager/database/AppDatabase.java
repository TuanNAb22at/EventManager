package com.example.eventmanager.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.eventmanager.database.dao.*;
import com.example.eventmanager.database.converter.DateConverter;
import com.example.eventmanager.model.*;
import com.example.eventmanager.utils.SessionManager;
import java.util.concurrent.Executors;

@Database(
    entities = {
        Event.class, Guest.class, Task.class, User.class, 
        Vendor.class, Budget.class, Location.class, 
        Schedule.class, Feedback.class, Role.class, UserRole.class,
        EventVendor.class
    },
    version = 18, // Tăng version từ 17 lên 18 để áp dụng thay đổi Index
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
    public abstract ScheduleDao scheduleDao();
    public abstract FeedbackDao feedbackDao();
    public abstract RoleDao roleDao();
    public abstract UserRoleDao userRoleDao();
    public abstract EventVendorDao eventVendorDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "event_manager_db"
                        )
                        .setJournalMode(JournalMode.TRUNCATE)
                        .fallbackToDestructiveMigration()
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    AppDatabase database = getInstance(context);
                                    database.roleDao().insertRole(new Role(SessionManager.ROLE_ORGANIZER));
                                    database.roleDao().insertRole(new Role(SessionManager.ROLE_STAFF));
                                });
                            }
                        })
                        .build();
                }
            }
        }
        return instance;
    }
}
