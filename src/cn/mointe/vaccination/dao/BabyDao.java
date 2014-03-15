package cn.mointe.vaccination.dao;

import java.util.ArrayList;
import java.util.List;

import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.provider.BabyProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

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
		values.put(DBHelper.BABY_COLUMN_VACCINATION_PLACE,
				baby.getVaccination_place());
		values.put(DBHelper.BABY_COLUMN_VACCINATION_PHONE,
				baby.getVaccination_phone());
		values.put(DBHelper.BABY_COLUMN_IS_DEFAULT, baby.getIs_default());

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
			mVaccinationDao.deleteVaccinationsByBabyName(baby.getName());
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
		values.put(DBHelper.BABY_COLUMN_VACCINATION_PLACE,
				baby.getVaccination_place());
		values.put(DBHelper.BABY_COLUMN_VACCINATION_PHONE,
				baby.getVaccination_phone());

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
			Baby baby = new Baby(id, name, birthday, imageUri, residence, sex,
					place, phone, isdefault);

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

	// 查询默认baby
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
			baby = new Baby(id, name, birthday, imageUri, residence, sex,
					place, phone, isdefault);
		}
		return baby;
	}

}
