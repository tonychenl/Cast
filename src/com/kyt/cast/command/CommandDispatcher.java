package com.kyt.cast.command;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.kyt.cast.Contect;

import android.content.Context;
import android.util.Log;

public class CommandDispatcher {
    private static final CommandDispatcher  dispatcher = new CommandDispatcher();
	private Context context;
	private Header header;
	private byte[] data;
	
	private static final Map<Object, Class> map = new HashMap<Object, Class>();
    
	/**
	 * 初始化所有命令及对应的处理类
	 */
    static{
        map.put((byte)154, BroadcastLookupDeviceCommand.class);//广播查询主机
        map.put((byte)150, VideoTalkCommand.class);//可视对讲
    }
	
	private CommandDispatcher(){
	}
	
	public static CommandDispatcher getDispatcher(){
	    return dispatcher;
	}
	
	private void clear(){
	    this.context = null;
	    this.header = null;
	    this.data = null;
	}
	
	public void init(byte[] data,Context context) {
	    clear();
		this.context = context;
		this.data = data;
		this.header = new Header(Arrays.copyOfRange(data, 0, 8));
	}
	
	public void doDispatcher(){
		//检查包头是否是XXXCID
		if(!Arrays.equals(Header.PACK_HEADER, header.getHeader())){
			Log.v(Contect.TAG, "包头不正确丢弃！");
			return ;
		}
		//获取命令对应的类
		Class instance = getCommandClass(header.getCommand());
		if(null != instance){
			try {
			    Method getInstance = instance.getMethod("getInstance", null);
			    Command command = (Command) getInstance.invoke(instance, null);
				//Command command = (Command) instance.newInstance();
				command.setHeader(header);
				command.setLocalAddress(getLocalAddress());
				command.setLocalIpAddress(getLocalIpAddress());
				command.setData(this.data);
				command.setContext(this.context);
				command.execute();
			} catch (Exception e) {
				Log.e(Contect.TAG, e.getMessage());
			}
		}
	}
	
	private Class getCommandClass(byte tp) {
        return map.get(tp);
    }

    private LocalAddress getLocalAddress() throws Exception{
		return new LocalAddress("S00010101010");
	    //return new LocalAddress("Z00010000000");
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
