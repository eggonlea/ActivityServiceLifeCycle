package com.lilioss.lifecycle.simpleactivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SimpleService extends Service {
  private final static String TAG = "LifeCycle: Service";

  public SimpleService() {
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate");
    super.onCreate();
  }

  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind");
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy");
    super.onDestroy();
  }
}