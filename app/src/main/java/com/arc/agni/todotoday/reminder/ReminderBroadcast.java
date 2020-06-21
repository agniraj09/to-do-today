package com.arc.agni.todotoday.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.activities.HomeScreenActivity;
import com.arc.agni.todotoday.helper.DateHelper;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.arc.agni.todotoday.service.NotificationMusicService;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;

import static com.arc.agni.todotoday.constants.AppConstants.CHANNEL_DESCRIPTION;
import static com.arc.agni.todotoday.constants.AppConstants.CHANNEL_ID;
import static com.arc.agni.todotoday.constants.AppConstants.CHANNEL_NAME;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_NOTIFICATION;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_NOTIFICATION_ID;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_TASK;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_TASK_DESCRIPTION;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_TASK_RECURRENCE;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_TASK_TIME;
import static com.arc.agni.todotoday.constants.AppConstants.NOTIFICATION_TEXT;
import static com.arc.agni.todotoday.constants.AppConstants.PATTERN_TIME;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_COLORS;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_DAILY;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_MONTHLY;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_NONE;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_NOTIFICATION_TEXT;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_WEEKLY;
import static com.arc.agni.todotoday.constants.AppConstants.REMINDER_TYPE_ALARM;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(INTENT_EXTRA_NOTIFICATION);
        long notificationID = intent.getLongExtra(INTENT_EXTRA_NOTIFICATION_ID, 0);
        Task task = intent.getParcelableExtra(INTENT_EXTRA_TASK);

        // Start alarm music for on booking day(actual) notifications
        if (REMINDER_TYPE_ALARM.equalsIgnoreCase(task.getReminderType())) {
            Intent alarmScreenIntent = new Intent(context, NotificationMusicService.class);
            alarmScreenIntent.putExtra(INTENT_EXTRA_TASK, (Parcelable) task);
            context.startService(alarmScreenIntent);
        } else {
            // Fire notification
            notificationManager.notify((int) notificationID, notification);
        }

        // Schedule next reminder
        scheduleNextReminderIfApplicable(context, task);
    }

    private void scheduleNextReminderIfApplicable(Context context, Task task) {
        if (null != task && !task.isTaskCompleted() && task.isReminderSet()) {
            buildAndScheduleNotification(context, task.getId(), task);
        }
    }

    private static void createChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static void buildAndScheduleNotification(Context context, long taskID, Task task) {

        long reminderDateAndTimeInMillis;

        Calendar reminderDateAndTime = Calendar.getInstance();
        reminderDateAndTime.set(Calendar.HOUR_OF_DAY, task.getReminderHour());
        reminderDateAndTime.set(Calendar.MINUTE, task.getReminderMinute());
        reminderDateAndTime.set(Calendar.SECOND, 0);
        reminderDateAndTime.add(Calendar.MINUTE, -task.getRemindBefore());

        if (reminderDateAndTime.getTime().after(Calendar.getInstance().getTime())) {
            reminderDateAndTimeInMillis = reminderDateAndTime.getTimeInMillis();
            Intent homeScreenIntent = new Intent(context, HomeScreenActivity.class);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            PendingIntent notificationActivityIntent = PendingIntent.getActivity(context, (int) taskID, homeScreenIntent, PendingIntent.FLAG_ONE_SHOT);
            scheduleNotification(context, taskID, task, reminderDateAndTimeInMillis, notificationActivityIntent);
        } else {
            if (!RECURRENCE_NONE.equalsIgnoreCase(task.getRecurrence())) {
                reminderDateAndTimeInMillis = calculateReminderNotificationTime(reminderDateAndTime, task);
                Intent homeScreenIntent = new Intent(context, HomeScreenActivity.class);
                homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                PendingIntent notificationActivityIntent = PendingIntent.getActivity(context, (int) taskID, homeScreenIntent, PendingIntent.FLAG_ONE_SHOT);
                scheduleNotification(context, taskID, task, reminderDateAndTimeInMillis, notificationActivityIntent);
            }

        }
    }

    private static long calculateReminderNotificationTime(Calendar reminderDateAndTime, Task task) {
        switch (task.getRecurrence()) {
            case RECURRENCE_DAILY: {
                reminderDateAndTime.add(Calendar.DAY_OF_YEAR, 1);
                break;
            }

            case RECURRENCE_WEEKLY: {
                reminderDateAndTime.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            }

            case RECURRENCE_MONTHLY: {
                reminderDateAndTime.add(Calendar.MONTH, 1);
                break;
            }
        }

        return reminderDateAndTime.getTimeInMillis();
    }

    private static void scheduleNotification(Context context, long taskID, Task task, long notificationTime, PendingIntent homeScreenPendingIntent) {
        // Create notification channel
        createChannel(context);

        // Create notification with passed text
        Notification notification = createNotification(task, context, homeScreenPendingIntent, taskID);

        // Create Intent with extra to pass to BroadcastReceiver
        Intent notificationIntent = new Intent(context, ReminderBroadcast.class);
        notificationIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        notificationIntent.putExtra(INTENT_EXTRA_NOTIFICATION, notification);
        notificationIntent.putExtra(INTENT_EXTRA_NOTIFICATION_ID, taskID);
        notificationIntent.putExtra(INTENT_EXTRA_TASK, (Parcelable) task);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) taskID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Scheduling the notification
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
        }
    }

    private static Notification createNotification(Task task, Context context, PendingIntent homeScreenPendingIntent, long taskID) {

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(task.getDescription());
        bigText.setSummaryText(RECURRENCE_NOTIFICATION_TEXT.get(RECURRENCE_VALUES.indexOf(task.getRecurrence())));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_label_plain))
                .setSmallIcon(R.drawable.ic_label_plain)
                .setColor(context.getResources().getColor(PRIORITY_COLORS.get(PRIORITY_VALUES.indexOf(task.getPriority()))))
                .setContentTitle("It's Task Time")
                //.setContentText(NOTIFICATION_TEXT)
                //.setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(bigText)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(homeScreenPendingIntent);
        return builder.build();
    }

    /**
     * This method is used to delete the set notification if the corresponding task is deleted
     */
    public static void cancelNotification(long taskID, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) taskID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}

