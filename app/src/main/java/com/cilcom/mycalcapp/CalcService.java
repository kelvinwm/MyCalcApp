package com.cilcom.mycalcapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class CalcService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }
    /**
     * IRemote defnition is available here
     */
    private final IRemote.Stub mBinder = new IRemote.Stub() {

        @Override
        public double add(int a, int b) throws RemoteException {
            // TODO Auto-generated method stub
            return (a + b);
        }

        @Override
        public double subtract(int a, int b) throws RemoteException {
            // TODO Auto-generated method stub
            return (a - b);
        }

        @Override
        public double divide(int a, int b) throws RemoteException {
            // TODO Auto-generated method stub
            return (a * 1.0 / b);
        }

        @Override
        public double multiply(int a, int b) throws RemoteException {
            // TODO Auto-generated method stub
            return (a * b);
        }
    };
}