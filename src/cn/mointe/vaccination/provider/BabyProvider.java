package cn.mointe.vaccination.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import cn.mointe.vaccination.db.DBHelper;

public class BabyProvider extends ContentProvider {

	private ContentResolver mContentResolver;
	public static final Uri CONTENT_URI = Uri
			.parse("content://cn.mointe.baby.babyprovider/baby");

	protected DBHelper mDBHelper;

	public static final int ALLROWS = 1;
	public static final int SINGLE_ROW = 2;
	public static final UriMatcher URI_MATCHER;

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI("cn.mointe.baby.babyprovider", "baby", ALLROWS);
		URI_MATCHER.addURI("cn.mointe.baby.babyprovider", "baby/#", SINGLE_ROW);
	}

	@Override
	public boolean onCreate() {
		mContentResolver = getContext().getContentResolver();
		mDBHelper = new DBHelper(getContext());
		return (mDBHelper == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case ALLROWS:
			return "vnd.android.cursor.dir/baby";
		case SINGLE_ROW:
			return "vnd.android.cursor.item/baby";
		default:
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		long rowid = db.insert(DBHelper.BABY_TABLE_NAME, null, contentValues);
		if (rowid > 0) {
			Uri insertUri = ContentUris.withAppendedId(CONTENT_URI, rowid);
			mContentResolver.notifyChange(uri, null);
			return insertUri;
		} else {
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}

		/*
		 * long id = 0; switch (uriMatcher.match(uri)) { case ALLROWS: id =
		 * db.insert(DBHelper.BABY_TABLE_NAME, null, contentValues); return
		 * ContentUris.withAppendedId(uri, id); case SINGLE_ROW: id =
		 * db.insert(DBHelper.BABY_TABLE_NAME, null, contentValues); String path
		 * = uri.toString(); return Uri.parse(path.substring(0,
		 * path.lastIndexOf("/")) + id); default:
		 * 
		 * throw new IllegalArgumentException("Unknown URI" + uri); }
		 */
		// return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		Cursor cursor = null;
		switch (URI_MATCHER.match(uri)) {
		case ALLROWS:
			cursor = db.query(DBHelper.BABY_TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case SINGLE_ROW:
			long rowid = ContentUris.parseId(uri);
			String where = DBHelper.BABY_COLUMN_ID + "=" + rowid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			cursor = db.query(DBHelper.BABY_COLUMN_NAME, projection, where,
					selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		cursor.setNotificationUri(mContentResolver, uri);
		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = 0;
		switch (URI_MATCHER.match(uri)) {
		case ALLROWS:
			count = db.delete(DBHelper.BABY_TABLE_NAME, selection,
					selectionArgs);
			break;
		case SINGLE_ROW:
			long babyid = ContentUris.parseId(uri);
			String where = DBHelper.BABY_COLUMN_ID + "=" + babyid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			count = db.delete(DBHelper.BABY_TABLE_NAME, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		mContentResolver.notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = 0;
		switch (URI_MATCHER.match(uri)) {
		case ALLROWS:
			count = db.update(DBHelper.BABY_TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case SINGLE_ROW:
			long questionid = ContentUris.parseId(uri);
			String where = "id = " + questionid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			count = db.update(DBHelper.BABY_TABLE_NAME, values, where,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		mContentResolver.notifyChange(uri, null);
		return count;
	}

}
