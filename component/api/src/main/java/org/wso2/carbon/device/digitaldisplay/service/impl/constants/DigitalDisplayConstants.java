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

package org.wso2.carbon.device.digitaldisplay.service.impl.constants;

public class DigitalDisplayConstants {

    public final static String DIGITAL_DISPLAY_MQTT_EP = "DIGITAL_DISPLAY_MQTT_EP";

    public final static String DEVICE_TYPE = "digitaldisplay";
    public final static String DEVICE_PLUGIN_DEVICE_NAME = "DEVICE_NAME";
    public final static String DEVICE_PLUGIN_DEVICE_ID = "digitaldisplay_DEVICE_ID";
    public final static String RESTART_SERVER_CONSTANT = "restart_server";
    public final static String RESTART_DISPLAY_CONSTANT = "restart_display";
    public final static String RESTART_BROWSER_CONSTANT = "restart_browser";
    public final static String TERMINATE_DISPLAY_CONSTANT = "terminate_display";
    public final static String EDIT_SEQUENCE_CONSTANT = "edit_sequence";
    public final static String UPLOAD_CONTENT_CONSTANT = "upload_content";
    public final static String ADD_NEW_RESOURCE_CONSTANT = "add_new_resource";
    public final static String REMOVE_RESOURCE_CONSTANT = "remove_resources";
    public final static String SCREENSHOT_CONSTANT = "get_screenshot";
    public final static String GET_CONTENTLIST_CONSTANT = "get_content_list";
    public final static String GET_DEVICE_STATUS_CONSTANT = "get_device_status";
    public final static String PUBLISH_TOPIC = "wso2/iot/digital_display/%s/digital_display_subscriber";
    public static final String DATA_SOURCE_NAME = "jdbc/DigitalDisplayDM_DB";

    //mqtt tranport related constants
    public static final String MQTT_ADAPTER_TOPIC_PROPERTY_NAME = "mqtt.adapter.topic";


    public static final String DEVICE_OWNER = "DEVICE_OWNER";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String MQTT_EP = "MQTT_EP";
    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String DEVICE_REFRESH_TOKEN = "DEVICE_REFRESH_TOKEN";
    public static final String CONSUMER_KEY = "COMSUMER_KEY";
    public static final String CONSUMER_SECRET = "CONSUMER_SECRET";
    public static final String SERVER_IP = "SERVER_IP";

    public static final String APIM_APPLICATION_TOKEN_VALIDITY_PERIOD = "3600";


    public static final String PERM_ENROLL_DIGITAL_DISPLAY = "/permission/admin/device-mgt/devices/enroll/digitaldisplay";
    public static final String PERM_OWNING_DEVICE_VIEW = "/permission/admin/device-mgt/devices/owning-device/view";

    public static final String ROLE_NAME = "internal/devicemgt-user";
}
