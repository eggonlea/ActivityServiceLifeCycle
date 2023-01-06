package com.lilioss.lifecycle.simpleactivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.NativeThread;

public class SimpleActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: SimpleActivity";

  private static final String REGISTER_BROADCAST = "com.lilioss.lifecycle.RegisterBroadcast";

  private JavaThread javaThread = null;
  private final NativeThread nativeThread = new NativeThread(TAG);

  BroadcastReceiver mReceiver = new SimpleBroadcastReceiver();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple);
    String simple = getApplicationContext().getCacheDir() + "/lock.simple";
    nativeThread.lockLocal(simple);
    //nativeThread.lockRemote(simple);

    IntentFilter filter = new IntentFilter(REGISTER_BROADCAST);
    ContextCompat.registerReceiver(getApplicationContext(), mReceiver, filter,
        ContextCompat.RECEIVER_EXPORTED | ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS);
  }

  @Override
  protected final void onStart() {
    Log.i(TAG, "onStart");
    super.onStart();
    nativeThread.start();
  }

  @Override
  protected final void onResume() {
    Log.i(TAG, "onResume");
    super.onResume();
    javaThread = new JavaThread(TAG);
    javaThread.start();
    SimpleIntentService.startActionFoo(getApplicationContext(),
        "param1",
        "param2");
  }

  @Override
  protected void onPause() {
    Log.i(TAG, "onPause");
    super.onPause();
    javaThread.finish();
  }

  @Override
  protected void onStop() {
    Log.i(TAG, "onStop");
    super.onStop();
    nativeThread.finish();
  }

  @Override
  protected void onDestroy() {
    Log.i(TAG, "onDestroy");
    super.onDestroy();
    getApplicationContext().unregisterReceiver(mReceiver);
  }

  @Override
  public void onTrimMemory(int level) {
    Log.i(TAG, "onTrimMemory: " + level);
    super.onTrimMemory(level);
  }
}