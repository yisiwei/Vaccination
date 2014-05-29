package cn.mointe.vaccination.dao;

import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.BabyImage;
import cn.mointe.vaccination.provider.BabyImageProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

public class BabyImageDao {
	private ContentResolver mResolver;

	public BabyImageDao(Context context) {
		mResolver = context.getContentResolver();
	}

	public boolean saveBabyImage(BabyImage babyImage) {
		boolean flag = false;

		ContentValues values = new ContentValues();
		values.put(DBHelper.BABY_IMAGE_COLUMN_BABY_NICKNAME,
				babyImage.getBabyNickName());
		values.put(DBHelper.BABY_IMAGE_COLUMN_DATE, babyImage.getDate());
		values.put(DBHelper.BABY_IMAGE_COLUMN_IMG_PATH, babyImage.getImgPath());
		try {
			mResolver.insert(BabyImageProvider.CONTENT_URI, values);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}
}
