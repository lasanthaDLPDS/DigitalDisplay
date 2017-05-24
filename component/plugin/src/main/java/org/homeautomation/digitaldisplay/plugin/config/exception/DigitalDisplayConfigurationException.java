/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.homeautomation.digitaldisplay.plugin.config.exception;

public class DigitalDisplayConfigurationException extends Exception {

    private static final long serialVersionUID = -3151279431229070297L;

    public DigitalDisplayConfigurationException(int errorCode, String message) {
        super(message);
    }

    public DigitalDisplayConfigurationException(int errorCode, String message, Throwable cause) {
        super(message, cause);
    }

    public DigitalDisplayConfigurationException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
    }

    public DigitalDisplayConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DigitalDisplayConfigurationException(String msg) {
        super(msg);
    }

    public DigitalDisplayConfigurationException() {
        super();
    }

    public DigitalDisplayConfigurationException(Throwable cause) {
        super(cause);
    }

}
