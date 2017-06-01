package org.homeautomation.digitaldisplay.plugin.impl;

import org.apache.commons.logging.LogFactory;
import org.homeautomation.digitaldisplay.plugin.model.ScreenShotModel;
import org.homeautomation.digitaldisplay.plugin.websocket.DigitalDisplayWebSocketServerEndPoint;
import org.json.JSONObject;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterSubscription;
import org.apache.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;


public class DigitalDisplayEventAdapterSubscription implements InputEventAdapterSubscription {

    private static Log log = LogFactory.getLog(DigitalDisplayEventAdapterSubscription.class);
    private Map<String, ScreenShotModel> screenshots = new HashMap<>();


    @Override
    public void onEvent(Object o) {
        String message = (String) o;
        String[] messageData = message.split("::");

        String sessionId = messageData[0];
        if(messageData.length == 2){
            String responseMessage = messageData[1];
            DigitalDisplayWebSocketServerEndPoint.sendMessage(sessionId, new StringBuilder(responseMessage));
        }  else if (messageData.length == 3){
            String response = messageData[2];
            JSONObject screenShot = new JSONObject(response);
            String picId = screenShot.getString("pic_id");
            String data = screenShot.getString("data");
            int pos = screenShot.getInt("pos");
            int length = screenShot.getInt("size");
            createScreenshot(sessionId, picId, pos, length, data);
        }
    }

    private void createScreenshot(String sessionId, String picId, int pos, int length, String data){
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
}
