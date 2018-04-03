;; NSIS script for DumpStack50

;; configuration
!addplugindir .
!define VERSION "1.0"
!include 'LogicLib.nsh'

Name "Excat障害情報分析ツール(for Java) ${VERSION}"

OutFile 'install-erroranalyzer.exe'

InstallDir c:\excat\ErrorAnalyzer

#LoadLanguageFile "${NSISDIR}\Contrib\Language files\English.nlf"
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Japanese.nlf"

;; pages
Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles



;; components
Section "Excat障害情報分析ツール(for Java)  ${VERSION}"

  SetOutPath "$INSTDIR"
  ;; setup Hello
  File bcel-5.1.jar
  File ErrorAnalyzer.jar
  File ErrorAnalyzer.bat
  File javaw.exe.manifest
  File libeay32_0_9_8d.dll
  File libLicense.dll
  File log4j-1.2.14.jar
  File org.eclipse.core.runtime_3.1.2.jar
  File org.eclipse.jface.text_3.1.2.jar
  File org.eclipse.jface_3.1.1.jar
  File org.eclipse.text_3.1.1.jar
  File ssleay32_0_9_8d.dll
  File swt.jar
  File swt-awt-win32-3138.dll
  File swt-gdip-win32-3138.dll
  File swt-win32-3138.dll
  File xercesImpl.jar
  File xml-apis.jar
  
  WriteRegStr HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\ErrorAnalyzerGUI' 'DisplayName' 'Excat障害情報分析ツール(for Java) ${VERSION}'
  WriteRegStr HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\ErrorAnalyzerGUI' 'UninstallString' '"$INSTDIR\uninstall-erroranalyzer.exe"'
  WriteRegDWORD HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\ErrorAnalyzerGUI' 'NoModify' 1
  WriteRegDWORD HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\ErrorAnalyzerGUI' 'NoRepair' 1
  WriteUninstaller 'uninstall-erroranalyzer.exe'
SectionEnd

Section "shortcut" DesktopIcon
   CreateShortCut "$DESKTOP\ErrorAnalyzer.lnk" "$INSTDIR\ErrorAnalyzer.bat"
SectionEnd

Section 'Uninstall'
  DeleteRegKey HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\ErrorAnalyzerGUI'
  Delete "$INSTDIR\uninstall-erroranalyzer.exe"

  ;; uninstall shortcut
  Delete "$DESKTOP\ErrorAnalyzer.lnk"

  ;; uninstall Hello
  Delete "$INSTDIR\bcel-5.1.jar"
  Delete "$INSTDIR\ErrorAnalyzer.jar"
  Delete "$INSTDIR\ErrorAnalyzer.bat"
  Delete "$INSTDIR\javaw.exe.manifest"
  Delete "$INSTDIR\libeay32_0_9_8d.dll"
  Delete "$INSTDIR\libLicense.dll"
  Delete "$INSTDIR\log4j-1.2.14.jar"
  Delete "$INSTDIR\org.eclipse.core.runtime_3.1.2.jar"
  Delete "$INSTDIR\org.eclipse.jface.text_3.1.2.jar"
  Delete "$INSTDIR\org.eclipse.jface_3.1.1.jar"
  Delete "$INSTDIR\org.eclipse.text_3.1.1.jar"
  Delete "$INSTDIR\ssleay32_0_9_8d.dll"
  Delete "$INSTDIR\swt.jar"
  Delete "$INSTDIR\swt-awt-win32-3138.dll"
  Delete "$INSTDIR\swt-gdip-win32-3138.dll" 
  Delete "$INSTDIR\swt-win32-3138.dll"
  Delete "$INSTDIR\xercesImpl.jar"
  Delete "$INSTDIR\xml-apis.jar"
  
  Delete "$INSTDIR\uninstall-erroranalyzer.exe"
  
  RMDir "$INSTDIR"
SectionEnd

;; the end of file
