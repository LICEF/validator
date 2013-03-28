package org.ariadne.validation;

import java.util.Vector;

import org.ariadne.validation.components.ValidationComponent;

public class ValidationScheme {

	protected String type = "xml";
	protected String uri = "";
	protected Vector<ValidationComponent> components = new Vector<ValidationComponent>();
	protected String topNode = null;
	protected String topNodeNs = null;
	protected String topNodeNsPrefix = null;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Vector<ValidationComponent> getComponents() {
		return components;
	}
	public void setComponents(Vector<ValidationComponent> components) {
		this.components = components;
	}
	public String getTopNode() {
		return topNode;
	}
	public void setTopNode(String topNode) {
		this.topNode = topNode;
	}
	public String getTopNodeNsPrefix() {
		return topNodeNsPrefix;
	}
	public void setTopNodeNsPrefix(String topNodeNsPrefix) {
		this.topNodeNsPrefix = topNodeNsPrefix;
	}
	public String getTopNodeNs() {
		return topNodeNs;
	}
	public void setTopNodeNs(String topNodeNs) {
		this.topNodeNs = topNodeNs;
	}
	
	
}
