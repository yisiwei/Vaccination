package cn.mointe.vaccination.activity;

import java.util.List;

import cn.mointe.vaccination.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 设置界面
 * 
 */
public class SettingActivity extends Activity implements OnClickListener {

	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private String[] mItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mListView = (ListView) this.findViewById(R.id.setting_lv);
		mItems = getResources().getStringArray(R.array.setting_item);
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItems);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0:
					//intent = new Intent(getApplicationContext(), cls)
					break;

				default:
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		
	}
}
