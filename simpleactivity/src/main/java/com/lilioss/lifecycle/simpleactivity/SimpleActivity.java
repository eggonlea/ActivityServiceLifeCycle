package com.lilioss.lifecycle.simpleactivity;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.NativeThread;

public class SimpleActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: SimpleActivity";
  private final JavaThread javaThread = new JavaThread(TAG);
  private final NativeThread nativeThread = new NativeThread(TAG);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    javaThread.start();
    nativeThread.start();
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
    nativeThread.finish();
  }
}