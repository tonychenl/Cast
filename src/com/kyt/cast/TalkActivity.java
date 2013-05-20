
package com.kyt.cast;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.LocalSocket;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.view.SurfaceView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.kyt.cast.command.Command;
import com.kyt.cast.command.Header;

public class TalkActivity extends Activity {
    public static boolean isTalking = false;
    private LocalSocket localSocket;
    private Timer onLive;
    private byte[] M_ADDR;
    private byte[] M_IP;
    private byte[] L_ADDR;
    private byte[] L_IP;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        SurfaceView sfView = (SurfaceView) findViewById(R.id.surfaceView1);
        MediaPlayer mediaPlayer = new MediaPlayer();
        M_ADDR = getIntent().getByteArrayExtra("M_ADDR");
        M_IP = getIntent().getByteArrayExtra("M_IP");
        L_ADDR = getIntent().getByteArrayExtra("L_ADDR");
        L_IP = getIntent().getByteArrayExtra("L_IP");
        
        onLive = new Timer();
        onLive.schedule(new TimerTask() {
			@Override
			public void run() {
				/*在线确认*/
				byte[]  opendoor = new byte[61];
				System.arraycopy(Header.PACK_HEADER, 0, opendoor, 0, 6);
				opendoor[6] = (byte)150;
				opendoor[7] = 1;
				opendoor[8] = 9;
				System.arraycopy(M_ADDR, 0, opendoor, 9, 20);
				System.arraycopy(M_IP, 0, opendoor, 29, 4);
				System.arraycopy(L_ADDR, 0, opendoor, 33, 20);
				System.arraycopy(L_IP, 0, opendoor, 53, 4);
				System.arraycopy(String.valueOf(UUID.randomUUID().getLeastSignificantBits()).getBytes(),0,opendoor,57,4);
				try {
					UdpProcessService.put(opendoor, InetAddress.getByAddress(M_IP), Contect.BROADCAST_PORT);
					Log.v("line", "on line ...");
				} catch (Exception e) {
					Log.e("line", e.getMessage());
				}
				/*
				byte[]  opendoor = new byte[57];
				System.arraycopy(Header.PACK_HEADER, 0, opendoor, 0, 6);
				opendoor[6] = (byte)150;
				opendoor[7] = 1;
				opendoor[8] = 10;
				System.arraycopy(M_ADDR, 0, opendoor, 9, 20);
				System.arraycopy(M_IP, 0, opendoor, 29, 4);
				System.arraycopy(L_ADDR, 0, opendoor, 33, 20);
				System.arraycopy(L_IP, 0, opendoor, 53, 4);
				try {
					UdpProcessService.put(opendoor, InetAddress.getByAddress(M_IP), Contect.BROADCAST_PORT);
					Log.v("open", "on open ...");
				} catch (Exception e) {
					Log.e("open", e.getMessage());
				}*/
			}
		},new Date(),5000);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	onLive.cancel();
    }

}
