package cn.mointe.vaccination.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.Inbox;

public class InboxAdapter extends BaseAdapter {

	private List<Inbox> mInboxs;
	private Context mContext;

	public InboxAdapter(Context context, List<Inbox> inboxs) {
		this.mContext = context;
		this.mInboxs = inboxs;
	}

	@Override
	public int getCount() {
		return mInboxs.size();
	}

	@Override
	public Object getItem(int position) {
		return mInboxs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.inbox_item, null);
			
			holder.inboxLayout = (RelativeLayout) convertView.findViewById(R.id.inbox_item_layout);
			holder.inboxImage = (ImageView) convertView
					.findViewById(R.id.inbox_image);
			holder.inboxDate = (TextView) convertView
					.findViewById(R.id.inbox_date);
			holder.inboxTitle = (TextView) convertView
					.findViewById(R.id.inbox_title);
			holder.inboxDetail = (TextView) convertView
					.findViewById(R.id.inbox_detail);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Inbox inbox = (Inbox) this.mInboxs.get(position);

		holder.inboxImage.setImageResource(R.drawable.mail_default);
		holder.inboxDate.setText(inbox.getDate());
		holder.inboxTitle.setText(inbox.getTitle());
		holder.inboxDetail.setText(inbox.getContent());
		
		if (inbox.getIsRead().equals("已读")) {
			holder.inboxLayout.setBackgroundResource(R.color.month_bg);
		}else{
			holder.inboxLayout.setBackgroundResource(R.color.unread);
		}

		return convertView;
	}

	private final class ViewHolder {
		RelativeLayout inboxLayout;
		ImageView inboxImage;
		TextView inboxDate;
		TextView inboxTitle;
		TextView inboxDetail;
	}

}
