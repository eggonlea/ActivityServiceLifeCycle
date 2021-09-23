package com.lilioss.lifecycle.simpleactivity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.lilioss.lifecycle.library.JavaThread;

public class BackgroundActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: Activity";
  private final int N = 5;
  private final JavaThread[] javaThread = new JavaThread[N];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_background);
    for (int i=0; i<N; i++) {
      javaThread[i] = new JavaThread(TAG + "#" + i);
      javaThread[i].setContext(getApplicationContext());
      javaThread[i].enableWorkload();
      javaThread[i].start();
    }
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

    SimpleIntentService.startActionFoo(getApplicationContext(),
        "param1",
        "param2");
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
    for (int i=0; i<N; i++) {
      javaThread[i].finish();
    }
  }
}