package cn.mointe.vaccination.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.DiaryDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Diary;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.Log;

public class AddDiaryActivity extends Activity implements OnClickListener {

	private EditText mDiaryContentText;
	private GridView mGridView;

	private MyAdapter mAdapter;

	private TextView mTitleText;
	private ImageButton mLeftBtn;
	private ImageButton mRightBtn;

	private DiaryDao mDiaryDao;
	private BabyDao mBabyDao;

	private Diary mDiary;
	private Baby mDefaultBaby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_diary);

		mDiaryDao = new DiaryDao(this);
		mBabyDao = new BabyDao(this);

		mDefaultBaby = mBabyDao.getDefaultBaby();

		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mLeftBtn = (ImageButton) this.findViewById(R.id.title_left_imgbtn);
		mRightBtn = (ImageButton) this.findViewById(R.id.title_right_imgbtn);

		mDiaryContentText = (EditText) this
				.findViewById(R.id.add_diary_content);
		mGridView = (GridView) this.findViewById(R.id.add_diary_gridview);

		mTitleText.setText("写日记");
		mRightBtn.setImageResource(R.drawable.diary_save);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mAdapter = new MyAdapter(this, getImages());
		mGridView.setAdapter(mAdapter);

		mDiary = mDiaryDao.queryDiary(mDefaultBaby.getName(),
				DateUtils.getCurrentFormatDate());
		if (mDiary != null) {
			mDiaryContentText.setText(mDiary.getDiaryContent());
		}
	}

	private void saveDiary() {
		boolean result = false;
		if (mDiary != null) {
			Log.i("MainActivity", "编辑");
			mDiary.setDiaryContent(mDiaryContentText.getText().toString());
			result = mDiaryDao.editDiary(mDiary);
		} else {
			Log.i("MainActivity", "新增");
			Diary diary = new Diary();
			diary.setBabyNickName(mDefaultBaby.getName());
			diary.setDate(DateUtils.getCurrentFormatDate());
			diary.setDiaryContent(mDiaryContentText.getText().toString());
			result = mDiaryDao.saveDiary(diary);
		}
		if (result) {
			Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_imgbtn:
			this.finish();
			break;
		case R.id.title_right_imgbtn:
			saveDiary();
			break;

		default:
			break;
		}
	}

	private List<String> getImages() {
		List<String> images = new ArrayList<String>();
		images.add("add");
		return images;
	}

	/**
	 * 自定义Adapter
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		private Context context;
		private List<String> mImages;

		public MyAdapter(Context context, List<String> images) {
			this.context = context;
			this.mImages = images;
		}

		@Override
		public int getCount() {
			return mImages.size();
		}

		@Override
		public Object getItem(int position) {
			return mImages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.add_diary_image_gridview_item, null);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.add_diary_item_image);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String image = mImages.get(position);
			final int mPosition = position;
			if (position == mImages.size() - 1) {
				holder.imageView
						.setBackgroundResource(R.drawable.diary_add_image);
			} else {
				if (FileUtils.fileIsExist(image)) {
					Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(
							image, 100, 100);
					holder.imageView.setImageBitmap(bitmap);
				}
			}
			holder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mPosition == mImages.size() - 1) {
						Toast.makeText(context, "添加照片", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});

			return convertView;
		}

		private final class ViewHolder {
			ImageView imageView;
		}
	}

}
