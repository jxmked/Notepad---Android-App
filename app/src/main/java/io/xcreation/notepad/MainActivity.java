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
import android.os.*;
import android.*;

public class MainActivity extends Activity {
    EditText editText1;
    String fileName;
    private static String DATA_FOLDER = ".xio";
	private String ROOT;
	
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setSoftInputMode(3);
		
		// Check if external data folder does exists
		
		isStoragePermissionGranted();
        setContentView(R.layout.activity_main);
		this.editText1 = (EditText) findViewById(R.id.mainTextField);
		start();
		
	}
	
	protected void start(){
		
			try {
				createExternalDataFolder();
				toFile();
				
				File file = new File(this.fileName);

				if(!file.exists()){
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

	public void toFile(){
		this.fileName = Paths.get(this.ROOT,getResources().getString(R.string.file_name)).toString();
	}
	
	public void createExternalDataFolder() {
		String __root__ = Environment.getExternalStorageDirectory().getAbsolutePath();
		String path = Paths.get(MainActivity.DATA_FOLDER, getResources().getString(R.string.app_name)).toString();
		File file = new File(__root__, path);
		
		if(! file.exists()) {
		 	file.mkdirs();
		}
		
		this.ROOT = file.getAbsolutePath();
		toFile();
	}
	
	public  boolean isStoragePermissionGranted() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (
				checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
				checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			) {
				requestPermissions(new String[]{
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE
				}, 225);
			}
		}
		
		//createExternalDataFolder();
		return true;
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			createExternalDataFolder();
			start();
		}else{
			finish();
		}
	}
	
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
