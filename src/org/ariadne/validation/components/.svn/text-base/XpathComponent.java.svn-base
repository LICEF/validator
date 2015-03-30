package org.ariadne.validation.components;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public abstract class XpathComponent extends ValidationComponent {

	protected Vector<XPath> xpath;

	protected Vector<Namespace> namespaces;

	protected SAXBuilder builder;

	public XpathComponent(Vector<String> paths) {
		init(paths);
	}

	public XpathComponent() {
	}

	private void init(Vector<String> paths) {
		builder = new SAXBuilder();
		xpath = new Vector<XPath>();

		for(String path : paths) {
			try {
				XPath xpath = XPath.newInstance(path);
				for(Namespace ns : namespaces) {
					xpath.addNamespace(ns);
				}
				getXpath().add(xpath);
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void validate(String metadata) throws ValidationException {
		try {
			Document xml = builder.build(new StringReader(metadata));

			if(getXpath().size() > 0) {
				for(XPath xpath : getXpath()) {
					List <Element> elements = xpath.selectNodes(xml.getRootElement());
					validateElements(elements, xpath.getXPath());
				}
			}else {
				List<Element> elements = new ArrayList<Element>();
				elements.add(xml.getRootElement());
				validateElements(elements, "/");
			}

		} catch (JDOMException e) {
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_GENERIC);
		} catch (IOException e) {
			throw new ValidationException(e.getMessage(), ValidationConstants.VAL_EXC_TYPE_GENERIC);
		}

	}

	@Override
	public void init(String name, Hashtable<String,String> table) throws InitialisationException {
		
		Namespace lomns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
		namespaces = new Vector<Namespace>();
		namespaces.add(lomns);
		String nsList = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".xpath.ns");
		if(nsList != null) {
			StringTokenizer nsTokens = new StringTokenizer(nsList,";");
			while(nsTokens.hasMoreTokens()) {
				String nsName = nsTokens.nextToken();
				String nsURI = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".xpath.ns." + nsName + ".URI");
				namespaces.add(Namespace.getNamespace(nsName.trim(),nsURI.trim()));
			}
		}

		Vector<String> paths = new Vector<String>();
		int i = 1;
		String xpath = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".xpath." + i);
		while(xpath != null) {
			paths.add(xpath.trim());
			xpath = (String)table.get(ValidationConstants.VAL_PROP_PREFIX + name + ".xpath." + ++i);
		}
		init(paths);
	}

	protected abstract void validateElements(List <Element> elements, String xpath) throws ValidationException;

	public Vector<XPath> getXpath() {
		return xpath;
	}

}
