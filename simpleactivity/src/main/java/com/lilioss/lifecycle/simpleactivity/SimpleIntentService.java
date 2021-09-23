package com.lilioss.lifecycle.simpleactivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;
import com.lilioss.lifecycle.library.ISimpleAidlInterface;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in a service on a
 * separate handler thread.
 * <p>
 *
 * TODO: Customize class - update intent actions, extra parameters and static helper methods.
 */
public class SimpleIntentService extends IntentService {
  private final static String TAG = "LifeCycle: IntentSvc";

  // TODO: Rename actions, choose action names that describe tasks that this
  // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
  private static final String ACTION_FOO = "com.lilioss.lifecycle.simpleactivity.action.FOO";
  private static final String ACTION_BAZ = "com.lilioss.lifecycle.simpleactivity.action.BAZ";

  // TODO: Rename parameters
  private static final String EXTRA_PARAM1 = "com.lilioss.lifecycle.simpleactivity.extra.PARAM1";
  private static final String EXTRA_PARAM2 = "com.lilioss.lifecycle.simpleactivity.extra.PARAM2";

  public SimpleIntentService() {
    super("SimpleIntentService");
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate");
    super.onCreate();
    NotificationManager mananger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationChannel channel = new NotificationChannel("1", "placeholder", NotificationManager.IMPORTANCE_DEFAULT);
    mananger.createNotificationChannel(channel);
    Notification notification = new Notification.Builder(getApplicationContext(), "1").build();
    startForeground(1, notification);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind");
    return super.onBind(intent);
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

  /**
   * Starts this service to perform action Foo with the given parameters. If the service is already
   * performing a task this action will be queued.
   *
   * @see IntentService
   */
  // TODO: Customize helper method
  public static void startActionFoo(Context context, String param1, String param2) {
    Log.i(TAG, "startActionFoo");
    Intent intent = new Intent(context, SimpleIntentService.class);
    intent.setAction(ACTION_FOO);
    intent.putExtra(EXTRA_PARAM1, param1);
    intent.putExtra(EXTRA_PARAM2, param2);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.i(TAG, "onHandleIntent");
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_FOO.equals(action)) {
        final String param1 = intent.getStringExtra(EXTRA_PARAM1);
        final String param2 = intent.getStringExtra(EXTRA_PARAM2);
        handleActionFoo(param1, param2);
      } else if (ACTION_BAZ.equals(action)) {
        final String param1 = intent.getStringExtra(EXTRA_PARAM1);
        final String param2 = intent.getStringExtra(EXTRA_PARAM2);
        handleActionBaz(param1, param2);
      }
    }
  }

  /**
   * Handle action Foo in the provided background thread with the provided parameters.
   */
  private void handleActionFoo(String param1, String param2) {
    // TODO: Handle action Foo
    Log.i(TAG, "handleActionFoo(" + param1 + ", " + param2 + ")");
  }

  /**
   * Handle action Baz in the provided background thread with the provided parameters.
   */
  private void handleActionBaz(String param1, String param2) {
    // TODO: Handle action Baz
    Log.i(TAG, "handleActionBaz(" + param1 + ", " + param2 + ")");
  }
}