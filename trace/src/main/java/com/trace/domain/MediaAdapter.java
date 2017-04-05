package com.trace.domain;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace.R;

public class MediaAdapter extends BaseAdapter {
	private Context context;
	private List<MediaListCellData> list = new ArrayList<MediaListCellData>();

	public MediaAdapter(Context context) {
		this.context = context;
	}

	public void add(MediaListCellData data) {
		list.add(data);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public MediaListCellData getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.media_list_cell, null);
		}

		MediaListCellData data = getItem(position);

		ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
		TextView tvPath = (TextView) convertView.findViewById(R.id.tvPath);

		ivIcon.setImageResource(data.iconId);
		tvPath.setText(data.path);
		return convertView;
	}

}