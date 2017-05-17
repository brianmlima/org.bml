#!/bin/bash --

################################################################################
## Resolves the directory this script is in. Tolerates symlinks.
SOURCE="${BASH_SOURCE[0]}" ;
while [[ -h "$SOURCE" ]] ; do # resolve $SOURCE until the file is no longer a symlink
  TARGET="$(readlink "${SOURCE}")"
  if [[ $SOURCE == /* ]]; then
    #echo "SOURCE '$SOURCE' is an absolute symlink to '$TARGET'"
    SOURCE="${TARGET}"
  else
    DIR="$( dirname "${SOURCE}" )"
    #echo "SOURCE '$SOURCE' is a relative symlink to '$TARGET' (relative to '$DIR')"
    SOURCE="${DIR}/${TARGET}" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
  fi
done
################################################################################
## Resolves the parent directory for this script.
BASEDIR="$( cd -P "$( dirname "${SOURCE}" )" && pwd )" ;
################################################################################
## Import Functions
source ${BASEDIR}/Functions.sh



pushd ${BASEDIR}/../ > /dev/null ;
 mvn clean package ${@} -DskipTests -Dcheckstyle.skip=true -Dfindbugs.skip=true ;
popd > /dev/null ;

