package com.kyt.cast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

import com.kyt.cast.command.CommandDispatcher;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView mTextView;
	private static final String BROADCAST_IP = "238.9.9.1";
	private static final int BROADCAST_PORT = 8302;
	private MulticastSocket broadSocket;
	private InetAddress broadAddress;
	private GetPacket packet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
		
		MulticastLock multicastLock = wifiManager.createMulticastLock("multicast.test");  
		multicastLock.acquire();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.textView1);
		try {
			broadSocket = new MulticastSocket(BROADCAST_PORT);
			broadAddress = InetAddress.getByName(BROADCAST_IP);
			broadSocket.joinGroup(broadAddress);
			broadSocket.setLoopbackMode(false);
			packet = new GetPacket();
			packet.start();
		} catch (Exception e) {
		}
		
		//SharedPreferences share = getSharedPreferences("com.kyt", 0);
		//SharedPreferences.Editor edit = share.edit();
		mTextView.setText("finsh");
	}
	
	  
	private String intToIp(int ipAddress)  {
		return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."   
		        + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff)); 
	} 
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStop() {
		packet.interrupted();
	}
	
	
	class GetPacket extends Thread{
		public void run() {
			try {
				DatagramPacket inPacket;
				DatagramSocket sender = new DatagramSocket();
				CommandDispatcher dispatcher;
				byte[] resault;
				DatagramPacket outPacket;
				while(true){
					Log.v("kyt", "re....");
					inPacket = new DatagramPacket(new byte[8], 8);
					broadSocket.receive(inPacket);
					dispatcher = new CommandDispatcher(inPacket.getData(), getApplicationContext(),broadSocket);
					resault = dispatcher.getDate();
					if(null != resault && resault.length>0){
						outPacket = new DatagramPacket(resault, 0, resault.length, inPacket.getAddress(), BROADCAST_PORT);
						sender.send(outPacket);
					}
				}
			} catch (Exception e) {
				Log.e("kyt", "error:"+e);
			}
		}
	}

}
