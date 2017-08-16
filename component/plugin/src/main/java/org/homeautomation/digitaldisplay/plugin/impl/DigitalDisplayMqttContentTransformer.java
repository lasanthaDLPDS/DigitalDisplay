package org.homeautomation.digitaldisplay.plugin.impl;

import org.wso2.carbon.device.mgt.input.adapter.extension.ContentTransformer;
import java.util.Map;

public class DigitalDisplayMqttContentTransformer implements ContentTransformer {

    @Override
    public String getType() {
        return "digital-display-meta-transformer";
    }

    @Override
    public Object transform(Object message, Map<String, Object> dynamicProperties) {
        return message;
    }
}
