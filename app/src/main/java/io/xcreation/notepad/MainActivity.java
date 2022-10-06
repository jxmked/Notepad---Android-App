package io.xcreation.notepad;

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
import android.app.Activity;
import android.content.pm.*;
import android.content.*;
import android.net.*;

public class MainActivity extends Activity {
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

        
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();

	    String toFile = Paths.get("xio", "main.txt").toAbsolutePath().toString();
		
		
		 // Check if directory does exists
		File file = new File(path,toFile);
		
		//Toast.makeText(this.MyContext, folde, Toast.LENGTH_SHORT).show();
		
		if(! file.getParentFile().exists()) {
		 	if(! file.mkdirs()){
		 		Toast.makeText(this.MyContext, "Unable to create path", Toast.LENGTH_SHORT).show();
		 		//finish();
		 		//System.exit(-1);
			}
		}
		
		
	 	//this.fileName = main_file.getAbsolutePath();
		 
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
					if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						//If user presses allow
						Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
						//Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num.getText().toString()));
						//startActivity(in);
					} else {
						//If user presses deny
						Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
					}
					break;
				}
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
