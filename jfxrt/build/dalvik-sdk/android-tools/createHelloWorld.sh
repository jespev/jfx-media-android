#!/bin/bash
# this is an example script. Change the parameters according to your local setup
export ANDROID_SDK=/opt/android-sdk
export PATH=$ANDROID_SDK/tools:$PATH
export WORKDIR=/home/johan/android/porting
gradlew --info createProject -PDEBUG -PDIR=$WORKDIR/android -PPACKAGE=com.lodgon.android -PNAME=PortJavaFX -PANDROID_SDK=$ANDROID_SDK -PJFX_SDK=/home/johan/open-jfx/android/code/rt/build/android-sdk -PJFX_APP=/home/johan/android/java/PortJavaFX/dist -PJFX_MAIN=com.lodgon.android.PortJavaFX 
