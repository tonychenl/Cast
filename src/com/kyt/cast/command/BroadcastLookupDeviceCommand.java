package com.kyt.cast.command;

import android.util.Log;

import com.kyt.cast.Contect;

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
    protected byte[] doExecute() {
        System.arraycopy(getData(),32,lookupDevice,0,20);
        Log.v(Contect.TAG, new String(lookupDevice));
        //如果目标地址是自己则处理
        if(Arrays.equals(lookupDevice, getLocalAddress().getData())){
            return getPassivityCallData();
        }
        return null;
    }


    @Override
    protected void prepare() {
        Arrays.fill(lookupDevice, 0, lookupDevice.length, (byte)'0');
    }


    /**
	 * 被叫
	 * @return
	 */
	private byte[] getPassivityCallData() {
		byte[] tmp = new byte[57];
		System.arraycopy(pkgHeader, 0, tmp, 0, 6); //包头
		tmp[6] = CMD; //命令
		tmp[7] = (byte)2; //命令类型
		System.arraycopy(getData(), 8, tmp, 8, 20);//主叫方地址
		System.arraycopy(getData(), 28, tmp, 28, 4);//主叫方IP
		tmp[32] = (byte)1;//地址个数
		System.arraycopy(getLocalAddress().getData(), 0, tmp, 33, 20);//解析地址
        System.arraycopy(getLocalIpAddress().getAddress(), 0, tmp, 53, 4);//解析IP
		return tmp;
	}

	/**
	 * 主叫
	 * @return
	 */
	public byte[] getMasterCallData() {
		return null;
	}
}
