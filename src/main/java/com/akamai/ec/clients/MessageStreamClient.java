package com.akamai.ec.clients;

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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MessageStreamClient {

    private static final Logger logger = LoggerFactory.getLogger(MessageStreamClient.class);

    private final HttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Long> lastTopicSequenceNumbers = new HashMap<>();

    private final ConnectionInfo connectionInfo;

    public MessageStreamClient(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        logger.info("Message StreamClient initialized for URL: {}", connectionInfo.getHost());
    }

    public Stream<MessageStreamDTO> pull(String topic) {
        try {
            final long lastTopicSequenceNumber = calculateNextTopicSequenceNo(topic);
            final String encodedTopic = encodeTopic(topic);
            logger.info("Retrieving messages from topic {} with last message: {}", topic, lastTopicSequenceNumber);

            final HttpGet get = new HttpGet("https://" + connectionInfo.getHost() + "/api/v1/buffer/topics/" + encodedTopic + "?offset=" + lastTopicSequenceNumber);
            get.setHeader("X-Akamai-DCP-Token", connectionInfo.getPassword());

            final HttpResponse httpResponse = httpClient.execute(get);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new IllegalStateException("Cannot connect to Message Stream service, status code: " + statusCode);
            }

            final HttpEntity entity = httpResponse.getEntity();
            final String response = EntityUtils.toString(entity);
            logger.debug("Response from Message Stream: {}", response);

            final Payload payload = objectMapper.readValue(response, Payload.class);

            if (payload.noOfMessages() > 0) {
                lastTopicSequenceNumbers.put(topic, payload.highestSequenceMessageId());
                logger.info("Last retrieved sequence number for topic {} was {}", topic, lastTopicSequenceNumbers.get(topic));
            }

            return payload.getMessages();
        } catch (Exception e) {
            logger.error("Error occurred while extracting messages from the Message Stream", e);
            return Stream.empty();
        }
    }

    private String encodeTopic(String topic) throws UnsupportedEncodingException {
        return URLEncoder.encode(topic, "UTF-8");
    }

    private long calculateNextTopicSequenceNo(String topic) {
        return lastTopicSequenceNumbers.getOrDefault(topic, -1L) + 1;
    }

    private static class Payload {
        private MessageStreamDTO[] messages;

        public Stream<MessageStreamDTO> getMessages() {
            return Stream.of(messages);
        }

        public int noOfMessages() {
            return messages.length;
        }

        public long highestSequenceMessageId() {
            return Stream.of(messages)
                    .mapToLong(MessageStreamDTO::getTopicSequenceNumber)
                    .max()
                    .orElse(0);
        }
    }
}
