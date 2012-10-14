package be.intec.shoplist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class SplashActivity extends Activity {
	Timer myTimer;

	File childFile;
	InputStream input;
	BufferedReader reader;
	FileWriter writer;
	File folders;
	File subfolder;
	
	
	int height;
	int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
// Create folders for filestorage
		
		folders = new File(Environment.getExternalStorageDirectory(),"ShopList/Items");
		subfolder= new File(Environment.getExternalStorageDirectory(),"ShopList/Lists");
		
			
//			else{
			
// delays intent to go to HomeActivity 
			myTimer = new Timer();
			
			myTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					timerMethod();
				}
			}, 3000);
//			}//end else

	}//end onCreate

//Runs timerMethod() in the UI Thread

	private void timerMethod()
	{
	this.runOnUiThread(goToHome);
	}

// make timerMethod()  runnable

	private Runnable goToHome = new Runnable() {
		public void run() {
			

			if(!folders.isDirectory()){
				folders.mkdirs();
				subfolder.mkdir();
				
	// create files for childnames
				
				ArrayList<String> groups = MyMethods.getGroupNames(SplashActivity.this);
				for (int i=0;i<groups.size();++i){
					int id = R.raw.aaaaagroup+i+1;
					String fileName = "ShopList/Items/"+groups.get(i);
					try{
						input = SplashActivity.this.getResources().openRawResource(id);
						reader=new BufferedReader(new InputStreamReader(input),150);
						childFile= new File(Environment.getExternalStorageDirectory(),fileName);
						writer = new FileWriter(childFile);
						String childString;
						while((childString=reader.readLine())!=null){
							writer.write(childString);
							writer.write(System.getProperty("line.separator"));
						}
						writer.close();
						reader.close();
					}catch(IOException e){e.printStackTrace();}
					
				}//end for
				MyMethods.writeItemToList(System.getProperty("line.separator"), "currentList");
				MyMethods.writeItemToList(System.getProperty("line.separator"), "scratchFile");

			}//end if(!)
			
			Intent intent= new Intent(SplashActivity.this,ShopListActivity.class);
			startActivity(intent);
			finish();
		
		}
	};

}
