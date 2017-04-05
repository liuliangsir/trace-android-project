package com.trace.activity;

import java.io.File;

import com.trace.R;
import com.trace.R.id;
import com.trace.R.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
 
/**
 * 显示照片的Activity
 * 
 * @author TOPS
 * 
 */
public class AtyPhotoViewer extends Activity {
 
	private ImageView iv;
 
	public static final String EXTRA_PATH = "path";
 
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view);
		iv = (ImageView)findViewById(R.id.photoIcon);
 
		String path = getIntent().getStringExtra(EXTRA_PATH);
		if (path != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			Log.i("photoViewer", "" + bitmap.getByteCount() +" " + bitmap.getHeight() + " " + bitmap.getWidth());
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, 800, 800);
			iv.setImageBitmap(bitmap);
		}
		
	}
 
}
