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
    
    private static final byte CALL      = 1; //������
    private static final byte BUSY      = 2; //��ռ��
    private static final byte REPLY     = 4; //������Ӧ��
    private static final byte START     = 6; //����ʼͨ�������з�->���з������з�Ӧ��
    private static final byte M_DATA    = 7; //��ͨ������1�����з�->���з���
    private static final byte S_DATA    = 8; //��ͨ������2�����з�->���з���
    private static final byte ONLINE    = 9; //��ͨ������ȷ�ϣ����з����ͣ��Ա����з�ȷ�����ߣ�
    private static final byte OPEN_DOOR = 10; //��Զ�̿���
    private static final byte FRAME_I   = 11; //��ǿ��I֡����
    private static final byte TOBIG     = 15; //���Ŵ�(720*480)
    private static final byte TOSMALL   = 16; //����С(352*240)
    private static final byte CANCEL    = 30; //��ͨ������

    
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
        //����
        if(getHeader().getType()==MASTER_CALL){
            doReturnCall();
        }
    }
    
    /**
     * ����
     * @return
     */
    private void doReturnCall() throws Exception{
        byte opType = getData()[8];
        switch (opType) {
            case CALL: //��������
                if(isCalling){
                    //ռ��
                    buildReturnBusy();
                }else{
                    //Ӧ��
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
            case CANCEL: //ͨ������
            	buildReturnCancel();
        		isCalling = false;
            	break;
        }
    }

    /**
     * ͨ������
     */
    private void buildReturnVideoData() throws Exception{
    	byte[]  response = new byte[10];
    	UdpProcessService.put(response, InetAddress.getByAddress(Arrays.copyOfRange(getData(), 29, 33)), Contect.BROADCAST_PORT);
	}

	/**
     * ͨ������
     * @param response
     */
	private void buildReturnCancel() throws Exception{
		byte[]  response = new byte[57];
		System.arraycopy(Header.PACK_HEADER, 0, response, 0, 6);
		response[6] = CMD;
		response[7] = PASSIVE_CALL;
		response[8] = CANCEL;
		System.arraycopy(getData(), 9, response, 9, 20);//��������ַ
		System.arraycopy(getData(), 29, response, 29, 4);//���з�ip
		System.arraycopy(getLocalAddress().getData(), 0, response, 33, 20);
		System.arraycopy(getLocalIpAddress().getAddress(), 0, response, 53, 4);
		//������ѹ�뷢���ж�
        UdpProcessService.put(response, InetAddress.getByAddress(Arrays.copyOfRange(getData(), 29, 33)), Contect.BROADCAST_PORT);
        
        out.close();
	}

	/**
	 * ���л�Ӧ
	 * @param response
	 */
	private void buildReturnReply() throws Exception{
		byte[] response = new byte[62];
		System.arraycopy(Header.PACK_HEADER, 0, response, 0, 6);
		response[6] = CMD;
		response[7] = MASTER_CALL;
		response[8] = REPLY;
		System.arraycopy(getData(), 9, response, 9, 20);//��������ַ
		System.arraycopy(getData(), 29, response, 29, 4);//���з�ip
		System.arraycopy(getLocalAddress().getData(), 0, response, 33, 20);
		System.arraycopy(getLocalIpAddress().getAddress(), 0, response, 53, 4);
		response[57]=0;
		System.arraycopy(broadcastIp.getAddress(), 0, response, 58, 4);
		//������ѹ�뷢���ж�
        UdpProcessService.put(response, InetAddress.getByAddress(Arrays.copyOfRange(getData(), 29, 33)), Contect.BROADCAST_PORT);
        
        File file = new File(Environment.getExternalStorageDirectory(), "tmp.mp4");
        out = new FileOutputStream(file,true);
	}

    /**
     * ����ռ��
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
		//������ѹ�뷢���ж�
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
     * ����
     * @return
     */
    public byte[] getMasterCallData() {
        return null;
    }
}
