package com.yatsyk.xml_generator.sample;

import com.yatsyk.xml_generator.Document;

public class Main {
    public static void main(String args[]) {
        Document x = new Document();
        x.root(
            x.tag("html",
                x.tag("head",
                    x.tag("title", "title of my page")
                ),
                x.tag("body", x.attr("bgcolor", "silver"),
                    x.tag("h1", "This is a sample"),
                    x.tag("p",
                        x.tag("ul", new Document.Element[] {
                            x.tag("li", "1"),
                            x.tag("li", "2"),
                            x.tag("li", "3"),
                            x.tag("li", "4"),
                        })
                    )
                )
            )
        );
        System.out.println(x.generate("xml"));
    }
}
