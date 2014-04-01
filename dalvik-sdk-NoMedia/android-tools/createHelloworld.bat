set ANDROID_SDK=c:/android-sdk-windows
set WORKDIR=c:/Users/jdoe/work/android-samples
set JFX_SDK=c:/Users/jdoe/work/dalvik-sdk
set APPS_HOME=c:/Users/jdoe/NetBeansProjects
set JFX_MAIN_CLASS=org.javafxports.HelloWorld
gradlew.bat --info createProject -PDEBUG -PDIR=%WORKDIR% -PPACKAGE=com.oracle.helloworld -PNAME=HelloWorld -PANDROID_SDK=%ANDROID_SDK% -PJFX_SDK=%JFX_SDK% -PJFX_APP=%APPS_HOME%/HelloWorld/dist -PJFX_MAIN=%JFX_MAIN_CLASS%
