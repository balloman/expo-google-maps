import {
  AndroidConfig,
  type ConfigPlugin,
  withAndroidManifest,
} from "@expo/config-plugins";

const withAndroidApiKey: ConfigPlugin<{ androidApiKey: string }> = (
  config,
  { androidApiKey },
) => {
  // biome-ignore lint/style/noParameterAssign: This is a plugin
  config = withAndroidManifest(config, (config) => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      config.modResults,
    );

    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      "com.google.android.geo.API_KEY",
      androidApiKey,
    );

    return config;
  });
  return config;
};

export default withAndroidApiKey;
