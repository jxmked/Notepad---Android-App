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
    private Context context;
    private String path;
    private File main_file;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setSoftInputMode(3);

        this.context = this.getApplicationContext();

        setContentView(R.layout.activity_main);

        this.editText1 = (EditText) findViewById(R.id.mainTextField);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        //this.fileName = path + getResources().getString(R.string.file_name);

        Path dir = Paths.get(path, "xio");
        this.main_file = Paths.get(dir.toString(), "main.txt").toFile();

        // Check if directory does exists
        File __dir = new File(dir.toString());
        if(! __dir.exists()) {
            Toast.makeText(context, __dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            if(! __dir.mkdirs()){
                Toast.makeText(context, "Unable to create path.", Toast.LENGTH_SHORT).show();
            }
        }

        try {
            FileInputStream openFileInput = openFileInput(this.main_file.getAbsolutePath());

            byte[] bArr = new byte[openFileInput.available()];

            int brakes = openFileInput.read(bArr);

            this.editText1.setText(new String(bArr));

            openFileInput.close();

            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception unused) {
            this.editText1.setText(this.fileName);
            //Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPause() {
        super.onPause();
        try {
            FileOutputStream openFileOutput = openFileOutput(this.main_file.getAbsolutePath(), 0);
            openFileOutput.write(this.editText1.getText().toString().getBytes());
            openFileOutput.close();
        } catch (Exception unused) {
            Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
        }
    }
}