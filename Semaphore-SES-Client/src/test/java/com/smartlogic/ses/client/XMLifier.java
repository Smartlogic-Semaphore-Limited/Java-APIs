package com.smartlogic.ses.client;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLifier<T> {

  @SuppressWarnings("rawtypes")
  private Class cls;

  protected JAXBContext context;

  public XMLifier(Class<T> cls) {
    this.cls = cls;
  }

  protected JAXBContext getJAXBContext(@SuppressWarnings("rawtypes") Class cls) throws Exception {
    return JAXBContext.newInstance(cls);
  }

  protected Marshaller getMarshaller(@SuppressWarnings("rawtypes") Class cls) throws Exception {
    Marshaller m = getJAXBContext(cls).createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

    return m;
  }

  protected Unmarshaller getUnmarshaller(@SuppressWarnings("rawtypes") Class cls) throws Exception {
    return getJAXBContext(cls).createUnmarshaller();
  }

  protected String objectAsXML(T object) throws Exception {
    StringWriter w = new StringWriter();
    getMarshaller(object.getClass()).marshal(object, w);
    return w.toString();
  }

  @SuppressWarnings("unchecked")
  protected T objectFromXML(String xml) throws Exception {
    return (T) getUnmarshaller(cls).unmarshal(new StringReader(xml));
  }

}
