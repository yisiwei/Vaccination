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

	public BabyDao(Context context) {
		mResolver = context.getContentResolver();
	}

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
		values.put(DBHelper.BABY_COLUMN_IS_DEFAULT,
				baby.getIs_default());

		try {
			mResolver.insert(BabyProvider.CONTENT_URI, values);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean deleteBaby(Baby baby) {
		boolean flag = false;
		Uri uri = BabyProvider.CONTENT_URI;
		try {
			mResolver.delete(uri, DBHelper.BABY_COLUMN_ID + "=?",
					new String[] { String.valueOf(baby.getId()) });
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

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
		values.put(DBHelper.BABY_COLUMN_IS_DEFAULT,
				baby.getIs_default());

		try {
			mResolver.update(BabyProvider.CONTENT_URI, values,
					DBHelper.BABY_COLUMN_ID + "=?",
					new String[] { String.valueOf(baby.getId()) });
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public List<Baby> queryBabys() {
		List<Baby> list = new ArrayList<Baby>();
		Cursor cursor = mResolver.query(BabyProvider.CONTENT_URI, null, null,
				null, null);
		
		while(cursor.moveToNext()) {
			
			int id = cursor.getInt(cursor.getColumnIndex(DBHelper.BABY_COLUMN_ID));
			String name = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_NAME));
			String residence = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_RESIDENCE));
			String sex = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_SEX));
			String place = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PLACE));
			String phone = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_VACCINATION_PHONE));
			String birthday = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_BIRTHDAY));
			String imageUri = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_IMAGE));
			String isdefault = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_COLUMN_IS_DEFAULT));
			Baby baby = new Baby(id, name, birthday, imageUri, residence, sex, place, phone, isdefault);
			
			list.add(baby);
		}
		return list;
	}
}
