package org.smartregister.util.mock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;

/**
 * Created by kaderchowdhury on 14/11/17.
 */

public class MockService {

    public static Service getService() {
        return new Service() {

            @Nullable
            @Override
            public IBinder onBind(Intent intent) {
                return new IBinder() {
                    @Override
                    public String getInterfaceDescriptor() throws RemoteException {
                        return null;
                    }

                    @Override
                    public boolean pingBinder() {
                        return false;
                    }

                    @Override
                    public boolean isBinderAlive() {
                        return false;
                    }

                    @Override
                    public IInterface queryLocalInterface(String s) {
                        return null;
                    }

                    @Override
                    public void dump(FileDescriptor fileDescriptor, String[] strings) throws RemoteException {

                    }

                    @Override
                    public void dumpAsync(FileDescriptor fileDescriptor, String[] strings) throws RemoteException {

                    }

                    @Override
                    public boolean transact(int i, Parcel parcel, Parcel parcel1, int i1) throws RemoteException {
                        return false;
                    }

                    @Override
                    public void linkToDeath(DeathRecipient deathRecipient, int i) throws RemoteException {

                    }

                    @Override
                    public boolean unlinkToDeath(DeathRecipient deathRecipient, int i) {
                        return false;
                    }
                };
            }
        };
    }
}
