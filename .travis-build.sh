#!/usr/bin/env bash

echo "running as $(whoami)"

set -e

SRC_DIR=$(readlink -f $(dirname $0))
echo "using source directory $SRC_DIR"
ls -ld ${SRC_DIR}
mkdir -p "${SRC_DIR}/target"
TESTFILE="${SRC_DIR}/target/testfile"
touch ${TESTFILE}
echo "touched ${TESTFILE}"
cd ${SRC_DIR}
MVN='/usr/local/maven-3.5.2/bin/mvn'
echo "mvn: resolving dependencies"
${MVN} -B --settings travis-maven-settings.xml dependency:resolve dependency:resolve-plugins >/dev/null
${MVN} -B --settings travis-maven-settings.xml verify -Dhar-replay.chromedriver.chrome.arguments=--no-sandbox
