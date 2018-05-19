@echo off

echo JAVA_HOME has value "%JAVA_HOME%" before 

@REM first oracle style search
if exist "%ProgramData%\Oracle\Java\javapath" set JAVA_HOME=%ProgramData%\Oracle\Java\javapath
if exist "%ProgramData%\Oracle\Java\javapath" goto withJavaPath

echo Java can't found.
echo exit. Sorry.
goto ExitNoJavaFound

:withJavaPath
echo JAVA_HOME has value "%JAVA_HOME%" after

@rem search fuer javaw.exe
set LOCAL_JAVA=javaw.exe
if exist "%JAVA_HOME%\bin\javaw.exe" set LOCAL_JAVA=%JAVA_HOME%\bin\javaw.exe
if exist "%JAVA_HOME%\javaw.exe" set LOCAL_JAVA=%JAVA_HOME%\javaw.exe 
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
set LOGGING=log4j2normal.xml 
set MAINCLASS=de.dmarcini.submatix.pclogger.gui.MainCommGUI
set JAR="%SUBMATIX_HOME%\SubmatixBTConfigPC.jar"
set LIB="lib"

@rem -Dsun.java2d.noddraw=true prevents performance problems on Win32 systems. 
@rem -Djava.library.path="%SUBMATIX_HOME%\lib" finds the rxtx-native libs
@rem Run with no command window. This may not work with versions of Windows prior to XP.
start "SUBMATIX Bluethooth Log/Config" /B "%LOCAL_JAVA%" -Xmx512m  -XX:+UseG1GC -Dsun.java2d.noddraw=true -Djava.library.path=%LIB% -Dlog4j.configurationFile=%LOGGING% -cp %JAR% %MAINCLASS% %PARAMS%
goto eof

:ExitNoJavaFound
goto eof

:eof

