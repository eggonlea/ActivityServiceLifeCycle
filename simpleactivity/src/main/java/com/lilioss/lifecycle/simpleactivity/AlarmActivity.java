package com.lilioss.lifecycle.simpleactivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.lilioss.lifecycle.library.JavaThread;

public class AlarmActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: AlarmActivity";
  private JavaThread javaThread = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple);

    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
    Intent intent = new Intent(this, AlarmReceiver.class);
    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
    //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000, alarmIntent);
    am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20000, alarmIntent);

    javaThread = new JavaThread(TAG);
    javaThread.start();
  }

  @Override
  protected final void onStart() {
    Log.i(TAG, "onStart");
    super.onStart();
  }

  @Override
  protected final void onResume() {
    Log.i(TAG, "onResume");
    super.onResume();
  }

  @Override
  protected void onPause() {
    Log.i(TAG, "onPause");
    super.onPause();
  }

  @Override
  protected void onStop() {
    Log.i(TAG, "onStop");
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    Log.i(TAG, "onDestroy");
    super.onDestroy();
    javaThread.finish();
  }
}