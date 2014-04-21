package cn.mointe.vaccination.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.mointe.vaccination.R;

public class MoreActivity extends ActionBarActivity{

	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private String[] mItems;
	
	private ActionBar mBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		
		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		mListView = (ListView) this.findViewById(R.id.more_lv);
		mItems = getResources().getStringArray(R.array.more_item);
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItems);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0:
					intent = new Intent(getApplicationContext(), MoreActivity.class);
					startActivity(intent);
					break;
				case 1:
					//PublicMethod.showToast(getApplicationContext(), "关于我们");
					break;
				case 2:
					//PublicMethod.showToast(getApplicationContext(), "用户反馈");
					break;
				case 3:
					//PublicMethod.showToast(getApplicationContext(), "版本更新");
					break;
				case 4:
					//PublicMethod.showToast(getApplicationContext(), "分享");
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
}
