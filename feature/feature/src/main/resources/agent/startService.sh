#"""
#/**
#* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#*
#* WSO2 Inc. licenses this file to you under the Apache License,
#* Version 2.0 (the "License"); you may not use this file except
#* in compliance with the License.
#* You may obtain a copy of the License at
#*
#* http://www.apache.org/licenses/LICENSE-2.0
#*
#* Unless required by applicable law or agreed to in writing,
#* software distributed under the License is distributed on an
#* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#* KIND, either express or implied. See the License for the
#* specific language governing permissions and limitations
#* under the License.
#**/
#"""

#!/bin/bash

echo "----------------------------------------------------------------"
echo "|		                 WSO2 IOT Digital				          "
echo "|		                      Display				                  "
echo "|	                     ----------------				          "
echo "|                ....initializing startup-script	              "
echo "----------------------------------------------------------------"

ps aux|grep wso2server.py|awk '{print $2}'|xargs kill -9
ps aux|grep httpserver.py|awk '{print $2}'|xargs kill -9
#xdotool mousemove 0 0
#xdotool search -name LXTerminal windowunmap
#cd ~/
if [ -e wso2_digital_display.zip ]; then
	unzip wso2_digital_display.zip > /dev/null && rm -rf wso2_digital_display.zip > /dev/null
fi

mkdir -p tmp/dd-kernel-test
python displayagent.py

while true; do
sleep 100
done