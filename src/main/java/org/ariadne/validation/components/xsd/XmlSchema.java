package org.ariadne.validation.components.xsd;

public class XmlSchema {

	protected String schemaNamespace;
	protected String schemaLocation;
	
	public XmlSchema(){
		
	}
	
	public XmlSchema(String schemaNamespace, String schemaLocation) {
		this.schemaNamespace = schemaNamespace;
		this.schemaLocation = schemaLocation;
	}

	public String getSchemaNamespace() {
		return schemaNamespace;
	}
	public void setSchemaNamespace(String schemaNamespace) {
		this.schemaNamespace = schemaNamespace;
	}
	public String getSchemaLocation() {
		return schemaLocation;
	}
	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}
	
}
