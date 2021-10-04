package edu.temple.foodiego;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ForegroundLocationService extends Service {
    public ForegroundLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}