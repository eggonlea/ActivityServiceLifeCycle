package com.lilioss.lifecycle.simpleactivity;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.NativeThread;

public class SimpleActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: SimpleActivity";
  private JavaThread javaThread = null;
  private final NativeThread nativeThread = new NativeThread(TAG);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple);
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
  }
}