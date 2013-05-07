package com.kyt.cast.command;

import android.content.Context;

import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class Command {
	private Header header;
	private LocalAddress localAddress;
	private InetAddress localIpAddress;
	private byte[]  data;
	private Context context;
	
	public byte[] execute(){
	    prepare();
	    return doExecute();
	}
	
	protected abstract byte[] doExecute();    
	protected abstract void prepare();
	
    public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public LocalAddress getLocalAddress() {
		return localAddress;
	}
	public void setLocalAddress(LocalAddress localAddress) {
		this.localAddress = localAddress;
	}
	public InetAddress getLocalIpAddress() {
		return localIpAddress;
	}
	public void setLocalIpAddress(InetAddress localIpAddress) {
		this.localIpAddress = localIpAddress;
	}
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
