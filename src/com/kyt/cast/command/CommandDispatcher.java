package com.kyt.cast.command;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

import com.kyt.cast.Contect;

import android.content.Context;
import android.util.Log;

public class CommandDispatcher {
	private Context context;
	private Header header;
	private MulticastSocket socket;
	public CommandDispatcher(byte[] data,Context context,MulticastSocket socket) {
		super();
		this.context = context;
		this.socket = socket;
		this.header = new Header(data);
	}
	
	public byte[] getDate(){
		//检查包头是否是XXXCID
		if(!Arrays.equals(Header.PACK_HEADER, header.getHeader())){
			Log.v(Contect.TAG, "包头不正确丢弃！");
			return null;
		}
		//获取命令对应的类
		Class instance = header.getCommandClass();
		if(null != instance){
			try {
				Command command = (Command) instance.newInstance();
				command.setHeader(header);
				command.setLocalAddress(getLocalAddress());
				command.setLocalIpAddress(getLocalIpAddress());
				command.setSocket(socket);
				return command.execute();
			} catch (Exception e) {
				Log.e(Contect.TAG, e.getMessage());
			}
		}
		return null;
	}
	
	private LocalAddress getLocalAddress() throws Exception{
		return new LocalAddress("S00010101010");
	}
	
	  /**
	 * 获取本地IP地址
	 * @return
	 */
	private InetAddress getLocalIpAddress() {  
        try {  
            for (Enumeration<NetworkInterface> en = NetworkInterface  
                    .getNetworkInterfaces(); en.hasMoreElements();) {  
                NetworkInterface intf = en.nextElement();  
                for (Enumeration<InetAddress> enumIpAddr = intf  
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                    InetAddress inetAddress = enumIpAddr.nextElement();  
                    if (!inetAddress.isLoopbackAddress()) {
                    	if (inetAddress instanceof Inet4Address) {
							return inetAddress;
						}
                    }  
                }  
            }  
        } catch (SocketException ex) {  
            Log.e("WifiPreference IpAddress", ex.toString());  
        }  
        return null;  
    }
}
