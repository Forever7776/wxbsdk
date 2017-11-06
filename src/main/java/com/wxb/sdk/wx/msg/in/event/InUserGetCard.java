package com.wxb.sdk.wx.msg.in.event;

/**
 * Created by luomhy on 2015/9/22.
 *
 <xml>
 <ToUserName><![CDATA[gh_4c24c68b7239]]></ToUserName>
 <FromUserName><![CDATA[oCCifuLz_hq6XqKxQRX3oSajSVmc]]></FromUserName>
 <CreateTime>1442885689</CreateTime>
 <MsgType><![CDATA[event]]></MsgType>
 <Event><![CDATA[user_get_card]]></Event>
 <CardId><![CDATA[pCCifuAcIoaALMrzleyuA-X_z-LU]]></CardId>
 <IsGiveByFriend>0</IsGiveByFriend>
 <UserCardCode><![CDATA[467911947959]]></UserCardCode>
 <FriendUserName><![CDATA[]]></FriendUserName>
 <OuterId>0</OuterId>
 <OldUserCardCode><![CDATA[]]></OldUserCardCode>
 </xml>
 */
public class InUserGetCard extends EventInMsg{
    private String cardId;
    private String isGiveByFriend;
    private String friendUserName;
    private Integer outerId;
    private String oldUserCardCode;

    public InUserGetCard(String toUserName, String fromUserName, Integer createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getIsGiveByFriend() {
        return isGiveByFriend;
    }

    public void setIsGiveByFriend(String isGiveByFriend) {
        this.isGiveByFriend = isGiveByFriend;
    }

    public String getFriendUserName() {
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    public Integer getOuterId() {
        return outerId;
    }

    public void setOuterId(Integer outerId) {
        this.outerId = outerId;
    }

    public String getOldUserCardCode() {
        return oldUserCardCode;
    }

    public void setOldUserCardCode(String oldUserCardCode) {
        this.oldUserCardCode = oldUserCardCode;
    }
}
