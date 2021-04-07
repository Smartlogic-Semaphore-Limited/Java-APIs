package com.smartlogic.ses.client;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "INDEX_METADATA")
@XmlAccessorType(XmlAccessType.FIELD)

public class IndexMetadata implements Serializable {

  private static final long serialVersionUID = -4735193980857758831L;

  Map<String, String> metas;

  protected final static DateTimeFormatter isoOffsetDateTimeFormatter =
      DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault());

  public IndexMetadata() {
  }

  public IndexMetadata(Element element) {
    NodeList nodeList = element.getChildNodes();
    metas = new HashMap<>();
    for (int n = 0; n < nodeList.getLength(); n++) {
      Node node = nodeList.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes()) {
        Element childElement = (Element) node;
        metas.put(childElement.getNodeName(), childElement.getFirstChild().getTextContent());
      }
    }
  }

  public IndexMetadata(Map<String, String> metas) {
    this.metas = metas;
  }

  public Map<String, String> getIndexmetadata() {
    return metas;
  }

  public void setIndexmetadata(Map<String, String> metas) {
    this.metas = metas;
  }

  public OffsetDateTime getPublishDate() {
    OffsetDateTime t = null;
    if (metas.containsKey("PUBLISH_DATE")) {
      t = OffsetDateTime.parse(metas.get("PUBLISH_DATE"), isoOffsetDateTimeFormatter);
    }
    return t;
  }

  public void setPublishDate(OffsetDateTime date) {
    metas.put("PUBLISH_DATE", isoOffsetDateTimeFormatter.format(date));
  }

}
