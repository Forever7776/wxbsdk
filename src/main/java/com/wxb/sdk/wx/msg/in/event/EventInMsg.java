/**
 * Copyright (c) 2011-2015, lmhy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.wxb.sdk.wx.msg.in.event;

import com.wxb.sdk.wx.msg.in.InMsg;

public abstract class EventInMsg extends InMsg
{
    protected String event;

    public EventInMsg(String toUserName, String fromUserName, Integer createTime, String msgType, String event)
    {
        super(toUserName, fromUserName, createTime, msgType);
        this.event = event;
    }

    public String getEvent()
    {
        return event;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }
}
