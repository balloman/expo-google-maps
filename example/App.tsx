import { MapFunctions, MapView, setApiKey } from "@balloman/expo-google-maps";
import * as Location from "expo-location";
import React, { useEffect } from "react";
import { Button, StyleSheet, Text, View } from "react-native";

import styleJson from "./style.json";

setApiKey(process.env.EXPO_PUBLIC_API_KEY!);

export default function App() {
  const mapViewRef = React.useRef<MapFunctions>(null);
  const [status, setStatus] = React.useState<Location.PermissionStatus>();

  useEffect(() => {
    Location.getForegroundPermissionsAsync()
      .then(({ status }) => {
        setStatus(status);
      })
      .catch(e => {
        if (e instanceof TypeError) {
          return;
        }
        console.error(e);
      });
  }, []);

  useEffect(() => {
    if (status === Location.PermissionStatus.UNDETERMINED) {
      Location.requestForegroundPermissionsAsync()
        .then(({ status }) => {
          setStatus(status);
        })
        .catch(e => {
          console.log("error", e);
        });
    }
  }, [status]);

  return (
    <View style={styles.container}>
      <MapView
        style={{ width: "100%", height: "100%" }}
        camera={{
          center: { latitude: 37.78825, longitude: -122.4324 },
          zoom: 13,
        }}
        styleJson={JSON.stringify(styleJson)}
        showUserLocation
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
        {/* <MarkerView
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
          tracksViewChanges={false}
          onMarkerPress={() => console.log("marker pressed")}
        >
          <View
            style={{
              alignItems: "center",
              backgroundColor: "blue",
            }}
          >
            <Text>Hello World</Text>
          </View>
        </MarkerView> */}
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
