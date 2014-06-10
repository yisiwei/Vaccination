package cn.mointe.vaccination.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.AddRecordActivity;
import cn.mointe.vaccination.adapter.DiaryAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.VaccinationRecord;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.CircleImageView;

import com.umeng.analytics.MobclickAgent;

/**
 * 接种日记界面
 * 
 */
public class VaccineDiaryFragment extends Fragment implements OnClickListener {

	private ListView mDiaryListView;
	private DiaryAdapter mDiaryAdapter;

	//private DiaryDao mDiaryDao;

	private BabyDao mBabyDao;
	private Baby mDefaultBaby;

	private VaccinationDao mVaccinationDao;
	// private List<Vaccination> mFinishVaccines;

	private ImageButton mAddBtn;// 补录

	private LoaderManager mLoaderManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBabyDao = new BabyDao(getActivity());
		//mDiaryDao = new DiaryDao(getActivity());
		mVaccinationDao = new VaccinationDao(getActivity());
		mLoaderManager = getLoaderManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_vaccine_diary, null);

		mDiaryListView = (ListView) view.findViewById(R.id.diary_list);
		mAddBtn = (ImageButton) view.findViewById(R.id.diary_add_btn);

		View headView = LayoutInflater.from(getActivity()).inflate(
				R.layout.diary_head, null);
		mDiaryListView.addHeaderView(headView, null, false);

		CircleImageView babyImageView = (CircleImageView) headView
				.findViewById(R.id.diary_baby_image);
		TextView babyNameView = (TextView) headView
				.findViewById(R.id.diary_baby_name);

		mDefaultBaby = mBabyDao.getDefaultBaby();

		mAddBtn.setOnClickListener(this);
		// 设置宝宝信息
		babyNameView.setText(mDefaultBaby.getName());
		String imgUri = mDefaultBaby.getImage();
		if (!StringUtils.isNullOrEmpty(imgUri)) {
			// XXX 判断图片路径是否存在
			Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri, 100,
					100);
			babyImageView.setImageBitmap(bitmap);
		} else {
			babyImageView.setImageResource(R.drawable.default_img);
		}
		
		mLoaderManager.initLoader(303, null, mCallBacks);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("VaccineDiaryFragment"); // 统计页面
		Log.i("MainActivity", "VaccineFragment OnResume");
		//mDiaryAdapter = new DiaryAdapter(getActivity(), getDiaries());
		//mDiaryListView.setAdapter(mDiaryAdapter);
		//mDiaryAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("VaccineDiaryFragment");
		Log.i("MainActivity", "VaccineFragment onPause");
	}

	/**
	 * 数据
	 * 
	 * @return
	 */
//	private List<Diary> getDiaries() {
//		List<Diary> diaries = mDiaryDao.queryDiaries(mDefaultBaby.getName());
//		for (Diary diary : diaries) {
//			List<Vaccination> vaccinations = mVaccinationDao
//					.queryFinishVaccinaitons(mDefaultBaby.getName(),
//							diary.getDate());
//			if (vaccinations != null && vaccinations.size() > 0) {
//				List<String> vaccines = new ArrayList<String>();
//				for (Vaccination vac : vaccinations) {
//					vaccines.add(vac.getVaccine_name() + "("
//							+ vac.getVaccination_number() + ")");
//				}
//				diary.setVaccines(vaccines);
//			}
//
//			List<String> images = new ArrayList<String>();
//			images.add(mDefaultBaby.getImage());
//			images.add(mDefaultBaby.getImage());
//			diary.setImages(images);
//		}
//		return diaries;
//	}

	private LoaderCallbacks<Cursor> mCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(getActivity());
			loader.setUri(VaccinationProvider.CONTENT_URI);
			loader.setProjection(new String[] { DBHelper.VACCINATION_COLUMN_FINISH_TIME });
			loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
					+ "=? and " + DBHelper.VACCINATION_COLUMN_RESERVE_TIME
					+ " is not null and "
					+ DBHelper.VACCINATION_COLUMN_FINISH_TIME
					+ " is not null group by "
					+ DBHelper.VACCINATION_COLUMN_FINISH_TIME);
			loader.setSelectionArgs(new String[] { mDefaultBaby.getName() });
			loader.setSortOrder(DBHelper.VACCINATION_COLUMN_FINISH_TIME
					+ " desc");
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			List<VaccinationRecord> vaccinationRecords = new ArrayList<VaccinationRecord>();
			while (data.moveToNext()) {
				VaccinationRecord vaccinationRecord = new VaccinationRecord();
				String finishDate = data.getString(data.getColumnIndex(DBHelper.VACCINATION_COLUMN_FINISH_TIME));
				vaccinationRecord.setDate(finishDate);
				List<String> vaccines =  mVaccinationDao.getFinishVaccineByDate(mDefaultBaby.getName(), finishDate);
				vaccinationRecord.setVaccines(vaccines);
				vaccinationRecords.add(vaccinationRecord);
			}
			mDiaryAdapter = new DiaryAdapter(getActivity(), vaccinationRecords);
			mDiaryListView.setAdapter(mDiaryAdapter);
			mDiaryAdapter.notifyDataSetChanged();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}

	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.diary_add_btn) {
			startActivity(new Intent(getActivity(), AddRecordActivity.class));
		}
	}

}
