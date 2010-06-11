package com.yatsyk.xml_generator;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.parsers.*;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlGenerator implements Document.Generator {
    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;

    public static String NAME = "xml";

    private static synchronized void initBuilder() throws ParserConfigurationException {
        if (factory==null) {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        }
    }

    private void fillTag(org.w3c.dom.Element el, Document.Tag tag) {
        if (tag.getAttributes() != null) {
            for (Document.Attribute attr : tag.getAttributes()) {
                el.setAttribute(attr.getName(), attr.getValue());
            }
        }
        if (tag.getChildren() != null) {
            for (Document.Element els : tag.getChildren()) {
                if (els instanceof Document.Tag) {
                    Document.Tag ctag = (Document.Tag) els;
                    Element cel = el.getOwnerDocument().createElement(ctag.getName());
                    fillTag(cel, ctag);
                    el.appendChild(cel);
                } else if (els instanceof Document.Text) {
                    Document.Text txt = (Document.Text) els;
                    Text tn = el.getOwnerDocument().createTextNode(txt.getText());
                    el.appendChild(tn);
                }
            }
        }
    }

    public String generate(Document doc) {
        try {
            initBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            org.w3c.dom.Document xmldoc = impl.createDocument(null, doc.getRoot().getName(), null);
            org.w3c.dom.Element root = xmldoc.getDocumentElement();
            fillTag(root, doc.getRoot());


            OutputFormat format = new OutputFormat(xmldoc);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(xmldoc);

            return out.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
