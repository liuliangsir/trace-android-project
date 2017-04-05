package com.trace.domain;

import com.trace.R;
import com.trace.common.MediaType;

public class MediaListCellData {
	int type = 0;
	int id = -1;
	String path = "";
	int iconId = R.drawable.ic_launcher;

	public MediaListCellData(String path, int id) {
		this(path);

		this.id = id;
	}

	public MediaListCellData(String path) {
		this.path = path;

		if (path.endsWith(".png") || path.endsWith(".jpg")) {
			iconId = R.drawable.icon_photo;
			type = MediaType.PHOTO;
		} else if (path.endsWith(".mp4")) {
			iconId = R.drawable.icon_video;
			type = MediaType.VIDEO;
		}
	}

	public int getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public int getId() {
		return id;
	}
}