#!/bin/bash
set -x

# Flip flags for enabling roles for AMI/EMI use vs appliance use

perl -i -pe 's:enable_appliance_mode\: true:enable_appliance_mode\: false:g' ./site.yml

perl -i -pe 's:enable_activemq\: false:enable_activemq\: true:g' ./site.yml

perl -i -pe 's:enable_mongo\: false:enable_mongo\: true:g' ./site.yml

perl -i -pe 's:enable_hadoop\: false:enable_hadoop\: true:g' ./site.yml

perl -i -pe 's:enable_hue_upgrade\: false:enable_hue_upgrade\: true:g' ./site.yml

perl -i -pe 's:enable_cassandra\: false:enable_cassandra\: true:g' ./site.yml

perl -i -pe 's:enable_docker\: false:enable_docker\: true:g' ./site.yml

perl -i -pe 's:enable_expressionoasis\: false:enable_expressionoasis\: true:g' ./site.yml

perl -i -pe 's:enable_gpl_jars\: false:enable_gpl_jars\: true:g' ./site.yml

perl -i -pe 's:enable_hardening\: false:enable_hardening\: true:g' ./site.yml