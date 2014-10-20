package com.money.captainbird.resources;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler{
	String content = null;
	Resource r = null;
	Properties p = null;
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
							throws SAXException {
		if(qName.equalsIgnoreCase("resource")) {
			r = new Resource();
			r.name = attributes.getValue("name");
		}
		if(qName.equalsIgnoreCase("properties")) {
			p = new Properties();
		}
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
		if(qName.equalsIgnoreCase("properties")) {
			ResourceManager.getInstance();
			ResourceManager.properties = p;
		}
		if(qName.equalsIgnoreCase("gravity_x")) {
			p.GRAVITY_X = content;
		}
		if(qName.equalsIgnoreCase("gravity_y")) {
			p.GRAVITY_Y = content;
		}
		if(qName.equalsIgnoreCase("vehicle_x")) {
			p.VEHICLE_X = content;
		}
		if(qName.equalsIgnoreCase("vehicle_y")) {
			p.VEHICLE_Y = content;
		}
		if(qName.equalsIgnoreCase("tap_x")) {
			p.TAP_X = content;
		}
		if(qName.equalsIgnoreCase("tap_y")) {
			p.TAP_Y = content;
		}
		if(qName.equalsIgnoreCase("level_w")) {
			p.LEVEL_W = content;
		}
		if(qName.equalsIgnoreCase("level_h")) {
			p.LEVEL_H = content;
		}
		if(qName.equalsIgnoreCase("resource")) {
			ResourceManager.getInstance();
			ResourceManager.resourceList.add(r);
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
			r.row = content;
		}
		if(qName.equalsIgnoreCase("col")) {
			r.col = content;
		}
		if(qName.equalsIgnoreCase("speed")) {
			r.speed = content;
		}
		if(qName.equalsIgnoreCase("x")) {
			r.x = content;
		}
		if(qName.equalsIgnoreCase("y")) {
			r.y = content;
		}
		super.endElement(uri, localName, qName);
	}
}

