package com.kyt.cast.command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

import com.kyt.cast.Contect;

import android.util.Log;

public class BroadcastLookupDeviceCommand  extends Command{
	private byte[] pkgHeader = Header.PACK_HEADER;
	private static final byte MASTER_CALL = 1;
	private DatagramPacket pack;
	private byte[] lookupDevice = new byte[20];
	
	@Override
	public byte[] execute() {
		pack = new DatagramPacket(new byte[48], 48);
		try {
			getSocket().receive(pack);
			if(null != pack && pack.getLength() > 0){
				System.arraycopy(pack.getData(),24,lookupDevice,0,20);
				Log.v(Contect.TAG, new String(pack.getData()));
				//如果目标地址是自己则处理
				if(Arrays.equals(lookupDevice, getLocalAddress().getData())){
					return getPassivityCallData();
				}
			}
		} catch (IOException e) {
			Log.e(Contect.TAG, "获取数据包出错");
		}
		return null;
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
