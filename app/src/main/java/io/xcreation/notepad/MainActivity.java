package io.xcreation.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {
    EditText editText1;
    String fileName;
    Context MyContext;
    //private String path;
    //private File main_file;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setSoftInputMode(3);

        this.MyContext = this.getApplicationContext();

        setContentView(R.layout.activity_main);

        this.editText1 = (EditText) findViewById(R.id.mainTextField);
        this.fileName = getResources().getString(R.string.file_name);

        /**
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        Path dir = Paths.get(path, "xio");
        this.main_file = Paths.get(dir.toString(), "main.txt").toFile();

        // Check if directory does exists
        File __dir = new File(dir.toString());
        if(! __dir.exists()) {
            if(! __dir.mkdirs()){
                Toast.makeText(this.MyContext, "Unable to create path", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(-1);
            }
        }
        **/
        try {
            FileInputStream openFileInput = openFileInput(this.fileName);

            byte[] bArr = new byte[openFileInput.available()];

            // Check if we can read it
            openFileInput.read(bArr);

            this.editText1.setText(new String(bArr));

            openFileInput.close();
        } catch (Exception err) {
            Toast.makeText(this.MyContext, err.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            FileOutputStream openFileOutput = openFileOutput(this.fileName, 0);
            openFileOutput.write(this.editText1.getText().toString().getBytes());
            openFileOutput.close();
        } catch (Exception unused) {
            Toast.makeText(this.MyContext, "Error while saving", Toast.LENGTH_SHORT).show();
        }
    }
}