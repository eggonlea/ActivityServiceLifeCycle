package com.lilioss.lifecycle.installpackages;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import com.lilioss.lifecycle.library.ISimpleAidlInterface;
import com.lilioss.lifecycle.library.JavaThread;
import com.lilioss.lifecycle.library.NativeThread;
import java.util.List;

public class InstallPackagesActivity extends AppCompatActivity {

  private final static String TAG = "LifeCycle: InsPkg";
  private static final String PKG_NAME = "com.lilioss.lifecycle.simpleactivity";
  private static final String INTENT_SVC = "com.lilioss.lifecycle.simpleactivity.SimpleIntentService";
  private static final String AIDL_SVC = "com.lilioss.lifecycle.simpleactivity.SimpleAIDLService";
  private static final String ACTION_BAZ = "com.lilioss.lifecycle.simpleactivity.action.BAZ";
  private static final String ACTION_AIDL = "com.lilioss.lifecycle.simpleactivity.action.AIDL";
  private static final String EXTRA_PARAM1 = "com.lilioss.lifecycle.simpleactivity.extra.PARAM1";
  private static final String EXTRA_PARAM2 = "com.lilioss.lifecycle.simpleactivity.extra.PARAM2";
  private static final String MANIFEST_BROADCAST = "com.lilioss.lifecycle.ManifestBroadcast";
  private static final String REGISTER_BROADCAST = "com.lilioss.lifecycle.RegisterBroadcast";

  private final JavaThread javaThread = new JavaThread(TAG);
  private final NativeThread nativeThread = new NativeThread(TAG);

  private Button buttonConnect;
  private Button buttonShare;
  private Button buttonBroadcast;
  private Button buttonOrderedBroadcast;
  private Button buttonDeadlock;
  private Button buttonForkRemote;
  private Button buttonForkLocal;
  private Button buttonCleanRemote;
  private Button buttonCleanLocal;
  private Button buttonDisconnect;
  private Button buttonReset;
  private SwitchCompat switchCount;

  private ISimpleAidlInterface mSimpleManager = null;
  private int mFd = -1;
  private boolean mCounting = false;
  private Thread mCountThread = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    /*
    Steps to grant the apk INSTALL_PACKAGES permission as a system app:

    1. Add the following code to AndroidManifest.xml
    <uses-permission android:name="android.permission.INSTALL_PACKAGES"
      tools:ignore="ProtectedPermissions" />

    2. Add the following code to /etc/permissions/privapp-permissions-platform.xml
    <privapp-permissions package="com.lilioss.lifecycle.installpackages">
        <permission name="android.permission.INSTALL_PACKAGES"/>
    </privapp-permissions>

    3. Put the apk to /system/priv-app/InstallPackages/installpackages.apk
    */
    if (checkSelfPermission("android.permission.INSTALL_PACKAGES") == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "INSTALL_PACKAGES permission granted");
    } else {
      Log.i(TAG, "INSTALL_PACKAGES permission denied");
    }

    buttonConnect = findViewById(R.id.buttonConnect);
    buttonConnect.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(Connect)");
        connect();
      }
    });

    buttonDisconnect = findViewById(R.id.buttonDisconnect);
    buttonDisconnect.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(disconnect)");
        disconnect();
      }
    });

    buttonReset = findViewById(R.id.buttonReset);
    buttonReset.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(reset)");
        reset();
      }
    });

    switchCount = findViewById(R.id.switchCount);
    switchCount.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(count)");
        if (switchCount.isChecked()) {
          int interval = Integer.parseInt(((EditText)findViewById(R.id.editInterval)).getText().toString());
          mCounting = true;
          mCountThread = new Thread() {
            /**
             * When an object implementing interface <code>Runnable</code> is
             * used to create a thread, starting the thread causes the
             * object's
             * <code>run</code> method to be called in that separately
             * executing thread.
             * <p>
             * The general contract of the method <code>run</code> is that it
             * may take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
              int i = 0;
              Log.i(TAG, "Start counting");
              while (mCounting && mSimpleManager != null) {
                try {
                  mSimpleManager.count(i);
                  Log.i(TAG, "Send counting " + i);
                  i++;
                  Thread.sleep(interval);
                } catch (InterruptedException | RemoteException e) {
                  e.printStackTrace();
                  break;
                }
              }
              Log.i(TAG, "Stop counting");
              mCounting = false;
            }
          };
          mCountThread.start();
        } else {
          mCounting = false;
        }
      }
    });

    buttonBroadcast = findViewById(R.id.buttonBroadcast);
    buttonBroadcast.findViewById(R.id.buttonBroadcast).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(broadcast)");
        broadcast(false);
      }
    });

    buttonOrderedBroadcast = findViewById(R.id.buttonOrderedBroadcast);
    buttonOrderedBroadcast.findViewById(R.id.buttonOrderedBroadcast).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(orderedbroadcast)");
        broadcast(true);
      }
    });

    buttonDeadlock = findViewById(R.id.buttonDeadlock);
    buttonDeadlock.findViewById(R.id.buttonDeadlock).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(deadlock)");
        deadlock();
      }
    });

    buttonShare = findViewById(R.id.buttonShare);
    buttonShare.findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(share)");
        share();
      }
    });

    buttonForkRemote = findViewById(R.id.buttonForkRemote);
    buttonForkRemote.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(fork remote)");
        forkRemote();
      }
    });

    buttonForkLocal = findViewById(R.id.buttonForkLocal);
    buttonForkLocal.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(fork local)");
        forkLocal();
      }
    });

    buttonCleanRemote = findViewById(R.id.buttonCleanRemote);
    buttonCleanRemote.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(clean remote)");
        cleanRemote();
      }
    });

    buttonCleanLocal = findViewById(R.id.buttonCleanLocal);
    buttonCleanLocal.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Code here executes on main thread after user presses button
        Log.i(TAG, "onClick(clean local)");
        cleanLocal();
      }
    });

    updateVisibility();

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

    startActionBaz(getApplicationContext(),
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
    javaThread.finish();
  }

  public void updateVisibility() {
    if (mSimpleManager == null) {
      buttonConnect.setEnabled(true);
      buttonDisconnect.setEnabled(false);
      buttonDeadlock.setEnabled(false);
      buttonShare.setEnabled(false);
      buttonForkRemote.setEnabled(false);
      buttonForkLocal.setEnabled(false);
      buttonCleanRemote.setEnabled(false);
    } else {
      buttonConnect.setEnabled(false);
      buttonDisconnect.setEnabled(true);
      buttonDeadlock.setEnabled(true);
      buttonForkRemote.setEnabled(true);
      buttonCleanRemote.setEnabled(true);

      if (mFd == -1) {
        buttonShare.setEnabled(true);
        buttonForkLocal.setEnabled(false);
      } else {
        buttonShare.setEnabled(false);
        buttonForkLocal.setEnabled(true);
      }
    }
  }

  public void connect() {
    Intent intent = new Intent();
    intent.setComponent(new ComponentName(PKG_NAME, AIDL_SVC));
    intent.setAction(ACTION_AIDL);
    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
  }

  public void disconnect() {
    unbindService(mServiceConnection);
    updateVisibility();
  }

  public void reset() {
    mSimpleManager = null;
    updateVisibility();
  }

  public void broadcast(boolean ordered) {
    if (ordered) {
      Intent intent = new Intent(MANIFEST_BROADCAST);
      PackageManager pm = getPackageManager();
      List<ResolveInfo> infos = pm.queryBroadcastReceivers(intent,
          PackageManager.ResolveInfoFlags.of(0));
      for (ResolveInfo info : infos) {
        ComponentName cn = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        Log.i(TAG, "Found component: " + cn);
        intent.setComponent(cn);
        sendOrderedBroadcast(intent, null);
      }
    } else {
      Intent intent = new Intent(REGISTER_BROADCAST);
      intent.setAction(REGISTER_BROADCAST);
      sendBroadcast(intent);
    }
  }

  public void deadlock() {
    try {
      String inst = getApplicationContext().getCacheDir() + "/lock.inst";
      nativeThread.lockLocal(inst);
      String remote = mSimpleManager.deadlock(inst);
      nativeThread.lockRemote(remote);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public void share() {
    try {
      ParcelFileDescriptor pfd = mSimpleManager.shareFileLock();
      if (pfd == null) {
        Log.i(TAG, "Receive: null");
        return;
      }
      mFd = pfd.detachFd();
      Log.i(TAG, "Receive: " + mFd);
      Log.i(TAG, "Original: " + nativeThread.getFD());
      nativeThread.setFD(mFd);
      nativeThread.start();
      updateVisibility();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public void forkRemote() {
    try {
      mSimpleManager.fork();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    updateVisibility();
  }

  public void forkLocal() {
    nativeThread.setFD(mFd);
    nativeThread.fork();
  }

  public void cleanRemote() {
    try {
      mSimpleManager.cleanFileLock();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    updateVisibility();
  }

  public void cleanLocal() {
    nativeThread.finish();
  }

  /**
   * Starts this service to perform action Baz with the given parameters. If the service is already
   * performing a task this action will be queued.
   *
   * @see IntentService
   */
  public void startActionBaz(Context context, String param1, String param2) {
    Log.i(TAG, "startActionBaz");
    Intent intent = new Intent();
    intent.setComponent(new ComponentName(PKG_NAME, INTENT_SVC));
    intent.setAction(ACTION_BAZ);
    intent.putExtra(EXTRA_PARAM1, param1);
    intent.putExtra(EXTRA_PARAM2, param2);
    context.startForegroundService(intent);
  }

  private final ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.i(TAG, "onServiceConnected");
      mSimpleManager = ISimpleAidlInterface.Stub.asInterface(service);
      if (mSimpleManager != null) {
        try {
          Log.i(TAG, mSimpleManager.basicTypes(1,
              2,
              true,
              3,
              4,
              "abc"));
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }
      updateVisibility();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      Log.i(TAG, "onServiceDisconnected");
      mSimpleManager = null;  // never executed!!!
      updateVisibility();
    }
  };
}