import {
  MapFunctions,
  MapView,
  MarkerView,
  setApiKey,
} from "@balloman/expo-google-maps";
import React from "react";
import { Button, StyleSheet, View } from "react-native";

import styleJson from "./style.json";

setApiKey(process.env.EXPO_PUBLIC_API_KEY!);

export default function App() {
  const mapViewRef = React.useRef<MapFunctions>(null);

  return (
    <View style={styles.container}>
      <MapView
        style={{ width: "100%", height: "100%" }}
        camera={{
          center: { latitude: 37.78825, longitude: -122.4324 },
          zoom: 13,
        }}
        styleJson={JSON.stringify(styleJson)}
        mapRef={mapViewRef}
        onMapIdle={({ cameraPosition }) =>
          console.log("cameraPosition", cameraPosition)
        }
        onDidChange={({ cameraPosition }) =>
          console.log("onDidChange", cameraPosition)
        }
        polygons={[
          {
            key: "1",
            coordinates: [
              { latitude: 37.78825, longitude: -122.4324 },
              { latitude: 37.78825, longitude: -122.44 },
              { latitude: 37.792, longitude: -122.44 },
              { latitude: 37.792, longitude: -122.4324 },
            ],
            strokeColor: "red",
            fillColor: "#5533CC22",
          },
        ]}
      >
        <MarkerView
          marker={{
            key: "1",
            position: {
              latitude: 37.78825,
              longitude: -122.4324,
            },
          }}
        />
        <MarkerView
          marker={{
            key: "2",
            position: {
              latitude: 37.78825,
              longitude: -122.44,
            },
            title: "Hello World",
          }}
        />
      </MapView>
      <View style={{ position: "absolute", top: "50%", alignSelf: "center" }}>
        <Button
          title="Center"
          onPress={() => {
            mapViewRef.current?.animateCamera(
              {
                center: { latitude: 37.78825, longitude: -122.44 },
                zoom: 13,
              },
              {
                animationDuration: 1,
              },
            );
          }}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
});
