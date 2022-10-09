package io.xcreation.notepad;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final EditText editText1 = (EditText) findViewById(R.id.mainTextField);
    private final View darkMode_toggle = findViewById(R.id.toggle_dark_mode);
    private final static String DATA_FOLDER_NAME = ".xio";
    private static String DATA_FOLDER;
    private String fileName;
    private final SharedPreferences Pref = getSharedPreferences("Setting", 0);

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setSoftInputMode(3);

        setContentView(R.layout.activity_main);

        final String __root__ = Environment.getExternalStorageDirectory().getAbsolutePath();

        // Overwrite External Data folder with absolute path
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainActivity.DATA_FOLDER = Paths.get(__root__, MainActivity.DATA_FOLDER_NAME, getResources().getString(R.string.app_name)).toString();
            this.fileName = Paths.get(MainActivity.DATA_FOLDER, String.valueOf(R.string.file_name)).toString();
        } else {
            MainActivity.DATA_FOLDER = __root__ + MainActivity.DATA_FOLDER_NAME + getResources().getString(R.string.app_name);
            this.fileName = MainActivity.DATA_FOLDER + R.string.file_name;
        }

        // Check if permission is already granted
        // If not, request.
        if (isStoragePermissionGranted()) {
            createFolder();
            start();
        }

        // Load Theme
       /* String theme = toggle_dark_light_mode();
        changeTheme(theme);

        // TODO: Add event listener to toggle between dark and light mode
        darkMode_toggle.setOnClickListener(v -> {

            // Get current theme
            String theme1 = toggle_dark_light_mode();

            // Switch light to dark mode
            if(theme1.equals("light")) {
                set_pref_theme("dark");
            } else {
                set_pref_theme("light");
            }

            // Get latest changes then apply
            theme1 = toggle_dark_light_mode();
            changeTheme(theme1);
        }); */

    }

    void set_pref_theme(String latest_theme){
        SharedPreferences.Editor setting = this.Pref.edit();
        setting.putString("theme", latest_theme);
        setting.apply();
    }

    String toggle_dark_light_mode(){
        // We don't apply a default value to tell us that it is not set
        String theme = this.Pref.getString("theme", "");

        if(theme.equals("")) {
            // No preferences
            SharedPreferences.Editor setting = this.Pref.edit();
            setting.putString("theme", "light");
            setting.apply();

            theme = "light";
        }

       return theme;
    }

    protected void changeTheme(String mode) {
        if(Objects.equals(mode, "dark")) {
            setContentView(R.layout.activity_main_dark);
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    protected void start() {
        try {
            File file = new File(this.fileName);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileInputStream fis = new FileInputStream(file);

            byte[] bArr = new byte[fis.available()];

            fis.read(bArr);

            this.editText1.setText(new String(bArr));

            fis.close();
        } catch (Exception err) {
            makeToast(err.toString());
        }
    }

    public void createFolder() {
        File file = new File(MainActivity.DATA_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                            checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            ) {
                // Requests permission
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 225);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Create Data Folder if not exists
            createFolder();
            start();

        } else {
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {

            FileOutputStream fio = new FileOutputStream(this.fileName);
            fio.write(this.editText1.getText().toString().getBytes());
            fio.close();
        } catch (Exception err) {
            makeToast(err.toString());
        }
    }

    public void makeToast(String str) {
        Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}