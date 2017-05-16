/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.homeautomation.digitaldisplay.plugin.impl.feature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.homeautomation.digitaldisplay.plugin.impl.DigitalDisplayManager;
import org.homeautomation.digitaldisplay.plugin.impl.util.DigitalDisplayUtils;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.Feature;
import org.wso2.carbon.device.mgt.common.FeatureManager;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;

import java.util.List;

import static org.homeautomation.digitaldisplay.plugin.constants.DigitalDisplayConstants.DEVICE_TYPE;

public class DigitalDisplayFeatureManager implements FeatureManager {

    private static final Log log = LogFactory.getLog(DigitalDisplayManager.class);

    @Override
    public boolean addFeature(Feature feature) throws DeviceManagementException {
        return false;
    }

    @Override
    public boolean addFeatures(List<Feature> features) throws DeviceManagementException {
        return false;
    }

    @Override
    public Feature getFeature(String name) throws DeviceManagementException {
        DeviceManagementProviderService dms;
        try {
            dms = DigitalDisplayUtils.getDeviceManagementService();
            FeatureManager fm = dms.getFeatureManager(DEVICE_TYPE);
            if (fm == null) {
                throw new DeviceManagementException("No feature manager is " +
                                                            "registered with the given type '" + DEVICE_TYPE + "'");
            }
            return fm.getFeature(name);
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the list of features of '" + DEVICE_TYPE + "' device type";
            log.error(msg, e);
            throw new DeviceManagementException(msg);
        }
    }

    @Override
    public List<Feature> getFeatures() throws DeviceManagementException {
        DeviceManagementProviderService dms;
        try {
            dms = DigitalDisplayUtils.getDeviceManagementService();
            FeatureManager fm = dms.getFeatureManager(DEVICE_TYPE);
            if (fm == null) {
                throw new DeviceManagementException("No feature manager is " +
                                                            "registered with the given type '" + DEVICE_TYPE + "'");
            }
            return fm.getFeatures();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the list of features of '" + DEVICE_TYPE + "' device type";
            log.error(msg, e);
            throw new DeviceManagementException(msg);
        }
    }

    @Override
    public boolean removeFeature(String name) throws DeviceManagementException {
        return false;
    }

    @Override
    public boolean addSupportedFeaturesToDB() throws DeviceManagementException {
        return false;
    }
}
