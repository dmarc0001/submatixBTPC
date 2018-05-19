@echo off
cls
::
:: exist JAVA_HONE and exist java execute
::
if exist "%JAVA_HOME%\bin\java.exe" (
    goto :withJavaPath
)

::
:: find current java in registry
::
setlocal ENABLEEXTENSIONS
set KEY_NAME="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment"
set VALUE_NAME=CurrentVersion
::
:: get the current version
::
FOR /F "usebackq skip=2 tokens=3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) DO (
    set ValueValue=%%A
)
if defined ValueValue (
    @echo the current Java runtime is  "%ValueValue%"
) else (
    @echo %KEY_NAME%\%VALUE_NAME% not found in registry. Please do set JAVA_HOME manually.
    goto ExitNoJavaFound
)
set JAVA_CURRENT="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment\%ValueValue%"
set JAVA_HOME=JavaHome
::
:: get the javahome
::
FOR /F "usebackq skip=2 tokens=3*" %%A IN (`REG QUERY %JAVA_CURRENT% /v %JAVA_HOME% 2^>nul`) DO (
    set JAVA_PATH=%%A %%B
)
set JAVA_HOME=%JAVA_PATH%
echo the path of the current Java JVM according to the registry is JAVA_HOME is
echo %JAVA_HOME%
echo.

:withJavaPath
echo JAVA_HOME has value "%JAVA_HOME%" after

@rem search fuer java.exe
set LOCAL_JAVA=java.exe
if exist "%JAVA_HOME%\bin\java.exe" set LOCAL_JAVA=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_HOME%\java.exe" set LOCAL_JAVA=%JAVA_HOME%\java.exe 
echo Using java: %LOCAL_JAVA%

@rem make homdir
set basedir=%~f0
:strip
set removed=%basedir:~-1%
set basedir=%basedir:~0,-1%
if NOT "%removed%"=="\" goto strip
set SUBMATIX_HOME=%basedir%

echo SUBMATIX directory is "%SUBMATIX_HOME%"

@rem Parameter avavible:
@rem -databasedir  DATABASEDIR
@rem -exportdir DIR-FOR-EXPORTS
@rem -langcode XX  (by example "de" or "fr" or "en" )
@rem -help

@rem start
set PARAMS= 
set LOGGING=log4j2debug.xml 
set MAINCLASS=de.dmarcini.submatix.pclogger.gui.MainCommGUI
set JAR=SubmatixBTConfigPC.jar

@rem -Dsun.java2d.noddraw=true prevents performance problems on Win32 systems. 
@rem -Djava.library.path="%SUBMATIX_HOME%\lib" finds the rxtx-native libs
@rem Run with no command window. This may not work with versions of Windows prior to XP.
"%LOCAL_JAVA%" -Xmx512m -XX:+UseG1GC -Dlog4j.configurationFile=%LOGGING% -cp "%SUBMATIX_HOME%\%JAR%" %MAINCLASS% %PARAMS%
goto eof

:ExitNoJavaFound
goto eof

:eof
