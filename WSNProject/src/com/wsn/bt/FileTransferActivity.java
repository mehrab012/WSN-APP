package com.wsn.bt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class FileTransferActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mBluetoothSocket;
	private String file;
    
	
	String mBtStatus;
	TextView tv;

	private static final boolean D = true;
	private static final String TAG = "FileTransferActivity";
	private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_FILE_NAME = 1;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.d(TAG, "++ ON CREATE ++");

		setContentView(R.layout.main);
		tv = (TextView) findViewById(R.id.tv);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
	    for (BluetoothDevice device : devices) {
	     tv.append("\nFound device: " + device.getAddress() + " human name:" + device.getName());
	    }

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (D)
			Log.d(TAG, "++ ON START ++");

		if (mBluetoothAdapter != null) {

			// continue setup
			if (!mBluetoothAdapter.isEnabled()) {
				// Send an intent to enable bt
				Intent enableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

			} else {
				// Bluetooth is already enabled on the device. No need to do
				// anything
				mBtStatus = "Bluetooth IS enabled from BEFORE" + " state: "
						+ mBluetoothAdapter.getState();

				Toast.makeText(this, mBtStatus, Toast.LENGTH_LONG).show();
			}
			
			
			
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (D)
			Log.d(TAG, "++ ON STOP ++");
		//mBluetoothAdapter.disable();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {

		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				// setupChat();
				String address = mBluetoothAdapter.getAddress();
				String name = mBluetoothAdapter.getName();
				mBtStatus = name + " : " + address + " state: "
						+ mBluetoothAdapter.getState();
				Toast.makeText(this, mBtStatus, Toast.LENGTH_LONG).show();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "Bluetooth not enabled",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			
		case REQUEST_FILE_NAME:
			if(resultCode == Activity.RESULT_OK){
				file = data.getExtras()
                .getString(ListFiles.EXTRA_FILE_NAME);
				
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	 switch (item.getItemId()) {
	 case R.id.itemServiceStart:
		 Intent i = new Intent(this,ConnectService.class);
		 i.putExtra("FILE", file);
		 startService(i);
	 return true;
	 case R.id.itemServiceStop:
		 stopService(new Intent(this,ConnectService.class));return true;
	 case R.id.itemFileSelect:
		 Intent fileIntent = new Intent(this,ListFiles.class);
		 startActivityForResult(fileIntent, REQUEST_FILE_NAME);
		 
		 return true;
		 
	 }
	 return false;
	 }

	
}
