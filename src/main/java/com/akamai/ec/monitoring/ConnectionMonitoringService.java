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

import com.akamai.ec.feeds.DataFeed;
import com.akamai.ec.feeds.SimpleDTO;
import com.akamai.ec.producers.BasicRecordProducer;
import com.akamai.ec.producers.SimpleRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class ConnectionMonitoringService implements MonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionMonitoringService.class);

    private final BasicRecordProducer<SimpleRecord> producer;

    private final DataFeed<SimpleDTO> feed;

    // for logging purposes
    private int run = 0;

    public ConnectionMonitoringService(BasicRecordProducer<SimpleRecord> producer, DataFeed<SimpleDTO> feed) {
        this.producer = producer;
        this.feed = feed;
    }

    @Override
    public void updateRecords() {
        if (feed.hasData()) {
            logger.info("Trying to push some records, run: {}", ++run);
            final Stream<SimpleRecord> stream = feed
                    .pull()
                    .map(SimpleRecord::create);

            producer.pushRecords(stream);
            logger.info("Records pushed on run: {}, at: {}", run, LocalDateTime.now());
        } else {
            logger.debug("No data to upload.");
        }
    }
}
