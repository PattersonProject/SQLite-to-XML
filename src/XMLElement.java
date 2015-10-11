import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A Class that represents an XML Element
 * @author Michael Patterson
 *
 */
public class XMLElement {
	private String name;
	private Map<String, String> attributes;
	private String data;
	private Boolean emptyElement;
	private int numParents;
	
	/** 
	 * Constructs an XML Element.  Empty Elements cannot contain data.
	 * 
	 * @param name  the name of the Element
	 * @param data	the data contained within the element.  Must be null for empty elements
	 * @param emptyElement	indicated if this is an empty element.  
	 * @param numParents indicates the number of parents of an element.  Controls indentation on output. 
	 * @throws Exception 
	 */
	public XMLElement(String name, String data, 
		boolean emptyElement, int numParents) throws Exception{
		this.name = name;
		
		if (emptyElement && name != null){
			throw new Exception ("A Closed XML element may not contain any data");
		}
		this.data = data;
		this.emptyElement = emptyElement;
		this.numParents = numParents;
		
		/*initialize empty attribute HashMap*/
		
		this.attributes = new LinkedHashMap<String,String>();
		
	}
	
	/**
	 * Adds an attribute to this element
	 * @param name  the name of the attribute 
	 * @param value  the value of the attribute
	 * @return
	 */
	public boolean addAttribute(String name, String value){
		
		if (name != null){
			this.attributes.put(name, value);
			return true;
		}else{
			return false;
		}
		
	}
	
	public boolean appendAttribute(String name, String addValue) throws Exception{
	
		if (name != null && !attributes.containsKey(name)){
			System.out.println(name);
			throw new Exception("Cannot find attribute in this element"+
								" Element must exist before it can be appended");
		}
		if (this.attributes.get(name) == null){
			return false;
		}else{
		String newAttribute = this.attributes.get(name) + " " + addValue;
		this.attributes.put(name, newAttribute);
	
		return true;
		}
	}
	
	/**
	 * Returns the Opening tag of the XML Element.
	 * If this element is empty return the complete tag
	 * @return
	 */
	public String getOpenTag(){
		String openTag = "";
		
		//Set indents
		for (int i=0; i < this.numParents; i++){
			openTag += "  ";
		}
		openTag += openTagText();
		
	
		return openTag;
	}
	
	/**
	 * Private Helper class that returns the open tag
	 * without indents
	 */
	private String openTagText() {
		String openTagText = "";
		if (this.emptyElement){
			openTagText += "<" + this.name + " />";
		}else{
		
			openTagText += "<" + this.name;
			
			//  Attributes
			if (!this.attributes.isEmpty()){
				for (String att : attributes.keySet()){
					openTagText += "  " + att + " = \"" 
							 + attributes.get(att)  + "\" ";
				}
			}
		
			openTagText += ">";
		}
		
		return openTagText;
	}
	
	/**
	 * Returns the closing tag of the SML Element.
	 * @return
	 * @throws Exception 
	 */
	public String getCloseTag() throws Exception{
		
		if (this.emptyElement){
			throw new Exception("Empty elements do not have closing tags!");
		}
		
		String closeTag = "";
		for (int i=0; i < this.numParents; i++){
			closeTag += "  ";
		}
		closeTag += closeTagText();
		return closeTag;
		
	}
	
	/**
	 * Private Helper class that returns the closing tag
	 * without indents
	 */
	private String closeTagText() {
		return "</" + this.name + ">";
	}
	
	/**
	 * Returns a complete inline XML Element
	 * @return
	 * @throws Exception 
	 */
	public String inlineElement() throws Exception{
		
		String element = "";
		for (int i=0; i < this.numParents; i++){
			element += "  ";
		}
		
		element += this.openTagText() + this.data + this.closeTagText();
		return element;
	}
	
}
