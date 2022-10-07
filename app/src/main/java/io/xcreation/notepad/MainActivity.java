package io.xcreation.notepad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {
    EditText editText1;
    ScrollView scrollView;
    String fileName;
    final private static String DATA_FOLDER_NAME = ".xio";
    private static String DATA_FOLDER;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setSoftInputMode(3);

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                setContentView(R.layout.activity_main_dark);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                setContentView(R.layout.activity_main);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                setContentView(R.layout.activity_main);
                break;
        }

        this.editText1 = (EditText) findViewById(R.id.mainTextField);
        this.scrollView = (ScrollView) findViewById(R.id.scrollView);

        final String __root__ = Environment.getExternalStorageDirectory().getAbsolutePath();

        // Overwrite External Data folder with absolute path
        MainActivity.DATA_FOLDER = Paths.get(__root__, MainActivity.DATA_FOLDER_NAME, getResources().getString(R.string.app_name)).toString();

        this.fileName = Paths.get(MainActivity.DATA_FOLDER, "main.txt").toString();

        // Check if permission is already granted
        // If not, request.
        if (isStoragePermissionGranted()) {
            createFolder();
            start();
        }

        // TODO: Touch layout/Scrollview then focus on textfield
        /**
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSoftKeyboard(view);
                return false;
            }
        });
        **/

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

            FileOutputStream fio = new FileOutputStream(new File(this.fileName));
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