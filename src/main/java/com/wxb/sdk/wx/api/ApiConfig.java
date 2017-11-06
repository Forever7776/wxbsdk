/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.wxb.sdk.wx.api;

/**
 * 存放 Weixin 服务号需要用到的各个参数
 */
public class ApiConfig {
	
	private String token = null;
	private String appId = null;
	private String appSecret = null;
	private String encodingAesKey = null;
	private boolean messageEncrypt = false;	// 消息加密与否

	private String payMchId = null;//商户号
	private String payKey = null;//支付密钥
	private String payCred =null;//证书路径

	public ApiConfig(){
	}
	
	public ApiConfig(String token) {
		setToken(token);
	}
	
	public ApiConfig(String token, String appId, String appSecret) {
		setToken(token);
		setAppId(appId);
		setAppSecret(appSecret);
	}
	
	public ApiConfig(String token, String appId, String appSecret, boolean messageEncrypt, String encodingAesKey) {
		setToken(token);
		setAppId(appId);
		setAppSecret(appSecret);
		setEncryptMessage(messageEncrypt);
		setEncodingAesKey(encodingAesKey);
	}
	
	public String getToken() {
		if (token == null)
			throw new IllegalStateException("token 未被赋值");
		return token;
	}
	
	public void setToken(String token) {
		if (token == null)
			throw new IllegalArgumentException("token 值不能为 null");
		this.token = token;
	}
	
	public String getAppId() {
		if (appId == null)
			throw new IllegalStateException("appId 未被赋值");
		return appId;
	}
	
	public void setAppId(String appId) {
		if (appId == null)
			throw new IllegalArgumentException("appId 值不能为 null");
		this.appId = appId;
	}
	
	public String getAppSecret() {
		if (appSecret == null)
			throw new IllegalStateException("appSecret 未被赋值");
		return appSecret;
	}
	
	public void setAppSecret(String appSecret) {
		if (appSecret == null)
			throw new IllegalArgumentException("appSecret 值不能为 null");
		this.appSecret = appSecret;
	}
	
	public String getEncodingAesKey() {
		if (encodingAesKey == null)
			throw new IllegalStateException("encodingAesKey 未被赋值");
		return encodingAesKey;
	}
	
	public void setEncodingAesKey(String encodingAesKey) {
		if (encodingAesKey == null)
			throw new IllegalArgumentException("encodingAesKey 值不能为 null");
		this.encodingAesKey = encodingAesKey;
	}
	
	public boolean isEncryptMessage() {
		return messageEncrypt;
	}
	
	/**
	 *  是否对消息进行加密，对应于微信平台的消息加解密方式：
	 *  1：true进行加密且必须配置 encodingAesKey
	 *  2：false采用明文模式，同时也支持混合模式
	 */
	public void setEncryptMessage(boolean messageEncrypt) {
		this.messageEncrypt = messageEncrypt;
	}

	public String getPayMchId() {
		if (payMchId == null)
			throw new IllegalStateException("payKey 未被赋值");
		return payMchId;
	}

	public void setPayMchId(String payMchId) {
		if(payMchId == null)
			throw new IllegalArgumentException("payMchId(商户号) 值不能为 null");
		this.payMchId = payMchId;
	}

	public String getPayKey() {
		if (payKey == null)
			throw new IllegalStateException("payKey 未被赋值");
		return payKey;
	}

	public void setPayKey(String payKey) {
		if(payKey == null)
			throw new IllegalArgumentException("payKey(商户密钥) 值不能为 null");
		this.payKey = payKey;
	}

	public String getPayCred() {
		if (payCred == null)
			throw new IllegalStateException("payCred 未被赋值");
		return payCred;
	}

	public void setPayCred(String payCred) {
		if(payCred == null)
			throw new IllegalArgumentException("payCred(P12证书) 值不能为 null");
		this.payCred = payCred;
	}
}


