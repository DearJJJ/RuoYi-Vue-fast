package com.ruoyi.project.biz.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "xml")
public class ImageMessage extends WeChatMessage {

    @JacksonXmlProperty(localName = "Image")
    private Image image;

    public ImageMessage() {
        setMsgType("image");
    }

    public ImageMessage(String toUserName, String fromUserName, String mediaId) {
        this();
        setToUserName(toUserName);
        setFromUserName(fromUserName);
        setCreateTime(System.currentTimeMillis() / 1000);
        this.image = new Image(mediaId);
    }

    @Data
    public static class Image {
        @JacksonXmlProperty(localName = "MediaId")
        private String mediaId;

        public Image(String mediaId) {
            this.mediaId = mediaId;
        }

    }
}
