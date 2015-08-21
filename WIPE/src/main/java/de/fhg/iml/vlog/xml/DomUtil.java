/* 
* This file is part of DISMOD Core. 
* © Copyright 2003-2011 Fraunhofer Institut f. Materialfluss und Logistik.
* http://www.iml.fraunhofer.de, http://www.verkehrslogistik.de
*
* DISMOD Core is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* DISMOD Core is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with DISMOD Core.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.fhg.iml.vlog.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.SAXException;

public class DomUtil {
	public static Map<String, String> attributeMap(Element element) {
		NamedNodeMap n = element.getAttributes();

		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < n.getLength(); i++) {
			Attr a = (Attr) n.item(i);
			map.put(a.getName(), a.getValue());
		}

		return map;
	}

	public static List<Element> elements(Node node) {
		ArrayList<Element> a = new ArrayList<Element>();

		NodeList l = node.getChildNodes();
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				a.add((Element) n);
			}
		}

		return a;
	}
	
	public static Document parse(File file) 
	throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		FileInputStream in = new FileInputStream(file);
		Document out = parse(in);
		in.close();
		return out;
	}
	
	public static Document parse(URL url) 
	throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		InputStream in = url.openStream();
		Document out = parse(in);
		in.close();
		return out;
	}

	public static Document parse(InputStream in) 
	throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(in);
		return document;
	}

	public static Document newDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.newDocument();
	}
	
	private static void save(Node doc, OutputStream stream, String encoding, boolean indent)
	throws IOException {
		save(doc, stream, encoding, indent, null, null);
	}
	
	private static void save(
			Node doc, OutputStream stream, String encoding,  
			boolean indent, String publicDocType, String systemDocType)
	throws IOException {
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			if (publicDocType != null) 
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, publicDocType);
			if (systemDocType != null) 
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemDocType);
			
			StreamResult result = new StreamResult(stream);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new IOException("Failure while writing dom result.", e);
		} 
	}

	@SuppressWarnings("resource")
	public static void save(Node doc, File file) throws IOException {
		save(doc, new FileOutputStream(file), "ISO-8859-1", true);
	}

	public static void save(Node doc, OutputStream stream) throws IOException {
		save(doc, stream, "ISO-8859-1", true);
	}
	
	public static void save(Node doc, OutputStream stream, String encoding) throws IOException {
		save(doc, stream, encoding, true);
	}
	
	public static void save(
			Node doc, OutputStream stream, String encoding, 
			String publicDocType, String systemDocType) throws IOException {
		save(doc, stream, encoding, true, publicDocType, systemDocType);
	}

	public static DOMImplementation newDOMImplementation() {
		DOMImplementation factory = null;
		try {
			factory = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
		} catch (Exception e) {
			throw new RuntimeException("Failure while obtaining dom implementation", e);
		}
		return factory;
	}

}
