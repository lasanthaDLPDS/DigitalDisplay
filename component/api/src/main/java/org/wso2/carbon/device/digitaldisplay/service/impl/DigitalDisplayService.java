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

import io.swagger.annotations.*;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "digitaldisplay"),
                                @ExtensionProperty(name = "context", value = "/digitaldisplay"),
                        })
                }
        ),
        tags = {
                @Tag(name = "digitaldisplay", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:digitaldisplay:enroll",
                        permissions = {"/device-mgt/devices/enroll/digitaldisplay"}
                )
        }
)
public interface DigitalDisplayService {
    String SCOPE = "scope";


    @Path("device/{deviceId}")
    @PUT
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response updateDevice(@PathParam("deviceId") String deviceId, @QueryParam("name") String name);

    @Path("device/{deviceId}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response getDevice(@PathParam("deviceId") String deviceId);

    @Path("device/{deviceId}")
    @DELETE
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "DELETE",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response removeDevice(@PathParam("deviceId") String deviceId);

    @Path("device/download")
    @GET
    @Produces("application/zip")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response downloadSketch(@QueryParam("deviceName") String customDeviceName, @QueryParam("sketchType") String sketchType);


    /**
     * Restart the running browser in the given digital display.
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     */
    @Path("device/{deviceId}/restart-browser")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response restartBrowser(@PathParam("deviceId") String deviceId, @QueryParam("sessionId") String sessionId);

    /**
     * Terminate all running processes. If this execute we have to reboot digital display manually.
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     */
    @Path("device/{deviceId}/terminate-display")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response terminateDisplay(@PathParam("deviceId") String deviceId, @QueryParam("sessionId") String sessionId);

    /**
     * Reboot running digital display
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     */
    @Path("device/{deviceId}/restart-display")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response restartDisplay(@PathParam("deviceId") String deviceId, @QueryParam("sessionId") String sessionId);

    /**
     * Search through the sequence and edit requested resource
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     * @param name      name of page need to change
     * @param attribute this can be path,time or type
     * @param newValue  page is used to replace path
     */
    @Path("device/{deviceId}/edit-sequence")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response editSequence(@PathParam("deviceId") String deviceId, @FormParam("name") String name,
                          @FormParam("attribute") String attribute, @FormParam("newValue") String newValue,
                          @QueryParam("sessionId") String sessionId);

    @Path("device/{deviceId}/upload-content")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response uploadContent(@PathParam("deviceId") String deviceId, @FormParam("remotePath") String remotePath,
                           @FormParam("screenName") String screenName, @QueryParam("sessionId") String sessionId);

    /**
     * Add new resource end to the existing sequence
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     * @param type      type of new resource
     * @param time      new resource visible time
     * @param path      URL of the new resource
     */
    @Path("device/{deviceId}/add-resource")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response addNewResource(@PathParam("deviceId") String deviceId, @FormParam("type") String type,
                            @FormParam("time") String time, @FormParam("path") String path,
                            @FormParam("name") String name, @FormParam("position") String position,
                            @QueryParam("sessionId") String sessionId);

    /**
     * Delete a resource in sequence
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     * @param name      name of the page no need to delete
     */
    @Path("device/{deviceId}/remove-resource")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response removeResource(@PathParam("deviceId") String deviceId, @FormParam("name") String name,
                            @QueryParam("sessionId") String sessionId);

    /**
     * Restart HTTP in running display
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     */
    @Path("device/{deviceId}/restart-server")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response restartServer(@PathParam("deviceId") String deviceId, @QueryParam("sessionId") String sessionId);

    /**
     * Get screenshot of running display
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     */
    @Path("device/{deviceId}/screenshot")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response showScreenshot(@PathParam("deviceId") String deviceId, @QueryParam("sessionId") String sessionId);

    /**
     * Get statistics of running display
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     */
    @Path("device/{deviceId}/get-device-status")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response getDevicestatus(@PathParam("deviceId") String deviceId, @QueryParam("sessionId") String sessionId);

    /**
     * Stop specific display
     *
     * @param deviceId  id of the controlling digital display
     * @param sessionId web socket id of the method invoke client
     */
    @Path("device/{deviceId}/get-content-list")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "digitaldisplay",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:digitaldisplay:enroll")
                    })
            }
    )
    Response getResources(@PathParam("deviceId") String deviceId, @QueryParam("sessionId") String sessionId);
}
