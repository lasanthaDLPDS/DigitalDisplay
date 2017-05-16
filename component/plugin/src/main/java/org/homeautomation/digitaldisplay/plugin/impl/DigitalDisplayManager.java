/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.homeautomation.digitaldisplay.plugin.exception.DigitalDisplayDeviceMgtPluginException;
import org.homeautomation.digitaldisplay.plugin.impl.dao.DigitalDisplayDAO;
import org.homeautomation.digitaldisplay.plugin.impl.feature.DigitalDisplayFeatureManager;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManager;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.FeatureManager;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.device.mgt.common.license.mgt.LicenseManagementException;
import java.util.List;


/**
 * This represents the DigitalDisplay implementation of DeviceManagerService.
 */
public class DigitalDisplayManager implements DeviceManager {

    private static final DigitalDisplayDAO digitalDisplayDAO = new DigitalDisplayDAO();
    private static final Log log = LogFactory.getLog(DigitalDisplayManager.class);
    private FeatureManager featureManager = new DigitalDisplayFeatureManager();
    @Override
    public FeatureManager getFeatureManager() {
        return featureManager;
    }

    @Override
    public boolean saveConfiguration(TenantConfiguration tenantConfiguration)
            throws DeviceManagementException {
        return false;
    }

    @Override
    public TenantConfiguration getConfiguration() throws DeviceManagementException {
        return null;
    }

    @Override
    public boolean enrollDevice(Device device) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Enrolling a new DigitalDisplay device : " + device.getDeviceIdentifier());
            }
            DigitalDisplayDAO.beginTransaction();
            status = digitalDisplayDAO.getDeviceDAO().addDevice(device);
            DigitalDisplayDAO.commitTransaction();
        } catch (DigitalDisplayDeviceMgtPluginException e) {
            try {
                DigitalDisplayDAO.rollbackTransaction();
            } catch (DigitalDisplayDeviceMgtPluginException iotDAOEx) {
                String msg = "Error occurred while roll back the device enrol transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while enrolling the DigitalDisplay device : " + device.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean modifyEnrollment(Device device) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Modifying the DigitalDisplay device enrollment data");
            }
            DigitalDisplayDAO.beginTransaction();
            status = digitalDisplayDAO.getDeviceDAO().updateDevice(device);
            DigitalDisplayDAO.commitTransaction();
        } catch (DigitalDisplayDeviceMgtPluginException e) {
            try {
                DigitalDisplayDAO.rollbackTransaction();
            } catch (DigitalDisplayDeviceMgtPluginException iotDAOEx) {
                String msg = "Error occurred while roll back the update device transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while updating the enrollment of the DigitalDisplay device : " +
                    device.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Dis-enrolling DigitalDisplay device : " + deviceId);
            }
            DigitalDisplayDAO.beginTransaction();
            status = digitalDisplayDAO.getDeviceDAO().deleteDevice(deviceId.getId());
            DigitalDisplayDAO.commitTransaction();
        } catch (DigitalDisplayDeviceMgtPluginException e) {
            try {
                DigitalDisplayDAO.rollbackTransaction();
            } catch (DigitalDisplayDeviceMgtPluginException iotDAOEx) {
                String msg = "Error occurred while roll back the device dis enrol transaction :" + deviceId.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while removing the DigitalDisplay device : " + deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException {
        boolean isEnrolled = false;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Checking the enrollment of DigitalDisplay device : " + deviceId.getId());
            }
            Device iotDevice = digitalDisplayDAO.getDeviceDAO().getDevice(deviceId.getId());
            if (iotDevice != null) {
                isEnrolled = true;
            }
        } catch (DigitalDisplayDeviceMgtPluginException e) {
            String msg = "Error while checking the enrollment status of DigitalDisplay device : " +
                    deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return isEnrolled;
    }

    @Override
    public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean setActive(DeviceIdentifier deviceId, boolean status)
            throws DeviceManagementException {
        return true;
    }

    @Override
    public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        Device device;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting the details of DigitalDisplay device : " + deviceId.getId());
            }
            device = digitalDisplayDAO.getDeviceDAO().getDevice(deviceId.getId());
        } catch (DigitalDisplayDeviceMgtPluginException e) {
            String msg = "Error while fetching the DigitalDisplay device : " + deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return device;
    }

    @Override
    public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType)
            throws DeviceManagementException {
        return true;
    }

    public boolean isClaimable(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        return false;
    }

    @Override
    public boolean setStatus(DeviceIdentifier deviceId, String currentOwner,
                             EnrolmentInfo.Status status) throws DeviceManagementException {
        return false;
    }

    @Override
    public License getLicense(String s) throws LicenseManagementException {
        return null;
    }

    @Override
    public void addLicense(License license) throws LicenseManagementException {

    }

    @Override
    public boolean requireDeviceAuthorization() {
        return true;
    }

    @Override
    public boolean updateDeviceInfo(DeviceIdentifier deviceIdentifier, Device device) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug(
                        "updating the details of DigitalDisplay device : " + deviceIdentifier);
            }
            DigitalDisplayDAO.beginTransaction();
            status = digitalDisplayDAO.getDeviceDAO().updateDevice(device);
            DigitalDisplayDAO.commitTransaction();
        } catch (DigitalDisplayDeviceMgtPluginException e) {
            try {
                DigitalDisplayDAO.rollbackTransaction();
            } catch (DigitalDisplayDeviceMgtPluginException iotDAOEx) {
                String msg = "Error occurred while roll back the update device info transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg =
                    "Error while updating the DigitalDisplay device : " + deviceIdentifier;
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public List<Device> getAllDevices() throws DeviceManagementException {
        List<Device> devices;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Fetching the details of all DigitalDisplay devices");
            }
            devices = digitalDisplayDAO.getDeviceDAO().getAllDevices();
        } catch (DigitalDisplayDeviceMgtPluginException e) {
            String msg = "Error while fetching all DigitalDisplay devices.";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return devices;
    }

}