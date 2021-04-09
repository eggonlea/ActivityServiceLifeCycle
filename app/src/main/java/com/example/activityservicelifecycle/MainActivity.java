package com.example.activityservicelifecycle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.activityservicelifecycle.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  private ActivityMainBinding binding;
  private final static String TAG="LifeCycleActivity";
  private MyAsyncTask asyncTask = new MyAsyncTask();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(TAG, "onCreate");
    asyncTask.execute();
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // Example of a call to a native method
    TextView tv = binding.sampleText;
    tv.setText(stringFromJNI());
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart");
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.i(TAG, "onPause");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.i(TAG, "onStop");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onStop");
  }

  /**
   * A native method that is implemented by the 'native-lib' native library, which is packaged with
   * this application.
   */
  public native String stringFromJNI();
}
