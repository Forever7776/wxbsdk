/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.wxb.sdk.wx.api;

import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.ParaMap;

/**
 * menu api
 */
public class MenuApi {
	
	private final static String getMenu = "https://api.weixin.qq.com/cgi-bin/menu/get";
	private final static String createMenu = "https://api.weixin.qq.com/cgi-bin/menu/create";
	
	/**
	 * 查询菜单
	 */
	public static ApiResult getMenu() {
		return new ApiResult(HttpKit.get(getMenu, ParaMap.createToken().getData()));
	}
	
	/**
	 * 创建菜单
	 */
	public static ApiResult createMenu(String jsonStr) {
		return new ApiResult(HttpKit.post(createMenu, ParaMap.createToken().getData(), jsonStr));
	}
}


