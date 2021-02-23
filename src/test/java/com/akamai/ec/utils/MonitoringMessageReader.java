package com.akamai.ec.utils;

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

import com.akamai.ec.utils.mocks.MonitoringMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MonitoringMessageReader {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringMessageReader.class);

    private final ObjectMapper objectMapper;

    private final String jsonFilePath;

    public MonitoringMessageReader(String jsonFilePath) {
        this.objectMapper = new ObjectMapper();
        this.jsonFilePath = jsonFilePath;
    }

    public MonitoringMessages readMonitoringFeed() {
        try {
            return getMonitoringMessage();
        } catch (NullPointerException | IOException e) {
            logger.debug("There was a problem reading feeds data from JSON file, path: {}", jsonFilePath);
            throw new RuntimeException(e);
        }
    }

    private MonitoringMessages getMonitoringMessage() throws NullPointerException, IOException {
        final URL resource = getClass().getClassLoader().getResource(jsonFilePath);
        final File file = new File(Objects.requireNonNull(resource).getFile());
        return objectMapper.readValue(file, MonitoringMessages.class);
    }
}
