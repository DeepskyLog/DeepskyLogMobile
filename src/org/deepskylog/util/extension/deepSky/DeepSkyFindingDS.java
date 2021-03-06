/* ====================================================================
 * extension/deepSky/DeepSkyFindingDS.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package org.deepskylog.util.extension.deepSky;

import android.annotation.SuppressLint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.deepskylog.util.IFinding;
import org.deepskylog.util.util.SchemaException;

/**
 * DeepSkyFindingDS extends the de.lehmannet.om.DeepSkyFinding class.
 * Its specialised for double star observations and their findings.
 * The class is mostly oriented after the recommondations of the
 * german "VdS - DeepSky" group 
 * (<a href="http://www.fachgruppe-deepsky.de/">Homepage</a>).<br>
 * The field rating is based on a seven step scale recommended
 * by "VDS - DeepSky" group. The scales value should be interpreted
 * as the following table explains:
 * <table>
 * <tr>
 * <td>1</td>
 * <td>Simple conspicuous object in the eyepiece</td>
 * <td>2</td>
 * <td>Good viewable with direct vision</td>
 * <td>3</td>
 * <td>Viewable with direct vision</td>
 * <td>4</td>
 * <td>Viewable only with averted vision</td>
 * <td>5</td>
 * <td>Object can hardly be seen with averted vision</td>
 * <td>6</td>
 * <td>Object dubiously sighted</td>
 * <td>7</td>
 * <td>Object not sighted</td>
 * </tr>
 * </table> 
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.5
 */
@SuppressLint("UseValueOf")
public class DeepSkyFindingDS extends DeepSkyFinding {

	// ---------
	// Constants ---------------------------------------------------------
	// ---------

	// XSML schema instance value. Enables class/schema loaders to identify this
	// class
	public static final String XML_XSI_TYPE_VALUE = "oal:findingsDeepSkyDSType";

	// Constant for XML representation: finding element character
	public static final String XML_ELEMENT_COLORMAIN = "colorMain";

	// Constant for XML representation: finding element character
	public static final String XML_ELEMENT_COLORCOMPANION = "colorCompanion";	
	
	// Constant for XML representation: finding element attribute character
	public static final String XML_ELEMENT_FINDING_ATTRIBUTE_EQUALBRIGHTNESS = "equalBrightness";
	
	// Constant for XML representation: finding element attribute character
	public static final String XML_ELEMENT_FINDING_ATTRIBUTE_NICESURROUNDING = "niceSurrounding";	
	
	// Star color types
	public static final String COLOR_WHITE = "white";
	public static final String COLOR_RED = "red";
	public static final String COLOR_ORANGE = "orange";
	public static final String COLOR_YELLOW = "yellow";
	public static final String COLOR_GREEN = "green";
	public static final String COLOR_BLUE = "blue";
	
	
	
	
	// ------------------
	// Instance Variables ------------------------------------------------
	// ------------------

	// Main star color
	private String colorMain = null;

	// Companion star color
	private String colorCompanion = null;	
	
	// 1 if both stars have an equal brightness
	// 0 if both stars haven't an equal brightness
	// -1 the value was not set
	private int equalBrightness = -1;	

	// 1 if both stars have a nice surrounding
	// 0 if both stars haven't a nice surrounding
	// -1 the value was not set
	private int niceSurrounding = -1;
	
	
	
	
	// ------------
	// Constructors ------------------------------------------------------
	// ------------

	public DeepSkyFindingDS(Node findingElement) throws SchemaException {
	
		super(findingElement);

		Element finding = (Element)findingElement;
		Element child = null;
		NodeList children = null;

		// Getting data

		// Get optional equalBrightness attribute
		String eqBr = finding.getAttribute(DeepSkyFindingDS.XML_ELEMENT_FINDING_ATTRIBUTE_EQUALBRIGHTNESS);
        if(   (eqBr != null)
           && (!"".equals(eqBr.trim())) 
            ) {
			this.setEqualBrightness(new Boolean(eqBr));
		}

		// Get optional niceSurrounding attribute
		String niSu =
			finding.getAttribute(
				DeepSkyFindingDS.XML_ELEMENT_FINDING_ATTRIBUTE_NICESURROUNDING);
        if(   (niSu != null)
           && (!"".equals(niSu.trim())) 
            ) {
			this.setNiceSurrounding(new Boolean(niSu));
		}

		// Get optional color of mainstar
		child = null;
		children = finding.getElementsByTagName(DeepSkyFindingDS.XML_ELEMENT_COLORMAIN);
		String c = "";
		if( children != null ) {
			if( children.getLength() == 1 ) {                   
				child = (Element)children.item(0);
				if( child != null ) {
		          	NodeList textElements = child.getChildNodes();
		        	if(   (textElements != null)
		        	   && (textElements.getLength() > 0) 
		        	   ) {
		        		for(int te=0; te < textElements.getLength(); te++) {
		        			c = c + textElements.item(te).getNodeValue();
		        		}
		        	}  
					//c = child.getFirstChild().getNodeValue();   
					this.setMainStarColor(c);                                  
		  	    } else {
				    throw new SchemaException("Problem while retrieving main color from DeepSkyFindingDS. ");                                       
                }
		    } else if( children.getLength() > 1 ) {
                throw new SchemaException("DeepSkyFindingDS can have only one main star entry. ");                 
            }               
        }			

		// Get optional color of companionstar
		child = null;
		children = finding.getElementsByTagName(DeepSkyFindingDS.XML_ELEMENT_COLORCOMPANION);
		c = "";
		if( children != null ) {
			if( children.getLength() == 1 ) {                   
				child = (Element)children.item(0);
				if( child != null ) {
		          	NodeList textElements = child.getChildNodes();
		        	if(   (textElements != null)
		        	   && (textElements.getLength() > 0) 
		        	   ) {
		        		for(int te=0; te < textElements.getLength(); te++) {
		        			c = c + textElements.item(te).getNodeValue();
		        		}
						//c = child.getFirstChild().getNodeValue();   
						this.setCompanionStarColor(c);		        		
		        	}                                    
		  	    } else {
				    throw new SchemaException("Problem while retrieving companion color from DeepSkyFindingDS. ");                                       
                }
		    } else if( children.getLength() > 1 ) {
                throw new SchemaException("DeepSkyFindingDS can have only one companion star entry. ");                 
            }               
        }		
		
	}
	
	// -------------------------------------------------------------------
	/**
	 * Constructs a new instance of a DeepSkyFindingDS.
	 * 
	 * @param description The description of the finding
	 * @param rating The rating of the finding
	 * @throws IllegalArgumentException if description was <code>null</code>
	 * or rating had a illegal value.
	 */
	public DeepSkyFindingDS(String description, int rating)
							throws IllegalArgumentException {

		super(description, rating);

	}	
	
	
	
	
	// ------
	// Object ------------------------------------------------------------
	// ------
	
	// -------------------------------------------------------------------
	/**
	 * Overwrittes toString() method from java.lang.Object.<br>
	 * Returns the field values of this DeepSkyFindingDS.
	
	 * @return This DeepSkyFindingDS field values
	 * @see java.lang.Object
	 */
	public String toString() {

		StringBuffer buffer = new StringBuffer(super.toString());
		
		if (equalBrightness != -1) {
			buffer.append(" EqualBrightness=");
			buffer.append(this.getEqualBrightness());
		}

		if (niceSurrounding != -1) {
			buffer.append(" NiceSurrounding=");
			buffer.append(this.getNiceSurrounding());
		}

		if (colorMain != null) {
			buffer.append(" ColorMain=");
			buffer.append(this.getColorMain());
		}				

		if (colorCompanion != null) {
			buffer.append(" ColorCompanion=");
			buffer.append(this.getColorCompanion());
		}						
		
		String result = buffer.toString();
		result = result.replaceAll("DeepSkyFinding", "DeepSkyFindingDS");

		return result;
		
	}

	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if this DeepSkyFindingDS and the given Object are equal. Two DeepSkyFindingDSs
	 * are equal if both return the same string from their toString() method and their
	 * XSI type is equal.<br>
	 * @param obj The Object to compare this DeepSkyFindingDS with.
	 * @return <code>true</code> if both Objects are instances from class DeepSkyFindingDS,
	 * both XSI types are equal and
	 * their fields contain the same values. (Can be checked with calling and comparing 
	 * both objects toString() method)
	 * @see java.lang.Object
	 */
	public boolean equals(Object obj) {

		if (obj == null || !(obj instanceof DeepSkyFindingDS)) {
			return false;
		}

		// As we overwritte the toString() method and access all fields there,
		// two DeepSkyFindingDSs are equal, if both objects return the same string 
		// from their toString() method.
		if (   (this.toString().equals(obj.toString()))
			&& (this.getXSIType().equals(((DeepSkyFindingDS)obj).getXSIType()))
			) {
			return true;
		}

		return false;

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

		return DeepSkyFindingDS.XML_XSI_TYPE_VALUE;

	}
	
	
	
	
	// -------
	// Finding -----------------------------------------------------------
	// -------

	// -------------------------------------------------------------------
	/**
	 * Adds this DeepSkyFindingDS to an given parent XML DOM Element.
	 * The DeepSkyFindingDS Element will be set as a child element of
	 * the passed Element.
	 * 
	 * @param parent The parent element for this DeepSkyFindingDS
	 * @return Returns the Element given as parameter with this 
	 * DeepSkyFindingDS as child Element.<br>
	 * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */
	public Element addToXmlElement(Element parent) {

		if (parent == null) {
			return null;
		}

		Document ownerDoc = parent.getOwnerDocument();

		Element e_Finding = super.createXmlFindingElement(parent);

		// Set XSI:Type
		e_Finding.setAttribute(IFinding.XML_XSI_TYPE,
							   DeepSkyFindingDS.XML_XSI_TYPE_VALUE);
		
		if (this.equalBrightness != -1) {
			e_Finding.setAttribute(
				XML_ELEMENT_FINDING_ATTRIBUTE_EQUALBRIGHTNESS,
				Boolean.toString(this.getEqualBrightness()));
		}

		if (this.niceSurrounding != -1) {
			e_Finding.setAttribute(
				XML_ELEMENT_FINDING_ATTRIBUTE_NICESURROUNDING,
				Boolean.toString(this.getNiceSurrounding()));
		}	
		
		if (this.colorMain != null) {
			Element e_ColorMain = ownerDoc.createElement(XML_ELEMENT_COLORMAIN);        
			Node n_ColorMainText = ownerDoc.createCDATASection(this.getColorMain().toString());
			e_ColorMain.appendChild(n_ColorMainText);        
	        e_Finding.appendChild(e_ColorMain);			
		}		

		if (this.colorCompanion != null) {
			Element e_ColorCompanion = ownerDoc.createElement(XML_ELEMENT_COLORCOMPANION);        
			Node n_ColorCompanionText = ownerDoc.createCDATASection(this.getColorCompanion().toString());
			e_ColorCompanion.appendChild(n_ColorCompanionText);        
	        e_Finding.appendChild(e_ColorCompanion);						
		}				
		
		parent.appendChild(e_Finding);

		return parent;

	}	
	
	
	
	
	// --------------
	// Public methods ----------------------------------------------------
	// --------------
	
	// -------------------------------------------------------------------
	/**
	 * Returns the equalBrightness value of this DeepSkyFindingDS.<br>
	 * Describes if both stars have an equal brightness.
	 * 
	 * @return <code>true</code> if both stars have an equal brightness
	 * @throws IllegalStateException if equalBrightness was not set by the user
	 * so the class cannot return <b>true</b> or <b>false</b>
	 */
	public boolean getEqualBrightness() throws IllegalStateException {

		if (this.equalBrightness == -1) {
			throw new IllegalStateException(
				"EqualBrightness value was never set for: " + this);
		}

		if (equalBrightness == 1) {
			return true;
		} else {
			return false;
		}

	}	
	
	// -------------------------------------------------------------------
	/**
	 * Returns the niceSurrounding value of this DeepSkyFindingDS.<br>
	 * Describes if the double star has a nice surrounding. 
	 * 
	 * @return <code>true</code> if the observed object has
	 * a nice surrounding.
	 * @throws IllegalStateException if niceSurrounding was not set by the user
	 * so the class cannot return <b>true</b> or <b>false</b>
	 */
	public boolean getNiceSurrounding() throws IllegalStateException {

		if (this.niceSurrounding == -1) {
			throw new IllegalStateException(
				"NiceSurrounding value was never set for: " + this);
		}

		if (niceSurrounding == 1) {
			return true;
		} else {
			return false;
		}

	}		
	
	// -------------------------------------------------------------------
	/**
	 * Returns the color of the main star of this DeepSkyFindingDS.<br>
	 * See DeepSkyFindingDS constants for valid color values.<br>
	 * 
	 * @return A string describing the color of the main star
	 * or <code>null</code> if the value was never set
	 */	
	public String getColorMain() {
		
		return this.colorMain;
		
	}
	
	// -------------------------------------------------------------------
	/**
	 * Returns the color of the companion star of this DeepSkyFindingDS.<br>
	 * See DeepSkyFindingDS constants for valid color values.<br>
	 * 
	 * @return A string describing the color of the companion star
	 * or <code>null</code> if the value was never set
	 */	
	public String getColorCompanion() {
		
		return this.colorCompanion;
		
	}	
	
	// -------------------------------------------------------------------
	/**
	 * Sets the equalBrightness value of this DeepSkyFindingDS.<br>
	 * Describes if both stars have an equal brightness.
	 * 
	 * @param equalBrightness The equalBrightness value to set for this DeepSkyFindingDS
	 * or <code>NULL</code> if the value should be not set at all.
	 */
	public void setEqualBrightness(Boolean equalBrightness) {

		if( equalBrightness == null ) {
			this.equalBrightness = -1;
			return;
		}
		
		if (equalBrightness.booleanValue() == true) {
			this.equalBrightness = 1;
		} else {
			this.equalBrightness = 0;
		}

	}	
	
	// -------------------------------------------------------------------
	/**
	 * Sets the niceSurrounding value of this DeepSkyFindingDS.<br>
	 * Describes if the observed object has a nice surrounding.
	 * 
	 * @param niceSurrounding The niceSurrounding value to set for this DeepSkyFindingDS
	 * or <code>NULL</code> if the value should be not set at all.
	 */
	public void setNiceSurrounding(Boolean niceSurrounding) {

		if( niceSurrounding == null ) {
			this.niceSurrounding = -1;
			return;
		}
		
		if (niceSurrounding.booleanValue() == true) {
			this.niceSurrounding = 1;
		} else {
			this.niceSurrounding = 0;
		}

	}
		

	// -------------------------------------------------------------------
	/**
	 * Sets the color of the main star of this DeepSkyFindingDS.<br>
	 * See DeepSkyFindingDS constants for valid color values.<br>
	 * 
	 * @param color The color value to set for this DeepSkyFindingDS
	 * or <code>NULL</code> if the value should be not set at all.
	 * @throws IllegalArgumentException if the given color value is invalid
	 */
	public void setMainStarColor(String color) throws IllegalArgumentException {
		
		if( color == null  ) {
			this.colorMain = null;
			return;
		}
		
		if(   DeepSkyFindingDS.COLOR_BLUE.equals(color)
		   || DeepSkyFindingDS.COLOR_GREEN.equals(color)
		   || DeepSkyFindingDS.COLOR_ORANGE.equals(color)
		   || DeepSkyFindingDS.COLOR_RED.equals(color)
		   || DeepSkyFindingDS.COLOR_WHITE.equals(color)
		   || DeepSkyFindingDS.COLOR_YELLOW.equals(color)
		   ) {
			this.colorMain = color;	
		} else {
			throw new IllegalArgumentException("Main star color value is not valid.\n");
		}
		
	}
	
	// -------------------------------------------------------------------
	/**
	 * Sets the color of the companion star of this DeepSkyFindingDS.<br>
	 * See DeepSkyFindingDS constants for valid color values.<br>
	 * 
	 * @param color The color value to set for this DeepSkyFindingDS
	 * or <code>NULL</code> if the value should be not set at all.
	 * @throws IllegalArgumentException if the given color value is invalid
	 */
	public void setCompanionStarColor(String color) throws IllegalArgumentException {
		
		if( color == null  ) {
			this.colorCompanion = null;
			return;
		}
		
		if(   DeepSkyFindingDS.COLOR_BLUE.equals(color)
		   || DeepSkyFindingDS.COLOR_GREEN.equals(color)
		   || DeepSkyFindingDS.COLOR_ORANGE.equals(color)
		   || DeepSkyFindingDS.COLOR_RED.equals(color)
		   || DeepSkyFindingDS.COLOR_WHITE.equals(color)
		   || DeepSkyFindingDS.COLOR_YELLOW.equals(color)
		   ) {
			this.colorCompanion = color;	
		} else {
			throw new IllegalArgumentException("Companion star color value is not valid.\n");
		}
		
	}	
	
}
