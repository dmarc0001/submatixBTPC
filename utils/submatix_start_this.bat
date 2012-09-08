@echo off

echo JAVA_HOME has value %JAVA_HOME% before 
if exist %JAVA_HOME% goto withJavaPath

@rem javahome not set, search for typical windows locations
if exist "%ProgramFiles(x86)%\Java\jre6" set JAVA_HOME=%ProgramFiles(x86)%\Java\jre6
if exist "%ProgramFiles(x86)%\Java\jre6" goto withJavaPath
if exist "%ProgramFiles%\Java\jre6" set JAVA_HOME=%ProgramFiles%\Java\jre6
if exist "%ProgramFiles%\Java\jre6" goto withJavaPath
if exist "%ProgramFiles(x86)%\Java\jre7" set JAVA_HOME=%ProgramFiles(x86)%\Java\jre7
if exist "%ProgramFiles(x86)%\Java\jre7" goto withJavaPath
if exist "%ProgramFiles%\Java\jre7" set JAVA_HOME=%ProgramFiles%\Java\jre7
if exist "%ProgramFiles%\Java\jre7" goto withJavaPath
echo Variable JAVA_HOME not set
echo Java can't found.
echo exit. Sorry.
goto ExitNoJavaFound

:withJavaPath
echo JAVA_HOME has value #%JAVA_HOME%# after

@rem search fuer javaw.exe
set LOCAL_JAVA=javaw.exe
if exist "%JAVA_HOME%\bin\javaw.exe" set LOCAL_JAVA=%JAVA_HOME%\bin\javaw.exe
echo Using java: %LOCAL_JAVA%

@rem make homdir
set basedir=%~f0
:strip
set removed=%basedir:~-1%
set basedir=%basedir:~0,-1%
if NOT "%removed%"=="\" goto strip
set SUBMATIX_HOME=%basedir%

echo SUBMATIX directory is "%SUBMATIX_HOME%"

@rem Check to see if we are running in a 1.6/1.7 JVM and inform the user if not and skip launch. versioncheck.jar 
@rem is a special jar file which has been compiled with javac version 1.2.2, which should be able to be run by 
@rem that version of higher.  The arguments to JavaVersionChecker below specify the minimum acceptable version 
@rem (first arg) and any other acceptable subsequent versions.  <MAJOR>.<MINOR> should be all that is 
@rem necessary for the version form. 
"%LOCAL_JAVA%" -cp "%SUBMATIX_HOME%\versioncheck.jar" JavaVersionChecker 1.6 1.7
if ErrorLevel 1 goto ExitForWrongJavaVersion

@rem start
set PARAMS=--cacheonstart --loglevel DEBUG

@rem -Dsun.java2d.noddraw=true prevents performance problems on Win32 systems. 

@rem Run with no command window. This may not work with versions of Windows prior to XP. 
@rem Remove 'start "SUBMATIX Bluethooth Log/Config" /B' for compatibility only if necessary 
start "SUBMATIX Bluethooth Log/Config" /B "%LOCAL_JAVA%" -Xmx512m -Dsun.java2d.noddraw=true -cp "%SUBMATIX_HOME%\submatixBTForPC.jar" de.dmarcini.submatix.pclogger.gui.MainCommGUI %PARAMS%
goto eof

:ExitNoJavaFound
goto eof

:ExitForWrongJavaVersion
echo wrong java version on this computer. must have version 6 or 7
pause

:eof