package com.trace.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.UnsupportedEncodingException;

import com.trace.NotesDB;
import com.trace.R;
import com.trace.R.id;
import com.trace.R.layout;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;

/**
 * 继承ListActivity的Activity，呈现已经存在的日志和添加日志按钮
 * 
 * @author TOPS
 * 
 */
public class MainActivity extends ListActivity {
	private SimpleCursorAdapter adapter = null;
	private NotesDB db;
	private SQLiteDatabase dbRead;
    private boolean isWeekShowButtonClick = false;
	public static final int REQUEST_CODE_ADD_NOTE = 1;
	public static final int REQUEST_CODE_EDIT_NOTE = 2;

	/**
	 * 实现OnClickListener接口，添加日志按钮的监听
	 */
	private OnClickListener btnClickHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 有返回结果的开启编辑日志的Activity，
			// requestCode If >= 0, this code will be returned
			// in onActivityResult() when the activity exits.
            Date currentTimeDate = null;
            String currentTime = null;
            String previousTime = null;
			switch (v.getId()) {
                case R.id.btnAddNote:
                    startActivityForResult(new Intent(MainActivity.this,
                            AtyEditNote.class), REQUEST_CODE_ADD_NOTE);
                    isWeekShowButtonClick = false;
                    break;
                case id.btnWeekShowNote:
                    isWeekShowButtonClick = true;
                    currentTimeDate = new Date();
                    currentTime = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss").format(currentTimeDate);
                    previousTime = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss").format(MainActivity.getWeekOfDate(currentTimeDate));
                    refreshNotesListView(previousTime, currentTime);

                    break;
                case id.btnMonthShowNote:
                    isWeekShowButtonClick = true;
                    currentTimeDate = new Date();
                    currentTime = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss").format(currentTimeDate);
                    previousTime = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss").format(MainActivity.getMonthOfDate());
                    refreshNotesListView(previousTime, currentTime);

                    break;
                case id.btnYearShowNote:
                    isWeekShowButtonClick = true;
                    currentTimeDate = new Date();
                    currentTime = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss").format(currentTimeDate);
                    previousTime = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss").format(MainActivity.getYearOfDate(currentTimeDate));
                    refreshNotesListView(previousTime, currentTime);
                    
                    break;
                default:
                    break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 操作数据库
		db = new NotesDB(this);
		dbRead = db.getReadableDatabase();

		// 查询数据库并将数据显示在ListView上。
		// 建议使用CursorLoader，这个操作因为在UI线程，容易引起无响应错误
		adapter = new SimpleCursorAdapter(this, R.layout.notes_list_cell, null,
				new String[] { NotesDB.COLUMN_NAME_NOTE_NAME,
						NotesDB.COLUMN_NAME_NOTE_DATE,
                        NotesDB.COLUMN_NAME_IS_COMPLETED}, new int[] {
						R.id.tvName, R.id.tvDate, R.id.tvIsCompleted}){
            @Override
            public void setViewText(TextView v, String text) {
                super.setViewText(v, convText(v, text));
            }
        };
        setListAdapter(adapter);

		refreshNotesListView();

		findViewById(R.id.btnAddNote).setOnClickListener(
				btnClickHandler);
		findViewById(id.btnWeekShowNote).setOnClickListener(
				btnClickHandler);
        findViewById(id.btnMonthShowNote).setOnClickListener(
                btnClickHandler);
        findViewById(id.btnYearShowNote).setOnClickListener(
                btnClickHandler);
	}

	/**
	 * 复写方法，笔记列表中的笔记条目被点击时被调用，打开编辑笔记页面，同事传入当前笔记的信息
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		// 获取当前笔记条目的Cursor对象
		Cursor c = adapter.getCursor();
		c.moveToPosition(position);

		// 显式Intent开启编辑笔记页面
		Intent i = new Intent(MainActivity.this, AtyEditNote.class);

		// 传入笔记id，name，content
		i.putExtra(AtyEditNote.EXTRA_NOTE_ID,
				c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_ID)));
		i.putExtra(AtyEditNote.EXTRA_NOTE_NAME,
				c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_NAME)));
		i.putExtra(AtyEditNote.EXTRA_NOTE_CONTENT,
				c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_CONTENT)));
		i.putExtra(AtyEditNote.COMPLETE_TIME, c.getString(c
				.getColumnIndex(NotesDB.COLUMN_NAME_COMPLETE_TIME)));
		i.putExtra(AtyEditNote.IS_COMPLETED, c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_IS_COMPLETED)));

		// 有返回的开启Activity
		startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);

		super.onListItemClick(l, v, position, id);
	}

	/**
	 * Called when an activity you launched exits, giving you the requestCode
	 * you started it with 当被开启的Activity存在并返回结果时调用的方法
	 * 
	 * 当从编辑笔记页面返回时调用，刷新笔记列表
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_CODE_ADD_NOTE:
		case REQUEST_CODE_EDIT_NOTE:
			if (resultCode == Activity.RESULT_OK) {
				refreshNotesListView();
			}
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 刷新笔记列表，内容从数据库中查询
	 */
	public void refreshNotesListView() {
		/**
		 * Change the underlying cursor to a new cursor. If there is an existing
		 * cursor it will be closed.
		 * 
		 * Parameters: cursor The new cursor to be used
		 */
		adapter.changeCursor(dbRead.query(NotesDB.TABLE_NAME_NOTES, null, null,
				null, null, null, null));

	}

    /**
     * 刷新笔记列表，根据当前时间从数据库中查询本周的计划完成情况
     */
    public void refreshNotesListView(String... time) {
        adapter.changeCursor(dbRead.query(
                    NotesDB.TABLE_NAME_NOTES,
                    null,
                    "complete_time BETWEEN ? AND ?",
                    new String[] {time[0], time[1]},
                    null,
                    null,
                    null
                )
        );

    }

    private String convText(TextView v, String text)
    {
        String formatedText = text;

        switch (v.getId())
        {
            case R.id.tvIsCompleted:
                if(!isWeekShowButtonClick) {
                    return "";
                }

                if(Integer.parseInt(formatedText) == 0) {
                        try {
                            byte[] iso8859 = "未完成".getBytes("ISO-8859-1");
                            formatedText = new String(iso8859,"UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }
                    } else {
                        try {
                            byte[] iso8859 = "已完成".getBytes("ISO-8859-1");
                            formatedText = new String(iso8859,"UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }
                }
                return formatedText;
            case R.id.tvDate:
                formatedText = MainActivity.stampToDate(formatedText);
        }
        return formatedText;
    }
    public static Date getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w <= 0) w = 7;
        return new Date(dt.getTime() - w * 24 * 3600 * 1000);
    }
    public static Date getMonthOfDate() {
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }
    public static Date getYearOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int year = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
}