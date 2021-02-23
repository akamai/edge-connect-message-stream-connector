package com.akamai.ec.feeds;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SimpleDataFeed<E> implements DataFeed<E> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDataFeed.class);

    private final BlockingQueue<E> queue;

    public SimpleDataFeed(int queueSize, boolean keepOrder) {
        this.queue = new ArrayBlockingQueue<>(queueSize, keepOrder);
    }

    public SimpleDataFeed(int queueSize) {
        this(queueSize, true);
    }

    @Override
    public Stream<E> pull() {
        int size = queue.size();
        logger.info("Pulling from queue, size: {}", size);
        final Supplier<Stream<E>> supplier
                = () -> Stream.generate(queue::poll).limit(size);
        return supplier.get(); // get a new instance of the stream
    }

    @Override
    public void update(E data) {
        if (data != null) {
            logger.info("Updating queue with new record: {}", data);
            queue.offer(data);
        }
    }

    @Override
    public boolean hasData() {
        return !queue.isEmpty();
    }
}
