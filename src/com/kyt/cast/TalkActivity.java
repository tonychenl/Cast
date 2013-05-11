
package com.kyt.cast;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.LocalSocket;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.SurfaceView;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class TalkActivity extends Activity {
    public static boolean isTalking = false;
    private LocalSocket localSocket;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        SurfaceView sfView = (SurfaceView) findViewById(R.id.surfaceView1);
        MediaPlayer mediaPlayer = new MediaPlayer();
        //等待视频通讯命令中发送数据线程完成
        /*while(true)
        {
            if(VideoTalkCommand.isReady)
                break;
        }*/
        //localSocket = new LocalSocket();
        //localSocket.connect(new LocalSocketAddress("com.kyt.talk"));
        try {
            /*DatagramSocket dsocket = new DatagramSocket(8888, InetAddress.getLocalHost());
            ParcelFileDescriptor pfd = ParcelFileDescriptor.fromDatagramSocket(dsocket);
            mediaPlayer.setDataSource(pfd.getFileDescriptor());
            mediaPlayer.setDisplay(sfView.getHolder());
            mediaPlayer.prepare();
            mediaPlayer.start();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
