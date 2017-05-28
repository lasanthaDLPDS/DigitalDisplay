/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.digitaldisplay.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.digitaldisplay.service.impl.constants.DigitalDisplayConstants;
import org.wso2.carbon.device.digitaldisplay.service.impl.exception.DigitalDisplayException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.digitaldisplay.service.impl.util.APIUtil;
import org.wso2.carbon.device.digitaldisplay.service.impl.util.DigitalDisplayMQTTConnector;
import org.wso2.carbon.device.digitaldisplay.service.impl.util.ZipArchive;
import org.wso2.carbon.device.digitaldisplay.service.impl.util.ZipUtil;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.apimgt.application.extension.exception.APIManagerException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.identity.jwt.client.extension.JWTClient;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.identity.jwt.client.extension.exception.JWTClientException;
import org.wso2.carbon.user.api.UserStoreException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DigitalDisplayServiceImpl implements DigitalDisplayService{

    private static Log log = LogFactory.getLog(DigitalDisplayServiceImpl.class);
    private static DigitalDisplayMQTTConnector digitalDisplayMQTTConnector;
    private static final String KEY_TYPE = "PRODUCTION";
    private static ApiApplicationKey apiApplicationKey;

    private boolean register(String deviceId, String name) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(DigitalDisplayConstants.DEVICE_TYPE);
        try {
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                return false;
            }
            Device device = new Device();
            device.setDeviceIdentifier(deviceId);
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setDateOfEnrolment(new Date().getTime());
            enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
            enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);
            device.setName(name);
            device.setType(DigitalDisplayConstants.DEVICE_TYPE);
            enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
            enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
            device.setEnrolmentInfo(enrolmentInfo);
            boolean added = APIUtil.getDeviceManagementService().enrollDevice(device);
            return added;
        } catch (DeviceManagementException e) {
            return false;
        }
    }

    public Response removeDevice(String deviceId) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(DigitalDisplayConstants.DEVICE_TYPE);
        try {
            boolean removed = APIUtil.getDeviceManagementService().disenrollDevice(
                    deviceIdentifier);
            if (removed) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_ACCEPTABLE.getStatusCode()).build();
            }
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }

    public Response updateDevice(String deviceId, String name) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(DigitalDisplayConstants.DEVICE_TYPE);
        try {
            Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);
            device.setDeviceIdentifier(deviceId);
            device.getEnrolmentInfo().setDateOfLastUpdate(new Date().getTime());
            device.setName(name);
            device.setType(DigitalDisplayConstants.DEVICE_TYPE);
            boolean updated = APIUtil.getDeviceManagementService().modifyEnrollment(device);
            if (updated) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_ACCEPTABLE.getStatusCode()).build();
            }
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }

    public Response getDevice(String deviceId) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(DigitalDisplayConstants.DEVICE_TYPE);
        try {
            Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);
            return Response.ok().entity(device).build();
        } catch (DeviceManagementException ex) {
            log.error("Error occurred while retrieving device with Id " + deviceId + "\n" + ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }

    @Path("device/download")
    @GET
    @Produces("application/zip")
    public Response downloadSketch(@QueryParam("deviceName") String deviceName,
                                   @QueryParam("sketchType") String sketchType) {
        try {
            ZipArchive zipFile = createDownloadFile(APIUtil.getAuthenticatedUser(), deviceName, sketchType);
            Response.ResponseBuilder response = Response.ok(FileUtils.readFileToByteArray(zipFile.getZipFile()));
            response.status(Response.Status.OK);
            response.type("application/zip");
            response.header("Content-Disposition", "attachment; filename=\"" + zipFile.getFileName() + "\"");
            Response resp = response.build();
            zipFile.getZipFile().delete();
            return resp;
        } catch (IllegalArgumentException ex) {
            return Response.status(400).entity(ex.getMessage()).build();//bad request
        } catch (DeviceManagementException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (JWTClientException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (APIManagerException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (UserStoreException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (DigitalDisplayException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        }
    }

    private ZipArchive createDownloadFile(String owner, String deviceName, String sketchType)
            throws DeviceManagementException, APIManagerException, JWTClientException,
            UserStoreException, DigitalDisplayException {
        //create new device id
        String deviceId = shortUUID();
        boolean status = register(deviceId, deviceName);
        if (!status) {
            String msg = "Error occurred while registering the device with " + "id: " + deviceId + " owner:" + owner;
            throw new DeviceManagementException(msg);
        }
        if (apiApplicationKey == null) {
            String applicationUsername =
                    PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm().getRealmConfiguration()
                            .getAdminUserName();
            APIManagementProviderService apiManagementProviderService = APIUtil.getAPIManagementProviderService();
            String[] tags = {DigitalDisplayConstants.DEVICE_TYPE};
            apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                    DigitalDisplayConstants.DEVICE_TYPE, tags, KEY_TYPE, applicationUsername, true,
                    DigitalDisplayConstants.APIM_APPLICATION_TOKEN_VALIDITY_PERIOD);
        }
        JWTClient jwtClient = APIUtil.getJWTClientManagerService().getJWTClient();
        String scopes = " device_" + deviceId;
        AccessTokenInfo accessTokenInfo = jwtClient.getAccessToken(apiApplicationKey.getConsumerKey(),
                apiApplicationKey.getConsumerSecret(), owner,
                scopes);
        String accessToken = accessTokenInfo.getAccessToken();
        String refreshToken = accessTokenInfo.getRefreshToken();

        //Getting consumer key and secret
        String consumerKey = apiApplicationKey.getConsumerKey();
        String consumerSecret = apiApplicationKey.getConsumerSecret();

        ZipUtil ziputil = new ZipUtil();
        ZipArchive zipFile = ziputil.createZipFile(owner, DigitalDisplayConstants.DEVICE_TYPE, deviceId, deviceName,
                accessToken, refreshToken, consumerKey, consumerSecret);
        return zipFile;
    }

    private static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes(StandardCharsets.UTF_8)).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }


//    private boolean waitForServerStartup() {
//        while (!IoTServerStartupListener.isServerReady()) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                return true;
//            }
//        }
//        return false;
//    }

    public DigitalDisplayMQTTConnector getDigitalDisplayMQTTConnector() {
        return DigitalDisplayServiceImpl.digitalDisplayMQTTConnector;
    }

    public void setDigitalDisplayMQTTConnector(final DigitalDisplayMQTTConnector digitalDisplayMQTTConnector) {

        Runnable connector = new Runnable() {
            public void run() {
                DigitalDisplayServiceImpl.digitalDisplayMQTTConnector = digitalDisplayMQTTConnector;
                if (MqttConfig.getInstance().isEnabled()) {
                    digitalDisplayMQTTConnector.connect();
                } else {
                    log.warn("MQTT disabled in 'devicemgt-config.xml'. " +
                            "Hence, DigitalDisplayMQTTConnector not started.");
                }
            }
        };
        Thread connectorThread = new Thread(connector);
        connectorThread.setDaemon(true);
        connectorThread.start();
    }

    public Response restartBrowser(String deviceId, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.RESTART_BROWSER_CONSTANT + "::", "","restart-browser");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response terminateDisplay(String deviceId, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.TERMINATE_DISPLAY_CONSTANT + "::", "","terminate-display");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    public Response restartDisplay(String deviceId, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.RESTART_DISPLAY_CONSTANT + "::", "","restart-display");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response editSequence(String deviceId, String name, String attribute, String newValue, String sessionId) {
        try {
            String params = name + "|" + attribute + "|" + newValue;
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.EDIT_SEQUENCE_CONSTANT + "::", params,"edit-sequence");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response uploadContent(String deviceId, String remotePath, String screenName, String sessionId) {
        try {
            String params = remotePath + "|" + screenName;
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.UPLOAD_CONTENT_CONSTANT + "::",
                    params,"upload-content");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response addNewResource(String deviceId, String type, String time, String path, String name, String position,
                                   String sessionId) {
        String params;
        try {
            if (position.isEmpty()) {
                params = type + "|" + time + "|" + path + "|" + name;
            } else {
                params = type + "|" + time + "|" + path + "|" + name +
                        "|" + "after=" + position;
            }
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.ADD_NEW_RESOURCE_CONSTANT + "::", params,"add-resource");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response removeResource(String deviceId, String name, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.REMOVE_RESOURCE_CONSTANT + "::", name,"remove-resource");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response restartServer(String deviceId, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.RESTART_SERVER_CONSTANT + "::", "","restart-server");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response showScreenshot(String deviceId, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.SCREENSHOT_CONSTANT + "::", "","screenshot");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response getDevicestatus(String deviceId, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.GET_DEVICE_STATUS_CONSTANT + "::", "","get-device-status");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response getResources(String deviceId, String sessionId) {
        try {
            sendCommandViaMQTT(deviceId, sessionId + "::" + DigitalDisplayConstants.GET_CONTENTLIST_CONSTANT + "::", "","get-content-list");
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (DigitalDisplayException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidDeviceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Send message via MQTT protocol
     *
     * @param deviceId  id of the target digital display
     * @param operation operation need to execute
     * @param param     parameters need to given operation
     * @throws DeviceManagementException
     * @throws DigitalDisplayException
     */
    private void sendCommandViaMQTT(String deviceId, String operation, String param, String code)
            throws DeviceManagementException, DigitalDisplayException, OperationManagementException, InvalidDeviceException {
//        String topic = String.format(DigitalDisplayConstants.PUBLISH_TOPIC, deviceId);
//        String payload = operation + param;
//        try {
//            digitalDisplayMQTTConnector.publishToDigitalDisplay(topic, payload, 2, false);
//        } catch (TransportHandlerException e) {
//            throw new DigitalDisplayException("Error while sending the message",e);
//        } finally {
//            PrivilegedCarbonContext.endTenantFlow();
//        }

        String topic = String.format(DigitalDisplayConstants.PUBLISH_TOPIC, APIUtil.getAuthenticatedUserTenantDomain(),
                DigitalDisplayConstants.DEVICE_TYPE, deviceId);

        String payload = operation + param;

        Operation commandOp = new CommandOperation();
        commandOp.setCode(code);
        commandOp.setType(Operation.Type.COMMAND);
        commandOp.setEnabled(true);
        commandOp.setPayLoad(payload);

        Properties props = new Properties();
        props.setProperty(DigitalDisplayConstants.MQTT_ADAPTER_TOPIC_PROPERTY_NAME, topic);
        commandOp.setProperties(props);

        List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
        deviceIdentifiers.add(new DeviceIdentifier(deviceId, DigitalDisplayConstants.DEVICE_TYPE));
        APIUtil.getDeviceManagementService().addOperation(DigitalDisplayConstants.DEVICE_TYPE, commandOp, deviceIdentifiers);
    }
}
//
//    }
//}
