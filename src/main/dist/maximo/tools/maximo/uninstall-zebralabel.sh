#!/bin/sh

. ./commonenv.sh

../java/jre/bin/java -Xmx5G -classpath .:./classes:$MAXIMO_CLASSPATH psdi.tools.RunScriptFile -czebralabel -funinstall
../java/jre/bin/java -Xmx5G -classpath .:./classes:$MAXIMO_CLASSPATH psdi.tools.RunScriptFile -czebralabel -funinstall-shared
../java/jre/bin/java -Xmx5G -classpath .:./classes:$MAXIMO_CLASSPATH psdi.tools.RunScriptFile -czebralabel -funinstall-screens

# Remove the product files.
rm -f ../../../maximo/deployment/product/zebralabel.xml
rm -f ../../../maximo/applications/maximo/lib/zebra-label.jar
rm -f ../../../maximo/applications/maximo/properties/product/zebralabel.xml
rm -f ../../../maximo/applications/maximo/maximouiweb/webmodule/WEB-INF/lib/zebra-label-web.jar
rm -rf ./en/zebralabel/
rm -rf ./classes/psdi/zebralabel
rm -f ./uninstall-zebralabel.sh
rm -f ./uninstall-zebralabel.bat

exit $?