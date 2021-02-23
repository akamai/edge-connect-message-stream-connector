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

import com.akamai.ec.feeds.DataFeed;
import com.akamai.ec.utils.mocks.MonitoringMessages;
import com.akamai.ec.utils.mocks.MonitoringRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public class TestFeedAsync implements DataFeed {

    private static final Logger logger = LoggerFactory.getLogger(TestFeedAsync.class);

    private final BlockingQueue<MonitoringMessages> queue;

    public TestFeedAsync(BlockingQueue<MonitoringMessages> queue) {
        this.queue = queue;
    }

    @Override
    public Stream<MonitoringRecord> pull() {
        logger.info("Pulling from queue, size: {}, empty: {}", queue.size(), queue.isEmpty());
        final MonitoringMessages data = queue.poll();
        return Objects.requireNonNull(data).getMessages().stream();
    }

    @Override
    public void update(Object data) {
        queue.add((MonitoringMessages) data);
    }

    @Override
    public boolean hasData() {
        return !queue.isEmpty();
    }
}
