package com.kyt.cast.command;

import java.util.Arrays;


public class LocalAddress {
	byte    type;
	private byte[]  building = new byte[4];
	private byte[]  unit = new byte[2];
	private byte[]  floor = new byte[2];
	private byte[]  room  = new byte[2];
	byte	device;
	private byte[]  data = new byte[20];
	
	public LocalAddress(String address) throws Exception{
		super();
		this.type = address.substring(0, 1).getBytes()[0];
		this.building = address.substring(1, 5).getBytes();
		this.unit = address.substring(5, 7).getBytes();
		this.floor = address.substring(7, 9).getBytes();
		this.room = address.substring(9, 11).getBytes();
		this.device = address.substring(11, 12).getBytes()[0];
		
		Arrays.fill(data, 0, data.length, (byte)'0');
		data[0] = type;
		System.arraycopy(building, 0, data, 1, 4);
		System.arraycopy(unit, 0, data, 5, 2);
		System.arraycopy(floor, 0, data, 7, 2);
		System.arraycopy(room, 0, data, 9, 2);
		data[11] = device;
	}
	
	public byte[] getData(){
		return data;
	}
}
