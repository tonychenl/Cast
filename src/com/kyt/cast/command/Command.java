package com.kyt.cast.command;

import android.content.Context;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class Command  implements Serializable{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1904098368175675990L;
	private Header header;
	private LocalAddress localAddress;
	private InetAddress localIpAddress;
	private byte[]  data;
	private Context context;
	protected static final byte PASSIVE_CALL = 2;
	protected static final byte MASTER_CALL = 1;
	
	public void execute() throws Exception{
	    prepare();
	    if(null != data && data.length>0 ){
	        doExecute();
	    }
	}
	
	protected abstract void prepare() throws Exception;
	protected abstract void doExecute() throws Exception;    
	
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
