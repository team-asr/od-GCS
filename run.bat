@echo off
echo ********************************************************************
echo *         UDB4 Tool, to evolve to                                  *
echo *         a full GCS                                               *
echo *                                                                  *
echo ********************************************************************
cd bin
copy ..\gcs-config.properties .
copy ..\udbtool.jpg .
rem set path="C:\Program Files (x86)\Java\jdk1.7.0_40\bin"
set wwd=C:\usr\dev\worldwind-v2.2.0
java -Xmx512m -Dsun.java2d.noddraw=true -classpath %wwd%\src;%wwd%\worldwindx.jar;%wwd%\worldwind.jar;%wwd%\jogl.jar;%wwd%\gluegen-rt.jar;%wwd%\gdal.jar;../lib/WWJApi.jar;../lib/SteelSeries-1.4.jar;../lib/trident.jar;. -splash:udbtool.jpg com.declspec.gichanga.GcsGui
pause
