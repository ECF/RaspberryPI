#!/bin/bash
JAVAPROG=java
if [ "$#" -eq  "0" ];then
 echo "No arguments supplied. Usage:  rpimgmthost.sh hostname"
 exit 1
fi
GENERICHOSTNAME="${1}"
echo "Hostname: ${GENERICHOSTNAME}"
JAVAPROPS="-Declipse.ignoreApp=true -Dosgi.noShutdown=true -Decf.generic.server.hostname=${GENERICHOSTNAME}"
echo "javaprops=${JAVAPROPS}"
EQUINOXJAR=plugins/org.eclipse.osgi_3.10.1.v20140909-1633.jar
echo "equinox=${EQUINOXJAR}"
PROGARGS="-configuration file:configuration -os linux -ws gtk -arch arm -console -consoleLog -debug"
${JAVAPROG} ${JAVAPROPS} -jar ${EQUINOXJAR} ${PROGARGS}
