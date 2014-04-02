package cn.mointe.vaccination.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.PackageUtil;

/**
 * 设置界面
 * 
 */
public class SettingActivity extends ActionBarActivity implements
		OnClickListener {

	private final static String TAG = "MainActivity";

	private ImageButton mSettingNotifyBtn;
	private boolean mIsNotifyOn;
	private VaccinationPreferences mPreference;
	private Button mVaccinationBeforeTime;
	private String[] mRemindTimeItems;

	private ActionBar mBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vaccination_remind);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		mPreference = new VaccinationPreferences(this);
		mSettingNotifyBtn = (ImageButton) this
				.findViewById(R.id.setting_notify_turn);
		mVaccinationBeforeTime = (Button) this
				.findViewById(R.id.vaccination_before_time);

		mIsNotifyOn = mPreference.getNotify();
		Log.e(TAG, "notify==" + mIsNotifyOn);
		if (mIsNotifyOn) {
			mSettingNotifyBtn.setBackgroundResource(R.drawable.btn_on);
		} else {
			mSettingNotifyBtn.setBackgroundResource(R.drawable.btn_off);
		}

		mRemindTimeItems = getResources().getStringArray(R.array.remind_time);

		mSettingNotifyBtn.setOnClickListener(this);
		mVaccinationBeforeTime.setOnClickListener(this);

		switch (mPreference.getRemindTime()) {
		case 1:
			mVaccinationBeforeTime.setText(mRemindTimeItems[0]);
			break;
		case 2:
			mVaccinationBeforeTime.setText(mRemindTimeItems[1]);
			break;
		case 3:
			mVaccinationBeforeTime.setText(mRemindTimeItems[2]);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_notify_turn:// 是否提醒
			if (mIsNotifyOn) {
				mSettingNotifyBtn.setBackgroundResource(R.drawable.btn_off);
				mPreference.setNotify(false);
				mIsNotifyOn = false;
				Intent remindService = new Intent(this,
						VaccinationRemindService.class);
				stopService(remindService);
			} else {
				mSettingNotifyBtn.setBackgroundResource(R.drawable.btn_on);
				mPreference.setNotify(true);
				mIsNotifyOn = true;
				Intent remindService = new Intent(this,
						VaccinationRemindService.class);
				startService(remindService);
			}
			break;
		case R.id.vaccination_before_time:// 提前几天提醒
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.choose_remind_time);
			builder.setItems(mRemindTimeItems,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:// 接种前1天
								mPreference.setRemindTime(1);
								mVaccinationBeforeTime
										.setText(mRemindTimeItems[0]);
								break;
							case 1:// 接种前2天
								mPreference.setRemindTime(2);
								mVaccinationBeforeTime
										.setText(mRemindTimeItems[1]);
								break;
							case 2:// 接种前3天
								mPreference.setRemindTime(3);
								mVaccinationBeforeTime
										.setText(mRemindTimeItems[2]);
								break;
							default:
								break;
							}
							// 如果服务启动正在运行，重新启动
							if (PackageUtil.isServiceRunning(
									getApplicationContext(),
									Constants.REMIND_SERVICE)) {
								stopService(new Intent(getApplicationContext(),
										VaccinationRemindService.class));
							}
							startService(new Intent(getApplicationContext(),
									VaccinationRemindService.class));
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create();
			builder.show();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
