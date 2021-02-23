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

import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KinesisProducerCallback implements FutureCallback<UserRecordResult> {

    private static final Logger logger = LoggerFactory.getLogger(KinesisProducerCallback.class);

    @Override
    public void onFailure(Throwable t) {
        // Analyze and respond to the failure
        logger.warn("There was an error when publishing feeds record to AWS Kinesis");
    }

    @Override
    public void onSuccess(UserRecordResult result) {
        // Respond to the success
        logger.info("Kinesis record successfully added: {}", result);
    }
}
