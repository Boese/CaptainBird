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
			ResourceManager.properties.add(p);
		}
		if(qName.equalsIgnoreCase("gravity_x")) {
			p.GRAVITY_X = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("gravity_y")) {
			p.GRAVITY_Y = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("vehicle_x")) {
			p.VEHICLE_X = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("vehicle_y")) {
			p.VEHICLE_Y = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("tap_x")) {
			p.TAP_X = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("tap_y")) {
			p.TAP_Y = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("level_w")) {
			p.LEVEL_W = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("level_h")) {
			p.LEVEL_H = Float.parseFloat(content);
		}
		if(qName.equalsIgnoreCase("levelNum")) {
			p.LEVEL_NUM = Integer.parseInt(content);
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
		if(qName.equalsIgnoreCase("spacing")) {
			r.spacing = Integer.parseInt(content);
		}
		if(qName.equalsIgnoreCase("yAxis")) {
			if(content.equalsIgnoreCase("true")) {
				r.yAxis = true;
			}
		}
		super.endElement(uri, localName, qName);
	}
}

