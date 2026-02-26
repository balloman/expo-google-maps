# @balloman/expo-google-maps

## 2.0.0

### Major Changes

- [#125](https://github.com/balloman/expo-google-maps/pull/125) [`30a7adb`](https://github.com/balloman/expo-google-maps/commit/30a7adb43caeb3bf43fcd25764688954aacb789d) Thanks [@balloman](https://github.com/balloman)! - Updated to support new architecture

  - Updated to the new method of expo modules
  - Swapped to using Bun
  - Updated to support Expo 54
  - Updated to require iOS 16

  ## Breaking

  This module now requires the new architecture, it doesn't support Expo old architecture

### Minor Changes

- [#125](https://github.com/balloman/expo-google-maps/pull/125) [`30a7adb`](https://github.com/balloman/expo-google-maps/commit/30a7adb43caeb3bf43fcd25764688954aacb789d) Thanks [@balloman](https://github.com/balloman)! - Updated the readme to have good examples

## 1.0.2

### Patch Changes

- [`5182f37`](https://github.com/balloman/expo-google-maps/commit/5182f37202e1de6d455b2a2720c90feca931cb89) Thanks [@balloman](https://github.com/balloman)! - Actually restore prepare script

## 1.0.1

### Patch Changes

- [`b0cff96`](https://github.com/balloman/expo-google-maps/commit/b0cff969a13cf069ff083b6814c1d68452dcfb92) Thanks [@balloman](https://github.com/balloman)! - Fix the build folder not being generated

## 1.0.0

### Major Changes

- [`589770c`](https://github.com/balloman/expo-google-maps/commit/589770cad3fd4c94602fcba10fd3d55f3cad0ab5) Thanks [@balloman](https://github.com/balloman)! - Initial Release and support for sdk 51

## 0.7.0

### Minor Changes

- [#72](https://github.com/balloman/expo-google-maps/pull/72) [`5b78093`](https://github.com/balloman/expo-google-maps/commit/5b78093f9b9ce4529b7dd4170891dc05d74f63bb) Thanks [@balloman](https://github.com/balloman)! - Renamed the `apiKey` property to `androidApiKey` to clarify its specific use for Android configurations in the Google Maps SDK.

## 0.6.1

### Patch Changes

- [#58](https://github.com/balloman/expo-google-maps/pull/58) [`20cc98b`](https://github.com/balloman/expo-google-maps/commit/20cc98b2e49232683ba0d3a6017bce9f2f865b79) Thanks [@balloman](https://github.com/balloman)! - Remove key

## 0.6.0

### Minor Changes

- [#56](https://github.com/balloman/expo-google-maps/pull/56) [`da7252b`](https://github.com/balloman/expo-google-maps/commit/da7252bc4b6dea45733732432092480e913aebbb) Thanks [@balloman](https://github.com/balloman)! - Add support for Expo SDK 50 (BREAKING)

## 0.5.1

### Patch Changes

- [#42](https://github.com/balloman/expo-google-maps/pull/42) [`c5368c5`](https://github.com/balloman/expo-google-maps/commit/c5368c51ce29bb9323f1135c4b766a36938dd054) Thanks [@balloman](https://github.com/balloman)! - Fix onPress listener not triggering on android

## 0.5.0

### Minor Changes

- [#36](https://github.com/balloman/expo-google-maps/pull/36) [`7afa151`](https://github.com/balloman/expo-google-maps/commit/7afa151ed8ea9d33d42bc6e8eb71ba11002801ca) Thanks [@balloman](https://github.com/balloman)! - Added android support!

  Android should have all the support iOS has which includes

  - Map Display
    - Android has a different method of supplying the api key, so now it will need to be provided with an expo plugin
  - Markers
    - Support for custom views is here as well, but it might be a little buggy on the android side, so let me know if you encounter any issues
  - Polygons

## 0.4.1

### Patch Changes

- [#20](https://github.com/balloman/expo-google-maps/pull/20) [`5a17b05`](https://github.com/balloman/expo-google-maps/commit/5a17b05ba9bd9ea8fedd5125f4d200ab2794f303) Thanks [@balloman](https://github.com/balloman)! - Fixed markers not displaying correctly if the size wasn't set

## 0.4.0

### Minor Changes

- [#18](https://github.com/balloman/expo-google-maps/pull/18) [`543b6f3`](https://github.com/balloman/expo-google-maps/commit/543b6f32540becad2cc0f476815ffc76e8e703c5) Thanks [@balloman](https://github.com/balloman)! - Add the ability to set tracks changes via prop

## 0.3.0

### Minor Changes

- [#16](https://github.com/balloman/expo-google-maps/pull/16) [`b67097d`](https://github.com/balloman/expo-google-maps/commit/b67097d8e7f99f75a8d7ed1a0da92ef96ee5f344) Thanks [@balloman](https://github.com/balloman)! - Add a prop for when a marker is pressed

### Patch Changes

- [#16](https://github.com/balloman/expo-google-maps/pull/16) [`b67097d`](https://github.com/balloman/expo-google-maps/commit/b67097d8e7f99f75a8d7ed1a0da92ef96ee5f344) Thanks [@balloman](https://github.com/balloman)! - Fix(for now) of markers that work but only allow markers that have a defined width and height

## 0.2.0

### Minor Changes

- [#13](https://github.com/balloman/expo-google-maps/pull/13) [`a5b676e`](https://github.com/balloman/expo-google-maps/commit/a5b676e5089cd08389eca6f15c1f5bceefdb577e) Thanks [@balloman](https://github.com/balloman)! - Added the ability to show the user's location on the map

- [#9](https://github.com/balloman/expo-google-maps/pull/9) [`67c77a3`](https://github.com/balloman/expo-google-maps/commit/67c77a300d1b905a99531c5edc2d0139952073fe) Thanks [@balloman](https://github.com/balloman)! - Added the ability to get a callback whenever the map is idle

  - Added an event called onMapIdle that returns the camera position whenever the map is idle

- [#12](https://github.com/balloman/expo-google-maps/pull/12) [`e7e55c2`](https://github.com/balloman/expo-google-maps/commit/e7e55c25442e993d82784b18e6a34512468406a6) Thanks [@balloman](https://github.com/balloman)! - Added an event callback for while the map is being moved

## 0.1.1

### Patch Changes

- [`8f98b96`](https://github.com/balloman/expo-google-maps/commit/8f98b961115b497a0966f449ed5903af57ed6623) Thanks [@balloman](https://github.com/balloman)! - Fix changesets config and swapped to using github changelog generator

## 0.1.0

### Minor Changes

- 0cff69d: Initial Release
