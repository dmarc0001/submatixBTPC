; Script generated by the HM NIS Edit Script Wizard.

;compiler direktiven
SetCompress force
SetCompressor /SOLID zlib
SetDatablockOptimize on
;compiler direktiven

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "submatixBTLog"
!define PRODUCT_VERSION "1.1"
!define PRODUCT_PUBLISHER "Dirk Marciniak"
!define PRODUCT_WEB_SITE "http://www.submatix.com"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "progicon.ico"
!define MUI_UNICON "unicon.ico"

; Language Selection Dialog Settings
!define MUI_LANGDLL_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_LANGDLL_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "license.txt"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!define MUI_FINISHPAGE_RUN "$INSTDIR\submatix_start.bat"
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "French"
!insertmacro MUI_LANGUAGE "German"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "SubmatixBTLog-1.1-Setup.exe"
InstallDir "$PROGRAMFILES\submatixBTLog"
ShowInstDetails show
ShowUnInstDetails show

Function .onInit
  !insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd

Section "MAIN" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite ifnewer
  File "..\..\..\submatix\submatixBTLog\versioncheck.jar"
  File "..\..\..\submatix\submatixBTLog\submatixBTForPC.jar"
  File "..\..\..\submatix\submatixBTLog\submatix_start_this_on_trouble.bat"
  File "..\..\..\submatix\submatixBTLog\submatix_start.bat"
  File "..\..\..\submatix\submatixBTLog\progicon.ico"
  File "..\..\..\submatix\submatixBTLog\unicon.ico"
  CreateDirectory "lib"
  SetOutPath "$INSTDIR\lib"
  File "..\..\..\submatix\submatixBTLog\lib\rxtxSerial_win_x86.dll"
  File "..\..\..\submatix\submatixBTLog\lib\rxtxSerial_win_amd64.dll"
  File "..\..\..\submatix\submatixBTLog\lib\rxtxParallel_win_x86.dll"
  File "..\..\..\submatix\submatixBTLog\lib\rxtxParallel_win_amd64.dll"
  File "..\..\..\submatix\submatixBTLog\lib\librxtxSerial_linux_x86.so"
  File "..\..\..\submatix\submatixBTLog\lib\librxtxSerial_linux_amd64.so"
  File "..\..\..\submatix\submatixBTLog\lib\librxtxSerial.jnilib"
  File "..\..\..\submatix\submatixBTLog\lib\librxtxParallel_linux_x86.so"
  File "..\..\..\submatix\submatixBTLog\lib\librxtxParallel_linux_amd64.so"
  CreateDirectory "$SMPROGRAMS\submatixBTLog"
  CreateShortCut "$SMPROGRAMS\submatixBTLog\submatixBTLog Ver. 1.1.lnk" "$INSTDIR\submatix_start.bat" icon.file "$INSTDIR\progicon.ico"
  CreateShortCut "$SMPROGRAMS\submatixBTLog\submatixBTLog Ver. 1.1 DEBUG.lnk" "$INSTDIR\submatix_start.bat" icon_file "$INSTDIR\progicon.ico"
SectionEnd

Section -AdditionalIcons
  SetOutPath $INSTDIR
  CreateDirectory "$SMPROGRAMS\submatixBTLog"
  CreateShortCut "$SMPROGRAMS\submatixBTLog\Uninstall.lnk" "$INSTDIR\uninst.exe" icon_file "$INSTDIR\unicon.ico"
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) wurde erfolgreich deinstalliert."
FunctionEnd

Function un.onInit
!insertmacro MUI_UNGETLANGUAGE
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "M�chten Sie $(^Name) und alle seinen Komponenten deinstallieren?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\lib\librxtxParallel_linux_x86.so"
  Delete "$INSTDIR\lib\librxtxParallel_linux_amd64.so"
  Delete "$INSTDIR\lib\librxtxSerial.jnilib"
  Delete "$INSTDIR\lib\librxtxSerial_linux_x86.so"
  Delete "$INSTDIR\lib\librxtxSerial_linux_amd64.so"
  Delete "$INSTDIR\lib\rxtxParallel_win_x86.dll"
  Delete "$INSTDIR\lib\rxtxParallel_win_amd64.dll"
  Delete "$INSTDIR\lib\rxtxSerial_win_x86.dll"
  Delete "$INSTDIR\lib\rxtxSerial_win_amd64.dll"
  Delete "$INSTDIR\submatix_start.bat"
  Delete "$INSTDIR\submatix_start_this_on_trouble.bat"
  Delete "$INSTDIR\submatixBTForPC.jar"
  Delete "$INSTDIR\versioncheck.jar"
  Delete "$INSTDIR\unicon.ico"
  Delete "$INSTDIR\progicon.ico"
  Delete "$INSTDIR\derby.log"
  Delete "$INSTDIR\spxLogProgram.conf"
  Delete "$SMPROGRAMS\submatixBTLog\Uninstall.lnk"
  Delete "$SMPROGRAMS\submatixBTLog\submatixBTLog Ver. 1.0a.lnk"
  Delete "$SMPROGRAMS\submatixBTLog\submatixBTLog Ver. 1.0a DEBUG.lnk"
  RMDir /r "$SMPROGRAMS\submatixBTLog"
  RMDir "$INSTDIR\lib"
  RMDir /r "$INSTDIR"
  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  SetAutoClose true
SectionEnd