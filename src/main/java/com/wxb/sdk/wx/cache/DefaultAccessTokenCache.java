package com.wxb.sdk.wx.cache;


import com.wxb.sdk.wx.api.AccessToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认存储与内存中
 */
public class DefaultAccessTokenCache implements IAccessTokenCache {

	private Map<String, AccessToken> map = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T get(String appId) {
		return (T) map.get(appId);
	}

	public void set(String appId, AccessToken accessToken) {
		map.put(appId, accessToken);
	}

	public void remove(String appId) {
		map.remove(appId);
	}

}
