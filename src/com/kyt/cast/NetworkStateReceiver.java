package com.kyt.cast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
	public NetworkStateReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				boolean isConnected = state == State.CONNECTED;// ��Ȼ����߿��Ը���ȷ��ȷ��״̬
				Log.v(Contect.TAG, "��������״̬�ı䣡");
				Intent service = new Intent(context, UdpProcessService.class);
				if (isConnected) {
					context.startService(service);
					Log.v(Contect.TAG, "��������״̬�ı䣡�򿪼���");
				} else {
					context.stopService(service);
					Log.v(Contect.TAG, "��������״̬�ı䣡�رռ���");
				}
			}
		}
	}
}
