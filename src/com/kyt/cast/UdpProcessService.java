package com.kyt.cast;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kyt.cast.command.CommandDispatcher;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpProcessService  extends Service{
    private static final String BROADCAST_IP = "238.9.9.1";
    private static final int BROADCAST_PORT = 8302;
    private MulticastSocket broadSocket;
    private DatagramSocket sender;
    private InetAddress broadAddress;
    private GetPacket getPacket;
    private ProcessPacket processPacket;
    private static boolean isRun;
    public static BlockingQueue<DatagramPacket> queue = new LinkedBlockingQueue<DatagramPacket>();
    private static final int BUFF_SIZE = 512*3;
    
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        start();
    }

    public UdpProcessService() {
        super();
    }
    
    public void start(){
        try {
            if(!isRun){
                isRun = true;
                broadSocket = new MulticastSocket(BROADCAST_PORT);
                sender = new DatagramSocket();
                broadAddress = InetAddress.getByName(BROADCAST_IP);
                broadSocket.joinGroup(broadAddress);
                broadSocket.setLoopbackMode(false);
                getPacket = new GetPacket();
                processPacket = new ProcessPacket();
                getPacket.start();
                processPacket.start();
            }
        } catch (Exception e) {
        }
    }
    
   private void stop(){
       isRun = false;
   }
    
    /**
     * ��ȡ���ݰ��������ж�
     */
    class GetPacket extends Thread{
        public void run() {
            while(isRun){
                try {
                    DatagramPacket inPacket;
                    Log.v("kyt", "re....");
                    inPacket = new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);
                    broadSocket.receive(inPacket);
                    queue.put(inPacket);
                } catch (Exception e) {
                    Log.e("kyt", "error:"+e);
                }
            }
            broadSocket.close();
            Log.v("kyt", "stop.....");
        }
    }
    
    /**
     * ���ж���ȡ�����ݰ�����
     *
     */
    class ProcessPacket extends Thread{
        private CommandDispatcher dispatcher;
        private DatagramPacket outPacket;
        DatagramPacket  packet;
        byte[]  resault;
        @Override
        public void run() {
            while(isRun){
                try {
                    packet = queue.take();
                    packet.getData();
                    getApplicationContext();
                    dispatcher = new CommandDispatcher(packet.getData(), getApplicationContext());
                    resault = dispatcher.getDate();
                    if(null != resault && resault.length>0){
                        outPacket = new DatagramPacket(resault, 0, resault.length, packet.getAddress(), BROADCAST_PORT);
                        sender.send(outPacket);
                    }
                } catch (Exception e) {
                    Log.e("kyt", "�����������!"+e.getMessage());
                }
            }
            sender.close();
        }
    }
}