package com.reanimatedeventbug;

import androidx.core.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import static com.reanimatedeventbug.MyCustomViewManager.EVENT_FROM_DISPATCHER;

public class MyCustomEvent extends Event<MyCustomEvent>{

    private static final int PROGRESS_EVENTS_POOL_SIZE = 7; // magic

    private static final Pools.SynchronizedPool<MyCustomEvent> EVENTS_POOL =
            new Pools.SynchronizedPool<>(PROGRESS_EVENTS_POOL_SIZE);

    public static MyCustomEvent obtain(
            int viewId,
            double elapsed) {
        MyCustomEvent event = EVENTS_POOL.acquire();
        if (event == null) {
            event = new MyCustomEvent();
        }
        event.init(viewId, elapsed);
        return event;
    }

    private WritableMap mExtraData;

    private MyCustomEvent() {
    }

    private void init(
            int viewId,
            double elapsed) {
        super.init(viewId);
        mExtraData = Arguments.createMap();

        mExtraData.putDouble("elapsed", elapsed);
    }

    @Override
    public void onDispose() {
        mExtraData = null;
        EVENTS_POOL.release(this);
    }

    @Override
    public String getEventName() {
        return EVENT_FROM_DISPATCHER;
    }

    @Override
    public boolean canCoalesce() {
        // TODO: coalescing
        return false;
    }

    @Override
    public short getCoalescingKey() {
        // TODO: coalescing
        return 0;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), EVENT_FROM_DISPATCHER, mExtraData);
    }
}