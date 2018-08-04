#!/bin/bash
set -x

# Test params
#export RTWS_BUCKET=rtws.account.3.appfs.us-east-1
#export RTWS_RELEASE=rtws-nightly.2014-11-20_1653
#export RTWS_SECRET_KEY=redacted
#export RTWS_ACCESS_KEY=redacted
#export RTWS_APPLIANCE_SYSTEM=logsearch-example-fixGET-fixes.aws-dev.aws-dev.deleidos.com


if [[ -z $RTWS_BUCKET || -z $RTWS_RELEASE || -z $RTWS_SECRET_KEY || -z $RTWS_ACCESS_KEY || -z $RTWS_APPLIANCE_SYSTEM ]]; then
	echo "ERROR: missing one or more params."
	exit 1
fi

if [ ! -f Dockerfile ]; then
    echo "ERROR Dockerfile not found."
    exit 1
fi

if [ ! -f rtws-base/site.yml ]; then
    echo "ERROR rtws-base/site.yml not found."
    exit 1
fi

if [ -f /etc/lsb-release ]; then
	which fromdos
	if [ $? -ne 0 ]; then
		apt-get -y install tofromdos
	fi
fi

if [ -f /etc/centos-release ]; then
	which perl
	if [ $? -ne 0 ]; then
		yum -y install perl
	fi
fi

perl -i -pe "s:RTWS_BUCKET:$RTWS_BUCKET:g" rtws-base/site.yml
perl -i -pe "s:RTWS_RELEASE:$RTWS_RELEASE:g" rtws-base/site.yml
perl -i -pe "s:RTWS_SECRET_KEY:$RTWS_SECRET_KEY:g" rtws-base/site.yml
perl -i -pe "s:RTWS_ACCESS_KEY:$RTWS_ACCESS_KEY:g" rtws-base/site.yml
perl -i -pe "s:RTWS_APPLIANCE_SYSTEM:$RTWS_APPLIANCE_SYSTEM:g" rtws-base/site.yml

docker build --rm=true --force-rm=true --tag $RTWS_APPLIANCE_SYSTEM:$RTWS_RELEASE .

if [ $? -ne 0 ]; then
	echo "ERROR building container."
	exit 1
fi

# export the newly created generic process group image
docker run -d --name $RTWS_APPLIANCE_SYSTEM_$RTWS_RELEASE $RTWS_APPLIANCE_SYSTEM:$RTWS_RELEASE true

# Export the newly created container
docker commit $( docker ps -a | grep $RTWS_APPLIANCE_SYSTEM | grep $RTWS_RELEASE | awk '{print $1}') $RTWS_APPLIANCE_SYSTEM:$RTWS_RELEASE
docker save $RTWS_APPLIANCE_SYSTEM:$RTWS_RELEASE > /tmp/${RTWS_APPLIANCE_SYSTEM}_${RTWS_RELEASE}.tar

if [ $? -ne 0 ]; then
	echo "ERROR saving image."
	exit 1
fi

# Cleanup
docker stop $( docker ps -a | grep $RTWS_RELEASE | awk '{print $1}')
docker rm $( docker ps -a | grep $RTWS_RELEASE | awk '{print $1}')
docker rmi $RTWS_APPLIANCE_SYSTEM:$RTWS_RELEASE
