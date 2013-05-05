package com.kyt.cast.command;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.kyt.cast.Contect;

public class Header {
	public static final byte[]  PACK_HEADER = "XXXCID".getBytes();
	
	private byte[] header = new byte[6];
	private byte command;
	private byte type;
	
	private static Map<Object, Class> map = new HashMap<Object, Class>();
	
	static{
		map.put((byte)154, BroadcastLookupDeviceCommand.class);
	}
	
	public Header(byte[] data) {
		try {
			System.arraycopy(data, 0, header, 0, 6);
			command = data[6];
			type = data[7];
		} catch (Exception e) {
			Log.e(Contect.TAG, "解析包头出错");
		}
	}
	
	public Class getCommandClass(){
		return map.get(command);
	}
	
	public byte[] getHeader() {
		return header;
	}

	public void setHeader(byte[] header) {
		this.header = header;
	}

	public byte getCommand() {
		return command;
	}

	public void setCommand(byte command) {
		this.command = command;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public static void main(String[] args) {
		byte[]  data = new byte[8];
		System.arraycopy(PACK_HEADER, 0, data, 0, 6);
		data[6] = (byte)154;
		data[7] = 1;
		Header h = new Header(data);
		
		try {
			Class xx = h.getCommandClass();
			if(null != xx){
				BroadcastLookupDeviceCommand instance = (BroadcastLookupDeviceCommand) h.getCommandClass().newInstance();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
