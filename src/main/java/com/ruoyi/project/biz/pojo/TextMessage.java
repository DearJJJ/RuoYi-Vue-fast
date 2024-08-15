package com.ruoyi.project.biz.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "xml")
public class TextMessage extends WeChatMessage {

    @JacksonXmlProperty(localName = "Content")
    private String content;


    public TextMessage() {
        setMsgType("text");
    }

    public TextMessage(String toUserName, String fromUserName, String content) {
        this();
        setToUserName(toUserName);
        setFromUserName(fromUserName);
        setCreateTime(System.currentTimeMillis() / 1000);
        this.content = content;
    }

}

