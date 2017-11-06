/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.wxb.sdk.wx.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.ParaMap;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 用户管理 API
 * https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
 */
public class UserApi {
	
	private static String getUserInfo = "https://api.weixin.qq.com/cgi-bin/user/info";
	private static String getFollowers = "https://api.weixin.qq.com/cgi-bin/user/get";
    private static String updateremark = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark";

	private static String group_create = "https://api.weixin.qq.com/cgi-bin/groups/create";
	private static String group_get = "https://api.weixin.qq.com/cgi-bin/groups/get";
	private static String group_get_id = "https://api.weixin.qq.com/cgi-bin/groups/getid";
	private static String group_update_name = "https://api.weixin.qq.com/cgi-bin/groups/update";
	private static String group_move = "https://api.weixin.qq.com/cgi-bin/groups/members/update";
	private static String group_move_more = "https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate";
	private static String group_delete = "https://api.weixin.qq.com/cgi-bin/groups/delete";
	//所有 参数为 ?access_token=ACCESS_TOKEN

	public static ApiResult getUserInfo(String openId) {
		ParaMap pm = ParaMap.createToken().put("openid", openId).put("lang", "zh_CN");
		return new ApiResult(HttpKit.get(getUserInfo, pm.getData()));
	}
	
	public static ApiResult getFollowers(String nextOpenid) {
		ParaMap pm = ParaMap.createToken();
		if (StringUtils.isNotBlank(nextOpenid))
			pm.put("next_openid", nextOpenid);
        return new ApiResult(HttpKit.get(getFollowers, pm.getData()));
	}

    /**
     * 更新备注名
     * @param openId
     * @param remark
     * @return
     */
    public static ApiResult updateremark(String openId,String remark){
        ParaMap pm = ParaMap.createToken();
        JSONObject jo = new JSONObject();
        jo.put("openid",openId);
        jo.put("remark", remark);
        String rs = HttpKit.post(updateremark, pm.getData(),jo.toJSONString());
        return new ApiResult(rs);
    }
	
	public static ApiResult getFollows(String accountId) {
		return getFollowers(null);
	}

	/**
	 * 一个公众账号，最多支持创建100个分组。
	 * 分组API-->http://mp.weixin.qq.com/wiki/0/56d992c605a97245eb7e617854b169fc.html
	 * @return
	 */
	public static ApiResult groupCreate(String groupName){
		ParaMap pm = ParaMap.createToken();
		JSONObject jo = new JSONObject();
		JSONObject jo1 = new JSONObject();
		jo1.put("name",groupName);
		jo.put("group", jo1);
		return new ApiResult(HttpKit.post(group_create, pm.getData(), jo.toJSONString()));
	}

	/**
	 * 查询所有分组
	 * @param aid
	 * @return
	 */
	public static ApiResult groupGet(String aid){
		ParaMap pm = ParaMap.createToken();
		return new ApiResult(HttpKit.get(group_get, pm.getData()));
	}

	/**
	 * 查询某OPENID 所在的分组
	 * @param openid
	 * @return
	 */
	public static ApiResult groupGetById(String openid){
		ParaMap pm = ParaMap.createToken();
		JSONObject jo = new JSONObject();
		jo.put("openid",openid);
		return new ApiResult(HttpKit.post(group_get_id, pm.getData(), jo.toJSONString()));
	}

	/**
	 * 修改分组名称
	 * @param gid
	 * @param name
	 * @return
	 */
	public static ApiResult groupUpdateName(String gid,String name){
		ParaMap pm = ParaMap.createToken();
		JSONObject jo = new JSONObject();
		JSONObject jo1 = new JSONObject();
		jo1.put("id",gid);
		jo1.put("name",name);
		jo.put("group", jo1);
		return new ApiResult(HttpKit.post(group_update_name, pm.getData(), jo.toJSONString()));
	}

	/**
	 * 移动用户至某分组
	 * @param openid
	 * @param toGid
	 * @return
	 */
	public static ApiResult groupMove(String openid,String toGid){
		ParaMap pm = ParaMap.createToken();
		JSONObject jo = new JSONObject();
		jo.put("openid", openid);
		jo.put("to_groupid", toGid);
		return new ApiResult(HttpKit.post(group_move, pm.getData(), jo.toJSONString()));
	}

	/**
	 * 批量移动用户至某分组
	 * @param toGid
	 * @param openidList
	 * @return
	 */
	public static ApiResult groupMoveMore(String toGid,List<String> openidList){
		ParaMap pm = ParaMap.createToken();
		JSONObject jo = new JSONObject();
		StringBuffer sb =  new StringBuffer("[");
		boolean isFirst = true;
		for(String str:openidList){
			if(isFirst){
				isFirst = false;
			}else{
				sb.append(",");
			}
			sb.append("\"").append(str).append("\"");
		}
		sb.append("]");
		jo.put("openid_list", sb.toString());
		jo.put("to_groupid",toGid);
		return new ApiResult(HttpKit.post(group_move_more, pm.getData(), jo.toJSONString()));
	}

	/**
	 * 批量移动用户至某分组
	 * @param toGid
	 * @param openidList
	 * @return
	 */
	public static ApiResult groupMoveMore(String toGid,String [] openidList){
		ParaMap pm = ParaMap.createToken();
		JSONObject jo = new JSONObject();
		jo.put("openid_list", openidList);
		jo.put("to_groupid",toGid);
		System.out.println(jo.toJSONString());
		return new ApiResult(HttpKit.post(group_move_more, pm.getData(), jo.toJSONString()));
	}

	/**
	 * 删除分组
	 * @param aid
	 * @param gid
	 * @return
	 */
	public static ApiResult groupDelete(String gid){
		ParaMap pm = ParaMap.createToken();
		JSONObject jo = new JSONObject();
		JSONObject jo1 = new JSONObject();
		jo1.put("id",gid);
		jo.put("group", jo1.toJSONString());
        String str = "{\"group\":{\"id\":%s}}";
		return new ApiResult(HttpKit.post(group_delete, pm.getData(),String.format(str,gid)));
	}
}
