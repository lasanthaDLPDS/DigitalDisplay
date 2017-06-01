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

package org.homeautomation.digitaldisplay.plugin.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.homeautomation.digitaldisplay.plugin.constants.DigitalDisplayConstants;
import org.homeautomation.digitaldisplay.plugin.internal.DigitalDisplayManagementDataHolder;
import org.homeautomation.digitaldisplay.plugin.mqtt.MqttConfig;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterConfiguration;
import org.wso2.carbon.event.input.adapter.core.MessageType;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains utility methods used by Digital Display plugin.
 */
public class DigitalDisplayUtils {

    private static Log log = LogFactory.getLog(DigitalDisplayUtils.class);

    public static void setupMqttInputAdapter() throws IOException {
        if (!MqttConfig.getInstance().isEnabled()) {
            return;
        }
        InputEventAdapterConfiguration inputEventAdapterConfiguration =
                createMqttInputEventAdapterConfiguration(DigitalDisplayConstants.MQTT_ADAPTER_NAME,
                        DigitalDisplayConstants.MQTT_ADAPTER_TYPE, MessageType.TEXT);
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(
                    DigitalDisplayConstants.DEVICE_TYPE_PROVIDER_DOMAIN, true);
            DigitalDisplayManagementDataHolder.getInstance().getInputEventAdapterService()
                    .create(inputEventAdapterConfiguration, new DigitalDisplayEventAdapterSubscription());
        } catch (InputEventAdapterException e) {
            log.error("Unable to create Input Event Adapter : " + DigitalDisplayConstants.MQTT_ADAPTER_NAME, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Create Output Event Adapter Configuration for given configuration.
     *
     * @param name      Input Event Adapter name
     * @param type      Input Event Adapter type
     * @param msgFormat Input Event Adapter message format
     * @return InputEventAdapterConfiguration instance for given configuration
     */
    private static InputEventAdapterConfiguration createMqttInputEventAdapterConfiguration(String name, String type,
                                                    String msgFormat) throws IOException {
        InputEventAdapterConfiguration inputEventAdapterConfiguration = new InputEventAdapterConfiguration();
        inputEventAdapterConfiguration.setName(name);
        inputEventAdapterConfiguration.setType(type);
        inputEventAdapterConfiguration.setMessageFormat(msgFormat);
        Map<String, String> mqttAdapterProperties = new HashMap<>();
        mqttAdapterProperties.put(DigitalDisplayConstants.USERNAME_PROPERTY_KEY, MqttConfig.getInstance().getUsername());
        mqttAdapterProperties.put(DigitalDisplayConstants.DCR_PROPERTY_KEY, MqttConfig.getInstance().getDcrUrl());
        mqttAdapterProperties.put(DigitalDisplayConstants.BROKER_URL_PROPERTY_KEY, MqttConfig.getInstance().getUrl());
        mqttAdapterProperties.put(DigitalDisplayConstants.SCOPES_PROPERTY_KEY, MqttConfig.getInstance().getScopes());
        mqttAdapterProperties.put(DigitalDisplayConstants.CLEAR_SESSION_PROPERTY_KEY, MqttConfig.getInstance()
                .getClearSession());
        mqttAdapterProperties.put(DigitalDisplayConstants.QOS_PROPERTY_KEY, MqttConfig.getInstance().getQos());
        mqttAdapterProperties.put(DigitalDisplayConstants.PASSWORD_PROPERTY_KEY, MqttConfig.getInstance().getPassword());
        mqttAdapterProperties.put(DigitalDisplayConstants.CLIENT_ID_PROPERTY_KEY, "");
        mqttAdapterProperties.put(DigitalDisplayConstants.TOPIC, DigitalDisplayConstants.SUBSCRIBED_TOPIC);
        mqttAdapterProperties.put(DigitalDisplayConstants.CONTENT_TRANSFORMATION,
                                  DigitalDisplayMqttContentTransformer.class.getName());
        mqttAdapterProperties.put(DigitalDisplayConstants.CONTENT_VALIDATION, "default");
        mqttAdapterProperties.put(DigitalDisplayConstants.RESOURCE, "input-event");
        inputEventAdapterConfiguration.setProperties(mqttAdapterProperties);

        return inputEventAdapterConfiguration;
    }

}
