/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.client.sniff;

import org.elasticsearch.client.RestClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Sniffer builder. Helps creating a new {@link Sniffer}.
 */
public final class SnifferBuilder {
    public static final long DEFAULT_SNIFF_INTERVAL = TimeUnit.MINUTES.toMillis(5);
    public static final long DEFAULT_SNIFF_AFTER_FAILURE_DELAY = TimeUnit.MINUTES.toMillis(1);

    private final RestClient restClient;
    private long sniffIntervalMillis = DEFAULT_SNIFF_INTERVAL;
    private long sniffAfterFailureDelayMillis = DEFAULT_SNIFF_AFTER_FAILURE_DELAY;
    private NodesSniffer nodesSniffer;

    /**
     * Creates a new builder instance by providing the {@link RestClient} that will be used to communicate with elasticsearch
     */
    SnifferBuilder(RestClient restClient) {
        Objects.requireNonNull(restClient, "restClient cannot be null");
        this.restClient = restClient;
    }

    /**
     * Sets the interval between consecutive ordinary sniff executions in milliseconds. Will be honoured when
     * sniffOnFailure is disabled or when there are no failures between consecutive sniff executions.
     * @throws IllegalArgumentException if sniffIntervalMillis is not greater than 0
     */
    public SnifferBuilder setSniffIntervalMillis(int sniffIntervalMillis) {
        if (sniffIntervalMillis <= 0) {
            throw new IllegalArgumentException("sniffIntervalMillis must be greater than 0");
        }
        this.sniffIntervalMillis = sniffIntervalMillis;
        return this;
    }

    /**
     * Sets the delay of a sniff execution scheduled after a failure (in milliseconds)
     */
    public SnifferBuilder setSniffAfterFailureDelayMillis(int sniffAfterFailureDelayMillis) {
        if (sniffAfterFailureDelayMillis <= 0) {
            throw new IllegalArgumentException("sniffAfterFailureDelayMillis must be greater than 0");
        }
        this.sniffAfterFailureDelayMillis = sniffAfterFailureDelayMillis;
        return this;
    }

    /**
     * Sets the {@link NodesSniffer} to be used to read hosts. A default instance of {@link ElasticsearchNodesSniffer}
     * is created when not provided. This method can be used to change the configuration of the {@link ElasticsearchNodesSniffer},
     * or to provide a different implementation (e.g. in case hosts need to taken from a different source).
     */
    public SnifferBuilder setNodesSniffer(NodesSniffer nodesSniffer) {
        Objects.requireNonNull(nodesSniffer, "nodesSniffer cannot be null");
        this.nodesSniffer = nodesSniffer;
        return this;
    }

    /**
     * Creates the {@link Sniffer} based on the provided configuration.
     */
    public Sniffer build() {
        if (nodesSniffer == null) {
            this.nodesSniffer = new ElasticsearchNodesSniffer(restClient);
        }
        return new Sniffer(restClient, nodesSniffer, sniffIntervalMillis, sniffAfterFailureDelayMillis);
    }
}
