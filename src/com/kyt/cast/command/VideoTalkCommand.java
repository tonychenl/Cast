package com.kyt.cast.command;

import com.kyt.cast.Contect;

import java.net.InetAddress;

public class VideoTalkCommand extends Command {
    private static Command command;
    private static final byte CMD = (byte)150;
    private static boolean isCalling = false;
    private static InetAddress broadcastIp;
    
    private static final byte CALL      = 1; //������
    private static final byte BUSY      = 2; //��ռ��
    private static final byte REPLY     = 4; //������Ӧ��
    private static final byte START     = 6; //����ʼͨ�������з�->���з������з�Ӧ��
    private static final byte M_DATA    = 7; //��ͨ������1�����з�->���з���
    private static final byte S_DATA    = 8; //��ͨ������2�����з�->���з���
    private static final byte ONLINE    = 9; //��ͨ������ȷ�ϣ����з����ͣ��Ա����з�ȷ�����ߣ�
    private static final byte OPEN_DOOR = 10; //��Զ�̿���
    private static final byte FRAME_I   = 11; //��ǿ��I֡����
    private static final byte TOBIG     = 15; //���Ŵ�(720*480)
    private static final byte TOSMALL   = 16; //����С(352*240)
    private static final byte CANCEL    = 30; //��ͨ������

    
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
        //����
        if(getHeader().getType()==MASTER_CALL){
            return getPassivityCallData();
        }
        return null;
    }
    
    /**
     * ����
     * @return
     */
    private byte[] getPassivityCallData() {
        byte opType = getData()[8];
        byte[]  response;
        switch (opType) {
            case CALL:
                if(isCalling){
                    //ռ��
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
                    //Ӧ��
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
     * ����
     * @return
     */
    public byte[] getMasterCallData() {
        return null;
    }
}
