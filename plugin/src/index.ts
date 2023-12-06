import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from "@expo/config-plugins";

const withAndroidApiKey: ConfigPlugin<{ apiKey: string }> = (
  config,
  { apiKey },
) => {
  config = withAndroidManifest(config, config => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      config.modResults,
    );

    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      "com.google.android.geo.API_KEY",
      apiKey,
    );

    return config;
  });
  return config;
};

export default withAndroidApiKey;
