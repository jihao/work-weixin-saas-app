package com.superhao.weixin.qyapi;

import com.superhao.weixin.qyapi.model.WxCallbackXmlMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.jupiter.api.Test;

public class Xml2JavaTest {
    @Test
    public void testParsePostXML() {
        String info = "<xml><SuiteId><![CDATA[wwd276a8d354646867]]></SuiteId><InfoType><![CDATA[suite_ticket]]></InfoType><TimeStamp>1595568610</TimeStamp><SuiteTicket><![CDATA[OND7Fy24-_3VtTUECQFoTblDSTv8Ml_LJ7zrM2aNwRbA2Kv95PeqI0UpwD8ZrJjR]]></SuiteTicket></xml>";
        XStream xstream = new XStream(new DomDriver());
//        xstream.alias("xml", WxCallbackXmlMessage.class);
        xstream.processAnnotations(WxCallbackXmlMessage.class);
        WxCallbackXmlMessage wxMessage = (WxCallbackXmlMessage) xstream.fromXML(info);
        String infoType = wxMessage.getInfoType();
        System.out.println(infoType);
        System.out.println( wxMessage.getSuiteId());
    }
}
