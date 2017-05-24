package org.homeautomation.digitaldisplay.plugin.impl;

import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterSubscription;
import org.apache.commons.logging.Log;


public class DigitalDisplayEventAdapterSubscription implements InputEventAdapterSubscription {

    private static Log log = LogFactory.getLog(DigitalDisplayEventAdapterSubscription.class);

    @Override
    public void onEvent(Object o) {
        String msg = (String) o;
        log.info("on event message "+msg);
    }
}
