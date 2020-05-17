package com.example.hfilproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent intent1 = new Intent(context, LocationService.class);

            context.stopService(new Intent(context, LocationService.class));

        }
    }
}
