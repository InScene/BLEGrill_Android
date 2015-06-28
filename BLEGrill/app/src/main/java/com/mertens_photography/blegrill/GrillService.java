package com.mertens_photography.blegrill;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by InScene on 25.05.2015.
 */
public class GrillService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onStop(){
        this.stopSelf();
    }

}
