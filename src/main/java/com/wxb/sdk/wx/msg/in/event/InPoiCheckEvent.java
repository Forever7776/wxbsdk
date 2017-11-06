package com.wxb.sdk.wx.msg.in.event;

/**
 *门店审核结果回调
 * <xml>
 *     <ToUserName><![CDATA[toUser]]></ToUserName>
 *     <FromUserName><![CDATA[fromUser]]></FromUserName>
 *     <CreateTime>1408622107</CreateTime>
 *     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[poi_check_notify]]></Event>
 *     <UniqId><![CDATA[123adb]]></UniqId>
 *     <PoiId><![CDATA[123123]]></PoiId>
 *     <Result><![CDATA[fail]]></Result>
 *     <Msg><![CDATA[xxxxxx]]></Msg>
 * </xml>

 */
public class InPoiCheckEvent extends EventInMsg {
    private String uniqId;//商户内部ID sid
    private String poiId;//微信门店ID，唯一标识
    private String result;//审核结果 成功 succ 或失败 fail
    private String msg;//成功的通知信息，或审核失败的驳回理由

    public InPoiCheckEvent(String toUserName, String fromUserName, Integer createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }


    public String getUniqId() {
        return uniqId;
    }

    public void setUniqId(String uniqId) {
        this.uniqId = uniqId;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
