package org.wso2.carbon.device.digitaldisplay.service.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.wso2.carbon.device.digitaldisplay.service.impl.constants.DigitalDisplayConstants;
import org.wso2.carbon.device.digitaldisplay.service.impl.model.ScreenShotModel;
import org.wso2.carbon.device.digitaldisplay.service.impl.transport.mqtt.MQTTTransportHandler;
import org.wso2.carbon.device.digitaldisplay.service.impl.transport.TransportHandlerException;
import org.wso2.carbon.device.digitaldisplay.service.impl.websocket.DigitalDisplayWebSocketServerEndPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@SuppressWarnings("no JAX-WS annotation")
public class DigitalDisplayMQTTConnector extends MQTTTransportHandler {

    private static Log log = LogFactory.getLog(DigitalDisplayMQTTConnector.class);
    private static final String MQTT_TOPIC_APPENDER = "wso2/iot";
    private static final String subscribeTopic =
            MQTT_TOPIC_APPENDER + "/" + DigitalDisplayConstants.DEVICE_TYPE + "/+/digitaldisplay_publisher";

    private static String iotServerSubscriber = UUID.randomUUID().toString().substring(0, 5);

    private ScheduledFuture<?> dataPushServiceHandler;

    private Map<String, ScreenShotModel> screenshots = new HashMap<>();

    private static final String BROKER_HOST;
    private static final String BROKER_PORT;

    static {
        BROKER_HOST = System.getProperty("mqtt.broker.host");
        BROKER_PORT = System.getProperty("mqtt.broker.port");
    }

    private DigitalDisplayMQTTConnector() {
        super(iotServerSubscriber, DigitalDisplayConstants.DEVICE_TYPE,
              "tcp://" + BROKER_HOST + ":" + BROKER_PORT, subscribeTopic);
    }

    @Override
    public void connect() {
        Runnable connector = new Runnable() {
            public void run() {
                while (!isConnected()) {
                    try {
                        String brokerUsername = MqttConfig.getInstance().getUsername();
                        String brokerPassword = MqttConfig.getInstance().getPassword();
                        connectToQueue(brokerUsername, brokerPassword);
                    } catch (TransportHandlerException e) {
                        log.error("Connection to MQTT Broker at: " + mqttBrokerEndPoint + " failed", e);
                        try {
                            Thread.sleep(timeoutInterval);
                        } catch (InterruptedException ex) {
                            log.error("MQTT-Connector: Thread Sleep Interrupt Exception.", ex);
                        }
                    }

                    try {
                        subscribeToQueue();
                    } catch (TransportHandlerException e) {
                        log.warn("Subscription to MQTT Broker at: " + mqttBrokerEndPoint + " failed", e);
                    }
                }
            }
        };

        Thread connectorThread = new Thread(connector);
        connectorThread.setDaemon(true);
        connectorThread.start();
    }

    @Override
    public void processIncomingMessage(MqttMessage message, String... messageParams) {
        String topic = messageParams[0];
        String[] topicParams = topic.split("/");
        String owner = topicParams[2];
        String deviceId = topicParams[4];
        String[] messageData = message.toString().split("::");

        if (log.isDebugEnabled()) {
            log.debug("Received MQTT message for: [OWNER-" + owner + "] & [DEVICE.ID-" + deviceId + "]");
        }

        String sessionId = messageData[0];
        if (messageData.length == 2) {
            String responseMessage = messageData[1];
            DigitalDisplayWebSocketServerEndPoint.sendMessage(sessionId, new StringBuilder(responseMessage));
        } else if (messageData.length == 3) {
            String response = messageData[2];
            JSONObject schreenShot = new JSONObject(response);
            String picId = schreenShot.getString("pic_id");
            String data = schreenShot.getString("data");
            int pos = schreenShot.getInt("pos");
            int length = schreenShot.getInt("size");
            createScreenShot(sessionId, picId, pos, length, data);
        }
    }

    @Override
    public void processIncomingMessage() {

    }

    @Override
    public void publishDeviceData(String... publishData) {

    }

    @Override
    public void publishDeviceData() {

    }

    private void createScreenShot(String sessionId, String picId, int pos, int length, String data) {

        ScreenShotModel screenShotModel = screenshots.get(picId);

        if (screenShotModel == null) {
            screenShotModel = new ScreenShotModel();
            screenShotModel.setScreenShotData(new String[length + 1]);
            screenShotModel.setLength(0);
            screenshots.put(picId, screenShotModel);
        }
        if (screenShotModel.getLength() <= length) {
            screenShotModel.getScreenShotData()[pos] = data;
            screenShotModel.setLength(screenShotModel.getLength() + 1);
            if (screenShotModel.getLength() == (length + 1)) {
                StringBuilder displayScreenShot = new StringBuilder("Screenshot||");
                for (String screenshot : screenShotModel.getScreenShotData()) {
                    displayScreenShot.append(screenshot);
                }
                screenshots.remove(picId);
                DigitalDisplayWebSocketServerEndPoint.sendMessage(sessionId, displayScreenShot);
            }
        }
    }

    public void publishToDigitalDisplay(String topic, String payLoad, int qos, boolean retained)
            throws TransportHandlerException {
        if (log.isDebugEnabled()) {
            log.debug("Publishing message [" + payLoad + "to topic [" + topic + "].");
        }
        publishToQueue(topic, payLoad, qos, retained);
    }

    @Override
    public void disconnect() {
        Runnable stopConnection = new Runnable() {
            public void run() {
                while (isConnected()) {
                    try {
                        closeConnection();
                    } catch (MqttException e) {
                        if (log.isDebugEnabled()) {
                            log.warn("Unable to 'STOP' MQTT connection at broker at: " + mqttBrokerEndPoint);
                        }

                        try {
                            Thread.sleep(timeoutInterval);
                        } catch (InterruptedException e1) {
                            log.error("MQTT-Terminator: Thread Sleep Interrupt Exception");
                        }
                    }
                }
            }
        };

        Thread terminatorThread = new Thread(stopConnection);
        terminatorThread.setDaemon(true);
        terminatorThread.start();
    }
}
