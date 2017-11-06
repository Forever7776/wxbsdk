/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.wxb.sdk.wx.msg;

import com.jfinal.kit.StrKit;
import com.wxb.sdk.wx.msg.in.*;
import com.wxb.sdk.wx.msg.in.event.*;
import com.wxb.sdk.wx.msg.in.speech_recognition.InSpeechRecognitionResults;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InMsgParaser {
	
	private InMsgParaser() {}
	
	/**
	 * 从 xml 中解析出各类消息与事件
	 */
	public static InMsg parse(String xml) {
		try {
			return doParse(xml);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 消息类型
	 * 1：text 文本消息
	 * 2：image 图片消息
	 * 3：voice 语音消息
	 * 4：video 视频消息
	 *	shortvideo 小视频消息
	 * 5：location 地址位置消息
	 * 6：link 链接消息
	 * 7：event 事件
	 */
	private static InMsg doParse(String xml) throws DocumentException {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		String toUserName = root.elementText("ToUserName");
		String fromUserName = root.elementText("FromUserName");
		Integer createTime = Integer.parseInt(root.elementText("CreateTime"));
		String msgType = root.elementText("MsgType");
		if ("text".equals(msgType))
			return parseInTextMsg(root, toUserName, fromUserName, createTime, msgType);
		if ("image".equals(msgType))
			return parseInImageMsg(root, toUserName, fromUserName, createTime, msgType);
		if ("voice".equals(msgType))
			return parseInVoiceMsgAndInSpeechRecognitionResults(root, toUserName, fromUserName, createTime, msgType);
		if ("video".equals(msgType))
			return parseInVideoMsg(root, toUserName, fromUserName, createTime, msgType);
		if ("shortvideo".equals(msgType))	 //支持小视频
			return parseInShortVideoMsg(root, toUserName, fromUserName, createTime, msgType);
		if ("location".equals(msgType))
			return parseInLocationMsg(root, toUserName, fromUserName, createTime, msgType);
		if ("link".equals(msgType))
			return parseInLinkMsg(root, toUserName, fromUserName, createTime, msgType);
		if ("event".equals(msgType))
			return parseInEvent(root, toUserName, fromUserName, createTime, msgType);
		throw new RuntimeException("无法识别的消息类型 " + msgType + "，请查阅微信公众平台开发文档");
	}
	
	private static InMsg parseInTextMsg(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		InTextMsg msg = new InTextMsg(toUserName, fromUserName, createTime, msgType);
		msg.setContent(root.elementText("Content"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}
	
	private static InMsg parseInImageMsg(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		InImageMsg msg = new InImageMsg(toUserName, fromUserName, createTime, msgType);
		msg.setPicUrl(root.elementText("PicUrl"));
		msg.setMediaId(root.elementText("MediaId"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}
	
	private static InMsg parseInVoiceMsgAndInSpeechRecognitionResults(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		String recognition = root.elementText("Recognition");
		if (StrKit.isBlank(recognition)) {
			InVoiceMsg msg = new InVoiceMsg(toUserName, fromUserName, createTime, msgType);
			msg.setMediaId(root.elementText("MediaId"));
			msg.setFormat(root.elementText("Format"));
			msg.setMsgId(root.elementText("MsgId"));
			return msg;
		}
		else {
			InSpeechRecognitionResults msg = new InSpeechRecognitionResults(toUserName, fromUserName, createTime, msgType);
			msg.setMediaId(root.elementText("MediaId"));
			msg.setFormat(root.elementText("Format"));
			msg.setMsgId(root.elementText("MsgId"));
			msg.setRecognition(recognition);			// 与 InVoiceMsg 唯一的不同之处
			return msg;
		}
	}
	
	private static InMsg parseInVideoMsg(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		InVideoMsg msg = new InVideoMsg(toUserName, fromUserName, createTime, msgType);
		msg.setMediaId(root.elementText("MediaId"));
		msg.setThumbMediaId(root.elementText("ThumbMediaId"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}

	private static InMsg parseInShortVideoMsg(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		InShortVideoMsg msg = new InShortVideoMsg(toUserName, fromUserName, createTime, msgType);
		msg.setMediaId(root.elementText("MediaId"));
		msg.setThumbMediaId(root.elementText("ThumbMediaId"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}
	
	private static InMsg parseInLocationMsg(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		InLocationMsg msg = new InLocationMsg(toUserName, fromUserName, createTime, msgType);
		msg.setLocation_X(root.elementText("Location_X"));
		msg.setLocation_Y(root.elementText("Location_Y"));
		msg.setScale(root.elementText("Scale"));
		msg.setLabel(root.elementText("Label"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}
	
	private static InMsg parseInLinkMsg(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		InLinkMsg msg = new InLinkMsg(toUserName, fromUserName, createTime, msgType);
		msg.setTitle(root.elementText("Title"));
		msg.setDescription(root.elementText("Description"));
		msg.setUrl(root.elementText("Url"));
		msg.setMsgId(root.elementText("MsgId"));
		return msg;
	}
	
	// 解析各种事件
	private static InMsg parseInEvent(Element root, String toUserName, String fromUserName, Integer createTime, String msgType) {
		String event = root.elementText("Event");
		String eventKey = root.elementText("EventKey");

		// 关注/取消关注事件（包括二维码扫描关注，二维码扫描关注事件与扫描带参数二维码事件是两回事）
		if (("subscribe".equals(event) || "unsubscribe".equals(event)) && StrKit.isBlank(eventKey)) {
			return new InFollowEvent(toUserName, fromUserName, createTime, msgType, event);
		}
		// 扫描带参数二维码事件之一		1: 用户未关注时，进行关注后的事件推送
		String ticket = root.elementText("Ticket");
		if ("subscribe".equals(event) && StrKit.notBlank(eventKey) && eventKey.startsWith("qrscene_")) {
			InQrCodeEvent e = new InQrCodeEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			e.setTicket(ticket);
			return e;
		}

		// 扫描带参数二维码事件之二		2: 用户已关注时的事件推送
		if ("SCAN".equals(event)) {
			InQrCodeEvent e = new InQrCodeEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			e.setTicket(ticket);
			return e;
		}

		// 上报地理位置事件
		if ("LOCATION".equals(event)) {
			InLocationEvent e = new InLocationEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setLatitude(root.elementText("Latitude"));
			e.setLongitude(root.elementText("Longitude"));
			e.setPrecision(root.elementText("Precision"));
			return e;
		}

		// 自定义菜单事件之一			1：点击菜单拉取消息时的事件推送
		if ("CLICK".equals(event)) {
			InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			return e;
		}
		// 自定义菜单事件之二			2：点击菜单跳转链接时的事件推送
		if ("VIEW".equals(event)) {
			InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setEventKey(eventKey);
			return e;
		}

		// 模板消息是否送达成功通知事件
		if ("TEMPLATESENDJOBFINISH".equals(event)) {
			InTemplateMsgEvent e = new InTemplateMsgEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setMsgId(root.elementText("MsgID"));
			e.setStatus(root.elementText("Status"));
			return e;
		}
		// 群发任务结束时是否送达成功通知事件
		if ("MASSSENDJOBFINISH".equals(event)) {
			InMassEvent e = new InMassEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setMsgId(root.elementText("MsgID"));
			e.setStatus(root.elementText("Status"));
			e.setTotalCount(root.elementText("TotalCount"));
			e.setFilterCount(root.elementText("FilterCount"));
			e.setSentCount(root.elementText("SentCount"));
			e.setErrorCount(root.elementText("ErrorCount"));
			return e;
		}
		// 多客服接入会话事件
		if ("kf_create_session".equals(event)) {
			InCustomEvent e = new InCustomEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setKfAccount(root.elementText("KfAccount"));
			return e;
		}
		// 多客服关闭会话事件
		if ("kf_close_session".equals(event)) {
			InCustomEvent e = new InCustomEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setKfAccount(root.elementText("KfAccount"));
			return e;
		}
		// 多客服转接会话事件
		if ("kf_switch_session".equals(event)) {
			InCustomEvent e = new InCustomEvent(toUserName, fromUserName, createTime, msgType, event);
			e.setKfAccount(root.elementText("KfAccount"));
			e.setToKfAccount(root.elementText("ToKfAccount"));
			return e;
		}
		// 微信摇一摇事件
		if ("ShakearoundUserShake".equals(event)){
			InShakearoundUserShakeEvent e = new InShakearoundUserShakeEvent(toUserName, fromUserName, createTime, msgType,event);
			e.setEvent(event);
			Element c = root.element("ChosenBeacon");
			e.setUuid(c.elementText("Uuid"));
			e.setMajor(Integer.parseInt(c.elementText("Major")));
			e.setMinor(Integer.parseInt(c.elementText("Minor")));
			e.setDistance(Float.parseFloat(c.elementText("Distance")));

			List list = root.elements("AroundBeacon");
			if (list != null && list.size() > 0) {
				InShakearoundUserShakeEvent.AroundBeacon aroundBeacon = null;
				List<InShakearoundUserShakeEvent.AroundBeacon> aroundBeacons = new ArrayList<>();
				for (Iterator it = list.iterator(); it.hasNext();) {
					Element elm = (Element) it.next();
					aroundBeacon = new InShakearoundUserShakeEvent.AroundBeacon();
					aroundBeacon.setUuid(elm.elementText("Uuid"));
					aroundBeacon.setMajor(Integer.parseInt(elm.elementText("Major")));
					aroundBeacon.setMinor(Integer.parseInt(elm.elementText("Minor")));
					aroundBeacon.setDistance(Float.parseFloat(elm.elementText("Distance")));
					aroundBeacons.add(aroundBeacon);
				}
				e.setAroundBeaconList(aroundBeacons);
			}
			return e;
		}
		//门店审核 事件
		if("poi_check_notify".equals(event)){
			InPoiCheckEvent e = new InPoiCheckEvent(toUserName,fromUserName,createTime,msgType,event);
			e.setUniqId(root.elementText("UniqId"));
			e.setPoiId(root.elementText("PoiId"));
			e.setResult(root.elementText("Result"));
			e.setMsg(root.elementText("Msg"));
			return e;
		}

		// 领取优惠券事件
		if("user_get_card".equals(event)){
			InUserGetCard e = new InUserGetCard(toUserName,fromUserName,createTime,msgType,event);
			e.setCardId(root.elementText("CardId")); //CAR ID
			e.setFriendUserName(root.elementText("FriendUserName"));
			e.setIsGiveByFriend(root.elementText("isGiveByFriend"));
			e.setOldUserCardCode(root.elementText("OldUserCardCode"));
			e.setOuterId(NumberUtils.toInt(root.elementText("CardId"),0));
			return e;
		}
		
		throw new RuntimeException("无法识别的事件类型，请查阅微信公众平台开发文档");
	}
}


