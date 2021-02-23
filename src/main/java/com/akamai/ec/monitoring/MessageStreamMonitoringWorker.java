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

import com.akamai.ec.clients.MessageStreamClient;
import com.akamai.ec.clients.MessageStreamDTO;
import com.akamai.ec.feeds.DataFeed;
import com.akamai.ec.feeds.SimpleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Simple thread responsible for feeding data from monitoring topic
 */
public class MessageStreamMonitoringWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MessageStreamMonitoringWorker.class);

    private final DataFeed<SimpleDTO> feed;
    private final MessageStreamClient client;

    public MessageStreamMonitoringWorker(DataFeed<SimpleDTO> feed) {
        this.feed = feed;
        this.client = new MessageStreamClient(Configuration.parseConnectionInfo());
    }

    public static void start(DataFeed<SimpleDTO> feed) {
        Executors
                .newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(new MessageStreamMonitoringWorker(feed), 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        final String CONNECTS_TOPIC = "$SYS/monitor/con/connects";
        final String ERRORS_TOPIC = "$SYS/monitor/con/errors";
        final String DROPS_TOPIC = "$SYS/monitor/con/drops";

        try (Stream<MessageStreamDTO> messages = client.pull(CONNECTS_TOPIC)) {
            messages
                    .map(SimpleDTO::new)
                    .forEach(feed::update);
        } catch (Exception e) {
            logger.error("Thread {} was interrupted, stopping", Thread.currentThread().getId());
            Thread.currentThread().interrupt();
        }
    }
}
