package com.wxb.sdk.wx.cache;


import com.wxb.sdk.wx.api.AccessToken;

public interface IAccessTokenCache {

	// 默认超时时间7200秒 5秒用于程序执行误差
	final int DEFAULT_TIME_OUT = 7200 - 5;

	<T> T get(String appId);

	void set(String appId, AccessToken accessToken);

	void remove(String appId);

}
