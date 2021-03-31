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

import com.akamai.ec.producers.BasicRecordProducer;
import com.akamai.ec.producers.SimpleRecord;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

public abstract class AbstractKinesisRecordProducer implements BasicRecordProducer<SimpleRecord> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractKinesisRecordProducer.class);

    private static final String KINESIS_PROPERTIES_FILE = "kinesis.properties";

    private KinesisProducerConfiguration config;
    private KinesisProducer producer;

    private FutureCallback<UserRecordResult> callback;

    public AbstractKinesisRecordProducer() {
        this.callback = new KinesisProducerCallback();
    }

    @Override
    public boolean initialize() {
        logger.info("Initializing AWS connection for Kinesis Producer");
        final URL resource = getKinesisPropertiesFileUrl();
        config = KinesisProducerConfiguration.fromPropertiesFile(Objects.requireNonNull(resource).getPath());
        logger.info("AWS connection successful");
        return true;
    }

    public KinesisProducer getProducer() {
        if (producer == null) {
            producer = new KinesisProducer(config);
        }
        return producer;
    }

    public FutureCallback<UserRecordResult> getCallback() {
        return callback;
    }

    protected Properties readKinesisProperties() {
        final URL input = getKinesisPropertiesFileUrl();
        final Properties properties = new Properties();
        try {
            properties.load(input.openStream());
        } catch (IOException e) {
            logger.error("Error reading Kinesis properties file, path: " + input.getPath());
        }
        return properties;
    }

    private URL getKinesisPropertiesFileUrl() {
        return getClass().getClassLoader().getResource(KINESIS_PROPERTIES_FILE);
    }

    protected void setCallback(FutureCallback<UserRecordResult> callback) {
        this.callback = callback;
    }
}
