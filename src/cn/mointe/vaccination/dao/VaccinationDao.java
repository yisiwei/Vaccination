package cn.mointe.vaccination.dao;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.VaccinationRule;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;

public class VaccinationDao {

	private ContentResolver mResolver;
	private VaccinationRuleDao mVaccinationRuleDao;

	public VaccinationDao(Context context) {
		mResolver = context.getContentResolver();
		mVaccinationRuleDao = new VaccinationRuleDao(context);
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
	public void saveVaccinations(String birthday, String babyName) {
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
		Cursor cursor = mResolver
				.query(VaccinationProvider.CONTENT_URI, null,
						DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
								+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME
								+ " is not null and "
								+ DBHelper.VACCINATION_COLUMN_FINISH_TIME
								+ " is null ", new String[] { babyName },
						DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		while (cursor.moveToNext()) {
			String reserveTime = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
			int result = DateUtils.compareDateToToday(reserveTime);
			if (result >= 0) {
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

		String babyNickName = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME));

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

	/**
	 * 查询疫苗是否存在
	 * 
	 * @param id
	 * @return
	 */
	public boolean queryVaccinationById(Vaccination vac) {
		boolean flag = false;
		Cursor cursor = mResolver
				.query(VaccinationProvider.CONTENT_URI,
						null,
						DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
								+ "=? and "
								+ DBHelper.VACCINATION_COLUMN_VACCINE_NAME
								+ "=? and "
								+ DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER
								+ "=?",
						new String[] { vac.getBaby_nickname(),
								vac.getVaccine_name(),
								vac.getVaccination_number() }, null);
		if (cursor.moveToFirst()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * V_1.0.3 将选择的已接种疫苗改为已接种
	 * 
	 * @param vaccinations
	 */
	public void updateHaveToFinish(List<Vaccination> vaccinations) {

		if (vaccinations != null && vaccinations.size() != 0) {
			for (Vaccination vac : vaccinations) {
				boolean result = queryVaccinationById(vac);// 查询该疫苗是否存在
				if (result) {// 如果存在修改为已接种
					ContentValues values = new ContentValues();
					values.put(DBHelper.VACCINATION_COLUMN_FINISH_TIME, "注册之前");// vac.getReserve_time()

					mResolver
							.update(VaccinationProvider.CONTENT_URI,
									values,
									DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
											+ "=? and "
											+ DBHelper.VACCINATION_COLUMN_VACCINE_NAME
											+ "=? and "
											+ DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER
											+ "=?",
									new String[] { vac.getBaby_nickname(),
											vac.getVaccine_name(),
											vac.getVaccination_number() });
				} else {// 如不存在，则添加并为已接种
					ContentValues values = new ContentValues();

					values.put(DBHelper.VACCINATION_COLUMN_VACCINE_NAME,
							vac.getVaccine_name());
					// values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME,
					// vac.getReserve_time());
					values.put(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME,
							vac.getBaby_nickname());

					values.put(DBHelper.VACCINATION_COLUMN_MOON_AGE,
							vac.getMoon_age());
					values.put(DBHelper.VACCINATION_COLUMN_CHARGE_STANDARD,
							vac.getCharge_standard());
					values.put(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE,
							vac.getVaccine_type());

					values.put(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER,
							vac.getVaccination_number());

					values.put(DBHelper.VACCINATION_COLUMN_FINISH_TIME, "注册之前");

					mResolver.insert(VaccinationProvider.CONTENT_URI, values);
				}
			}
		}
	}

	/**
	 * 将用户选择的预约的疫苗改为未接种
	 * 
	 * @param vaccinations
	 * @param reservationDate
	 */
	public void updateChooseToNoVaccination(List<Vaccination> vaccinations,
			String reservationDate) {
		for (Vaccination vac : vaccinations) {
			boolean result = queryVaccinationById(vac);// 查询该疫苗是否存在
			if (result) {// 如果存在修改为未接种
				ContentValues values = new ContentValues();
				values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME,
						reservationDate);

				mResolver
						.update(VaccinationProvider.CONTENT_URI,
								values,
								DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
										+ "=? and "
										+ DBHelper.VACCINATION_COLUMN_VACCINE_NAME
										+ "=? and "
										+ DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER
										+ "=?",
								new String[] { vac.getBaby_nickname(),
										vac.getVaccine_name(),
										vac.getVaccination_number() });
			} else {// 如不存在，则添加并为未接种
				ContentValues values = new ContentValues();

				values.put(DBHelper.VACCINATION_COLUMN_VACCINE_NAME,
						vac.getVaccine_name());
				values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME,
						reservationDate);// 预约时间为用户选择的时间
				values.put(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME,
						vac.getBaby_nickname());

				values.put(DBHelper.VACCINATION_COLUMN_MOON_AGE,
						vac.getMoon_age());
				values.put(DBHelper.VACCINATION_COLUMN_CHARGE_STANDARD,
						vac.getCharge_standard());
				values.put(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE,
						vac.getVaccine_type());

				values.put(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER,
						vac.getVaccination_number());

				// values.put(DBHelper.VACCINATION_COLUMN_FINISH_TIME,
				// vac.getReserve_time());

				mResolver.insert(VaccinationProvider.CONTENT_URI, values);
			}
		}
	}

	/**
	 * V_1.0.3 生成接种列表
	 * 
	 * @param babyName
	 * @param birthday
	 * @return
	 * @throws ParseException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Vaccination> createVaccinations(String babyName, String birthday)
			throws ParseException, XmlPullParserException, IOException {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Date birthdayDate = DateUtils.stringToDate(birthday);
		Calendar calendar = Calendar.getInstance();
		List<VaccinationRule> vaccinationRules = mVaccinationRuleDao
				.getVaccinationRules();
		for (int i = 0; i < vaccinationRules.size(); i++) {
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
			String birthDayFormat = DateUtils.dateToString(nowDate);
			VaccinationRule rule = vaccinationRules.get(i);
			Vaccination vaccination = new Vaccination(rule.getVaccineName(),
					birthDayFormat, rule.getMoonAge(), rule.getVaccineType(),
					rule.getIsCharge(), rule.getVaccinationNumber(), babyName);
			vaccinations.add(vaccination);
		}
		return vaccinations;
	}

	/**
	 * V_1.0.3 生成接种列表(只是一类疫苗)
	 * 
	 * @param babyName
	 * @return
	 * @throws ParseException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public void createVaccinations(String babyName) throws ParseException,
			XmlPullParserException, IOException {
		List<VaccinationRule> vaccinationRules = mVaccinationRuleDao
				.getVaccinationRules();
		for (int i = 0; i < vaccinationRules.size(); i++) {
			VaccinationRule rule = vaccinationRules.get(i);
			if (rule.getVaccineType().equals("一类")) {
				Vaccination vaccination = new Vaccination(
						rule.getVaccineName(), null, rule.getMoonAge(),
						rule.getVaccineType(), rule.getIsCharge(),
						rule.getVaccinationNumber(), babyName);
				saveVaccination(vaccination);
			}
		}
	}

	/**
	 * 选择已接种过的疫苗
	 * 
	 * @param babyName
	 * @param birthdate
	 * @param addDate
	 * @return
	 * @throws ParseException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Vaccination> getVaccinationsOfChoose(String babyName,
			String birthdate, String addDate) throws ParseException,
			XmlPullParserException, IOException {
		List<Vaccination> list = new ArrayList<Vaccination>();
		List<Vaccination> vaccinations = createVaccinations(babyName, birthdate);
		for (Vaccination vaccination : vaccinations) {
			int result = DateUtils.compareDate(vaccination.getReserve_time(),
					addDate);
			if (result <= 0) {
				list.add(vaccination);
			}
		}
		return list;
	}

	/**
	 * 预约下次接种疫苗
	 * 
	 * @param babyName
	 * @param birthdate
	 * @return
	 * @throws ParseException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Vaccination> getVaccinationsOfReservation(String babyName,
			String birthdate) throws ParseException, XmlPullParserException,
			IOException {
		List<Vaccination> vaccinations = createVaccinations(babyName, birthdate);
		return vaccinations;
	}

	/**
	 * V_1.0.3 根据宝宝查询未预约疫苗
	 * 
	 * @param babyName
	 * @return
	 */
	public List<Vaccination> getNoReserveVaccinations(String babyName) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Cursor cursor = mResolver
				.query(VaccinationProvider.CONTENT_URI, null,
						DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
								+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME
								+ " is null and "
								+ DBHelper.VACCINATION_COLUMN_FINISH_TIME
								+ " is null ", new String[] { babyName }, null);
		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			vaccinations.add(vaccination);
		}

		return vaccinations;
	}

	/**
	 * 预约下次接种
	 * 
	 * @param vaccinations
	 * @param reserveDate
	 */
	public void reserveNextVaccinations(List<Vaccination> vaccinations,
			String reserveDate) {
		for (Vaccination vaccination : vaccinations) {
			updateReserveTimeById(vaccination.getId(), reserveDate);
		}
	}

	/**
	 * 查询二类疫苗(Xml)
	 * 
	 * @return
	 * @throws ParseException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Vaccination> queryTwoTypeVaccinesOfXml() throws ParseException,
			XmlPullParserException, IOException {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		List<VaccinationRule> vaccinationRules = mVaccinationRuleDao
				.getVaccinationRules();
		for (int i = 0; i < vaccinationRules.size(); i++) {
			VaccinationRule rule = vaccinationRules.get(i);
			if (rule.getVaccineType().equals("二类")) {
				Vaccination vaccination = new Vaccination(
						rule.getVaccineName(), null, rule.getMoonAge(),
						rule.getVaccineType(), rule.getIsCharge(),
						rule.getVaccinationNumber(), null);
				vaccinations.add(vaccination);
			}
		}
		return vaccinations;
	}

	/**
	 * 查询二类疫苗(DB)
	 * 
	 * @param babyName
	 * @return
	 */
	public List<Vaccination> queryTwoTypeVaccinesOfDB(String babyName) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_VACCINE_TYPE + "=?",
				new String[] { babyName, "二类" }, null);

		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			vaccinations.add(vaccination);
		}

		return vaccinations;
	}

	/**
	 * 添加二类疫苗
	 * 
	 * @param vaccinations
	 * @param babyName
	 * @param reserveDate
	 */
	public void addTwoTypeVaccine(List<Vaccination> vaccinations,
			String babyName, String reserveDate) {
		for (Vaccination vac : vaccinations) {
			ContentValues values = new ContentValues();

			values.put(DBHelper.VACCINATION_COLUMN_VACCINE_NAME,
					vac.getVaccine_name());
			values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME, reserveDate);// 预约时间为下次接种时间
			values.put(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME, babyName);

			values.put(DBHelper.VACCINATION_COLUMN_MOON_AGE, vac.getMoon_age());
			values.put(DBHelper.VACCINATION_COLUMN_CHARGE_STANDARD,
					vac.getCharge_standard());
			values.put(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE,
					vac.getVaccine_type());

			values.put(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER,
					vac.getVaccination_number());

			mResolver.insert(VaccinationProvider.CONTENT_URI, values);
		}
	}

	/**
	 * V_1.0.3 查询未接种的一类疫苗
	 * 
	 * @param babyName
	 * @return
	 */
	/*
	 * public List<Vaccination> queryType1NotVaccination(String babyName, String
	 * date) { List<Vaccination> vaccinations = new ArrayList<Vaccination>();
	 * Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
	 * DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and " +
	 * DBHelper.VACCINATION_COLUMN_VACCINE_TYPE + "=? and " +
	 * DBHelper.VACCINATION_COLUMN_FINISH_TIME + " is null and (" +
	 * DBHelper.VACCINATION_COLUMN_RESERVE_TIME + " is null or " +
	 * DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "=?)", new String[] {
	 * babyName, "一类", date }, null); while (cursor.moveToNext()) { Vaccination
	 * vaccination = cursorToVaccination(cursor); vaccinations.add(vaccination);
	 * } return vaccinations; }
	 */

	/**
	 * V_1.0.3 查询未接种的二类疫苗 <接种列表中没有二类疫苗，需要从基础表中找出所有二类疫苗，然后再到接种列表中找出
	 * 已预约二类疫苗，将其排除，最后得到的即未接种的二类疫苗>
	 * 
	 * @param babyName
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws ParseException
	 */
	public List<Vaccination> queryType2NotVaccination(String babyName)
			throws ParseException, XmlPullParserException, IOException {

		List<Vaccination> allType2List = queryTwoTypeVaccinesOfXml();
		List<Vaccination> alreadyReservationType2List = queryTwoTypeVaccinesOfDB(babyName);
		if (alreadyReservationType2List != null
				&& alreadyReservationType2List.size() != 0) {
			Iterator<Vaccination> iterator = allType2List.iterator();
			while (iterator.hasNext()) {
				Vaccination vac = iterator.next();
				for (Vaccination vaccination : alreadyReservationType2List) {
					if (vac.getVaccine_name().equals(
							vaccination.getVaccine_name())
							&& vac.getVaccination_number().equals(
									vaccination.getVaccination_number())) {
						iterator.remove();// 已预约过，将其移除
						continue;
					}
				}
			}
		}

		return allType2List;
	}

	/**
	 * V_1.0.3 预约一类疫苗
	 * 
	 * @param vaccination
	 * @return
	 */
	public boolean reservationType1(Vaccination vaccination,
			String reservationDate) {
		boolean flag = false;
		// 一类疫苗在接种列表中一定存在，所以直接修改预约时间即可
		ContentValues values = new ContentValues();
		values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME, reservationDate);
		int result = mResolver.update(VaccinationProvider.CONTENT_URI, values,
				DBHelper.VACCINATION_COLUMN_ID + "=?",
				new String[] { String.valueOf(vaccination.getId()) });
		if (result > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * V_1.0.3 预约二类疫苗
	 * 
	 * @param vaccination
	 * @param reserveDate
	 * @return
	 */
	public boolean reservationType2(Vaccination vaccination,
			String reservationDate) {
		boolean flag = false;
		// 先判断二类疫苗在接种列表中是否存在，如存在则修改预约时间即可，如不存在，则添加此二类疫苗
		boolean result = queryVaccinationById(vaccination);// 查询该疫苗是否存在
		if (result) {// 如果存在修改预约时间
			ContentValues values = new ContentValues();
			values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME,
					reservationDate);

			int count = mResolver.update(
					VaccinationProvider.CONTENT_URI,
					values,
					DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
							+ DBHelper.VACCINATION_COLUMN_VACCINE_NAME
							+ "=? and "
							+ DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER
							+ "=?",
					new String[] { vaccination.getBaby_nickname(),
							vaccination.getVaccine_name(),
							vaccination.getVaccination_number() });
			if (count > 0) {
				flag = true;
			}
		} else {// 如不存在，则添加
			ContentValues values = new ContentValues();

			values.put(DBHelper.VACCINATION_COLUMN_VACCINE_NAME,
					vaccination.getVaccine_name());
			values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME,
					reservationDate);// 预约时间为用户选择的时间
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
			flag = true;
		}
		return flag;
	}

	/**
	 * V_1.0.3 查询当前年、当前月预约的疫苗(当天及当天之后)
	 * 
	 * @param currentYear
	 * @param currentMonth
	 * @param babyName
	 * @return
	 */
	public List<Vaccination> getTagDate(int currentYear, int currentMonth,
			String babyName) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();

		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME
						+ " is not null and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + ">=?",
				new String[] { babyName, DateUtils.getCurrentFormatDate() },
				null);
		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			// TODO 判断预约时间是否是当前年、当前月
			String reserveDate = vaccination.getReserve_time();
			String[] arr = reserveDate.split("-");
			if (Integer.valueOf(arr[0]) == currentYear
					&& Integer.valueOf(arr[1]) == currentMonth) {
				vaccinations.add(vaccination);
			}
		}

		return vaccinations;
	}

	/**
	 * V_1.0.3 查询当前年、当前月预约的疫苗(当天之前预约未完成的)
	 * 
	 * @param currentYear
	 * @param currentMonth
	 * @param babyName
	 * @return
	 */
	public List<Vaccination> getTagDate3(int currentYear, int currentMonth,
			String babyName) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();

		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME
						+ " is not null and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "<? and "
						+ DBHelper.VACCINATION_COLUMN_FINISH_TIME + " is null",
				new String[] { babyName, DateUtils.getCurrentFormatDate() },
				null);
		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			// TODO 判断预约时间是否是当前年、当前月
			String reserveDate = vaccination.getReserve_time();
			String[] arr = reserveDate.split("-");
			if (Integer.valueOf(arr[0]) == currentYear
					&& Integer.valueOf(arr[1]) == currentMonth) {
				vaccinations.add(vaccination);
			}
		}

		return vaccinations;
	}

	/**
	 * V_1.0.3 查询当前年、当前月预约的疫苗(当天之前预约完成的)
	 * 
	 * @param currentYear
	 * @param currentMonth
	 * @param babyName
	 * @return
	 */
	public List<Vaccination> getTagDate2(int currentYear, int currentMonth,
			String babyName) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();

		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME
						+ " is not null and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "<? and "
						+ DBHelper.VACCINATION_COLUMN_FINISH_TIME
						+ " is not null",
				new String[] { babyName, DateUtils.getCurrentFormatDate() },
				null);
		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			// TODO 判断预约时间是否是当前年、当前月
			String reserveDate = vaccination.getReserve_time();
			String[] arr = reserveDate.split("-");
			if (Integer.valueOf(arr[0]) == currentYear
					&& Integer.valueOf(arr[1]) == currentMonth) {
				vaccinations.add(vaccination);
			}
		}

		return vaccinations;
	}

	/**
	 * 根据预约时间查询疫苗
	 * 
	 * @param babyName
	 * @param reservationDate
	 * @return
	 */
	public List<Vaccination> getVaccinationByReservationDate(String babyName,
			String reservationDate) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "=?",
				new String[] { babyName, reservationDate }, null);

		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			vaccinations.add(vaccination);
		}

		return vaccinations;
	}

	/**
	 * 根据日期查询当天已接种疫苗
	 * 
	 * @param babyName
	 * @param date
	 * @return
	 */
	public List<Vaccination> queryFinishVaccinaitons(String babyName,
			String date) {
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		Cursor cursor = mResolver.query(VaccinationProvider.CONTENT_URI, null,
				DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.VACCINATION_COLUMN_FINISH_TIME + "=?",
				new String[] { babyName, date }, null);
		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			vaccinations.add(vaccination);
		}
		return vaccinations;
	}

	/**
	 * 查询未接种疫苗
	 * 
	 * @param babyName
	 * @return
	 */
	public List<Vaccination> queryNotVaccinations(String babyName) {

		List<Vaccination> vaccinations = new ArrayList<Vaccination>();

		Cursor cursor = mResolver
				.query(VaccinationProvider.CONTENT_URI, null,
						DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
								+ DBHelper.VACCINATION_COLUMN_FINISH_TIME
								+ " is null ", new String[] { babyName }, null);
		while (cursor.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(cursor);
			vaccinations.add(vaccination);
		}

		return vaccinations;
	}

	/**
	 * 取消接种<接种日记中的>
	 * 
	 * @param babyName
	 * @param vaccineName
	 * @param vaccineNumber
	 */
	public void cancelVaccination(String babyName, String vaccineName,
			String vaccineNumber, String date) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.VACCINATION_COLUMN_RESERVE_TIME, date);
		values.put(DBHelper.VACCINATION_COLUMN_FINISH_TIME, date);
		mResolver
				.update(VaccinationProvider.CONTENT_URI,
						values,
						DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
								+ "=? and "
								+ DBHelper.VACCINATION_COLUMN_VACCINE_NAME
								+ "=? and "
								+ DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER
								+ "=?", new String[] { babyName, vaccineName,
								vaccineNumber });
	}
}
