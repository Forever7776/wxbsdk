/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.wxb.sdk.wx.api;

import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.ParaMap;

/**
 * 模板消息 API
 */
public class TemplateMsgApi {
	
	private static String sendApiUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	
	/**
	 * 发送模板消息
	 */
	public static ApiResult send(String jsonStr) {
		String jsonResult = HttpKit.post(sendApiUrl , ParaMap.createToken().getData(), jsonStr);
		return new ApiResult(jsonResult);
	}
}


