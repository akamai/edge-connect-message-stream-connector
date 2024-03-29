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

import com.akamai.ec.clients.MessageStreamDTO;

public class SimpleDTO {
    private final byte[] payload;

    public SimpleDTO(MessageStreamDTO message) {
        this.payload = message.getPayload();
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "SimpleDTO#" + payload.length + " bytes";
    }
}
