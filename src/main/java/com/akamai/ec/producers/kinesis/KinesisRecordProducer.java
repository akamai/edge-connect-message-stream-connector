package com.akamai.ec.producers.kinesis;

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

import com.akamai.ec.producers.SimpleRecord;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KinesisRecordProducer extends AbstractKinesisRecordProducer {

    private static final Logger logger = LoggerFactory.getLogger(KinesisRecordProducer.class);

    private final String streamName;

    public KinesisRecordProducer(String streamName) {
        this.streamName = streamName;
    }

    @Override
    public void pushRecords(Stream<SimpleRecord> stream) {
        final AtomicInteger count = new AtomicInteger(0);
        List<SimpleRecord> recordList = stream.collect(Collectors.toList());
        long total_records = recordList.size();
        recordList.forEach(kr -> {
            logger.info("Pushing {} / {} records for client id: {}", count.incrementAndGet(), total_records, kr.getClientId());
            doPush(kr);
        });
    }

    /**
     * Based on AWS docs: https://docs.aws.amazon.com/en_pv/streams/latest/dev/kinesis-kpl-writing.html
     *
     * @param record - payload to be pushed to a specific stream partition
     */
    private void doPush(SimpleRecord record) {
        final ByteBuffer data = ByteBuffer.wrap(record.getData());
        final ListenableFuture<UserRecordResult> future = getProducer().addUserRecord(this.streamName, record.getClientId(), data);
        // If the Future is complete by the time we call addCallback, the callback will be invoked immediately.
        Futures.addCallback(future, getCallback());
    }

}
