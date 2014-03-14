package cn.mointe.vaccination.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.SortAdapter;
import cn.mointe.vaccination.domain.SortModel;
import cn.mointe.vaccination.tools.CharacterParser;
import cn.mointe.vaccination.tools.PinyinComparator;
import cn.mointe.vaccination.view.ClearEditText;
import cn.mointe.vaccination.view.SideBar;
import cn.mointe.vaccination.view.SideBar.OnTouchingLetterChangedListener;

/**
 * 疫苗库界面
 * 
 */
public class VaccineLibraryFragment extends Fragment implements OnClickListener {

	private ListView mSortListView;
	private SideBar mSideBar;
	private TextView mTextDialog;
	private SortAdapter mSortAdapter;
	private ClearEditText mClearEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// 汉字转换成拼音的类
	private CharacterParser mCharacterParser;
	private List<SortModel> mDataList;

	// 根据拼音排序的类
	private PinyinComparator mPinyinComparator;

	private static final String[] data = { "乙肝疫苗", "卡介苗", "脊灰疫苗", "百白破疫苗",
			"流脑疫苗", "麻疹疫苗", "乙脑减毒疫苗", "甲肝疫苗", "麻风腮疫苗", "A+C流脑疫苗", "白破二联疫苗",
			"A群流脑疫苗" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_vaccine_library, null);

		// 实例化汉字转拼音类
		mCharacterParser = CharacterParser.getInstance();

		mPinyinComparator = new PinyinComparator();

		mSideBar = (SideBar) view.findViewById(R.id.sidrbar);
		mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);
		mTextDialog = (TextView) view.findViewById(R.id.dialog);
		mSideBar.setTextView(mTextDialog);

		mSortListView = (ListView) view.findViewById(R.id.country_lvcountry);

		mDataList = filledData(data);

		// 根据a-z进行排序
		Collections.sort(mDataList, mPinyinComparator);
		mSortAdapter = new SortAdapter(getActivity(), mDataList);
		mSortListView.setAdapter(mSortAdapter);

		// 设置右侧触摸监听
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mSortAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mSortListView.setSelection(position);
				}
			}
		});

		// listView item点击监听
		mSortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = ((SortModel) mSortAdapter.getItem(position))
						.getName();
				Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
			}
		});

		// 根据输入框输入的值来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {

	}

	// listView 填充数据
	private List<SortModel> filledData(String[] data) {
		List<SortModel> sortList = new ArrayList<SortModel>();
		for (int i = 0; i < data.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(data[i]);
			// 汉字转换成拼音
			String pinyin = mCharacterParser.getSelling(data[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString);
			} else {
				sortModel.setSortLetters("#");
			}
			sortList.add(sortModel);
		}

		return sortList;
	}

	// 根据输入框的值来过滤数据并更新ListView
	private void filterData(String filterStr) {
		List<SortModel> filterDataList = new ArrayList<SortModel>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDataList = mDataList;
		} else {
			filterDataList.clear();
			for (SortModel sortModel : mDataList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| mCharacterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDataList.add(sortModel);
				}
			}
		}
		Collections.sort(filterDataList, mPinyinComparator);
		mSortAdapter.updateListView(filterDataList);
	}

}
