package com.akamai.ec.monitoring;

/*-
 * #%L
 * edge-connect-message-stream-connector
 * %%
 * Copyright (C) 2020 - 2021 Akamai Technologies, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.akamai.ec.ConnectionInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Configuration {

    private static final String MQTT_CONNECTION_INFO_FILE = "iec-connection.json";

    public static ConnectionInfo parseConnectionInfo() {
        try {
            final URL resource = Configuration.class.getClassLoader().getResource(MQTT_CONNECTION_INFO_FILE);
            final File file = new File(Objects.requireNonNull(resource).getFile());

            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(file, ConnectionInfo.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read MQTT connection info from file: " + MQTT_CONNECTION_INFO_FILE, e);
        }
    }
}
