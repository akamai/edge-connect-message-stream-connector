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

/**
 * Utils to parse monitoring payload from input object
 * <p>
 * Sample payloads:
 * 1a;89.221.219.231;1576152922;subClientId;00
 * 1a;24.162.37.163;1576153423;PubClientId;00;
 */
public final class MonitoringPayloadParser {

    private static final int FIELDS_TO_SPLIT = 6;
    private static final int CLIENT_ID_IDX = 3;

    public static String parseClientId(byte[] payload) {
        return getValue(new String(payload), CLIENT_ID_IDX);
    }

    private static String getValue(String payload, int idx) {
        String[] split = payload.split(";", FIELDS_TO_SPLIT);
        return split.length > idx ? split[idx] : "";
    }

}
