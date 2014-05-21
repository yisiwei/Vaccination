package cn.mointe.vaccination.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Diary;
import cn.mointe.vaccination.provider.DiaryProvider;

public class DiaryDao {

	private ContentResolver mResolver;

	public DiaryDao(Context context) {
		mResolver = context.getContentResolver();
	}

	/**
	 * 添加日记
	 * 
	 * @param diary
	 * @return
	 */
	public boolean saveDiary(Diary diary) {
		boolean flag = false;
		ContentValues values = new ContentValues();
		values.put(DBHelper.DIARY_COLUMN_BABY_NICKNAME, diary.getBabyNickName());
		values.put(DBHelper.DIARY_COLUMN_DATE, diary.getDate());
		values.put(DBHelper.DIARY_COLUMN_DIARY_CONTENT, diary.getDiaryContent());
		try {
			mResolver.insert(DiaryProvider.CONTENT_URI, values);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 编辑日记
	 * 
	 * @param diary
	 * @return
	 */
	public boolean editDiary(Diary diary) {
		boolean flag = false;
		ContentValues values = new ContentValues();
		values.put(DBHelper.DIARY_COLUMN_BABY_NICKNAME, diary.getBabyNickName());
		values.put(DBHelper.DIARY_COLUMN_DATE, diary.getDate());
		values.put(DBHelper.DIARY_COLUMN_DIARY_CONTENT, diary.getDiaryContent());
		int count = mResolver.update(DiaryProvider.CONTENT_URI, values,
				DBHelper.DIARY_COLUMN_ID + "=?",
				new String[] { String.valueOf(diary.getId()) });
		if (count > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 查询日记List
	 * 
	 * @return
	 */
	public List<Diary> queryDiaries(String babyName) {
		List<Diary> diaries = new ArrayList<Diary>();

		Cursor cursor = mResolver
				.query(DiaryProvider.CONTENT_URI, null,
						DBHelper.DIARY_COLUMN_BABY_NICKNAME + "=?",
						new String[] { babyName }, DBHelper.DIARY_COLUMN_DATE
								+ " desc");
		while (cursor.moveToNext()) {
			Diary diary = cursorToDiary(cursor);
			diaries.add(diary);
		}
		return diaries;
	}

	/**
	 * 根据日期查询日记
	 * 
	 * @param date
	 * @return
	 */
	public Diary queryDiary(String babyName, String date) {
		Diary diary = null;
		Cursor cursor = mResolver.query(DiaryProvider.CONTENT_URI, null,
				DBHelper.DIARY_COLUMN_BABY_NICKNAME + "=? and "
						+ DBHelper.DIARY_COLUMN_DATE + "=? ", new String[] {
						babyName, date }, null);

		if (cursor.moveToFirst()) {
			diary = cursorToDiary(cursor);
		}

		return diary;
	}

	/**
	 * Cursor转化为Diary
	 * 
	 * @param cursor
	 * @return
	 */
	public Diary cursorToDiary(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(DBHelper.DIARY_COLUMN_ID));
		String date = cursor.getString(cursor
				.getColumnIndex(DBHelper.DIARY_COLUMN_DATE));
		String babyNickName = cursor.getString(cursor
				.getColumnIndex(DBHelper.DIARY_COLUMN_BABY_NICKNAME));
		String diaryContent = cursor.getString(cursor
				.getColumnIndex(DBHelper.DIARY_COLUMN_DIARY_CONTENT));
		Diary diary = new Diary(id, babyNickName, date, diaryContent);
		return diary;
	}
}
