package edu.temple.foodiego;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class NFCAddFriendService extends HostApduService {
    public NFCAddFriendService() {
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.d(TAG, "processCommandApdu: received Apdu: " + Arrays.toString(commandApdu));
        //send the username to the device that is reading

        return new byte[0];
    }

    @Override
    public void onDeactivated(int reason) {

    }

}