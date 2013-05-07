package com.kyt.cast.command;

import android.util.Log;

import com.kyt.cast.Contect;

import java.util.Arrays;

public class BroadcastLookupDeviceCommand  extends Command{
    private static Command command;
	private byte[] pkgHeader = Header.PACK_HEADER;
	private static final byte MASTER_CALL = 1;
	private byte[] lookupDevice = new byte[20];
	
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
	    if(null != getData() && getData().length > 0){
            System.arraycopy(getData(),32,lookupDevice,0,20);
            Log.v(Contect.TAG, new String(lookupDevice));
            //如果目标地址是自己则处理
            if(Arrays.equals(lookupDevice, getLocalAddress().getData())){
                return getPassivityCallData();
            }
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
		
		return null;
	}

	/**
	 * 主叫
	 * @return
	 */
	public byte[] getMasterCallData() {
		return null;
	}
}
