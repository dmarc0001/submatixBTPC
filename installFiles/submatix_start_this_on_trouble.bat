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
set LIB="%SUBMATIX_HOME%\lib"

@rem -Dsun.java2d.noddraw=true prevents performance problems on Win32 systems. 
@rem -Djava.library.path="%SUBMATIX_HOME%\lib" finds the rxtx-native libs
@rem Run with no command window. This may not work with versions of Windows prior to XP.
"%LOCAL_JAVA%" -Xmx512m -XX:+UseG1GC -Djava.library.path=%LIB% -Dsun.java2d.noddraw=true -Dlog4j.configurationFile=%LOGGING% -cp "%SUBMATIX_HOME%\%JAR%" %MAINCLASS% %PARAMS%
goto eof

:ExitNoJavaFound
goto eof

:eof
