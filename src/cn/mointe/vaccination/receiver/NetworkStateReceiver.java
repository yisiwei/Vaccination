package cn.mointe.vaccination.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.NetworkUtil;

public class NetworkStateReceiver extends BroadcastReceiver {

	private static final String TAG = "MainActivity";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "network state changed.");
		if (!NetworkUtil.isNetworkAvailable(context)) {
			Toast.makeText(context, "network disconnected!", Toast.LENGTH_SHORT)
					.show();

		}
	}


}
