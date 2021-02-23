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

import java.util.stream.Stream;

public interface DataFeed<E> {

    /**
     * Pull the records from feed
     *
     * @return stream of records
     */
    Stream<E> pull();

    /**
     * Updates the messages
     *
     * @param data - messaging data
     */
    void update(E data);

    /**
     * Is feed ready to be pulled
     *
     * @return true or false
     */
    boolean hasData();

}
