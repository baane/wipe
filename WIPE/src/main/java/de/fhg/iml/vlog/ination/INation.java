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
package de.fhg.iml.vlog.ination;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.fhg.iml.vlog.xml.DomUtil;

/** INation means internationalization. This class is an implementation for an easy to use multiple language support.
 * <p>
 * The several static methods of INation are needed for the ordinary use of the INation implementation. <br/>
 * Use openAndRegister() to load an INation resource file and obtain an INation object for it. 
 * The INation instance will be registered to be notified, if the global localization settings change. 
 * Use deregister() to remove an INation instance from the registry. 
 * </p>
 * 
 * <p>
 * The several getTranslation%(..) Methods can be used to get the translated string 
 * representation for an given string identifier and some optional parameters. 
 * There are also some experimental text injection functions which can be used by calling translate(..) or inject%(..) methods.
 * </p>
 * 
 * 
 * @author Bernd Schmidt, Stefan Tannenbaum
 */
public class INation {

	static Set<INation> iNations = new HashSet<INation>();
	static Locale globalLocale = Locale.getDefault();
	
	Map<String,String> entries = new HashMap<String,String>();
	Map<Locale,HashMap<String,String>> languages = new HashMap<Locale,HashMap<String,String>>(2);
	Locale defaultLocale;
	
	List<INation> parents = new LinkedList<INation>();

	Locale currentLocale;
	HashMap<?,?> currentMap;

	WeakHashMap<LocaleChangeListener, LocaleChangeListener> listenerMap = 
		new WeakHashMap<LocaleChangeListener, LocaleChangeListener>();
	
	Map<Component,TextStructure> connectedComponents = new HashMap<Component,TextStructure>(0);
	
	URL url;
	boolean fake;
	
	public static int msg = 2;
	
	public final static int ERROR = 0;
	public final static int DEFINE = 1;
	public final static int ENTRY = 2;
	
	
	// CREATION ********************************************************************
	public INation() {
	}
	
	public INation(boolean fake) {
		this.fake = fake;
	}
	
	public static boolean fileExists(Class<?> forClass){
		URL u = resource(forClass);
		return u != null;
	}
	
	public static INation openAndRegister(Class<?> forClass) {
		if (fileExists(forClass)){
			INation iNation = new INation();
			URL u = resource(forClass);
			iNation.load(u);
			register(iNation);
			iNation.url = u;
			return iNation;
		} 
		System.err.println("[INATION]  Missing iNation file for : "+forClass.getSimpleName());
		return new INation(true);
	}
	
	public static INation openAndRegister(URL u) {
		INation iNation = new INation();
		iNation.load(u);
		register(iNation);
		iNation.url = u;
		return iNation;
	}
	
	public static INation open(Class<?> forClass) {
		INation iNation = new INation();
		URL u = resource(forClass);
		iNation.load(u);
		iNation.setToBestLocaleFor(globalLocale);
		iNation.url = u;
		return iNation;		
	}
	
	public static INation open(URL u) {
		INation iNation = new INation();
		iNation.load(u);
		iNation.setToBestLocaleFor(globalLocale);
		return iNation;
	}
	
	private static URL resource(Class<?> forClass) {
		String className = forClass.getName().substring(forClass.getPackage().getName().length() + 1);
		return forClass.getResource("INation" + className + ".xml");
	}
	
	// GLOBAL ****************************************************************	
	/**
	 * Synchronizes the iNations language with the global locale at once and
	 * propagates updates of the global locale to the iNation in the future.
	 */
	public static void register(INation iNation) {
		iNations.add(iNation);
		iNation.setToBestLocaleFor(globalLocale);
	}
	
	/**
	 * Stops propagating updates to the global locale to the iNations.
	 */
	public static void deregister(INation iNation) {
		iNations.remove(iNation);
	}
	
	/**
	 * Changes the global locale to locale and propagates this update to all
	 * registered iNations and to Locale.default. 
	 */
	public static void setGlobalLocale(Locale locale) {
		globalLocale = locale;
		
		for (Iterator<?> i = iNations.iterator(); i.hasNext();) {
			INation iNation = (INation) i.next();
			iNation.setToBestLocaleFor(locale);
		} 
		Locale.setDefault(locale);
	}
	
	/**
	 * Returns the global locale. The global locale is initialized to Local.default.
	 */
	public static Locale getGlobalLocale() {
		return globalLocale;
	}

	
	// DEFINING MODIFICATIONS *******************************************************
	public void addEntry(String id, String comment) {
		entries.put(id, comment);
	}

	public void addLocale(Locale locale) {
		languages.put(locale, new HashMap<String,String>());
	}

	public void addTranslation(Locale locale, String id, String text) {
		languages.get(locale).put(id, text);
	}
	
	public void fillWithEmptyTranslations(Locale locale) {
		HashMap<String,String> language = languages.get(locale);
		
		for (Iterator<?> i = entries.keySet().iterator(); i.hasNext();) {
		 	String id = (String) i.next();
		 	language.put(id, "");
		}
	}
	
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	

	// BASIC OPERATION **************************************************************
	public Set<Locale> getAvailableLocales() {
		return languages.keySet();
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public Locale getCurrentLocale() {
		return currentLocale;
	}

	public void setCurrentLocale(Locale locale) {
		currentLocale = locale;
		currentMap = languages.get(currentLocale);
		
		updateEntrys();
		
		for (LocaleChangeListener listener : listenerMap.values()) {
			listener.localeChanged();
		}
	}

	public String getComment(String name) {
		return  entries.get(name);
	}

	public String getTranslation(String name) {
		return getTranslation(name, true);
	}
	
	public String getTranslation(String name, boolean sysout) {
		if(fake) return name;
		
		if(currentMap == null){ 
			System.err.println("[INATION] Missing translations for locale \"" + currentLocale + "\" in \"" + url + "\"");
			return name;
		}
		
		String translation = (String) currentMap.get(name);
		
		if (translation == null) {
			for (INation p : parents) {
				String out = p.getTranslation(name, false);
				if(out != null) return out;
			}
			if(sysout) {
				if(msg == 0) System.err.println("[INATION] Missing Translation for \"" + name + "\" in \"" + url + "\"");
				else if (msg == 1) System.err.println("[INATION] \t<define id=\"" + name + "\" comment=\""+name.replace('_',' ')+"\"/>" );
				else if (msg == 2) {
					if (name != null) {
						System.err.println("[INATION] "+url.toString());
						System.err.println("[INATION] \t<entry id=\"" + name + "\" translation=\""+name.replace('_',' ')+"\"/>");
					}
				}
				return name;
			}
			return null;
		} 
		String out = translation;
		//FIXME Does not work with JDK 8
		if (translation.contains("\\n")){
			out = translation.replace("\\n", "\n"); //FIX
//			out = translation.replace("\\n", "<br>");
//			out = "<HTML>" + out+ "</HTML>";
		}
		return out;
	}

	public void addChangeListener(LocaleChangeListener listener) {
		listenerMap.put(listener, listener);
	}

	public void removeChangeListener(LocaleChangeListener listener) {
		listenerMap.remove(listener);
	}
	
	
	// TRANSLATE WITH PARAMETERS 	
	public String getTranslationWithParameters(String name, int p1) {
		return getTranslationWithParameters(name, new Object[]{new Integer(p1)});
	}

	public String getTranslationWithParameters(String name, int p1, int p2) {
		return getTranslationWithParameters(name, new Object[]{new Integer(p1), new Integer(p2)});
	}

	public String getTranslationWithParameters(String name, Object[] parameters) {
		String baseText = getTranslation(name);
		MessageFormat m = new MessageFormat(baseText, currentLocale);
		return m.format(parameters);
	}

	
	// ADVANCED LOCALE MANAGING
	public void setToBestLocaleFor(Locale desired) {
		setCurrentLocale(getBestLocaleFor(desired));
	}
	
	public Locale getBestLocaleFor(Locale desired) {
		List<Locale> language = new ArrayList<Locale>(0);
		List<Locale> country = new ArrayList<Locale>(0);
		List<Locale> variant = new ArrayList<Locale>(0);

		for (Iterator<Locale> i = languages.keySet().iterator(); i.hasNext();) {
			Locale l = i.next();

			if (!l.getLanguage().equals(desired.getLanguage())) continue;
			language.add(l);
			if (!l.getCountry().equals(desired.getCountry())) continue;
			country.add(l);
			if (!l.getVariant().equals(desired.getVariant())) continue;
			variant.add(l);
		}

		if (!variant.isEmpty()) return variant.get(0);
		if (!country.isEmpty()) return country.get(0);
		if (!language.isEmpty()) return language.get(0);
		return defaultLocale;
	}
	
	// LOAD & SAVE ******************************************************************
	public void load(URL url) {
		this.url = url;
	
		try {
			Document doc = DomUtil.parse(url.openStream());
			Element root = doc.getDocumentElement();

			entries.clear();
			languages.clear();
			
			NodeList parent = root.getElementsByTagName("parents");
			if(parent.getLength() > 0){
				loadParents((Element) parent.item(0));
			}
			
			Element description = (Element) root.getElementsByTagName("description").item(0);
			loadEntries(description, this.entries, "id", "comment");
			loadLanguages(root);

			defaultLocale = decodeLocale(root.getAttribute("default-locale"));
			setCurrentLocale(defaultLocale);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	private void loadParents(Element parentsElt) {
		NodeList e = parentsElt.getElementsByTagName("parent");
		for(int i = 0; i < e.getLength(); i++) {
			String c = ((Element) e.item(i)).getAttribute("class");
			try {
				Class<?> clazz = Class.forName(c);
				INation iNation = openAndRegister(clazz);
				parents.add(iNation);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void save(File file) {
		try {
			URL url = getClass().getResource("i18nTemplate.xml");
			Document doc = DomUtil.parse(url.openStream());
			Element root = doc.getDocumentElement();
			root.setAttribute("default-locale", encodeLocale(defaultLocale));
			Element description = doc.createElement("description");
			saveEntries(doc, description, this.entries, "define", "id", "comment");
			root.appendChild(description);
			saveLanguages(doc, root);
			DomUtil.save(doc, file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	private void loadLanguages(Element root) {
		NodeList list = root.getElementsByTagName("language");
		for (int i = 0; i < list.getLength(); i++) {
			Element elt = (Element) list.item(i);
			HashMap<String,String> map = new HashMap<String,String>();
			languages.put(decodeLocale(elt.getAttribute("locale")), map);
			loadEntries(elt, map, "id", "translation");
		}
	}

	private void saveLanguages(Document doc, Element root) {
		ArrayList<Map.Entry<Locale,HashMap<String,String>>> entries = new ArrayList<Map.Entry<Locale,HashMap<String,String>>>();
		entries.addAll(languages.entrySet());
		Collections.sort(entries, new LanguageComparator());

		Iterator<Map.Entry<Locale,HashMap<String,String>>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<Locale,HashMap<String,String>> entry = it.next();
			Element language = doc.createElement("language");
			language.setAttribute("locale", encodeLocale(entry.getKey()));
			root.appendChild(language);
			saveEntries(doc, language, entry.getValue(), "entry", "id", "translation");
		}
	}

	private void loadEntries(Element language, Map<String,String> map, String keyName, String valueName) {
		List<Element> list = DomUtil.elements(language);
		for (int i = 0; i < list.size(); i++) {
			Element elt = list.get(i);
			map.put(elt.getAttribute(keyName), elt.getAttribute(valueName));
		}
	}

	private void saveEntries(Document doc, Element element, Map<String,String> map, String tagName, String keyName, String valueName) {
		ArrayList<Map.Entry<String,String>> entries = new ArrayList<Map.Entry<String,String>>(map.entrySet());
		Collections.sort(entries, new EntryComparator());
		Iterator<?> it = entries.iterator();
		while (it.hasNext()) {
			Entry<?,?> entry = (Entry<?, ?>) it.next();
			Element elt = doc.createElement(tagName);
			elt.setAttribute(keyName, (String) entry.getKey());
			elt.setAttribute(valueName, (String) entry.getValue());
			element.appendChild(elt);
		}
	}

	static Locale decodeLocale(String locale) {
		StringTokenizer t = new StringTokenizer(locale, "-");
		String language = t.nextToken();
		String country = t.hasMoreTokens() ? t.nextToken() : "";
		String variant = t.hasMoreTokens() ? t.nextToken() : "";
		return new Locale(language, country, variant);
	}

	static String encodeLocale(Locale locale) {
		String s = locale.getLanguage();
		if (locale.getCountry().length() != 0) s += "-" + locale.getCountry();
		if (locale.getVariant().length() != 0) s += "-" + locale.getVariant();
		return s;
	}

	static class LanguageComparator implements Comparator<Map.Entry<Locale,HashMap<String,String>>> {
		@Override
		public int compare(Map.Entry<Locale,HashMap<String,String>> o1, Map.Entry<Locale,HashMap<String,String>> o2) {
			Locale l1 = o1.getKey();
			Locale l2 = o2.getKey();
			return encodeLocale(l1).compareTo(encodeLocale(l2));
		}
	}

	static class EntryComparator implements Comparator<Map.Entry<String,String>> {
		@Override
		public int compare(Map.Entry<String,String> o1, Map.Entry<String,String> o2) {
			String o1Comp= o1.getKey();
			String o2Comp= o2.getKey();
			return o1Comp.compareTo(o2Comp);
		}
	}

	
	// SWING SUPPORT (EXPERIMENTAL) *************************************************
	public void translate(Component component) {
		TextStructure text = extractText(component);
		TextStructure translation = translate(text);
		injectText(component, translation);
	}
	
	public void translateAll(Component componentRoot) {
		translate(componentRoot);
		
		if (componentRoot instanceof Container) {
			Component[] children = ((Container)componentRoot).getComponents();
			for (int i = 0; i < children.length; i++) {
				connectAll(children[i]);
			}
		}
	}
	
	public void connect(Component component) {
		TextStructure text = extractText(component);
		TextStructure translation = translate(text);
		injectText(component, translation);
		connectedComponents.put(component, text);
	}
	
	public void connectAll(Component componentRoot) {
		connect(componentRoot);
		
		if (componentRoot instanceof Container) {
			Component[] children = ((Container)componentRoot).getComponents();
			for (int i = 0; i < children.length; i++) {
				connectAll(children[i]);
			}
		}
	}
	
	public void disconnect(Component component) {
		connectedComponents.remove(component);
	}
	
	public void disconnectAll(Component componentRoot) {
		disconnect(componentRoot);
		
		if (componentRoot instanceof Container) {
			Component[] children = ((Container)componentRoot).getComponents();
			for (int i = 0; i < children.length; i++) {
				connectAll(children[i]);
			}
		}
	}

	
	public void addEntries(Component component) {
		TextStructure text = extractText(component);
		addEntries(text);
	}
	
	public void addEntriesAll(Component componentRoot) {
		addEntries(componentRoot);
		
		if (componentRoot instanceof Container) {
			Component[] children = ((Container)componentRoot).getComponents();
			for (int i = 0; i < children.length; i++) {
				addEntriesAll(children[i]);
			}
		}
	}
	
		
	private void updateEntrys() {	
		for (Iterator<?> i = connectedComponents.entrySet().iterator(); i.hasNext();) {
			Map.Entry<?,?> e = (Map.Entry<?,?>) i.next();
			Component component = (Component) e.getKey();
			TextStructure text = (TextStructure) e.getValue();
			
			TextStructure translation = translate(text);
			injectText(component, translation);
		}
	}
	
	private static class TextStructure {
		String masterText;
		List<String> dataTexts = new ArrayList<String>(0);
		List<String> borderTexts = new ArrayList<String>(0);
	}
	
	private TextStructure translate(TextStructure t) {
		TextStructure r = new TextStructure();
		
		r.masterText = (t.masterText != null) ? getTranslation(t.masterText) : null;
		for (Iterator<?> i = t.dataTexts.iterator(); i.hasNext();) {
			r.dataTexts.add(getTranslation((String) i.next()));
		}
		for (Iterator<?> i = t.borderTexts.iterator(); i.hasNext();) {
			r.borderTexts.add(getTranslation((String) i.next()));
		}
		
		return r;
	}
	
	private void addEntries(TextStructure t) {
		if (t.masterText != null) addEntry(t.masterText, "");
		
		for (Iterator<?> i = t.dataTexts.iterator(); i.hasNext();) {
			addEntry((String) i.next(), "");
		}
		for (Iterator<?> i = t.borderTexts.iterator(); i.hasNext();) {
			addEntry((String) i.next(), "");
		}
	}
	
	private static TextStructure extractText(Component component) {
		TextStructure t = new TextStructure();
		
		extractMasterText(component, t);
		extractBorderText(component, t);
		
		return t;
	}
	
	private static void extractMasterText(Component component, TextStructure t) {
		if (component instanceof AbstractButton) {
			t.masterText = ((AbstractButton) component).getText();
		} else if (component instanceof JLabel) {
			t.masterText = ((JLabel) component).getText();
		} else if (component instanceof Frame) {
			t.masterText = ((Frame) component).getTitle();
		} else if (component instanceof Dialog) {
			t.masterText = ((Dialog) component).getTitle();
		}
	}
	
	private static void extractBorderText(Component c, TextStructure t) {
		if (c instanceof JComponent) {
			extractBorderText0(((JComponent)c).getBorder(), t);
		}
	}
	
	private static void extractBorderText0(Border b, TextStructure t) {
		if (b instanceof TitledBorder) {
			String title = ((TitledBorder)b).getTitle();
			t.borderTexts.add(title);
		} else if (b instanceof CompoundBorder) {
			CompoundBorder c = (CompoundBorder) b;
			extractBorderText0(c.getOutsideBorder(), t);
			extractBorderText0(c.getInsideBorder(), t);			
		}
	}
	
	private static void injectText(Component c, TextStructure t) {
		injectMasterText(c, t);
		injectBorderText(c, t);
	}
	
	private static void injectMasterText(Component component, TextStructure t) {
		if (component instanceof AbstractButton) {
			((AbstractButton) component).setText(t.masterText);
		} else if (component instanceof JLabel) {
			((JLabel) component).setText(t.masterText);
		} else if (component instanceof Frame) {
			((Frame) component).setTitle(t.masterText);
		} else if (component instanceof Dialog) {
			((Dialog) component).setTitle(t.masterText);
		}
	}
	
	private static void injectBorderText(Component c, TextStructure t) {
		if (c instanceof JComponent) {
			Border b = ((JComponent)c).getBorder();
			injectBorderText0(b, t.borderTexts.iterator());
		}
	}
	
	private static void injectBorderText0(Border b, Iterator<?> i) {
		if (b instanceof TitledBorder) {			
			String title = (String) i.next();
			((TitledBorder)b).setTitle(title);
		} else if (b instanceof CompoundBorder) {
			CompoundBorder c = (CompoundBorder) b;
			injectBorderText0(c.getOutsideBorder(), i);
			injectBorderText0(c.getInsideBorder(), i);			
		}
	}
	
	public void inject(JLabel label){
		label.setText(getTranslation(label.getName()));
	}
	
	public void inject(JButton button){
		button.setText(getTranslation(button.getName()));
	}
	
	public void injectTip(JComponent c,String name){
		c.setToolTipText(getTranslation(name));
	}
	
	public void injectBorderTitle(JPanel panel,String name){
		((TitledBorder)panel.getBorder()).setTitle(getTranslation(name));
	}
}
