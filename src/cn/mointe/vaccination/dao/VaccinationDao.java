package cn.mointe.vaccination.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.StringUtils;

public class VaccinationDao {

	private ContentResolver mResolver;

	public VaccinationDao(Context context) {
		mResolver = context.getContentResolver();
	}

	/**
	 * 根据baby加载接种列表
	 * 
	 * @param babyName
	 * @return
	 */
	public List<Vaccination> loadVaccinations(String babyName) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Cursor cursor = mResolver
				.query(VaccinationProvider.CONTENT_URI,
						null,
						DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=?",
						new String[] { babyName },
						"case "
								+ DBHelper.VACCINATION_COLUMN_MOON_AGE
								+ " when '出生24小时内' then 1 when '1月龄' then 2 when '2月龄' then 3 when '3月龄' then 4 "
								+ " when '4月龄' then 5 when '5月龄' then 6 when '6月龄' then 7 when '7月龄' then 8 "
								+ " when '8月龄' then 9 when '9月龄' then 10 when '1周岁' then 11 when '14月龄' then 12 "
								+ " when '1岁半' then 13 when '2周岁' then 14 when '3周岁' then 15 when '4周岁' then 16 "
								+ " when '6周岁' then 17 end,"
								+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		while (cursor.moveToNext()) {

			int id = cursor.getInt(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_ID));

			String reserveDate = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
			String age = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_MOON_AGE));
			String vaccineName = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME));

			String vaccination_number = cursor
					.getString(cursor
							.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER));
			String vaccine_type = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE));
			String finish_time = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_FINISH_TIME));

			String charge_standard = cursor
					.getString(cursor
							.getColumnIndex(DBHelper.VACCINATION_COLUMN_CHARGE_STANDARD));

			Vaccination vaccination = new Vaccination();

			vaccination.setId(id);

			vaccination.setReserve_time(reserveDate);
			vaccination.setMoon_age(age);
			vaccination.setVaccine_name(vaccineName);

			vaccination.setVaccination_number(vaccination_number);
			vaccination.setVaccine_type(vaccine_type);
			vaccination.setCharge_standard(charge_standard);

			vaccination.setFinish_time(finish_time);

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
	public void savaVaccinations(String birthday, String babyName) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date birthdayDate = null;
		try {
			birthdayDate = format.parse(birthday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < Constants.VACCINE_NAME.length; i++) {
			calendar.setTime(birthdayDate);
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
					Constants.VACCINE_TYPE2[i], Constants.CHARGE_STANDARD[i],
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

	/**
	 * 根据Baby昵称删除接种类表
	 * 
	 * @param babyName
	 */
	public void deleteVaccinationsByBabyName(String babyName) {

		mResolver.delete(VaccinationProvider.CONTENT_URI,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=?",
				new String[] { babyName });
	}

	/**
	 * 修改预约时间
	 * 
	 * @param id
	 * @param reserve_time
	 */
	public void updateReserveTimeById(int id, String reserve_time) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME, reserve_time);

		mResolver.update(VaccinationProvider.CONTENT_URI, values,
				DBHelper.VACCINATION_COLUMN_ID + "=?",
				new String[] { String.valueOf(id) });
	}

	/**
	 * 完成接种
	 * 
	 * @param vaccination
	 */
	public void updateFinishTimeById(Vaccination vaccination) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.VACCINATION_COLUMN_FINISH_TIME,
				vaccination.getFinish_time());

		mResolver.update(VaccinationProvider.CONTENT_URI, values,
				DBHelper.VACCINATION_COLUMN_ID + "=?",
				new String[] { String.valueOf(vaccination.getId()) });
	}

	/**
	 * 修改宝宝昵称 <当宝宝昵称修改时,对应的接种列表的宝宝昵称也需要修改>
	 * 
	 * @param oldNickName
	 * @param newNickName
	 */
	public void updateBabyNickName(String oldNickName, String newNickName) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME, newNickName);
		mResolver.update(VaccinationProvider.CONTENT_URI, values,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=?",
				new String[] { oldNickName });
	}

	/**
	 * 接种完成后查询下次接种日期
	 * 
	 * @param babyName
	 *            宝宝昵称
	 * @param currentVaccinationDate
	 *            当前接种日期
	 * @return
	 * @throws ParseException
	 */
	public String findNextVaccinationDate(String babyName,
			String currentVaccinationDate) throws ParseException {
		List<Vaccination> vaccinations = loadVaccinations(babyName);
		Date current = DateUtils.stringToDate(currentVaccinationDate);
		for (Vaccination vaccination : vaccinations) {
			Date next = DateUtils.stringToDate(vaccination.getReserve_time());
			int resultNext = DateUtils.compareDateToToday(vaccination
					.getReserve_time());
			int result = next.compareTo(current);
			if (result == 1 && resultNext == 1) {
				return vaccination.getReserve_time();
			}
		}
		return null;
	}

	/**
	 * 查询下次接种日期
	 * 
	 * @param babyName
	 * @return
	 * @throws ParseException
	 */
	public String findNextDate(String babyName) throws ParseException {
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=?",
				new String[] { babyName },
				DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		while (cursor.moveToNext()) {
			String reserveTime = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
			String finishTime = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_FINISH_TIME));
			int result = DateUtils.compareDateToToday(reserveTime);
			if (StringUtils.isNullOrEmpty(finishTime) && result >= 0) {
				cursor.close();
				return reserveTime;
			}
		}
		return null;
	}

	/**
	 * baby昵称和注册时间查询已经过期的疫苗
	 * 
	 * @param babyName
	 *            宝宝昵称
	 * @param addDate
	 *            添加宝宝日期
	 * @return
	 */
	public List<Vaccination> getVaccinationsByBabyNameAndAddBabyDate(
			String babyName, String addDate) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "<=?",
				new String[] { babyName, addDate },
				DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			vaccinations.add(vaccination);
		}
		cursor.close();
		return vaccinations;
	}

	/**
	 * 将Cursor转换成Vaccination
	 * 
	 * @param cursor
	 * @return
	 */
	public Vaccination cursorToVaccination(Cursor cursor) {

		int id = cursor.getInt(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_ID));
		
		String babyNickName = cursor.getString(cursor.getColumnIndex(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME));

		String reserveDate = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
		String age = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_MOON_AGE));
		String vaccineName = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME));

		String vaccination_number = cursor
				.getString(cursor
						.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER));
		String vaccine_type = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE));
		String finish_time = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_FINISH_TIME));

		String charge_standard = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_CHARGE_STANDARD));

		Vaccination vaccination = new Vaccination();

		vaccination.setId(id);
		vaccination.setBaby_nickname(babyNickName);

		vaccination.setReserve_time(reserveDate);
		vaccination.setMoon_age(age);
		vaccination.setVaccine_name(vaccineName);

		vaccination.setVaccination_number(vaccination_number);
		vaccination.setVaccine_type(vaccine_type);
		vaccination.setCharge_standard(charge_standard);

		vaccination.setFinish_time(finish_time);

		return vaccination;
	}

	/**
	 * 根据选择的疫苗查询该疫苗的默认预约时间
	 * 
	 * @param babyName
	 * @param vaccineName
	 * @param vaccineNumber
	 * @return
	 */
	public String getReserveDateByChooseVaccine(String babyName,
			String vaccineName, String vaccineNumber) {
		String reserveDate = null;
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_VACCINE_NAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER
						+ "=? ", new String[] { babyName, vaccineName,
						vaccineNumber },
				DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		if (cursor.moveToFirst()) {
			reserveDate = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
		}
		cursor.close();
		return reserveDate;
	}

	/**
	 * 根据baby昵称和预约时间查询小于该时间的疫苗信息
	 * 
	 * @param babyName
	 * @param reserveTime
	 * @return
	 */
	public List<Vaccination> getVaccinationsByReserveTime(String babyName,
			String reserveTime) {
		List<Vaccination> list = new ArrayList<Vaccination>();
		Vaccination vaccination = null;
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "<=?",
				new String[] { babyName, reserveTime },
				DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		while (cursor.moveToNext()) {
			vaccination = cursorToVaccination(cursor);
			list.add(vaccination);
		}
		cursor.close();
		return list;
	}

	/**
	 * 将已过期的疫苗一键改为已接种
	 * 
	 * @param vaccinations
	 */
	public void updateAllDueToFinish(List<Vaccination> vaccinations) {

		for (Vaccination vac : vaccinations) {
			ContentValues values = new ContentValues();
			values.put(DBHelper.VACCINATION_COLUMN_FINISH_TIME,
					vac.getReserve_time());

			mResolver.update(VaccinationProvider.CONTENT_URI, values,
					DBHelper.VACCINATION_COLUMN_ID + "=?",
					new String[] { String.valueOf(vac.getId()) });
		}
	}

}
