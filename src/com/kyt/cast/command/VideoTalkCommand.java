package com.kyt.cast.command;

import com.kyt.cast.Contect;

import java.net.InetAddress;

public class VideoTalkCommand extends Command {
    private static Command command;
    private static final byte CMD = (byte)150;
    private static boolean isCalling = false;
    private static InetAddress broadcastIp;
    
    private static final byte CALL      = 1; //－呼叫
    private static final byte BUSY      = 2; //－占线
    private static final byte REPLY     = 4; //－呼叫应答
    private static final byte START     = 6; //－开始通话（被叫方->主叫方，主叫方应答）
    private static final byte M_DATA    = 7; //－通话数据1（主叫方->被叫方）
    private static final byte S_DATA    = 8; //－通话数据2（被叫方->主叫方）
    private static final byte ONLINE    = 9; //－通话在线确认（被叫方发送，以便主叫方确认在线）
    private static final byte OPEN_DOOR = 10; //－远程开锁
    private static final byte FRAME_I   = 11; //－强制I帧请求
    private static final byte TOBIG     = 15; //－放大(720*480)
    private static final byte TOSMALL   = 16; //－缩小(352*240)
    private static final byte CANCEL    = 30; //－通话结束

    
    private VideoTalkCommand(){
    }
    
    public static Command getInstance() throws Exception{
        if(command==null){
            synchronized (VideoTalkCommand.class) {
                if(command==null){
                    broadcastIp = InetAddress.getByName(Contect.BROADCAST_IP);
                    command = new VideoTalkCommand();
                }
            }
        }
        return command;
    }

    @Override
    protected void prepare() {
    }
    
    @Override
    protected byte[] doExecute() {
        //被叫
        if(getHeader().getType()==MASTER_CALL){
            return getPassivityCallData();
        }
        return null;
    }
    
    /**
     * 被叫
     * @return
     */
    private byte[] getPassivityCallData() {
        byte opType = getData()[8];
        byte[]  response;
        switch (opType) {
            case CALL:
                if(isCalling){
                    //占线
                    response = new byte[57];
                    System.arraycopy(Header.PACK_HEADER, 0, response, 0, 6);
                    response[6] = CMD;
                    response[7] = MASTER_CALL;
                    response[8] = BUSY;
                    System.arraycopy(getData(), 9, response, 9, 20);
                    System.arraycopy(getData(), 29, response, 29, 4);
                    System.arraycopy(getLocalAddress(), 0, response, 33, 20);
                    System.arraycopy(getLocalIpAddress().getAddress(), 0, response, 53, 4);
                }else{
                    //应答
                    response = new byte[62];
                    System.arraycopy(Header.PACK_HEADER, 0, response, 0, 6);
                    response[6] = CMD;
                    response[7] = MASTER_CALL;
                    response[8] = REPLY;
                    System.arraycopy(getData(), 9, response, 9, 20);
                    System.arraycopy(getData(), 29, response, 29, 4);
                    System.arraycopy(getLocalAddress(), 0, response, 33, 20);
                    System.arraycopy(getLocalIpAddress().getAddress(), 0, response, 53, 4);
                    response[57]=0;
                    System.arraycopy(broadcastIp.getAddress(), 0, response, 58, 4);
                }
                break;
            case M_DATA:
                isCalling = true;
                response = new byte[0];
                break;
            default:
                response = new byte[0];
                break;
        }
        return response;
    }

    /**
     * 主叫
     * @return
     */
    public byte[] getMasterCallData() {
        return null;
    }
}
