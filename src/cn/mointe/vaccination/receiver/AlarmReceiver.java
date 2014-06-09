package cn.mointe.vaccination.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.mointe.vaccination.tools.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String data = bundle.getString("data");
		Log.i("MainActivity", "收到广播-" + data);
	}

}
