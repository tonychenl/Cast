package com.kyt.cast.command;

import android.util.Log;

import com.kyt.cast.Contect;

public class Header {
	public static final byte[]  PACK_HEADER = "XXXCID".getBytes();
	
	private byte[] header = new byte[6];
	private byte command;
	private byte type;
	
	
	public Header(byte[] data) {
		try {
			System.arraycopy(data, 0, header, 0, 6);
			command = data[6];
			type = data[7];
		} catch (Exception e) {
			Log.e(Contect.TAG, "解析包头出错");
		}
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

}
