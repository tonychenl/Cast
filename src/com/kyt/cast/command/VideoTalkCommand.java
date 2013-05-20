package com.kyt.cast.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Arrays;

import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Environment;

import com.kyt.cast.Contect;
import com.kyt.cast.TalkActivity;
import com.kyt.cast.UdpProcessService;

public class VideoTalkCommand extends Command {
    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8021796105434145476L;
	private static Command command;
    private static final byte CMD = (byte)150;
    private static boolean isCalling = false;
    public static boolean isReady = false;
    private static InetAddress broadcastIp;
    private LocalServerSocket localServer;
    private LocalSocket       localSocket;
    private OutputStream      outStream;
    
    private FileOutputStream out;
    
    private static final byte CALL      = 1; //－呼叫
    private static final byte BUSY      = 2; //－占线
    private static final byte REPLY     = 4; //－呼叫应答
    private static final byte START     = 6; //－开始通话（被叫方->主叫方，主叫方应答）
    private static final byte M_DATA    = 7; //－通话数据1（主叫方->被叫方）
    private static final byte S_DATA    = 8; //－通话数据2（被叫方->主叫方）
    private static final byte ONLINE    = 9; //－通话在线确认（被叫方发送，以便主叫方确认在线）
    private static final byte OPEN_DOOR = 10; //－远程开锁
    private static final byte FRAME_I   = 11; //－强制I帧请求
    private static final byte TOBIG     = 15; //－放大(720*480)
    private static final byte TOSMALL   = 16; //－缩小(352*240)
    private static final byte CANCEL    = 30; //－通话结束

    
    private VideoTalkCommand(){
    }
    
    public static Command getInstance() throws Exception{
        if(command==null){
            synchronized (VideoTalkCommand.class) {
                if(command==null){
                    broadcastIp = InetAddress.getByName(Contect.BROADCAST_IP);
                    command = new VideoTalkCommand();
                }
            }
        }
        return command;
    }

    @Override
    protected void prepare() {
    }
    
    @Override
    protected void doExecute() throws Exception{
        //被叫
        if(getHeader().getType()==MASTER_CALL){
            doReturnCall();
        }
    }
    
    /**
     * 被叫
     * @return
     */
    private void doReturnCall() throws Exception{
        byte opType = getData()[8];
        switch (opType) {
            case CALL: //呼叫请求
                if(isCalling){
                    //占线
                    buildReturnBusy();
                }else{
                    //应答
                    buildReturnReply();
                    
                    isCalling = true;
                    Intent talk = new Intent(getContext(), TalkActivity.class);
                    talk.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    talk.putExtra("M_ADDR", Arrays.copyOfRange(getData(), 9, 29));
                    talk.putExtra("M_IP", Arrays.copyOfRange(getData(), 29, 33));
                    talk.putExtra("L_ADDR", getLocalAddress().getData());
                    talk.putExtra("L_IP", getLocalIpAddress().getAddress());
                    getContext().startActivity(talk);
                    
                }
                break;
            case M_DATA:
            	if(!isCalling){
            		//buildReturnVideoData();
            	}else{
            		out.write(getData(),76,getData().length-76);
                	out.flush();
            	}
                break;
            case CANCEL: //通话结束
            	buildReturnCancel();
        		isCalling = false;
            	break;
        }
    }

    /**
     * 通话数据
     */
    private void buildReturnVideoData() throws Exception{
    	byte[]  response = new byte[10];
    	UdpProcessService.put(response, InetAddress.getByAddress(Arrays.copyOfRange(getData(), 29, 33)), Contect.BROADCAST_PORT);
	}

	/**
     * 通话结束
     * @param response
     */
	private void buildReturnCancel() throws Exception{
		byte[]  response = new byte[57];
		System.arraycopy(Header.PACK_HEADER, 0, response, 0, 6);
		response[6] = CMD;
		response[7] = PASSIVE_CALL;
		response[8] = CANCEL;
		System.arraycopy(getData(), 9, response, 9, 20);//主叫主地址
		System.arraycopy(getData(), 29, response, 29, 4);//主叫方ip
		System.arraycopy(getLocalAddress().getData(), 0, response, 33, 20);
		System.arraycopy(getLocalIpAddress().getAddress(), 0, response, 53, 4);
		//将数据压入发送列队
        UdpProcessService.put(response, InetAddress.getByAddress(Arrays.copyOfRange(getData(), 29, 33)), Contect.BROADCAST_PORT);
        
        out.close();
	}

	/**
	 * 呼叫回应
	 * @param response
	 */
	private void buildReturnReply() throws Exception{
		byte[] response = new byte[62];
		System.arraycopy(Header.PACK_HEADER, 0, response, 0, 6);
		response[6] = CMD;
		response[7] = MASTER_CALL;
		response[8] = REPLY;
		System.arraycopy(getData(), 9, response, 9, 20);//主叫主地址
		System.arraycopy(getData(), 29, response, 29, 4);//主叫方ip
		System.arraycopy(getLocalAddress().getData(), 0, response, 33, 20);
		System.arraycopy(getLocalIpAddress().getAddress(), 0, response, 53, 4);
		response[57]=0;
		System.arraycopy(broadcastIp.getAddress(), 0, response, 58, 4);
		//将数据压入发送列队
        UdpProcessService.put(response, InetAddress.getByAddress(Arrays.copyOfRange(getData(), 29, 33)), Contect.BROADCAST_PORT);
        
        File file = new File(Environment.getExternalStorageDirectory(), "tmp.mp4");
        out = new FileOutputStream(file,true);
	}

    /**
     * 构造占线
     * @param response
     */
	private void buildReturnBusy() throws Exception{
		byte[] response = new byte[57];
		System.arraycopy(Header.PACK_HEADER, 0, response, 0, 6);
		response[6] = CMD;
		response[7] = MASTER_CALL;
		response[8] = BUSY;
		System.arraycopy(getData(), 9, response, 9, 20);
		System.arraycopy(getData(), 29, response, 29, 4);
		System.arraycopy(getLocalAddress().getData(), 0, response, 33, 20);
		System.arraycopy(getLocalIpAddress().getAddress(), 0, response, 53, 4);
		//将数据压入发送列队
        UdpProcessService.put(response, InetAddress.getByAddress(Arrays.copyOfRange(getData(), 29, 33)), Contect.BROADCAST_PORT);
	}
    
    /**
     * 
     */
    class ListenTalk extends Thread{
        @Override
        public void run() {
            try {
                localServer = new LocalServerSocket("com.kyt.talk");
                isReady = true;
                localSocket = localServer.accept();
                outStream = localSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 主叫
     * @return
     */
    public byte[] getMasterCallData() {
        return null;
    }
}
