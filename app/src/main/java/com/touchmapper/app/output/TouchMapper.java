package com.touchmapper.app.output;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.touchmapper.app.output.config.TouchConfig;
import com.touchmapper.app.output.touch.TouchMapping;

/**
 * Created by shyri on 08/09/17.
 */

public class TouchMapper {
    private final static String LOG_TAG = "TouchMapper";
    private final TouchSimulator touchSimulator;
    private final TouchConfig touchConfig;

    public TouchMapper(TouchConfig touchConfig) throws Exception {
        this.touchConfig = touchConfig;

        touchSimulator = new TouchSimulator();

        for (int i = 0; i < touchConfig.mappings.size(); i++) {
            touchConfig.mappings.get(i)
                                .setPointerId(i);
            touchConfig.mappings.get(i)
                                .setTouchSimulator(touchSimulator);
        }
    }

    public void processEvent(KeyEvent event) {
        Log.d(LOG_TAG, "Key Event received");
        for (TouchMapping tapMapping : touchConfig.mappings) {
            tapMapping.processEvent(event);
        }
    }

    public void processEvent(MotionEvent event) {
        Log.d(LOG_TAG, "Motion Event received");
        for (TouchMapping tapMapping : touchConfig.mappings) {
            tapMapping.processEvent(event);
        }
    }
}
