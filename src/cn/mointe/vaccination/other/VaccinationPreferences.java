package cn.mointe.vaccination.other;

import android.content.Context;
import android.content.SharedPreferences;

public class VaccinationPreferences {

	private static final String SHAREDPREFERENCES = "sharedPreferences";
	private SharedPreferences mPreferences;

	public VaccinationPreferences(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				SHAREDPREFERENCES, Context.MODE_PRIVATE);
		this.mPreferences = preferences;
	}

	// 是否提醒
	public boolean getNotify() {
		return mPreferences.getBoolean("notify", false);
	}

	public void setNotify(boolean b) {
		mPreferences.edit().putBoolean("notify", b).commit();
	}

	// 是否存在Baby
	public boolean getIsExistBaby() {
		return mPreferences.getBoolean("IsExistBaby", false);
	}

	public void setIsExistBaby(boolean b) {
		mPreferences.edit().putBoolean("IsExistBaby", b).commit();
	}

	// 接种提醒日期
	public String getRemindDate() {
		return mPreferences.getString("remindDate", null);
	}

	public void setRemindDate(String remindDate) {
		mPreferences.edit().putString("remindDate", remindDate).commit();
	}

	// 提醒时间(提前多少天提醒)
	public int getRemindTime() {
		return mPreferences.getInt("remindTime", 1);
	}

	public void setRemindTime(int remindTime) {
		mPreferences.edit().putInt("remindTime", remindTime).commit();
	}

}
