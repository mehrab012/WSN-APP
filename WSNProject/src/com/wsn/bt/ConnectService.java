package com.wsn.bt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ConnectService extends Service {

	private int mState;
	
	

	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3;

	private String file_name;

	private static final boolean D = true;
	private static final String TAG = "ConnectService";
	private volatile Connect mConnect;
	public boolean isRunning = false;

	private OutputStream outStream = null;
	// BluetoothDevice mBluetoothDevice;
	//private static String address = "D0:37:61:C7:1A:BF";
	//private static String address = "D0:37:61:C7:50:7E";
	
	//Acer A500
	private static String address = "E0:B9:A5:7A:60:BD";
	//REMOTE ADDRESS
	//private static String address = "38:E7:D8:36:92:A7";
	// private static final UUID MY_UUID = UUID
	// .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mBluetoothSocket;

	@Override
	public void onCreate() {
		super.onCreate();
		if (D)
			Log.d(TAG, "++ ON CREATE ++");

		mConnect = new Connect();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	}

	@Override
	public synchronized void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (D)
			Log.d(TAG, "++ ON START ++");
		Bundle extras = intent.getExtras();
		if (extras != null) {
			file_name = extras.getString("FILE");
		}

		// Start the connect
		if (!this.isRunning) {
			isRunning = true;
			mConnect.start();
		}

		Toast.makeText(this, R.string.local_service_started, Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public synchronized void onDestroy() {

		super.onDestroy();
		if (D)
			Log.d(TAG, "++ ON DESTROY ++");
		if (this.isRunning) {
			mConnect.interrupt();
		}
		mConnect = null;
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT)
				.show();

	}

	class Connect extends Thread {
		static final long DELAY = 1;

		@Override
		public void run() {

			// super.run();
			// while (isRunning) {
			// try {
			Log.d(TAG, " Connect Thread run'ing");
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			try {
				mBluetoothSocket = device
						.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				Log.d(TAG, "ON START: Socket creation failed.", e);
			}
			
			

			mBluetoothAdapter.cancelDiscovery();

			// Blocking connect, for a simple client nothing else can
			// happen until a successful connection is made, so we
			// don't care if it blocks.
			try {
				mBluetoothSocket.connect();
				Log.d(TAG,
						"ON START: BT connection established, data transfer link open.");
				// Send file
				// Get attach output stream to the socket
				try {
					outStream = mBluetoothSocket.getOutputStream();
					Log.d(TAG, "outStream created success!");
				} catch (IOException e) {
					Log.d(TAG, "ON RESUME: Output stream creation failed.", e);
				}

				File myFile = new File(file_name);
				Log.d(TAG, "file /source.pdf created success!");

				//byte[] mybytearray = new byte[(int) myFile.length()];
				//Log.d(TAG, "file length() =" + (int) myFile.length());

				FileInputStream fis = new FileInputStream(myFile);
				Log.d(TAG, "fis created");

				BufferedInputStream bis = new BufferedInputStream(fis, 8 * 1024);
				Log.d(TAG, "bis created success");

				//int len = bis.read(mybytearray, 0, mybytearray.length);
				//Log.d(TAG, "ALL Bytes read from bis" + len);

				//outStream.write(mybytearray, 0, mybytearray.length);
				//outStream.write(mybytearray, 161280, mybytearray.length - 161280);
				
				Log.d(TAG, "BYTES WRITTEN to OUTSTREAM of socket");

				
				byte[] buffer = new byte[8192];
				int len;
				while ((len = bis.read(buffer)) != -1) {
				    outStream.write(buffer, 0, len);
				}

				///////////////////
				//FileOutputStream fos = new FileOutputStream("/sdcard/check.pdf");
				//fos.write(mybytearray, 0, mybytearray.length);
				//fos.flush();
				//fos.close();
				
				
				
				
				/////////////////
				outStream.flush();
				Log.d(TAG, "bytes flushed");
				outStream.close();

				// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Create a data stream so we can talk to server.
				// Log.d(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");
				//
				// try {
				// outStream = mBluetoothSocket.getOutputStream();
				// } catch (IOException e) {
				// Log.d(TAG,
				// "ON RESUME: Output stream creation failed.",
				// e);
				// }
				//
				// String message =
				// "Hello message from client to server.";
				// byte[] msgBuffer = message.getBytes();
				// try {
				// outStream.write(msgBuffer);
				// } catch (IOException e) {
				// Log.d(TAG, "ON RESUME: Exception during write.", e);
				// }
				//
				// if (outStream != null) {
				// try {
				// outStream.flush();
				// } catch (IOException e) {
				// Log.d(TAG,
				// "ON PAUSE: Couldn't flush output stream.",
				// e);
				// }
				//
				// try {
				// mBluetoothSocket.close();
				// } catch (IOException e2) {
				// Log.d(TAG, "ON PAUSE: Unable to close socket.", e2);
				// }
				// }
				// //////////////////////////////////////////////////////////////////////////////////////////////////////////////

			} catch (IOException e) {
				try {
					mBluetoothSocket.close();
				} catch (IOException e2) {

					Log.d(TAG,
							"ON START: Unable to close socket during connection failure",
							e2);
				}
			}

			// Thread.sleep(DELAY);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// isRunning = false;
			// //break;
			// }

			// }//while(is running)

		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
