A short description of the files in this repository is shown here:

1.- /jfxrtmedia

This folder is a mirror of the source code provided by the javafxports community at https://bitbucket.org/javafxports/android-graphics-rt for the creation of a javafx runtime wich runs on the top of Dalvik.

Some changes have been made in order to generate the share objects files related to the media capabilities of Javafx in Android devices:

1.1 - File: /build.gradle 1.1.1 Line:305; property IS_COMPILE_MEDIA set to 'true' to build the media capabilites 1.1.2 Line 1936; if (t.name.startsWith("target")) statement modified in order to apply extra ldflags with target='dal' (dalvik)

1.2 - Added modules/media/src/main/native/gstreamer/projects/dalvik folder as a mirror of modules/media/src/main/native/gstreamer/projects/linux

1.3 - File /modules/media/src/main/native/gstreamer/projects/dalvik/gstreamer-lite/Makefile 1.3.1 Option -Werror=implicit-function-declaration disabled 1.3.2 Added several include lines 1.3.3 LDFLAGS modified

1.4 - File /modules/media/src/main/native/gstreamer/projects/dalvik/fxplugins/Makefile 1.4.1 Added several include lines 1.4.2 LDFLAGS modified

1.5 - A shell script named buildrt.sh has been made in order to call the build.gradle script with the proper parameters.

IMPORTANT: Please modify the parameters of buildrt.sh and the include lines on the previous Makefiles according to your local paths.

The current version of the javafx-android runtime with media capabilites but with runtime errors can be found in the path /build/dalvik-sdk inside this zip file.

2.- gstreamer_include

This folder is a copy of the of the gstreamer-sdk-android-arm-debug 'include' folder provided at http://docs.gstreamer.com/display/GstSDK/Installing+for+Android+development needed to compile gstreamer and fxplugins (previous Makefiles).

3.- dalvik-sdk-NoMedia

This folder is a copy of the runtime provided by the javafxports community where all media capabilities are not supported.

4.- JavaFX2_Media_Description.pdf A short description of the media functionalities in JavaFX2.

Considerations:

    Instructions to deploy a JavaFX application(without media capabilities) using the javafx-android runtime provided by the javafxports community can be found here: https://bitbucket.org/javafxports/android/wiki/Building%20and%20deploying%20JavaFX%20Applications

    Instructions to create the javafx-android runtime can be fount at: https://bitbucket.org/javafxports/android/wiki/Building%20the%20JavaFX%20Android%20Runtime

