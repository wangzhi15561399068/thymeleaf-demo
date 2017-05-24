package com.anyun.cloud.demo.api.node.cache.entity;

/**
 * @auth TwitchGG <twitchgg@yahoo.com>
 * @since 1.0.0 on 22/05/2017
 */
public interface CacheEntiry<T> {
    String getName();

    T getContent();
}