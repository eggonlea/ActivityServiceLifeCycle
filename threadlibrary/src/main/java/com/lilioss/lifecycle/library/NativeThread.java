package com.lilioss.lifecycle.library;

public class NativeThread {
  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  private final String tag;

  public NativeThread(String s) {
    tag = "std::thread_" + s;
  }

  public void start() {
    nativeStart(tag);
  }

  public void finish() {
    nativeFinish();
  }

  /**
   * A native method that is implemented by the 'native-lib' native library, which is packaged with
   * this application.
   */
  private native void nativeStart(String s);
  private native void nativeFinish();
}
