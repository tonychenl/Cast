package com.kyt.cast.command;

import android.util.Log;

import com.kyt.cast.Contect;
import com.kyt.cast.UdpProcessService;

import java.net.InetAddress;
import java.util.Arrays;

public class BroadcastLookupDeviceCommand  extends Command{
    private static Command command;
    private static final byte CMD = (byte)154;
	private byte[] pkgHeader = Header.PACK_HEADER;
	private byte[] lookupDevice = new byte[20];
	
	private BroadcastLookupDeviceCommand(){
	}
	
    public static Command getInstance() {
	    if(command==null){
            synchronized (BroadcastLookupDeviceCommand.class) {
                if(command==null){
                    command = new BroadcastLookupDeviceCommand();
                }
            }
        }
        return command;
    }
    
	@Override
    protected void doExecute() throws Exception{
        System.arraycopy(getData(),32,lookupDevice,0,20);
        Log.v(Contect.TAG, new String(lookupDevice));
        //�ж�������Դ�����з����Ǳ��з�
        if(getHeader().getType()==MASTER_CALL){
        	//���з�
        	//���Ŀ���ַ���Լ�����
            if(Arrays.equals(lookupDevice, getLocalAddress().getData())){
                returnLocalInfo();
            }
        }else{
        	//���з�
        	
        }
        
    }


    @Override
    protected void prepare()  throws Exception{
        Arrays.fill(lookupDevice, 0, lookupDevice.length, (byte)'0');
    }


    /**
	 * ����
	 * @return
	 */
	private void returnLocalInfo()  throws Exception{
		byte[] tmp = new byte[57];
		System.arraycopy(pkgHeader, 0, tmp, 0, 6); //��ͷ
		tmp[6] = CMD; //����
		tmp[7] = (byte)2; //��������
		System.arraycopy(getData(), 8, tmp, 8, 20);//���з���ַ
		System.arraycopy(getData(), 28, tmp, 28, 4);//���з�IP
		tmp[32] = (byte)1;//��ַ����
		System.arraycopy(getLocalAddress().getData(), 0, tmp, 33, 20);//������ַ
        System.arraycopy(getLocalIpAddress().getAddress(), 0, tmp, 53, 4);//����IP
        byte[] xx = getLocalIpAddress().getAddress();
        byte[] xxxx = Arrays.copyOfRange(tmp, 28, 32);
        //������ѹ�뷢���ж�
        InetAddress x = InetAddress.getByAddress(Arrays.copyOfRange(tmp, 28, 32));
        UdpProcessService.put(tmp, InetAddress.getByAddress(Arrays.copyOfRange(tmp, 28, 32)), Contect.BROADCAST_PORT);
	}

	/**
	 * ����
	 * @return
	 */
	public byte[] getMasterCallData() {
		return null;
	}
}
