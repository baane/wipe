package de.baane.wipe.control.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.DataHolder;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;

public class DataIO {
	
	private static final String CHAR_CLASS = "charClass";
	private static final String STATUS = "status";
	private static final String ROOT = "wow_progress";
	private static final String CHAR_ROOT = "characters";
	private static final String CHARACTER2 = "character";
	private static final String INSTANCE_ROOT = "instances";
	private static final String INSTANCE2 = "instance";
	
	private static final String ID = "id";
	private static final String NAME = "name";
	
	public static void saveToXML(File file, DataHolder p) throws IOException {
		Element root = new Element(ROOT);
		root.addContent(getCharacterData(p));
		root.addContent(getInstanceData(p));
		
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());
		@SuppressWarnings("resource")
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		out.output(new Document(root), writer);
	}
	
	private static Element getCharacterData(DataHolder p) {
		Element characters = new Element(CHAR_ROOT);
		for (Character c : p.getCharacters()) {
			Element character = new Element(CHARACTER2);
			character.setAttribute(ID, c.getId() + "");
			character.setAttribute(NAME, c.getName());
			character.setAttribute(CHAR_CLASS, c.getCharClass().name());
			
			Element instances = new Element(INSTANCE_ROOT);
			LinkedHashMap<Instance, RaidStatus> progress = c.getProgresses();
			for (Entry<Instance, RaidStatus> e : progress.entrySet()) {
				Element progressE = new Element(INSTANCE2);
				progressE.setAttribute(ID, e.getKey().getId() + "");
				progressE.setAttribute(STATUS, e.getValue().name() + "");
				instances.addContent(progressE);
			}
			character.addContent(instances);
			characters.addContent(character);
		}
		return characters;
	}
	
	private static Element getInstanceData(DataHolder p) {
		Element instances = new Element(INSTANCE_ROOT);
		for (Instance ini : p.getInstances()) {
			Element instance = new Element(INSTANCE2);
			instance.setAttribute(ID, ini.getId() + "");
			instance.setAttribute(NAME, ini.getName());
			instances.addContent(instance);
		}
		return instances;
	}
	
	public static DataHolder loadFromXMl(File file) {
		if (file == null || !file.exists()) return null;
		
		DataHolder p = new DataHolder();
		try {
			Document doc = new SAXBuilder().build(file.getAbsolutePath());
			Element root = doc.getRootElement();
			loadInstances(p, root.getChild(INSTANCE_ROOT));
			loadCharacters(p, root.getChild(CHAR_ROOT));
		} catch (JDOMException e) {
			//TODO: Add Exception handling
			e.printStackTrace();
		} catch (IOException e) {
			//TODO: Add Exception handling
			e.printStackTrace();
		}
		
		return p;
	}
	
	private static void loadInstances(DataHolder p, Element instances) {
		for (Element instance : instances.getChildren()) {
			int id = Integer.valueOf(instance.getAttributeValue(ID));
			String name = instance.getAttributeValue(NAME);
			p.addInstance(new Instance(id, name));
		}
	}
	
	private static void loadCharacters(DataHolder p, Element characters) {
		for (Element character : characters.getChildren()) {
			int id = Integer.valueOf(character.getAttributeValue(ID));
			String name = character.getAttributeValue(NAME);
			String charClassName = character.getAttributeValue(CHAR_CLASS);
			CharacterClass charClass = CharacterClass.valueOf(charClassName);
			
			Character c = new Character(id, name, charClass);
			
			for (Element e : character.getChild(INSTANCE_ROOT).getChildren()) {
				int iniID = Integer.valueOf(e.getAttributeValue(ID));
				Instance instance = p.findInstance(iniID);
				
				String statusValue = e.getAttributeValue(STATUS);
				RaidStatus status;
				try {
					status = RaidStatus.valueOf(statusValue);
				} catch (Exception e2) {
					// Bug fix for older versions
					boolean done = Boolean.valueOf(e.getAttributeValue(STATUS));
					status = done ? RaidStatus.DONE : RaidStatus.DEFAULT;
				}
				c.getProgresses().put(instance, status);
			}
			p.addCharacter(c);
		}
	}
}
