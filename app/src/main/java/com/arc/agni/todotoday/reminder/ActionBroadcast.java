package com.arc.agni.todotoday.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arc.agni.todotoday.service.NotificationMusicService;

public class ActionBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, NotificationMusicService.class));
    }
}