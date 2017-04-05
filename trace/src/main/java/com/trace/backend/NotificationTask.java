package com.trace.backend;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.trace.activity.AtyEditNote;
import com.trace.service.AlarmReceiver;

public class NotificationTask extends Service {
	private static final int GAP_COMPLETED_DATE = 10 * 60 * 1000;// 任务完成的十分钟前提醒
    public static final String MESSAGE = "message";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Integer taskCompleted = intent.getIntExtra(AtyEditNote.TASK_COMPLETED, 0);
		Integer alarmId = intent.getIntExtra(AtyEditNote.EXTRA_NOTE_ID,0);
        String noteName = intent.getStringExtra(AtyEditNote.EXTRA_NOTE_NAME);
		if (taskCompleted == 1) {
			cancelAlarm(alarmId);
		} else {
			try {
				String completeTimeStr = intent
						.getStringExtra(AtyEditNote.COMPLETE_TIME);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				Date completeTime = sdf.parse(completeTimeStr);
				startAlarm(alarmId,completeTime, noteName);
			} catch (Exception e) {
				Log.i("NotificationTask", "start failed" + e.getMessage(), e);
			}
		}
		return super.onStartCommand(intent, flags, startId);


	}

	/**
	 * 开启闹钟
	 * @param completeTime
     */
	private void startAlarm( Integer alarmId, Date completeTime,String noteName) {
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //闹钟提醒的时间
		long triggerAtTime = completeTime.getTime();// - GAP_COMPLETED_DATE;
        // 此处设置开启AlarmReceiver这个Service
		Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra(MESSAGE, noteName + " 还没有完成请及时处理哦！");
		PendingIntent pi = PendingIntent.getBroadcast(this, alarmId, i, 0); // ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起，并且会唤醒CPU。
		manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
	}

	/**
	 * 取消闹钟
	 */
	private void cancelAlarm(Integer alarmId) {
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent i = new Intent(this, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, alarmId, i, 0);
        if (pi != null) {
            manager.cancel(pi);
        }
	}

	@Override
	public void onDestroy() {
		super.onDestroy(); // 在Service结束后关闭AlarmManager
	}
}
