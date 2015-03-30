package org.ariadne.validation.components;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.vcard4j.java.VCard;
import net.sf.vcard4j.java.type.N;
import net.sf.vcard4j.java.type.VERSION;
import net.sf.vcard4j.parser.DomParser;
import net.sf.vcard4j.parser.VCardException;

import org.ariadne.validation.exception.ValidationException;
import org.ariadne.validation.utils.ValidationConstants;
import org.jdom.Element;
import org.w3c.dom.Document;

public class VcardComponent extends XpathComponent {
	
	protected DocumentBuilder documentBuilder = null;

	protected static final int[] VCARD_VERSION = {3,0};
	
	protected DomParser vCardParser = new DomParser();

	public VcardComponent(Vector<String> xPaths) {
		super(xPaths);
		init();
	}
	
	public VcardComponent() {
		init();
	}
	
	private void init() {
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void validateElements(List<Element> elements, String xpath) throws ValidationException {
		ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_VCARD);
		for(Element vcard : elements) {
			try {
				String vcardString = vcard.getText();
				if(vcardString == null || vcardString.trim().equals("")) {
					throw new VCardException("VCard can't be an empty string.");
				}
				parseVcard(vcardString, false);
			} catch (VCardException e) {
				exc.addException("VCard Validation Exception : (at " + xpath + ") " + e.getMessage());
			}
		}
		if(exc.getAllExceptions().size() > 0) throw exc;
	}
	
	public void parseVcard(String vcardString, boolean unescape) {
		ValidationException exc = new ValidationException(ValidationConstants.VAL_EXC_TYPE_VCARD);
		if (unescape) vcardString = unescapeEntity(vcardString);
		vCardParser.setConvertVersion(false);
		Document vCardDocument;
//		try {
			vCardDocument = documentBuilder.newDocument();
//		} catch (ParserConfigurationException pce) {
//			VCardException pe = new VCardException(pce.getClass().getName() + ": " + pce.getMessage());
//			pe.initCause(pce);
//			throw pe;
//		}
		try {
			vCardParser.parse(new StringReader(vcardString), vCardDocument);
//			} catch (VCardException vce) {
//			ParseException pe = new ParseException(vce.getClass().getName() + ": " + vce.getMessage(), -1);
//			pe.initCause(vce);
//			throw pe;
		} catch (IOException ioe) {
			VCardException pe = new VCardException(ioe.getClass().getName() + ": " + ioe.getMessage());
			pe.initCause(ioe);
			throw pe;
		}
		org.w3c.dom.Element addressBookElement = vCardDocument.getDocumentElement();
		org.w3c.dom.Element vCardElement = (org.w3c.dom.Element)addressBookElement.getElementsByTagNameNS(DomParser.VCARD4J_NAMESPACE,DomParser.XML_VCARD).item(0);
		net.sf.vcard4j.java.VCard vCard = new net.sf.vcard4j.java.VCard(vCardElement);
		Iterator nIterator = vCard.getTypes("N");
		N n = (nIterator.hasNext()) ? (N)nIterator.next() : null;
		if (n == null) exc.addException("vCard has null N type");
		Iterator versionIterator = vCard.getTypes("VERSION");
		VERSION version = (versionIterator.hasNext()) ? (VERSION)versionIterator.next() : null;
		if ((version == null) || (version.get() == null)) exc.addException("vCard has null VERSION type");
		int[] versionNum;
		try {
			versionNum = getVersion(version.get());
			if (compareVersions(versionNum, VCARD_VERSION) < 0) {
				String ver = version.get();
				if (ver == null || ver.equals("")) ver = "empty";
				exc.addException("Invalid vCard VERSION (version given was " + ver + ", must be " + parseVersion(VCARD_VERSION) + " or higher)");
			}
		} catch (NumberFormatException nfe) {
			exc.addException(nfe.getClass().getName() + ": " + nfe.getMessage());
		}
		if(exc.getAllExceptions().size() > 0) throw new VCardException(exc.getMessage());
	}
	
	/**
	 * @deprecated : this is legacy code stolen from LOM Java API
	 * @param entity
	 * @param unescape
	 * @return
	 */
	public VCard parseVcard2(String entity, final boolean unescape) {
		if (unescape) entity = unescapeEntity(entity);
		if (entity == null) return null;
		final DomParser vCardParser = new DomParser();
		vCardParser.setConvertVersion(false);
		final Document vCardDocument;
//		try {
			vCardDocument = documentBuilder.newDocument();
//		} catch (ParserConfigurationException pce) {
//			final VCardException pe = new VCardException(pce.getClass().getName() + ": " + pce.getMessage());
//			pe.initCause(pce);
//			throw pe;
//		}
		try {
			vCardParser.parse(new StringReader(entity), vCardDocument);
//			} catch (VCardException vce) {
//			final ParseException pe = new ParseException(vce.getClass().getName() + ": " + vce.getMessage(), -1);
//			pe.initCause(vce);
//			throw pe;
		} catch (IOException ioe) {
			final VCardException pe = new VCardException(ioe.getClass().getName() + ": " + ioe.getMessage());
			pe.initCause(ioe);
			throw pe;
		}
		org.w3c.dom.Element addressBookElement = vCardDocument.getDocumentElement();
		org.w3c.dom.Element vCardElement = (org.w3c.dom.Element)addressBookElement.getElementsByTagNameNS(DomParser.VCARD4J_NAMESPACE,DomParser.XML_VCARD).item(0);
		net.sf.vcard4j.java.VCard vCard = new net.sf.vcard4j.java.VCard(vCardElement);
		Iterator nIterator = vCard.getTypes("N");
		N n = (nIterator.hasNext()) ? (N)nIterator.next() : null;
		if (n == null) throw new VCardException("vCard has null N type");
		Iterator versionIterator = vCard.getTypes("VERSION");
		VERSION version = (versionIterator.hasNext()) ? (VERSION)versionIterator.next() : null;
		if ((version == null) || (version.get() == null)) throw new VCardException("vCard has null VERSION type");
		int[] versionNum;
		try {
			versionNum = getVersion(version.get());
		} catch (NumberFormatException nfe) {
			final VCardException pe = new VCardException(nfe.getClass().getName() + ": " + nfe.getMessage());
			pe.initCause(nfe);
			throw pe;
		}
		if (compareVersions(versionNum, VCARD_VERSION) < 0) {
			String ver = version.get();
			if (ver == null || ver.equals("")) ver = "empty";
			throw new VCardException("Invalid vCard VERSION (version given was " + ver + ", must be " + parseVersion(VCARD_VERSION) + " or higher)");
		}
		return vCard;
	}

	/**
	 * Extract a hierarchical version number (1.2.1) from a String.
	 */
	protected int[] getVersion(String versionString) throws NumberFormatException {
		if (versionString == null || versionString.equals("")) return new int[] {0};
		String[] versionComps = versionString.split("\\.");
		int[] versionNums = new int[versionComps.length];
		for (int i = 0; i < versionComps.length; i++)
			versionNums[i] = Integer.parseInt(versionComps[i]);
		return versionNums;
	}

	/**
	 * Extract a hierarchical version number (1.2.1) from a String.
	 */
	protected String parseVersion( int[] version){
		if (version == null || version.length == 0) return "0";
		String versionString = Integer.toString(version[0]);
		for (int i = 1; i < version.length; i++)
			versionString += "." + Integer.toString(version[i]);
		return versionString;
	}

	/**
	 * Compare two hierarchical numeric version numbers (ie 1.0 vs 1.2.1).
	 */
	protected int compareVersions(int[] version1, int[] version2) {
		int max = Math.max(version1.length, version2.length);
		for (int i = 0; i < max; i++) {
			int v1 = (i < version1.length) ? version1[i] : 0;
			int v2 = (i < version2.length) ? version2[i] : 0;
			if (v1 < v2) return -1;
			if (v1 > v2) return 1;
		}
		return 0;
	}

	public String unescapeEntity(String entity) {
		entity = mkNull(entity, true);
		if (entity == null) return null;
		entity = entity.replaceAll("\r\n[ \t]", ""); // RFC2425 sec 5.8.1 MIME-DIR unfolding: remove any CRLF followed by a space or tab
		entity = entity.replaceAll("\\\\[nN]", "\r\n"); // RFC2426 sec 2.4.2 'vcard value type' decoding: replace "\n" or "\N" strings with a real CRLF
		entity = entity.replaceAll("\\\\,", ","); // replace "\," strings with a real comma
		entity = entity.replaceAll("\\\\;", ";");
		entity = entity.replaceAll("\\\\:", ":");
		entity = entity.replaceAll("\\\\\\\\", "\\");
		return entity;
	}

	protected String mkNull(String s, boolean trim) {
		if (s == null) return null;
		if (trim) s = s.trim();
		if (s.length() == 0) return null;
		return s;
	}
}
