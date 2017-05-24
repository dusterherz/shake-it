package com.dusterherz.android.shakeit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.Random;

public class ShakeActivity extends Activity {

    private static final String TAG = "ShakeActivity";
    private static final String LED_RED_PIN = "BCM6";
    private static final String LED_GREEN_PIN = "BCM5";
    private static final String LED_BLUE_PIN = "BCM13";
    private static final String TILT_PIN = "BCM21";

    public static Gpio mRedLedGpio;
    public static Gpio mGreenLedGpio;
    public static Gpio mBlueLedGpio;
    GpioCallback mTiltCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.d(TAG, "Tilted !");
            Random r = new Random();
            boolean frequencyRed = r.nextBoolean();
            boolean frequencyGreen = r.nextBoolean();
            boolean frequencyBlue = r.nextBoolean();
            try {
                ShakeActivity.mRedLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                ShakeActivity.mGreenLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                ShakeActivity.mBlueLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                if (frequencyRed) {
                    ShakeActivity.mRedLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
                }
                if (frequencyGreen) {
                    ShakeActivity.mGreenLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
                }
                if (frequencyBlue) {
                    ShakeActivity.mBlueLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
                }
            } catch (IOException e) {
                Log.e(TAG, "An IOException occured", e);
            }
            return true;
        }
    };
    private Gpio mTiltGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, service.getGpioList().toString());
        try {
            mRedLedGpio = service.openGpio(LED_RED_PIN);
            mRedLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mGreenLedGpio = service.openGpio(LED_GREEN_PIN);
            mGreenLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mBlueLedGpio = service.openGpio(LED_BLUE_PIN);
            mBlueLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mTiltGpio = service.openGpio(TILT_PIN);
            mTiltGpio.setDirection(Gpio.DIRECTION_IN);
            mTiltGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            mTiltGpio.registerGpioCallback(mTiltCallback);
        } catch (IOException e) {
            Log.e(TAG, "An IOException occured", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTiltGpio != null) {
            mTiltGpio.unregisterGpioCallback(mTiltCallback);
            try {
                mTiltGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "An IOException occured", e);
            }
        }
    }
}
