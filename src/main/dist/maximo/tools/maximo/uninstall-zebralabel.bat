@ECHO OFF

SETLOCAL

call commonenv.bat

@..\java\jre\bin\java  -classpath .;.\classes;%MAXIMO_CLASSPATH%  psdi.tools.RunScriptFile -czebralabel -funinstall

REM Remove the product files.
DEL /F ..\..\..\maximo\deployment\product\zebralabel.xml
DEL /F ..\..\..\maximo\applications\maximo\lib\zebra-label.jar
DEL /F ..\..\..\maximo\applications\maximo\properties\product\zebralabel.xml
DEL /F ..\..\..\maximo\applications\maximo\maximouiweb\webmodule\WEB-INF\lib\zebra-label-web.jar
RMDIR en\zebralabel /S /Q
RMDIR classes\psdi\zebralabel /S /Q
DEL /F .\uninstall-zebralabel.sh
DEL /F .\uninstall-zebralabel.bat