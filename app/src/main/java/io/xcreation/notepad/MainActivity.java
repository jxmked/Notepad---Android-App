package io.xcreation.notepad;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText editText1;
    String fileName;
    final private static String DATA_FOLDER_NAME = ".xio";
    private static String DATA_FOLDER;
    private SharedPreferences Pref;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setSoftInputMode(3);

        // Load theme then set view
        String theme = get_current_theme();
        changeTheme(theme);

        this.editText1 = (EditText) findViewById(R.id.mainTextField);
        this.Pref = getSharedPreferences("setting", 0);
        final String __root__ = Environment.getExternalStorageDirectory().getAbsolutePath();

        // Overwrite External Data folder with absolute path
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainActivity.DATA_FOLDER = Paths.get(__root__, MainActivity.DATA_FOLDER_NAME, getResources().getString(R.string.app_name)).toString();
            this.fileName = Paths.get(MainActivity.DATA_FOLDER, String.valueOf(R.string.file_name)).toString();
        } else {
            MainActivity.DATA_FOLDER = __root__ + MainActivity.DATA_FOLDER_NAME + getResources().getString(R.string.app_name);
            this.fileName = MainActivity.DATA_FOLDER + getResources().getString(R.string.file_name);
        }

        // Check if permission is already granted
        // If not, request.
        if (isStoragePermissionGranted()) {
            createFolder();
            start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.toggle_dark_mode) {
            // Get saved theme data
            String theme = get_current_theme();

            if(theme.equals("light")) {
                theme = "dark";
            } else {
                theme = "light";
            }

            set_pref_theme(theme);
            changeTheme(theme);

            onPause();
            start();
        }
        return super.onOptionsItemSelected(item);
    }

    void set_pref_theme(String latest_theme){
        SharedPreferences.Editor setting = this.Pref.edit();
        setting.putString("theme", latest_theme);
        setting.apply();
    }

    String get_current_theme(){
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Create Data Folder if not exists
            createFolder();
            start();

        } else {
            finish();
        }
    }

    /**
     public void showSoftKeyboard(View view) {
     // Focus on TextField
     this.editText1.requestFocus();

     InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
     imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

     }
     **/
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