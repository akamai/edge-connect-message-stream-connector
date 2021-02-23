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

import com.akamai.ec.utils.MonitoringMessageReader;
import com.akamai.ec.utils.mocks.MonitoringMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MonitoringMessageReaderTest {

    private final static String JSON_FILENAME = "cm-http-output-sample.json";

    private final MonitoringMessageReader reader;

    MonitoringMessageReaderTest() {
        this.reader = new MonitoringMessageReader(JSON_FILENAME);
    }

    @Test
    void shouldMapJsonArrayToMonitoringRecords() {
        final MonitoringMessages monitoringMessages = reader.readMonitoringFeed();

        Assertions.assertNotNull(monitoringMessages);
        Assertions.assertNotNull(monitoringMessages.getMessages());
        Assertions.assertTrue(monitoringMessages.getMessages().size() > 0);
    }

}
