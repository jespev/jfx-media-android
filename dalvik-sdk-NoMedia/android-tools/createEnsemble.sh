#!/bin/bash
# this is an example script. Change the parameters according to your local setup
export ANDROID_SDK=/opt/android-sdk
export PATH=$ANDROID_SDK/tools:$PATH
export CURRENT_DIR=`pwd`
export WORKDIR=$CURRENT_DIR/work
gradlew --info createProject -PDEBUG -PDIR=$WORKDIR -PPACKAGE=org.javafxports.ensemble -PNAME=Ensemble -PANDROID_SDK=$ANDROID_SDK -PJFX_SDK=$CURRENT_DIR/../build/dalvik-sdk -PJFX_APP=$CURRENT_DIR/../apps/samples/Ensemble8/dist -PJFX_MAIN=ensemble.EnsembleApp
