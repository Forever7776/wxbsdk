/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.wxb.sdk.wx.api;

import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.cache.DefaultAccessTokenCache;
import com.wxb.sdk.wx.cache.IAccessTokenCache;
import com.wxb.sdk.wx.cache.RedisAccessTokenCache;
import com.wxb.sdk.wx.kit.ParaMap;

import java.util.Map;

/**
 * 认证并获取 access_token API
 * http://mp.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96access_token
 */
public class AccessTokenApi {

	// "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	private static String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

	public static String ACCESS_TOKEN = "access_token";

	// 利用 appId 与 accessToken 建立关联，支持多账户
	static IAccessTokenCache accessTokenCache = new RedisAccessTokenCache();

	/**
	 * 从缓存中获取 access token，如果未取到或者 access token 不可用则先更新再获取
	 */
	public static AccessToken getAccessToken() {
		String appId = ApiConfigKit.getApiConfig().getAppId();
		AccessToken result = accessTokenCache.get(appId);
		if (result != null && result.isAvailable())
			return result;

		refreshAccessToken();
		return accessTokenCache.get(appId);
	}


	/**
	 * 强制更新 access token 值
	 */
	public static synchronized void refreshAccessToken() {
		ApiConfig ac = ApiConfigKit.getApiConfig();
		AccessToken result = null;
		for (int i=0; i<3; i++) {	// 最多三次请求
			String appId = ac.getAppId();
			String appSecret = ac.getAppSecret();
			Map<String, String> queryParas = ParaMap.create("appid", appId).put("secret", appSecret).getData();
			String json = HttpKit.get(url, queryParas);
			result = new AccessToken(json);

			if (result.isAvailable())
				break;
		}

		// 三次请求如果仍然返回了不可用的 access token 仍然 put 进去，便于上层通过 AccessToken 中的属性判断底层的情况
		accessTokenCache.set(ac.getAppId(), result);
	}

	public static String token(){
		AccessToken token = getAccessToken();
		return token.getAccessToken();
	}
}
