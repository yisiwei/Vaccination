package cn.mointe.vaccination.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.Constants;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class VaccinationDao {

	private ContentResolver mResolver;

	public VaccinationDao(Context context) {
		mResolver = context.getContentResolver();
	}

	// 加载接种列表
	public List<Vaccination> loadVaccinations() {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				null, null, DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		while (cursor.moveToNext()) {

			String reserveDate = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
			String age = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_MOON_AGE));
			String vaccineName = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME));

			Vaccination vaccination = new Vaccination();

			vaccination.setReserve_time(reserveDate);
			vaccination.setMoon_age(age);
			vaccination.setVaccine_name(vaccineName);

			vaccinations.add(vaccination);

		}
		cursor.close();
		return vaccinations;

	}

	/**
	 * 生成接种列表
	 * 
	 * @param birthday
	 * @param babyName
	 */
	@SuppressLint("SimpleDateFormat")
	public void savaVaccinations(String birthday, String babyName) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date birthdayDate = null;
		try {
			birthdayDate = format.parse(birthday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthdayDate);
		for (int i = 0; i < Constants.VACCINE_NAME.length; i++) {
			if (i == 0 || i == 1) {// 出生24小时内
				calendar.add(Calendar.MONTH, 0);
			} else if (i == 2) {// 1月龄
				calendar.add(Calendar.MONTH, 1);
			} else if (i == 3 || i == 4 || i == 5 || i == 6 || i == 7) {// 2月龄
				calendar.add(Calendar.MONTH, 2);
			} else if (i == 8 || i == 9 || i == 10 || i == 11 || i == 12
					|| i == 13) {// 3月龄
				calendar.add(Calendar.MONTH, 3);
			} else if (i == 14 || i == 15 || i == 16 || i == 17 || i == 18
					|| i == 19) {// 4月龄
				calendar.add(Calendar.MONTH, 4);
			} else if (i == 20 || i == 21) {// 5月龄
				calendar.add(Calendar.MONTH, 5);
			} else if (i == 22 || i == 23 || i == 24) {// 6月龄
				calendar.add(Calendar.MONTH, 6);
			} else if (i == 25) {// 7月龄
				calendar.add(Calendar.MONTH, 7);
			} else if (i == 26) {// 8月龄
				calendar.add(Calendar.MONTH, 8);
			} else if (i == 27) {// 9月龄
				calendar.add(Calendar.MONTH, 9);
			} else if (i == 28 || i == 29) {// 1周岁
				calendar.add(Calendar.MONTH, 12);
			} else if (i == 30) {// 14月龄
				calendar.add(Calendar.MONTH, 14);
			} else if (i == 31 || i == 32 || i == 33 || i == 34 || i == 35
					|| i == 36 || i == 37) {// 1岁半
				calendar.add(Calendar.MONTH, 18);
			} else if (i == 38 || i == 39 || i == 40 || i == 41) {// 2周岁
				calendar.add(Calendar.YEAR, 2);
			} else if (i == 42) { // 3周岁
				calendar.add(Calendar.YEAR, 3);
			} else if (i == 43 || i == 44) { // 4周岁
				calendar.add(Calendar.YEAR, 4);
			} else if (i == 45 || i == 46 || i == 47) { // 6周岁
				calendar.add(Calendar.YEAR, 6);
			}

			Date nowDate = calendar.getTime();
			String birthDayFormat = format.format(nowDate);
			saveVaccination(new Vaccination(Constants.VACCINE_NAME[i],
					birthDayFormat, Constants.MOON_AGE[i],
					Constants.VACCINE_TYPE[i], Constants.CHARGE_STANDARD[i],
					Constants.VACCINATION_NUMBER[i], babyName));
		}
	}

	/**
	 * 保存Vaccination
	 * 
	 * @param vaccination
	 */
	public void saveVaccination(Vaccination vaccination) {

		ContentValues values = new ContentValues();

		values.put(DBHelper.VACCINATION_COLUMN_VACCINE_NAME,
				vaccination.getVaccine_name());
		values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME,
				vaccination.getReserve_time());
		values.put(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME,
				vaccination.getBaby_nickname());

		values.put(DBHelper.VACCINATION_COLUMN_MOON_AGE,
				vaccination.getMoon_age());
		values.put(DBHelper.VACCINATION_COLUMN_CHARGE_STANDARD,
				vaccination.getCharge_standard());
		values.put(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE,
				vaccination.getVaccine_type());

		values.put(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER,
				vaccination.getVaccination_number());

		mResolver.insert(VaccinationProvider.CONTENT_URI, values);
	}

}
