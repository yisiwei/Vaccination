package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.ChooseAdapter;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.other.AddBabyAgent;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.CircleImageView;

public class VaccineChooseActivity extends ActionBarActivity {

	private static final String TAG = "MainActivity";

	private VaccinationDao mVaccinationDao;

	private CircleImageView mBabyImage;// 宝宝头像
	private Button mBtn;// 下一步按钮

	private Button mAllBtn;// 全选按钮
	private Button mNoneBtn;// 全不选按钮
	private ChooseAdapter mChooseAdapter;
	private ListView mVaccinationView;
	private List<Vaccination> mSelectVaccinations;// 选择的疫苗List

	private Baby mBaby;// 宝宝
	private String mFirstAdd;// 首次添加宝宝

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccine_choose);
		
		AddBabyAgent.getInstance().addActivity(this);

		mVaccinationDao = new VaccinationDao(getApplicationContext());

		mBabyImage = (CircleImageView) this.findViewById(R.id.cho_baby_image);
		mBtn = (Button) this.findViewById(R.id.cho_next);
		mAllBtn = (Button) this.findViewById(R.id.choose_btn_all);
		mNoneBtn = (Button) this.findViewById(R.id.choose_btn_none);
		mVaccinationView = (ListView) this.findViewById(R.id.choose_vac_list);

		mBaby = (Baby) getIntent().getSerializableExtra("baby");
		mFirstAdd = getIntent().getStringExtra("firstAdd");// 首次添加
		Log.i(TAG,
				mFirstAdd + "-传过来的baby：" + mBaby.getName() + "-" + mBaby.getBirthdate());
		if (mBaby != null) {
			Log.i(TAG, mBaby.getName());
			String imgUri = mBaby.getImage();
			if (!StringUtils.isNullOrEmpty(imgUri)) {
				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri,
						100, 100);
				mBabyImage.setImageBitmap(bitmap);
			} else {
				mBabyImage.setImageResource(R.drawable.default_img);
			}
		}

		// 全选按钮监听
		mAllBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChooseAdapter.selectAll();
			}
		});
		// 全不选按钮监听
		mNoneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChooseAdapter.disSelectAll();
			}
		});

		// adapter
		mChooseAdapter = new ChooseAdapter(this, getVaccinations(),
				new ChooseAdapter.OnSelectedItemChanged() {

					@Override
					public void getSelectedItem(Vaccination vaccination) {
//						Toast.makeText(getApplicationContext(),
//						vaccination.getVaccine_name()+"("+vaccination.getVaccination_number()+")",
//						Toast.LENGTH_SHORT).show();
					}

					@Override
					public void getSelectedCount(int count) {

					}
				});
		mVaccinationView.setAdapter(mChooseAdapter);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(
				R.string.loading_wait));
		mProgressDialog.setTitle(R.string.hint);

		// 确认按钮监听
		mBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 查询选择的疫苗，并将选择的疫苗以及之前的疫苗状态改为已完成
				mSelectVaccinations = mChooseAdapter.currentSelect();
				Intent intent = new Intent(VaccineChooseActivity.this, ReservationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("baby", mBaby);
				bundle.putString("firstAdd", mFirstAdd);
				//bundle.putSerializable("selectVaccinations", (Serializable)mSelectVaccinations);
				intent.putExtra("selectVaccinations", (Serializable)mSelectVaccinations);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}


	/**
	 * 根据宝宝注册时间估算已打过的疫苗
	 * 
	 * @return
	 */
	private List<Vaccination> getVaccinations() {
		List<Vaccination> vaccinations = null;
		if (mBaby != null) {
			String addDate = DateUtils.getCurrentFormatDate();
//			vaccinations = mVaccinationDao
//					.getVaccinationsByBabyNameAndAddBabyDate(mBaby.getName(),
//							addDate);
			try {
				vaccinations = mVaccinationDao
						.getVaccinationsOfChoose(mBaby.getName(), mBaby.getBirthdate(), addDate);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return vaccinations;
	}
}
