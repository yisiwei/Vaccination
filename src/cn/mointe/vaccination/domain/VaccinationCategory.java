package cn.mointe.vaccination.domain;

import java.util.ArrayList;
import java.util.List;

public class VaccinationCategory {

	private String categoryName;
	private List<Vaccination> categoryItem = new ArrayList<Vaccination>();

	public VaccinationCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void addItem(Vaccination item) {
		categoryItem.add(item);
	}

	/**
	 * 获取Item内容
	 * 
	 * @param position
	 * @return
	 */
	public Object getItem(int position) {
		// Category排在第一位
		if (position == 0) {
			return categoryName;
		} else {
			return categoryItem.get(position - 1);
		}
	}

	/**
	 * 当前类别Item总数。Category也需要占用一个Item
	 * 
	 * @return
	 */
	public int getItemCount() {
		return categoryItem.size() + 1;
	}

	public List<Vaccination> getCategoryItem() {
		return categoryItem;
	}

	public void setCategoryItem(List<Vaccination> categoryItem) {
		this.categoryItem = categoryItem;
	}
	
	

}
