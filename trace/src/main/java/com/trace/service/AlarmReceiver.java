package com.trace.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.trace.R;
import com.trace.backend.NotificationTask;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String message = intent.getStringExtra(NotificationTask.MESSAGE);
		// 设置通知内容并在onReceive()这个函数执行时开启
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				message, System.currentTimeMillis());
		notification.setLatestEventInfo(context, message,
				message, null);
		notification.defaults = Notification.DEFAULT_ALL;
		manager.notify(1, notification); // 再次开启LongRunningService这个服务，从而可以
		Intent i = new Intent(context, NotificationTask.class);
		context.startService(i);
	}

}
