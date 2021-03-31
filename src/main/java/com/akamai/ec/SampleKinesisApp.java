package com.akamai.ec;

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
import com.akamai.ec.feeds.SimpleDataFeed;
import com.akamai.ec.monitoring.ConnectionMonitoringService;
import com.akamai.ec.monitoring.MessageStreamMonitoringWorker;
import com.akamai.ec.monitoring.MonitoringService;
import com.akamai.ec.producers.BasicRecordProducer;
import com.akamai.ec.producers.SimpleRecord;
import com.akamai.ec.producers.kinesis.KinesisRecordProducer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SampleKinesisApp {

    public static void main(String[] args) {
        final BasicRecordProducer<SimpleRecord> producer = new KinesisRecordProducer();

        // initialize Blob Storage producer
        if (producer.initialize()) {
            final DataFeed<SimpleDTO> feed = new SimpleDataFeed<>(100);

            // initialize Message Stream consumer
            MessageStreamMonitoringWorker.start(feed);

            final MonitoringService monitoringService = new ConnectionMonitoringService(producer, feed);

            Executors
                    .newSingleThreadScheduledExecutor()
                    .scheduleWithFixedDelay(monitoringService::updateRecords, 5, 10, TimeUnit.SECONDS);
        }
    }
}
