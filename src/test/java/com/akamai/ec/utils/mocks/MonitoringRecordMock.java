package com.akamai.ec.utils.mocks;

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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MonitoringRecordMock {

    public static MonitoringRecord getMock(String clientId, Long topicSequenceNumber, Long timestamp) {
        return new MonitoringRecord(clientId, getBase64Payload(timestamp), topicSequenceNumber);
    }

    public static Stream<MonitoringRecord> getClientRecordsMock(String clientId, int size) {
        final List<MonitoringRecord> list = new ArrayList<>();
        long seq = 0;
        while (size-- > 0) {
            list.add(getMock(clientId, seq++, Instant.now().toEpochMilli()));
        }
        return list.stream();
    }

    private static String getBase64Payload(Long timestamp) {
        // TODO: change timestamp, encode
        return "MWE7RkU7MTU3MTIyOTA1NjtQdWJsaXNoZXJfdEM1NnkydXQ7Rjc7ZW5kMmVuZC90b3BpYw==";
    }
}
