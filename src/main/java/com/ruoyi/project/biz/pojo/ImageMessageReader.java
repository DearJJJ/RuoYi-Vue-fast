package com.ruoyi.project.biz.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "xml")
public class ImageMessageReader extends WeChatMessage {

    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;
}
