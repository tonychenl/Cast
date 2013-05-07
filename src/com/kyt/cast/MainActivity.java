package com.kyt.cast;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView mTextView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
		
		MulticastLock multicastLock = wifiManager.createMulticastLock("multicast.test");  
		multicastLock.acquire();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.textView1);
		
		//SharedPreferences share = getSharedPreferences("com.kyt", 0);
		//SharedPreferences.Editor edit = share.edit();
		Intent service = new Intent();
		service.setClass(this, UdpProcessService.class);
		startService(service);
		mTextView.setText("finsh");
	}
}
