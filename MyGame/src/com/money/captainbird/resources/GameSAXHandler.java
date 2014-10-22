package com.money.captainbird.resources;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GameSAXHandler extends SAXHandler {

	List<Resource> g = ResourceManager.gameProperties;
	Resource r = null;
	String content = null;
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("resource")) {
			r = new Resource();
			r.name = attributes.getValue("name");
		}
		if(qName.equalsIgnoreCase("gameproperties")) {
			ResourceManager.worlds = Integer.parseInt(attributes.getValue("worlds"));
		}
		super.startElement(uri, localName, qName, attributes);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		content = String.copyValueOf(ch, start, length).trim();
		super.characters(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equalsIgnoreCase("gameproperties")) {
		}
		if(qName.equalsIgnoreCase("resource")) {
			g.add(r);
		}
		if(qName.equalsIgnoreCase("object")) {
			r.object = content;
		}
		if(qName.equalsIgnoreCase("type")) {
			r.type = content;
		}
		if(qName.equalsIgnoreCase("texture")) {
			r.texture = content;
		}
		if(qName.equalsIgnoreCase("image")) {
			r.image = content;
		}
		if(qName.equalsIgnoreCase("row")) {
			r.row = Integer.parseInt(content);
		}
		if(qName.equalsIgnoreCase("col")) {
			r.col = Integer.parseInt(content);
		}
		if(qName.equalsIgnoreCase("speed")) {
			r.speed = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("x")) {
			r.x = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("y")) {
			r.y = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("yAxis")) {
			if(content.equalsIgnoreCase("true")) {
				r.yAxis = true;
			}
		}
		super.endElement(uri, localName, qName);
	}

}
