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

public final class MonitoringRecord {

    public MonitoringRecord() {
    }

    public MonitoringRecord(String payload) {
        this.payload = payload;
    }

    public MonitoringRecord(String clientId, String payload, Long topicSequenceNumber) {
        this.clientId = clientId;
        this.payload = payload;
        this.topicSequenceNumber = topicSequenceNumber;
    }

    private String clientId;

    private String payload;

    private Long topicSequenceNumber;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getTopicSequenceNumber() {
        return topicSequenceNumber;
    }

    public void setTopicSequenceNumber(Long topicSequenceNumber) {
        this.topicSequenceNumber = topicSequenceNumber;
    }

}

