package io.xcreation.notepad;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {
    EditText editText1;
    String fileName;
    final private static String DATA_FOLDER_NAME = ".xio";
    private static String DATA_FOLDER;
    SharedPreferences Pref = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setSoftInputMode(3);
        setContentView(R.layout.activity_main);

        this.editText1 = findViewById(R.id.mainTextField);
        this.Pref = getSharedPreferences("setting", 0);

        final String __root__ = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String FILENAME = getResources().getString(R.string.file_name);
        final String APP_NAME = getResources().getString(R.string.app_name);

        // Overwrite External Data folder with absolute path
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainActivity.DATA_FOLDER = Paths.get(__root__, MainActivity.DATA_FOLDER_NAME, APP_NAME).toString();
            this.fileName = Paths.get(MainActivity.DATA_FOLDER, FILENAME).toString();
        } else {
            MainActivity.DATA_FOLDER = __root__ + MainActivity.DATA_FOLDER_NAME + APP_NAME;
            this.fileName = MainActivity.DATA_FOLDER + FILENAME;
        }

        // Check if permission is already granted
        // If not, request.
        if (isStoragePermissionGranted()) {
            createFolder();
            start();
            reload_themes();
        }
    }

    @SuppressLint("ApplySharedPref")
    void set_theme() {
        final boolean mode = this.Pref.getBoolean("dark_mode", true);
        final SharedPreferences.Editor editor = this.Pref.edit();

        editor.putBoolean("dark_mode", !mode);
        editor.commit();
    }

    void reload_themes(){
        final boolean mode = this.Pref.getBoolean("dark_mode", true);
        final int theme = (mode) ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;

        // Set app theme mode
        AppCompatDelegate.setDefaultNightMode(theme);

       // getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.toggle_dark_mode) {
            set_theme();
            reload_themes();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void start() {
        try {
            File file = new File(this.fileName);

            if (! file.exists() && file.createNewFile()) {
                makeToast("Failed to create text file to store data. Exiting...");
                finish();
            }

            FileInputStream fis = new FileInputStream(file);

            byte[] bArr = new byte[fis.available()];

            // Throws an error if the file is empty
            // or failed to read
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
            if(! file.mkdirs()) {
                makeToast("Failed to create required directory. Exiting...");
                finish();
            }
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
            reload_themes();

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