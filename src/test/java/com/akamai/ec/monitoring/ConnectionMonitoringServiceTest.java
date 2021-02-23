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
import com.akamai.ec.producers.BasicRecordProducer;
import com.akamai.ec.utils.TestFeedAsync;
import com.akamai.ec.utils.TestMonitoringWorker;
import com.akamai.ec.utils.mocks.MonitoringMessages;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.mockito.Mockito.*;

class ConnectionMonitoringServiceTest {

    private static final int MAX_NO_OF_RECORDS = 5;

    @Test
    void shouldAsynchronouslyUpdateRecordsFromQueue() throws ExecutionException, InterruptedException {
        final BasicRecordProducer producer = mock(BasicRecordProducer.class);

        final BlockingQueue<MonitoringMessages> queue = new ArrayBlockingQueue<>(MAX_NO_OF_RECORDS, true);
        final DataFeed feed = new TestFeedAsync(queue);

        final TestMonitoringWorker worker = new TestMonitoringWorker(queue, MAX_NO_OF_RECORDS);
        final Future<Integer> workerFuture = Executors.newSingleThreadExecutor().submit(worker);

        final MonitoringService monitoringService = getMonitoringService(producer, feed);

        while (!workerFuture.isDone()) {
            monitoringService.updateRecords();
        }

        verify(producer, times(workerFuture.get())).pushRecords(any());
    }

    private ConnectionMonitoringService getMonitoringService(BasicRecordProducer producer, DataFeed feed) {
        return new ConnectionMonitoringService(producer, feed);
    }

}
