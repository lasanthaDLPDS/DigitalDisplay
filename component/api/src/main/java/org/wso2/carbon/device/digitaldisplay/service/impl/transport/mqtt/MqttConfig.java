/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.digitaldisplay.service.impl.transport.mqtt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.digitaldisplay.service.impl.config.DeviceManagementConfiguration;
import org.wso2.carbon.device.digitaldisplay.service.impl.config.DigitalDisplayConfig;
import org.wso2.carbon.device.digitaldisplay.service.impl.config.EventListenerConfiguration;
import org.wso2.carbon.device.digitaldisplay.service.impl.config.exception.DigitalDisplayConfigurationException;

import java.util.List;

public class MqttConfig {

    private static MqttConfig mqttConfig = new MqttConfig();
    private static final Log log = LogFactory.getLog(MqttConfig.class);

    private boolean enabled;
    private String url;
    private String username;
    private String password;
    private String dcrUrl;
    private String qos;
    private String scopes;
    private String clearSession;

    private MqttConfig() {
        try {
            DigitalDisplayConfig.initialize();
            DeviceManagementConfiguration deviceManagementConfiguration =
                    DigitalDisplayConfig.getInstance().getDeviceTypeConfiguration();
            List<EventListenerConfiguration.Property> properties = deviceManagementConfiguration
                    .getEventListenerConfiguration().getProperties();
            String provider = deviceManagementConfiguration.getEventListenerConfiguration().getEventListenerProvider();
            if (provider.equals("MQTT")) {
                enabled = true;
            }
            if (enabled) {
                for (EventListenerConfiguration.Property property : properties) {
                    switch (property.getName()) {
                        case "url":
                            url = property.getValue();
                            break;
                        case "username":
                            username = property.getValue();
                            break;
                        case "password":
                            password = property.getValue();
                            break;
                        case "dcrUrl":
                            dcrUrl = property.getValue();
                            break;
                        case "qos":
                            qos = property.getValue();
                            break;
                        case "scopes":
                            scopes = property.getValue();
                            break;
                        case "clearSession":
                            clearSession = property.getValue();
                            break;
                    }
                }
            }
        } catch (DigitalDisplayConfigurationException e) {
            log.error("Error occurred while initializing DigitalDisplayConfig", e);
        }
    }

    public static MqttConfig getInstance() {
        return mqttConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDcrUrl() {
        return dcrUrl;
    }

    public String getQos() {
        return qos;
    }

    public String getScopes() {
        return scopes;
    }

    public String getClearSession() {
        return clearSession;
    }
}
