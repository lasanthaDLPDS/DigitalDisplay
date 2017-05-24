/*
* Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.homeautomation.digitaldisplay.plugin.constants;

public class DigitalDisplayConstants {
    //mqtt tranport related constants
    public static final String MQTT_ADAPTER_TOPIC_PROPERTY_NAME = "mqtt.adapter.topic";


    public static final String DEVICE_OWNER = "DEVICE_OWNER";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String MQTT_EP = "MQTT_EP";
    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String DEVICE_REFRESH_TOKEN = "DEVICE_REFRESH_TOKEN";

    public static final String APIM_APPLICATION_TOKEN_VALIDITY_PERIOD = "3600";


    public static final String MQTT_ADAPTER_NAME = "digital_display_mqtt";
    public static final String MQTT_ADAPTER_TYPE = "oauth-mqtt";
    public final static String DEVICE_TYPE_PROVIDER_DOMAIN = "carbon.super";

    public static final String USERNAME_PROPERTY_KEY = "username";
    public static final String PASSWORD_PROPERTY_KEY = "password";
    public static final String DCR_PROPERTY_KEY = "dcrUrl";
    public static final String BROKER_URL_PROPERTY_KEY = "url";
    public static final String SCOPES_PROPERTY_KEY = "scopes";
    public static final String QOS_PROPERTY_KEY = "qos";
    public static final String CLIENT_ID_PROPERTY_KEY = "qos";
    public static final String CLEAR_SESSION_PROPERTY_KEY = "clearSession";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBED_TOPIC = "carbon.super/digitaldisplay/+/digital_display_publisher";

    public static final String CONTENT_VALIDATION = "contentValidator";
    public static final String CONTENT_TRANSFORMATION = "contentTransformer";
    public static final String RESOURCE = "resource";
}
