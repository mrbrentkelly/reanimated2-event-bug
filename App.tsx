/* eslint-disable react-native/no-inline-styles */
/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React from 'react';
import {Text, View, requireNativeComponent} from 'react-native';
import Animated, {
  useAnimatedStyle,
  useSharedValue,
  // @ts-ignore
  useEvent,
} from 'react-native-reanimated';

const MyCustomView = requireNativeComponent('MyCustomView');
const AnimatedMyCustomView = Animated.createAnimatedComponent(MyCustomView);

type ElapsedEvent = {elapsed: number};

function useOnEventDispatcher(handler: (event: ElapsedEvent) => void) {
  return useEvent(
    (event: ElapsedEvent) => {
      'worklet';
      handler(event);
    },
    ['onEventDispatcher'],
  );
}

function useOnEventRCT(handler: (event: ElapsedEvent) => void) {
  return useEvent(
    (event: ElapsedEvent) => {
      'worklet';
      handler(event);
    },
    ['onEventRCT'],
  );
}

export default function App() {
  const rctElapsed = useSharedValue(0);
  const rctEventStyle = useAnimatedStyle(() => {
    return {
      backgroundColor: 'blue',
      height: 20,
      width: rctElapsed.value,
    };
  });
  const onEventRCT = useOnEventRCT((event) => {
    rctElapsed.value = event.elapsed * 10;
  });

  const dispatcherElapsed = useSharedValue(0);
  const dispatchedEventStyle = useAnimatedStyle(() => {
    return {
      backgroundColor: 'red',
      height: 20,
      width: dispatcherElapsed.value,
    };
  });
  const onEventDispatcher = useOnEventDispatcher((event) => {
    dispatcherElapsed.value = event.elapsed * 10;
  });

  return (
    <View
      style={{
        flex: 1,
        flexDirection: 'column',
      }}>
      <AnimatedMyCustomView
        emitEvents={true}
        {...{onEventRCT, onEventDispatcher}}
      />
      <Text>Tracking RCT Event (broken)</Text>
      <Animated.View style={rctEventStyle} />
      <Text>Tracking Dispatcher Event</Text>
      <Animated.View style={dispatchedEventStyle} />
    </View>
  );
}
