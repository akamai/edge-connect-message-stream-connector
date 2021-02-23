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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class TestMonitoringWorker implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(TestMonitoringWorker.class);

    private final MonitoringMessageReader reader = new MonitoringMessageReader("cm-http-output-sample.json");

    private final BlockingQueue<MonitoringMessages> queue;
    private final int maxNoOfRecords;

    public TestMonitoringWorker(BlockingQueue<MonitoringMessages> queue, int maxNoOfRecords) {
        this.queue = queue;
        this.maxNoOfRecords = maxNoOfRecords;
    }

    private MonitoringMessages produce() {
        return reader.readMonitoringFeed();
    }

    @Override
    public Integer call() throws InterruptedException {
        logger.info("Run in thread: {}", Thread.currentThread().getName());

        int countRuns = 0;
        while (countRuns < maxNoOfRecords) {
            logger.info("Filling queue, remaining capacity: {}", queue.remainingCapacity());
            queue.put(produce());
            countRuns++;
            TimeUnit.SECONDS.sleep(1);
        }

        logger.info("Exiting the thread: {}", Thread.currentThread().getName());
        return countRuns;
    }
}
