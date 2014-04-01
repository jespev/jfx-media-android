set ANDROID_SDK=c:/android-sdk-windows
set WORKDIR=c:/Users/jdoe/work/android-samples
set JFX_SDK=c:/Users/jdoe/work/dalvik-sdk
gradlew.bat --info createProject -PDEBUG -PDIR=%WORKDIR% -PPACKAGE=org.javafxports.ensemble -PNAME=Ensemble -PANDROID_SDK=%ANDROID_SDK% -PJFX_SDK=%JFX_SDK% -PJFX_APP=%APPS_HOME%/Ensemble/dist -PJFX_MAIN=ensemble.EnsembleApp
