package cn.mointe.vaccination.other;

import android.content.Context;
import android.content.SharedPreferences;

public class VaccinationPreferences {

	private static final String SHAREDPREFERENCES = "sharedPreferences";
	private SharedPreferences mPreferences;

	public VaccinationPreferences(Context context) {
		this.mPreferences = context.getSharedPreferences(SHAREDPREFERENCES,
				Context.MODE_PRIVATE);
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
		return mPreferences.getString("remindDate", "");
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

	// 接种前一周是否提醒
	public boolean getWeekBeforeIsRemind() {
		return mPreferences.getBoolean("weekBeforeIsRemind", true);
	}

	public void setWeekBeforeIsRemind(boolean b) {
		mPreferences.edit().putBoolean("weekBeforeIsRemind", b).commit();
	}

	// 接种前一天是否提醒
	public boolean getDayBeforeIsRemind() {
		return mPreferences.getBoolean("dayBeforeIsRemind", true);
	}

	public void setDayBeforeIsRemind(boolean b) {
		mPreferences.edit().putBoolean("dayBeforeIsRemind", b).commit();
	}

	// 接种当天是否提醒
	public boolean getTodayIsRemind() {
		return mPreferences.getBoolean("todayIsRemind", true);
	}

	public void setTodayIsRemind(boolean b) {
		mPreferences.edit().putBoolean("todayIsRemind", b).commit();
	}

	// 接种前一周提醒时间
	public String getWeekBeforeRemindTime() {
		return mPreferences.getString("weekBeforeRemindTime", "09:30");
	}

	public void setWeekBeforeRemindTime(String remindTime) {
		mPreferences.edit().putString("weekBeforeRemindTime", remindTime)
				.commit();
	}

	// 接种前一天提醒时间
	public String getDayBeforeRemindTime() {
		return mPreferences.getString("dayBeforeRemindTime", "09:30");
	}

	public void setDayBeforeRemindTime(String remindTime) {
		mPreferences.edit().putString("dayBeforeRemindTime", remindTime)
				.commit();
	}

	// 接种当天提醒时间
	public String getTodayRemindTime() {
		return mPreferences.getString("todayRemindTime", "09:30");
	}

	public void setTodayRemindTime(String remindTime) {
		mPreferences.edit().putString("todayRemindTime", remindTime).commit();
	}

	// QQ登录 Openid
	public String getOpenid() {
		return mPreferences.getString("openid", "");
	}

	public void setOpenid(String openid) {
		mPreferences.edit().putString("openid", openid).commit();
	}

	// QQ登录 AccessToken
	public String getAccessToken() {
		return mPreferences.getString("access_token", "");
	}

	public void setAccessToken(String access_token) {
		mPreferences.edit().putString("access_token", access_token).commit();
	}

	// QQ登录 ExpiresIn
	public String getExpiresIn() {
		return mPreferences.getString("expires_in", "");
	}

	public void setExpiresIn(String expires_in) {
		mPreferences.edit().putString("expires_in", expires_in).commit();
	}
}
