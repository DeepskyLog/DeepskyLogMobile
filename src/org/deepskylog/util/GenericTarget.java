/* ====================================================================
 * /GenericTarget.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package org.deepskylog.util;

import android.annotation.SuppressLint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.deepskylog.util.util.SchemaException;

@SuppressLint("DefaultLocale")
public class GenericTarget extends Target {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:observationTargetType";	
	
    
    
    
	// -----------
	// Constructor -------------------------------------------------------
	// -----------

	// ------------------------------------------------------------------- 
    public GenericTarget(Node targetElement,
                         IObserver[] observers) throws SchemaException {
        
        super(targetElement, observers);
                
    }


    // ------------------------------------------------------------------- 
    public GenericTarget(String name,
                         String datasource) throws IllegalArgumentException {
        
        super(name, datasource);  
                
    }


    // -------------------------------------------------------------------   
    public GenericTarget(String name,
                         IObserver observer) throws IllegalArgumentException {
        
    	super(name, observer);
                
    }
        
    
    
    
	// ------
	// Object ------------------------------------------------------------
	// ------

	// -------------------------------------------------------------------
	/**
	 * Overwrittes toString() method from java.lang.Object.<br>
	 * Returns the field values of this GenericTarget.

	 * @return This GenericTarget field values
	 * @see java.lang.Object
	 */
	public String toString() {
        
		StringBuffer buffer = new StringBuffer();
		buffer.append("GenericTarget Name=");
		buffer.append(super.getName());
		
		if(   (super.getAliasNames() != null)
		   && (super.getAliasNames().length > 0 )
			) {
			buffer.append(" Alias names=");
			String[] an = super.getAliasNames();
			for(int i=0; i < an.length; i++) {
				buffer.append(an[i]);
				if( i <= an.length-2 ) {
					buffer.append(", ");	
				}
			}									
		} 
		
		if( super.getPosition() != null ) {
			buffer.append(" Position=");
			buffer.append(super.getPosition());		
		} 		
        
		return buffer.toString();
        
	}


	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if this GenericTarget and the given Object are equal. The given 
	 * object is equal with this GenericTarget, if it derives from ITarget, 
	 * both XSI types are equal and its name equals this GenericTarget name.<br>
	 * @param obj The Object to compare this GenericTarget with.
	 * @return <code>true</code> if the given Object is an instance of ITarget,
	 * both XSI types are equal and its name is equal to this GenericTarget name.<br>
	 * (Name comparism is <b>not</b> casesensitive)
	 * @see java.lang.Object
	 */    
	@SuppressLint("DefaultLocale")
	public boolean equals(Object obj) {
        
		if(   obj == null
		   || !(obj instanceof ITarget)
		   ) {
			return false;
		}
                        
		ITarget target = (ITarget)obj; 
		
		String targetName = target.getName();
		if( targetName == null ) {
  		  	return false;			                       	
		}
                        
		if(   (super.getName().toLowerCase().equals(targetName.toLowerCase()))
		   && (this.getXSIType()).equals(target.getXSIType())
		   ) {
			return true;
		} 
        
		return false;
                         
	}    
    
    
    
	
    // ------
    // Target ------------------------------------------------------------
    // ------

    // -------------------------------------------------------------------
	/**
	 * Adds this Target to a given parent XML DOM Element.
	 * The Target element will be set as a child element of
	 * the passed element.
	 * 
	 * @param parent The parent element for this Target
	 * @return Returns the element given as parameter with this 
	 * Target as child element.<br>
     * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */
	public Element addToXmlElement(Element element) {

        if( element == null ) {
            return null;
        }
        
        Document ownerDoc = element.getOwnerDocument();                                                                                          
                        
        // Get or create the container element        
        Element e_Targets = null;
        boolean created = false;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
            e_Targets = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
            created = true;
        } else {
            e_Targets = (Element)nodeList.item(0);  // there should be only one container element
        }                       
        
        // Check if this element doesn't exist so far
        nodeList = e_Targets.getElementsByTagName(ITarget.XML_ELEMENT_TARGET);
        if( nodeList.getLength() > 0 ) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for(int i=0; i < nodeList.getLength(); i++) {   // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();   
                Node idAttribute = attributes.getNamedItem(SchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if(   (idAttribute != null)    // if ID attribute is set and equals this objects ID, return existing element
                   && (idAttribute.getNodeValue().trim().equals(super.getID().trim()))
                  ) {
                		// Not sure if this is good!? Maybe we should return currentNode and make doublicity check in caller
                	    // class!?
                        return null;
                }
            }
        }        
        
        // Create the new target element
        Element e_Target = super.createXmlTargetElement(e_Targets);                                      
        e_Targets.appendChild(e_Target);      


        // Set XSI:Type
        e_Target.setAttribute(ITarget.XML_XSI_TYPE, this.getXSIType());
        
        // If container element was created, add container here so that XML sequence fits forward references
        // Calling the appendChild in the if avbe would cause the session container to be located before
        // observers and sites container
        if( created ) {
        	ownerDoc.getDocumentElement().appendChild(e_Targets);
        }
        
        return e_Target;        
		
	}

	
	
	
    // ------------------------
    // IExtendableSchemaElement ------------------------------------------
    // ------------------------
    
    // -------------------------------------------------------------------
    /**
     * Returns the XML schema instance type of the implementation.<br>
     * Example:<br>
     * <target xsi:type="myOwnTarget"><br>
     * </target><br>
     * 
     * @return The xsi:type value of this implementation 
     */ 	
	public String getXSIType() {
		
		return GenericTarget.XML_XSI_TYPE_VALUE;
		
	}

}
