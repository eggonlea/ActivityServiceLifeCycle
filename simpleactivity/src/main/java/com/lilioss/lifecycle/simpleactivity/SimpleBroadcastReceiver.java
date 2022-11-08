package com.lilioss.lifecycle.simpleactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SimpleBroadcastReceiver extends BroadcastReceiver {
  private final static String TAG = "LifeCycle: BroadcastReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "onReceive: " + intent);
  }
}
