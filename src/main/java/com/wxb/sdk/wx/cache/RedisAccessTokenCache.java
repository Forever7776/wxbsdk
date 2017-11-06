package com.wxb.sdk.wx.cache;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.wxb.sdk.wx.api.AccessToken;

public class RedisAccessTokenCache implements IAccessTokenCache {
	
	private final String ACCESS_TOKEN_PREFIX = "access_token:";
	
	private Cache cache = Redis.use();
	
	public <T> T get(String appId) {
		return cache.get(ACCESS_TOKEN_PREFIX + appId);
	}
	
	public void set(String appId, AccessToken accessToken) {
		cache.setex(ACCESS_TOKEN_PREFIX + appId, DEFAULT_TIME_OUT, accessToken);
	}
	
	public void remove(String appId) {
		cache.del(ACCESS_TOKEN_PREFIX + appId);
	}
	
}
