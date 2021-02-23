package com.akamai.ec.producers.blob;

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
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.AppendBlobCreateOptions;
import com.azure.storage.blob.options.BlockBlobOutputStreamOptions;
import com.azure.storage.blob.specialized.AppendBlobClient;
import com.azure.storage.blob.specialized.BlobOutputStream;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class BlobStorageProducer implements BasicRecordProducer<SimpleRecord> {

    private static final Logger logger = LoggerFactory.getLogger(BlobStorageProducer.class);
    private static final String BLOB_PROPERTIES_FILE = "blob.properties";
    private static final byte[] NEW_LINE = "\n".getBytes();
    private static final int FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000;

    private final Properties props = initializeProps();

    private BlobContainerClient blobContainerClient;

    @Override
    public boolean initialize() {
        final StorageSharedKeyCredential credentials = credentials(props);

        final BlobServiceClient storageClient = new BlobServiceClientBuilder()
                .endpoint(endpoint(credentials))
                .credential(credentials)
                .buildClient();

        blobContainerClient = storageClient.getBlobContainerClient(containerName(props));
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }

        logger.info("Blob Storage initialized for account: {} and container: {}", blobContainerClient.getAccountName(), blobContainerClient.getBlobContainerName());
        return blobContainerClient.exists();
    }

    @Override
    public void pushRecords(Stream<SimpleRecord> stream) {
        final BlobClient blobClient = initializeBlobClient();

        try {
            try (final BlobOutputStream blobOutputStream = getBlobOutputStream(blobClient)) {
                logger.info("Started uploading data to container: {} in blob: {}", blobClient.getContainerName(), blobClient.getBlobName());
                stream
                        .map(SimpleRecord::getData)
                        .forEach(data -> writeDataToStream(data, blobOutputStream));
                logger.info("Upload complete");
            }
        } catch (IOException e) {
            logger.error("Error while uploading data", e);
        }
    }

    private StorageSharedKeyCredential credentials(Properties props) {
        if (props.containsValue("connectionString")) {
            return StorageSharedKeyCredential.fromConnectionString(Objects.requireNonNull(props.getProperty("connectionString")));
        } else {
            final String accountName = Objects.requireNonNull(props.getProperty("accountName"));
            final String accountKey = Objects.requireNonNull(props.getProperty("accountKey"));
            return new StorageSharedKeyCredential(accountName, accountKey);
        }
    }

    private String containerName(Properties props) {
        return Objects.requireNonNull(props.getProperty("containerName"));
    }

    private String endpoint(StorageSharedKeyCredential credential) {
        return String.format(Locale.ROOT, "https://%s.blob.core.windows.net", credential.getAccountName());
    }

    private boolean shouldAppend() {
        return Boolean.parseBoolean(props.getProperty("append", "false"));
    }

    private String prefix() {
        return props.getProperty("prefix", "connector_");
    }

    private BlobClient initializeBlobClient() {
        return shouldAppend() ? fiveMinuteBufferBlobClient() : currentMillisBlobClient();
    }

    private BlobClient currentMillisBlobClient() {
        long now = System.currentTimeMillis();
        return blobContainerClient.getBlobClient(prefix() + now);
    }

    private BlobClient fiveMinuteBufferBlobClient() {
        long nearestFiveMinuteInterval = (System.currentTimeMillis() / FIVE_MINUTES_IN_MILLIS) * FIVE_MINUTES_IN_MILLIS;
        return blobContainerClient.getBlobClient(prefix() + nearestFiveMinuteInterval);
    }

    private BlobOutputStream getBlobOutputStream(BlobClient blobClient) {
        return shouldAppend() ? appendBlobOutputStream(blobClient) : blockBlobOutputStream(blobClient);
    }

    private BlobOutputStream blockBlobOutputStream(BlobClient blobClient) {
        logger.info("Selected block blob client");
        final BlockBlobClient blockBlobClient = blobClient.getBlockBlobClient();

        final BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions();
        options.setHeaders(defaultHeaders());

        return blockBlobClient.getBlobOutputStream(options);
    }

    private BlobHttpHeaders defaultHeaders() {
        return new BlobHttpHeaders().setContentType("text/plan; charset=utf-8");
    }

    private BlobOutputStream appendBlobOutputStream(BlobClient blobClient) {
        logger.info("Selected append blob client");
        final AppendBlobClient appendBlobClient = blobClient.getAppendBlobClient();
        if (!appendBlobClient.exists()) {
            final AppendBlobCreateOptions options = new AppendBlobCreateOptions();
            options.setHeaders(defaultHeaders());

            appendBlobClient.createWithResponse(options, Duration.ofSeconds(5), Context.NONE);
        }
        return appendBlobClient.getBlobOutputStream();
    }

    private void writeDataToStream(byte[] data, BlobOutputStream blobOutputStream) {
        logger.debug("Writing {} bytes to output stream: {}", data.length + NEW_LINE.length, blobOutputStream);
        blobOutputStream.write(data);
        blobOutputStream.write(NEW_LINE);
    }

    private static Properties initializeProps() {
        final URL resource = BlobStorageProducer.class.getClassLoader().getResource(BLOB_PROPERTIES_FILE);
        try (final InputStream inStream = Objects.requireNonNull(resource).openStream()) {
            final Properties props = new Properties();
            props.load(inStream);
            return props;
        } catch (IOException e) {
            logger.error("Error while reading configuration");
            return new Properties();
        }
    }
}
