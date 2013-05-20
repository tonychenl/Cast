package com.kyt.cast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kyt.cast.command.CommandDispatcher;

public class UdpProcessService  extends Service{
    private MulticastSocket broadSocket;
    private InetAddress broadAddress;
    private GetPacket getPacket;
    private ProcessPacket processPacket;
    private OutPacket outPacket;
    private static boolean isRun;
    private static BlockingQueue<DatagramPacket> in_queue = new LinkedBlockingQueue<DatagramPacket>();
    private static BlockingQueue<DatagramPacket> out_queue = new LinkedBlockingQueue<DatagramPacket>();
    
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
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	this.stop();
    }

    public UdpProcessService() {
        super();
    }
    
    public void start(){
        try {
            if(!isRun){
                isRun = true;
                broadSocket = new MulticastSocket(Contect.BROADCAST_PORT);
                broadAddress = InetAddress.getByName(Contect.BROADCAST_IP);
                broadSocket.joinGroup(broadAddress);
                broadSocket.setLoopbackMode(false);
                getPacket = new GetPacket();
                processPacket = new ProcessPacket();
                processPacket.setPriority(7);
                outPacket = new OutPacket();
                outPacket.setPriority(Thread.MAX_PRIORITY);
                getPacket.start();
                processPacket.start();
                outPacket.start();
            }
        } catch (Exception e) {
        	Log.e(Contect.TAG, e.getMessage());
        }
    }
    
   private void stop(){
       isRun = false;
   }
   
   public static void put(byte[] data,InetAddress addr,int port){
	   try {
			out_queue.put(new DatagramPacket(data, data.length, addr, port));
		} catch (Exception e) {
			Log.e(Contect.TAG, "���뷢���жӳ���");
		}
   }
    
    /**
     * ��ȡ���ݰ��������ж�
     */
    class GetPacket extends Thread{
    	private CommandDispatcher dispatcher = CommandDispatcher.getDispatcher();
        public void run() {
            while(isRun){
                try {
                    DatagramPacket inPacket;
                    Log.v("kyt", "re....");
                    inPacket = new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);
                    broadSocket.receive(inPacket);
                    in_queue.put(inPacket);
                } catch (Exception e) {
                    Log.e("kyt", "error:"+e);
                }
            }
            if(!broadSocket.isClosed())
            	broadSocket.close();
            Log.v("kyt", "stop.....");
        }
    }
    
    /**
     * ���ж���ȡ�����ݰ�����
     *
     */
    class ProcessPacket extends Thread{
        private CommandDispatcher dispatcher = CommandDispatcher.getDispatcher();
        DatagramPacket  packet;
        byte[]  resault;
        @Override
        public void run() {
            while(isRun){
                try {
                    packet = in_queue.take();
                    if(null != packet){
                    	Log.v(Contect.TAG, "process....");
                        dispatcher.init(packet.getData(), getApplicationContext());
                        dispatcher.doDispatcher();
                    }
                } catch (Exception e) {
                    Log.e("kyt", "�����������!"+e.getMessage());
                }
            }
            if(!broadSocket.isClosed())
            	broadSocket.close();
        }
    }
    
    /**
     * �������ݰ�
     * @author Administrator
     *
     */
    class OutPacket extends Thread{
    	DatagramPacket  out_packet;
    	@Override
    	public void run() {
    		while(isRun){
    			try {
    				//out_packet = out_queue.take();
    				
    				out_packet = out_queue.take();
    				if(null != out_packet){
    					Log.v(Contect.TAG, "send....");
        				broadSocket.send(out_packet);
    				}
    				
				} catch (Exception e) {
					Log.e("kyt", "�����������!"+e.getMessage());
				}
    		}
    		 if(!broadSocket.isClosed())
             	broadSocket.close();
    	}
    }
}
