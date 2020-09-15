package com.reanimatedeventbug;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MyCustomViewManager extends SimpleViewManager {

    private CompositeDisposable disposables = new CompositeDisposable();

    private String EVENT_FROM_RCT = "onEventRCT";
    public static String EVENT_FROM_DISPATCHER = "onEventDispatcher";

    @NonNull
    @Override
    public String getName() {
        return "MyCustomView";
    }

    @NonNull
    @Override
    protected View createViewInstance(@NonNull ThemedReactContext reactContext) {
        TextView tv = new TextView((reactContext));
        tv.setText("Hello World");
        return tv;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();

        builder.put(EVENT_FROM_RCT, MapBuilder.of("registrationName", EVENT_FROM_RCT));
        builder.put(EVENT_FROM_DISPATCHER, MapBuilder.of("registrationName", EVENT_FROM_DISPATCHER));

        return builder.build();
    }

    @ReactProp(name = "emitEvents")
    public void setEmitEvents(TextView view, Boolean emitEvents) {
        if (!emitEvents) {
            disposables.clear();
        } else {
            disposables.add(Observable.interval(1, TimeUnit.SECONDS).subscribe(elapsed -> {
                WritableMap args = Arguments.createMap();
                args.putDouble("elapsed", elapsed.doubleValue());

                // This isn't picked up by the useEvent hook from reanimated2
                ((ThemedReactContext) view.getContext())
                        .getJSModule(RCTEventEmitter.class).receiveEvent(view.getId(), EVENT_FROM_RCT, args);

                // This is picked up
                EventDispatcher eventDispatcher = ((ThemedReactContext)view.getContext())
                        .getNativeModule(UIManagerModule.class)
                        .getEventDispatcher();
                MyCustomEvent event = MyCustomEvent.obtain(
                        view.getId(),
                        elapsed.doubleValue());
                eventDispatcher.dispatchEvent(event);
            }));
        }
    }
}
