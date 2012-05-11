package com.wsn.bt;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListFiles extends ListActivity {
	
	public static final String TAG = "ListFiles Activity";
	
	//    ------- /sdcard/Bluetooth --------
	String path = Environment.getExternalStorageDirectory().toString()
			+ "/Bluetooth";
    public static String EXTRA_FILE_NAME = "file_name";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				getDir(path)));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();

				// Create the result Intent and include the MAC address
				Intent intent = new Intent();
				intent.putExtra(EXTRA_FILE_NAME, ((TextView) view).getText());

				// Set result and finish this Activity
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	ArrayList<String> getDir(String path) {
		File dir = new File(path);
		File[] files = dir.listFiles();
		ArrayList<String> fileNames = new ArrayList<String>();
		for(File f : files){
			String n = f.toString();
			fileNames.add(n);
			Log.d(TAG,n);
		}

		return fileNames;

	}

}