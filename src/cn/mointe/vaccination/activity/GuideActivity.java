package cn.mointe.vaccination.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.tools.PackageUtil;

/**
 * 引导页
 * 
 * @author yi_siwei
 * 
 */
public class GuideActivity extends Activity {

	private ViewPager mViewPager;
	private List<View> mDots; // 圆点

	private List<View> mViews;// 引导页界面

	private static final String SHAREDPREFERENCES = "sharedPreferences";

	private boolean isExistBaby = false;// 是否存在Baby

	private SharedPreferences preferences;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置无标题
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.activity_guide);

		// 引导页界面
		LayoutInflater inflater = LayoutInflater.from(this);
		mViews = new ArrayList<View>();
		mViews.add(inflater.inflate(R.layout.guide_one, null));
		mViews.add(inflater.inflate(R.layout.guide_two, null));
		mViews.add(inflater.inflate(R.layout.guide_three, null));
		mViews.add(inflater.inflate(R.layout.guide_four, null));
		mViews.add(inflater.inflate(R.layout.guide_normal, null));// 最后一页空白

		// 圆点
		mDots = new ArrayList<View>();
		mDots.add(this.findViewById(R.id.guide_dot0));
		mDots.add(this.findViewById(R.id.guide_dot1));
		mDots.add(this.findViewById(R.id.guide_dot2));
		mDots.add(this.findViewById(R.id.guide_dot3));

		// 初始化ViewPager
		mViewPager = (ViewPager) this.findViewById(R.id.guide_vp);
		// 设置Adapter
		mViewPager.setAdapter(new MyAdapter());
		// 设置监听
		mViewPager.setOnPageChangeListener(new MyPagerChangeListener());

		preferences = this.getSharedPreferences(SHAREDPREFERENCES,
				Context.MODE_PRIVATE);
		isExistBaby = preferences.getBoolean("IsExistBaby", false);
	}

	/**
	 * 自定义PagerAdapter
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(mViews.get(position));

			if (position == mViews.size() - 1 - 1) { // 最后一页为空白页所以再-1
				ImageButton imageButton = (ImageButton) container
						.findViewById(R.id.guide_imageButton);
				imageButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						setVersionCode();
						goHome();
					}
				});
			}

			return mViews.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mViews.get(position));
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}

	/**
	 * 跳转到主界面
	 */
	private void goHome() {
		if (isExistBaby) {// 如果存在baby调到主界面
			Intent intent = new Intent(GuideActivity.this, MainActivity.class);
			startActivity(intent);
			GuideActivity.this.finish();
		} else { // 不存在调到添加baby界面
			Intent intent = new Intent(GuideActivity.this,
					NoticeActivity.class);
			startActivity(intent);
			GuideActivity.this.finish();
		}
	}

	/**
	 * 
	 * 设置VersionCode
	 */
	private void setVersionCode() {
		Editor editor = preferences.edit();
		// 将VersionCode存入SharedPreferences
		editor.putInt("VersionCode", PackageUtil.getVersionCode(this));
		editor.commit();
	}

	/**
	 * PagerAdapter监听
	 * 
	 */
	private class MyPagerChangeListener implements OnPageChangeListener {

		private int oldPosition = 0;

		@Override
		public void onPageScrollStateChanged(int state) {
			/* state: 0空闲，1是滑行中，2加载完毕 */
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			if (position <= mViews.size() - 2) {
				mDots.get(oldPosition).setBackgroundResource(
						R.drawable.dot_normal);
				mDots.get(position).setBackgroundResource(
						R.drawable.dot_focused);
				oldPosition = position;
			}
			// 滑到最后一页(空白页)跳转到主界面
			if (position == mViews.size() - 1) {
				setVersionCode();
				goHome();
			}
		}
	}

}
