package cn.mointe.vaccination.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.provider.BabyProvider;

public class BabyDao {

	private ContentResolver mResolver;
	private VaccinationDao mVaccinationDao;

	public BabyDao(Context context) {
		mResolver = context.getContentResolver();
		mVaccinationDao = new VaccinationDao(context);
	}

	/**
	 * 添加baby
	 * 
	 * @param baby
	 * @return
	 */
	public boolean saveBaby(Baby baby) {
		boolean flag = false;

		ContentValues values = new ContentValues();

		values.put(DBHelper.BABY_COLUMN_NAME, baby.getName());
		values.put(DBHelper.BABY_COLUMN_BIRTHDAY, baby.getBirthdate());
		values.put(DBHelper.BABY_COLUMN_RESIDENCE, baby.getResidence());

		values.put(DBHelper.BABY_COLUMN_SEX, baby.getSex());
		values.put(DBHelper.BABY_COLUMN_IMAGE, baby.getImage());
		values.put(DBHelper.BABY_COLUMN_VACCINATION_PLACE,
				baby.getVaccination_place());

		values.put(DBHelper.BABY_COLUMN_VACCINATION_PHONE,
				baby.getVaccination_phone());
		values.put(DBHelper.BABY_COLUMN_IS_DEFAULT, baby.getIs_default());
		values.put(DBHelper.BABY_COLUMN_CITY_CODE, baby.getCityCode());

		try {
			mResolver.insert(BabyProvider.CONTENT_URI, values);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除baby
	 * 
	 * @param baby
	 * @return
	 */
	public boolean deleteBaby(Baby baby) {
		boolean flag = false;
		Uri uri = BabyProvider.CONTENT_URI;
		int result = mResolver.delete(uri, DBHelper.BABY_COLUMN_ID + "=?",
				new String[] { String.valueOf(baby.getId()) });
		if (result > 0) {
			flag = true;
			mVaccinationDao.deleteVaccinationsByBabyName(baby.getName());// 删除对应的接种列表
		}
		return flag;
	}

	/**
	 * 修改baby
	 * 
	 * @param baby
	 * @return
	 */
	public boolean updateBaby(Baby baby) {
		boolean flag = false;
		ContentValues values = new ContentValues();

		values.put(DBHelper.BABY_COLUMN_NAME, baby.getName());
		values.put(DBHelper.BABY_COLUMN_RESIDENCE, baby.getResidence());
		values.put(DBHelper.BABY_COLUMN_SEX, baby.getSex());

		values.put(DBHelper.BABY_COLUMN_IMAGE, baby.getImage());
		values.put(DBHelper.BABY_COLUMN_VACCINATION_PLACE,
				baby.getVaccination_place());
		values.put(DBHelper.BABY_COLUMN_VACCINATION_PHONE,
				baby.getVaccination_phone());
		values.put(DBHelper.BABY_COLUMN_CITY_CODE, baby.getCityCode());

		int result = mResolver.update(BabyProvider.CONTENT_URI, values,
				DBHelper.BABY_COLUMN_ID + "=?",
				new String[] { String.valueOf(baby.getId()) });
		if (result > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 查询所有baby
	 * 
	 * @return
	 */
	public List<Baby> queryBabys() {
		List<Baby> list = new ArrayList<Baby>();
		Cursor cursor = mResolver.query(BabyProvider.CONTENT_URI, null, null,
				null, null);

		while (cursor.moveToNext()) {

			int id = cursor.getInt(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_ID));
			String name = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_NAME));
			String residence = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_RESIDENCE));
			String sex = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_SEX));
			String place = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PLACE));
			String phone = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PHONE));
			String birthday = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_BIRTHDAY));
			String imageUri = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_IMAGE));
			String isdefault = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_IS_DEFAULT));
			String cityCode = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_CITY_CODE));
			Baby baby = new Baby(id, name, birthday, imageUri, residence, sex,
					place, phone, isdefault, cityCode);

			list.add(baby);
		}
		return list;
	}

	/**
	 * 修改为默认baby
	 * 
	 * @param baby
	 */
	public void updateBabyIsDefault(int id) {

		List<Baby> babys = queryBabys();
		for (int i = 0; i < babys.size(); i++) {
			ContentValues values = new ContentValues();
			if (babys.get(i).getId() == id) {
				values.put(DBHelper.BABY_COLUMN_IS_DEFAULT, "1");
			} else {
				values.put(DBHelper.BABY_COLUMN_IS_DEFAULT, "0");
			}
			mResolver.update(BabyProvider.CONTENT_URI, values,
					DBHelper.BABY_COLUMN_ID + "=?",
					new String[] { String.valueOf(babys.get(i).getId()) });
		}
	}

	/**
	 * 查询默认baby
	 * 
	 * @return
	 */
	public Baby getDefaultBaby() {
		Baby baby = null;
		ContentValues values = new ContentValues();
		values.put(DBHelper.BABY_COLUMN_IS_DEFAULT, "1");

		Cursor cursor = mResolver.query(BabyProvider.CONTENT_URI, null,
				DBHelper.BABY_COLUMN_IS_DEFAULT + "=?", new String[] { "1" },
				null);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_ID));
			String name = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_NAME));
			String residence = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_RESIDENCE));
			String sex = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_SEX));
			String place = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PLACE));
			String phone = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PHONE));
			String birthday = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_BIRTHDAY));
			String imageUri = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_IMAGE));
			String isdefault = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_IS_DEFAULT));
			String cityCode = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_CITY_CODE));
			baby = new Baby(id, name, birthday, imageUri, residence, sex,
					place, phone, isdefault, cityCode);
		}
		cursor.close();
		return baby;
	}

	/**
	 * Cursor转化为Baby对象
	 * 
	 * @param cursor
	 * @return
	 */
	public Baby cursorToBaby(Cursor cursor) {

		int id = cursor.getInt(cursor.getColumnIndex(DBHelper.BABY_COLUMN_ID));
		String name = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_NAME));
		String residence = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_RESIDENCE));

		String sex = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_SEX));
		String place = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PLACE));
		String phone = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PHONE));

		String birthday = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_BIRTHDAY));
		String imageUri = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_IMAGE));
		String isdefault = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_IS_DEFAULT));
		String cityCode = cursor.getString(cursor
				.getColumnIndex(DBHelper.BABY_COLUMN_CITY_CODE));

		Baby baby = new Baby(id, name, birthday, imageUri, residence, sex,
				place, phone, isdefault, cityCode);

		return baby;
	}

	/**
	 * 将baby修改为默认/非默认
	 * 
	 * @param baby
	 */
	public void updateBabyIsDefault(Baby baby) {
		ContentValues values = new ContentValues();
		if (baby.getIs_default().equals("1")) {
			values.put(DBHelper.BABY_COLUMN_IS_DEFAULT, "0");
		} else {
			values.put(DBHelper.BABY_COLUMN_IS_DEFAULT, "1");
		}
		mResolver.update(BabyProvider.CONTENT_URI, values,
				DBHelper.BABY_COLUMN_ID + "=?",
				new String[] { String.valueOf(baby.getId()) });
	}

	/**
	 * 检查baby是否存在
	 * 
	 * @param babyName
	 * @return
	 */
	public boolean checkBabyIsExist(String babyName) {
		boolean flag = false;
		Cursor cursor = mResolver.query(BabyProvider.CONTENT_URI, null,
				DBHelper.BABY_COLUMN_NAME + "=?", new String[] { babyName },
				null);
		if (cursor.moveToFirst()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 查询宝宝是否是默认宝宝
	 * 
	 * @param baby
	 * @return 1是默认 0不是默认
	 */
	public String checkIsDefault(Baby baby) {
		String isDefault = null;
		Cursor cursor = mResolver.query(BabyProvider.CONTENT_URI, null,
				DBHelper.BABY_COLUMN_NAME + "=?",
				new String[] { baby.getName() }, null);
		if (cursor.moveToFirst()) {
			isDefault = cursor.getString(cursor
					.getColumnIndex(DBHelper.BABY_COLUMN_IS_DEFAULT));
		}
		return isDefault;
	}

}
