package com.example.eventmanager.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.Role;
import com.example.eventmanager.ui.auth.LoginActivity;
import com.example.eventmanager.ui.main.MainActivity;
import com.example.eventmanager.utils.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        executorService.execute(() -> {
            // Ensure roles are initialized
            initializeRoles();
            
            runOnUiThread(() -> {
                SessionManager sessionManager = new SessionManager(this);
                Intent intent;
                if (sessionManager.canAutoLogin()) {
                    intent = new Intent(this, MainActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            });
        });
    }

    private void initializeRoles() {
        AppDatabase db = AppDatabase.getInstance(this);
        // Double check roles exist
        String[] roles = {SessionManager.ROLE_ORGANIZER, SessionManager.ROLE_VENDOR, SessionManager.ROLE_STAFF};
        for (String roleName : roles) {
            if (db.roleDao().getRoleByName(roleName) == null) {
                db.roleDao().insertRole(new Role(roleName));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
