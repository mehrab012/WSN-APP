package com.bt.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

//16,471
public class ReceiveService extends Service {

	AcceptThread mlisten;
	private long elapsed;
	private static final String TAG = "ReceiveService";
	private boolean D = true;
	
	private boolean isRunning = false;
	// Name for the SDP record when creating server socket
	private static final String NAME = "BluetoothServer";

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mBluetoothSocket;
	// 17,942,908

	private int mState;
	private static final int FILE_SIZE = 2100000 ; //tutorial.pdf
	
	

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "++ON CREATE++");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mlisten = new AcceptThread();
		Toast.makeText(this, R.string.local_service_started, Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public synchronized void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "++ON START++");
		mState = 0;
		if (!isRunning) {
			isRunning = true;
			mlisten.start();
		}

	}

	@Override
	public synchronized void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "++ON DESTROY++");
		if (isRunning) {
			mlisten.interrupt();
		}
		mlisten = null;
		// Tell the user we stopped.
		Toast.makeText(this,"Time taken: " + elapsed, Toast.LENGTH_SHORT)
		.show();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;

			 try {
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
				 NAME, MY_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mmServerSocket = tmp;
		}

		public void run() {

			if (D)
				Log.d(TAG, "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;
			while (isRunning) {
				Log.d(TAG, "run'ing");
				// try {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
					// If a connection was accepted
					if (socket != null) {
						Log.d(TAG,
								"socket has a value now: " + socket.toString());
						byte[] buffer = new byte[FILE_SIZE];
						int bytesRead = 0;
						int current = 0;
						InputStream mIn = null;
						// Attach the i/p stream to the socket
						try {
							InputStream in = socket.getInputStream();
							mIn = in;
							Log.d(TAG, "input stream acquired");

						} catch (IOException e1) {
							e1.printStackTrace();
						}
						// Create output streams & write to file & count time
						long startTime = System.currentTimeMillis();
						FileOutputStream fos = new FileOutputStream(
								Environment.getExternalStorageDirectory()
										+ "/copy.pdf");
						
						try {
							bytesRead = mIn.read(buffer, 0, buffer.length);
							Log.d(TAG, "bytesRead first time =" + bytesRead);
							current = bytesRead;

							do {
								Log.d(TAG, "do-while -- current: " + current);
								bytesRead = mIn.read(buffer, current,
										buffer.length - current);
								Log.d(TAG, "bytesRead: =" + bytesRead);

								if (bytesRead >= 0)
									current += bytesRead;
							} while (bytesRead > -1);

							
							
						
						} catch (IOException e) {
							e.printStackTrace();
							Log.d(TAG, "do while end:-- buffer len= "
									+ buffer.length + "  current: " + current + "bytesRead = " + bytesRead);

							fos.write(buffer);
							fos.flush();
							fos.close();
							long endTime = System.currentTimeMillis();
							elapsed = endTime - startTime;

						}
					}
					socket.close();

				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);

				}

				// Thread.sleep(1);
				// }
				// catch (InterruptedException e) {
				// e.printStackTrace();
				try {
					mmServerSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					Log.d(TAG, "Cannot close SERVER SOCKET");
				}
				isRunning = false;

				// break;
				// }
			} // while(isRunning)

		}// run()
	}

}
