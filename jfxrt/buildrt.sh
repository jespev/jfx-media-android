export ANDROID_SDK=/opt/JFX/android-sdk-linux
export ANDROID_NDK=/opt/JFX/NDK/android-ndk-r5b
export BINARY_STUB=/opt/JFX/JDK/jdk1.8.0/jre/lib/ext/jfxrt.jar
export FREETYPE_DIR=/opt/JFX/freetype-2.4.0/install
export JAVA_HOME=/opt/JFX/JDK/jdk1.7.0_51
export JDK_HOME=/opt/JFX/JDK/jdk1.7.0_51
export JAVA8_HOME=/opt/JFX/JDK/jdk1.8.0
export GRADLE_BIN=/opt/JFX/gradle-1.8/bin
export PATH=$PATH:$JAVA_HOME:$JAVA8_HOME:$GRADLE_BIN

gradle -PANDROID_SDK=$ANDROID_SDK -PANDROID_NDK=$ANDROID_NDK -PDEBUG -PDALVIK_VM=true -PBINARY_STUB=$BINARY_STUB -PFREETYPE_DIR=$FREETYPE_DIR -PCOMPILE_TARGETS=dalvik
