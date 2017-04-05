package com.trace.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.trace.NotesDB;
import com.trace.R;
import com.trace.backend.NotificationTask;
import com.trace.common.MediaType;
import com.trace.domain.MediaAdapter;
import com.trace.domain.MediaListCellData;

public class AtyEditNote extends ListActivity {
	private int noteId = -1;
	private EditText etName, etContent;
	private TextView eCompleteTime;
	private CheckBox checkBox;
	private MediaAdapter adapter;
	private NotesDB db;
	private SQLiteDatabase dbRead, dbWrite;
	private String currentPath = null;

	public static final int REQUEST_CODE_GET_PHOTO = 1;
	public static final int REQUEST_CODE_GET_VIDEO = 2;
	public static final int REQUEST_CODE_GET_TIME = 3;

	public static final String EXTRA_NOTE_ID = "noteId";
	public static final String EXTRA_NOTE_NAME = "noteName";
	public static final String EXTRA_NOTE_CONTENT = "noteContent";
	public static final String COMPLETE_TIME = "completeTime";
	public static final String IS_COMPLETED = "isCompleted";//是否完成
	public static final String TASK_COMPLETED ="taskCompleted";

	private OnClickListener btnClickHandler = new OnClickListener() {

		Intent i;
		File f;


		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnAddPhoto:
				i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				f = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				currentPath = f.getAbsolutePath();
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				startActivityForResult(i, REQUEST_CODE_GET_PHOTO);
				break;
			case R.id.btnAddVideo:
				i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				f = new File(getMediaDir(), System.currentTimeMillis() + ".mp4");
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				currentPath = f.getAbsolutePath();
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

				startActivityForResult(i, REQUEST_CODE_GET_VIDEO);
				break;
			case R.id.btnSave:
				saveMedia(saveNote());
				setResult(RESULT_OK);
				finish();
				break;
			case R.id.btnCancel:// 取锟斤拷锟斤拷钮
				setResult(RESULT_CANCELED);
				finish();
				break;
			case R.id.btnAddCTime://设定完成时间
				i = new Intent(AtyEditNote.this,AtySelectTime.class);
				i.putExtra(COMPLETE_TIME, eCompleteTime.getText());
				startActivityForResult(i, REQUEST_CODE_GET_TIME);
				break;
			default:
				break;
			}
		}
	};

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_eidt_note);

		db = new NotesDB(this);
		dbRead = db.getReadableDatabase();
		dbWrite = db.getWritableDatabase();

		adapter = new MediaAdapter(this);
		setListAdapter(adapter);

		etName = (EditText) findViewById(R.id.etName);
		etContent = (EditText) findViewById(R.id.etContent);
		eCompleteTime = (TextView) findViewById(R.id.textCompleteTime);
		checkBox = (CheckBox) findViewById(R.id.cbIsCompleted);

		noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);


		if (noteId > -1) {
			etName.setText(getIntent().getStringExtra(EXTRA_NOTE_NAME));
			etContent.setText(getIntent().getStringExtra(EXTRA_NOTE_CONTENT));
			eCompleteTime.setText(getIntent().getStringExtra(COMPLETE_TIME));
			Integer isCompleted = getIntent().getIntExtra(IS_COMPLETED,0);
			checkBox.setChecked(isCompleted == 0? false: true);
			Cursor c = dbRead.query(NotesDB.TABLE_NAME_MEDIA, null,
					NotesDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?",
					new String[] { noteId + "" }, null, null, null);
			while (c.moveToNext()) {
				adapter.add(new MediaListCellData(c.getString(c
						.getColumnIndex(NotesDB.COLUMN_NAME_MEDIA_PATH)), c
						.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_ID))));
			}

			/**
			 * Notifies the attached observers that the underlying data has been
			 * changed and any View reflecting the data set should refresh
			 * itself.
			 */
			adapter.notifyDataSetChanged();
		}

		findViewById(R.id.btnSave).setOnClickListener(btnClickHandler);
		findViewById(R.id.btnCancel).setOnClickListener(btnClickHandler);
		findViewById(R.id.btnAddPhoto).setOnClickListener(btnClickHandler);
		findViewById(R.id.btnAddVideo).setOnClickListener(btnClickHandler);
		findViewById(R.id.btnAddCTime).setOnClickListener(btnClickHandler);
	}

	private Date getCompleteTimeDate() {
		int hour = 1;
		int minute = 2;
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		Log.i("dateViewer", "year" + year + " month:" + month + " date :"
				+ date + " hour:" + hour + " minutes:" + minute);
		return new Date(year, month, date, hour, minute);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		MediaListCellData data = adapter.getItem(position);
		Intent i;

		switch (data.getType()) {
		case MediaType.PHOTO:
			i = new Intent(this, AtyPhotoViewer.class);
			i.putExtra(AtyPhotoViewer.EXTRA_PATH, data.getPath());
			startActivity(i);
			break;
		case MediaType.VIDEO:
			i = new Intent(this, AtyVideoViewer.class);
			i.putExtra(AtyVideoViewer.EXTRA_PATH, data.getPath());
			startActivity(i);
			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//		System.out.println(data);

		switch (requestCode) {
		case REQUEST_CODE_GET_PHOTO:
		case REQUEST_CODE_GET_VIDEO:
			if (resultCode == RESULT_OK) {
				adapter.add(new MediaListCellData(currentPath));
				adapter.notifyDataSetChanged();
			}
			break;
		case REQUEST_CODE_GET_TIME:
			if (resultCode == RESULT_OK) {
				String completeTime = data.getStringExtra(AtyEditNote.COMPLETE_TIME);
				eCompleteTime.setText(completeTime);

				Log.i("dateViewer", "completeDate:" + completeTime);

			}
			break;
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public File getMediaDir() {
		File dir = new File(Environment.getExternalStorageDirectory(),
				"NotesMedia");

		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public void saveMedia(int noteId) {

		MediaListCellData data;
		ContentValues cv;

		for (int i = 0; i < adapter.getCount(); i++) {
			data = adapter.getItem(i);

			if (data.getId() <= -1) {
				cv = new ContentValues();
				cv.put(NotesDB.COLUMN_NAME_MEDIA_PATH, data.getPath());
				cv.put(NotesDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, noteId);
				dbWrite.insert(NotesDB.TABLE_NAME_MEDIA, null, cv);
			}
		}

	}

	public int saveNote() {
		checkBox = (CheckBox)findViewById(R.id.cbIsCompleted);
		Integer isCompleted = checkBox.isChecked()? 1: 0;
		ContentValues cv = new ContentValues();
		cv.put(NotesDB.COLUMN_NAME_NOTE_NAME, etName.getText().toString());
        cv.put(NotesDB.COLUMN_NAME_NOTE_CONTENT, etContent.getText().toString());
        cv.put(NotesDB.COLUMN_NAME_NOTE_DATE, new Date().getTime());
//      cv.put(NotesDB.COLUMN_NAME_NOTE_DATE, new SimpleDateFormat(
//				"yyyy-MM-dd hh:mm:ss").format(new Date()));
		cv.put(NotesDB.COLUMN_NAME_COMPLETE_TIME, eCompleteTime.getText().toString());
		cv.put(NotesDB.COLUMN_NAME_IS_COMPLETED, isCompleted);
		Log.i("dateViewer", "saveNote:" + eCompleteTime.getText().toString());
		if (isCompleted == 0) {
			Toast.makeText(this, "任务未完成提醒已经开启", Toast.LENGTH_LONG);
		} else {
			Toast.makeText(this, "任务未完成提醒已经关闭", Toast.LENGTH_LONG);
		}
		if (noteId > -1) {
			dbWrite.update(NotesDB.TABLE_NAME_NOTES, cv, NotesDB.COLUMN_NAME_ID
					+ "=?", new String[] { noteId + "" });
		} else {
			noteId = (int) dbWrite.insert(NotesDB.TABLE_NAME_NOTES, null, cv);
		}
		// 设置提醒功能
		Intent intent = new Intent(this, NotificationTask.class);
		intent.putExtra(COMPLETE_TIME, eCompleteTime.getText().toString());

		intent.putExtra(TASK_COMPLETED, isCompleted);//任务是否完成 1 完成 0 未完成
		intent.putExtra(EXTRA_NOTE_ID, noteId);// 笔记的id，也是用来标记哪个闹钟
		intent.putExtra(EXTRA_NOTE_NAME, etName.getText().toString());//笔记标题
		startService(intent);
		return noteId;
	}

	@Override
	protected void onDestroy() {
		dbRead.close();
		dbWrite.close();
		super.onDestroy();
	}

}
