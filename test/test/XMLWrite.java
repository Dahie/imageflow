package test;

import java.io.IOException;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLWrite {
	

	public static void main(String[] args) throws IOException {
		Element root = new Element("r");
		Document doc = new Document(root);
		
				Element inner = new Element("inner");
		root.setAttribute("attribute", "value");
		root.addContent(inner);
		inner.addContent("inner content");
		root.addContent(new Comment("comment text"));
		root.addContent("some inline text");
		root.addContent(new Element("inner2"));

		new XMLOutputter(Format.getPrettyFormat()).output(doc, System.out);

	}
	
	
}
