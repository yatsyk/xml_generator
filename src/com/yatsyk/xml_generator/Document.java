package com.yatsyk.xml_generator;

import java.util.*;

public class Document {

    private Tag root = null;

    public class Element {

    };

    public interface Generator {

        public String generate(Document doc);
    };

    public class Text extends Element {

        private String text;

        Text(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    };

    public class Tag extends Element {

        private String name;
        private Element[] children = new Element[]{};
        private Attribute[] attribs = new Attribute[]{};

        Tag(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Element[] getChildren() {
            return children;
        }

        public Attribute[] getAttributes() {
            return attribs;
        }
    };

    public class Attribute {

        private String name, value;

        Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    };

    public Attribute attr(String name, Object o) {
        return new Attribute(name, o.toString());
    }

    public void root(Tag el) {
        this.root = el;
    }

    public Tag getRoot() {
        return root;
    }

    private void processArg(Object o, Vector<Attribute> attrs, Vector<Element> els) {
        if (o instanceof Attribute) {
            attrs.add((Attribute) o);
        } else if (o instanceof Element) {
            els.add((Element) o);
        } else if (o instanceof String) {
            els.add(new Text((String) o));
        } else if (o instanceof Text) {
            els.add((Text) o);
        } else if (o instanceof Map) {
            Map m = (Map) o;
            for (Object k : m.keySet()) {
                Attribute attr = new Attribute(k.toString(), "" + m.get(k));
                attrs.add(attr);
            }
        } else if (o instanceof Object[]) {
            Object[] arr = (Object[]) o;
            for (Object el : arr) {
                processArg(el, attrs, els);
            }
        }
    }

    public Tag tag(String name, Object... other) {
        Vector<Attribute> attrs = new Vector<Attribute>();
        Vector<Element> els = new Vector<Element>();
        for (Object o : other) {
            processArg(o, attrs, els);
        }
        Attribute[] aa = new Attribute[attrs.size()];
        Element[] ea = new Element[els.size()];
        attrs.toArray(aa);
        els.toArray(ea);
        return tag(name, aa, ea);
    }

    /*
    public Tag tag(String name) {return tag(name, new Attribute[]{}, new Element[]{});}
    public Tag tag(String name, String text) {return tag(name, new Attribute[]{}, new Element[]{new Text(text)});}

    public Tag tag(String name, Attribute attr1) {return tag(name, new Attribute[]{attr1}, null);}
    public Tag tag(String name, Attribute attr1, Attribute attr2) {return tag(name, new Attribute[]{attr1, attr2}, null);}
    public Tag tag(String name, Attribute attr1, Attribute attr2, Attribute attr3) {return tag(name, new Attribute[]{attr1, attr2, attr3}, null);}

    public Tag tag(String name, Element c1) {return tag(name, null, new Element[]{c1});}
    public Tag tag(String name, Attribute attr1, Element c1) {return tag(name, new Attribute[]{attr1}, new Element[]{c1});}
    public Tag tag(String name, Attribute attr1, Attribute attr2, Element c1) {return tag(name, new Attribute[]{attr1, attr2}, new Element[]{c1});}
    public Tag tag(String name, Attribute attr1, Attribute attr2, Attribute attr3, Element c1) {return tag(name, new Attribute[]{attr1, attr2, attr3}, new Element[]{c1});}

    public Tag tag(String name, Element c1, Element c2) {return tag(name, null, new Element[]{c1, c2});}
    public Tag tag(String name, Attribute attr1, Element c1, Element c2) {return tag(name, new Attribute[]{attr1}, new Element[]{c1, c2});}
    public Tag tag(String name, Attribute attr1, Attribute attr2, Element c1, Element c2) {return tag(name, new Attribute[]{attr1, attr2}, new Element[]{c1, c2});}
    public Tag tag(String name, Attribute attr1, Attribute attr2, Attribute attr3, Element c1, Element c2) {return tag(name, new Attribute[]{attr1, attr2, attr3}, new Element[]{c1, c2});}

    public Tag tag(String name, Attribute attr1, String text) {return tag(name, new Attribute[]{attr1}, new Element[]{new Text(text)});}
    public Tag tag(String name, Attribute attr1, Attribute attr2, String text) {return tag(name, new Attribute[]{attr1, attr2}, new Element[]{new Text(text)});}
    public Tag tag(String name, Attribute attr1, Attribute attr2, Attribute attr3, String text) {return tag(name, new Attribute[]{attr1, attr2, attr3}, new Element[]{new Text(text)});}

    public Tag tag(String name, Element[] children) {return tag(name, null, children);}
    public Tag tag(String name, Attribute[] attrs) {return tag(name, attrs, null);}
     */
    public Tag tag(String name, Attribute[] attrs, Element[] children) {
        Tag t = new Tag(name);
        t.attribs = attrs;
        t.children = children;
        return t;
    }
    private static Map<String, Generator> generators = new HashMap<String, Generator>();

    public static void registerGenerator(String name, Generator generator) {
        generators.put(name, generator);
    }

    public String generate(String generaror) {
        Generator g = generators.get(generaror);
        if (g == null) {
            throw new RuntimeException("Generator " + generaror + " not found");
        }
        return g.generate(this);
    }

    static {
        registerGenerator(XmlGenerator.NAME, new XmlGenerator());
    }
}
