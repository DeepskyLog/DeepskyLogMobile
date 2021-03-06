/* ====================================================================
 * extension/deepSky/DeepSkyFinding.java
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

import org.deepskylog.util.Angle;
import org.deepskylog.util.Finding;
import org.deepskylog.util.IFinding;
import org.deepskylog.util.util.SchemaException;

/**
 * DeepSkyFinding extends the de.lehmannet.om.Finding class.
 * Its specialised for DeepSky observations and their findings.
 * A DeepSky object can be an astronomical object outside our
 * solar system.
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
 * <td>99</td>
 * <td>unknown</td> 
 * </tr>
 * </table> 
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
@SuppressLint("UseValueOf")
public class DeepSkyFinding extends Finding {

	// ---------
	// Constants ---------------------------------------------------------
	// ---------

	// XSML schema instance value. Enables class/schema loaders to identify this
	// class
	public static final String XML_XSI_TYPE_VALUE = "oal:findingsDeepSkyType";

	// Constant for XML representation: finding element attribute stellar
	public static final String XML_ELEMENT_FINDING_ATTRIBUTE_STELLAR = "stellar";

	// Constant for XML representation: finding element attribute resolved
	public static final String XML_ELEMENT_FINDING_ATTRIBUTE_RESOLVED =	"resolved";

	// Constant for XML representation: finding element attribute mottled
	public static final String XML_ELEMENT_FINDING_ATTRIBUTE_MOTTLED = "mottled";

	// Constant for XML representation: finding element attribute extended
	public static final String XML_ELEMENT_FINDING_ATTRIBUTE_EXTENDED = "extended";	
	
	// Constant for XML representation: smallDiameter element name
	public static final String XML_ELEMENT_SMALLDIAMETER = "smallDiameter";

	// Constant for XML representation: largeDiameter element name
	public static final String XML_ELEMENT_LARGEDIAMETER = "largeDiameter";

	// Constant for XML representation: rating element name
	public static final String XML_ELEMENT_RATING = "rating";

	
	
	
	// ------------------
	// Instance Variables ------------------------------------------------
	// ------------------

	// 1 if the object seems to be stellar
	// 0 if the object did not appear stellar
	// -1 the value was not set
	private int stellar = -1;

	// 1 if the object could be resolved
	// 0 if the object could not be resolved
	// -1 the value was not set    
	private int resolved = -1;

	// 1 if the object could be seen with structures
	// 0 if no structures could be seen
	// -1 the value was not set
	private int mottled = -1;

	// 1 if the object could was extended
	// 0 if the object wasn't extended
	// -1 the value was not set
	private int extended = -1;	
	
	// The small diameter of the object (only positiv values allowed)
	private Angle smallDiameter = null;

	// The large diameter of the object (only positiv values allowed)
	private Angle largeDiameter = null;

	// The visuell rating (after VdS - Deep Sky scale). 
	// See http://www.naa.net/deepsky/download/dsl_einfuehrung_v70.pdf for more information)
	private int rating = -1;

	
	
	
	// ------------
	// Constructors ------------------------------------------------------
	// ------------

	@SuppressLint("UseValueOf")
	public DeepSkyFinding(Node findingElement) throws SchemaException {

		super(findingElement);

		Element finding = (Element) findingElement;
		Element child = null;
		NodeList children = null;

		// Getting data
		// First mandatory stuff and down below optional data

		// Get mandatory rating
		children = finding.getElementsByTagName(DeepSkyFinding.XML_ELEMENT_RATING);
		if ((children == null) || (children.getLength() != 1)) {
			//throw new SchemaException("DeepSkyFinding must have exact one rating value. ");
			this.setRating(99);   // Use unknown rating value since 2.0
		} else {
			child = (Element) children.item(0);
			String rating = null;
			if (child == null) {			
				throw new SchemaException("DeepSkyFinding must have a rating value. ");
			} else {
				rating = child.getFirstChild().getNodeValue();
				this.setRating(Integer.parseInt(rating));
			}
		}

		// Get optional small diameter
		children = finding.getElementsByTagName(DeepSkyFinding.XML_ELEMENT_SMALLDIAMETER);
		Angle smallDiameter = null;
		if (children != null) {
			if (children.getLength() == 1) {
				child = (Element) children.item(0);
				String value = child.getFirstChild().getNodeValue();
				String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
				smallDiameter = new Angle(Double.parseDouble(value), unit);
				this.setSmallDiameter(smallDiameter);
			} else if( children.getLength() > 1 ) {
				throw new SchemaException("DeepSkyFinding can only have one small diameter entry. ");
			}
		}

		// Get optional large diameter
		children = finding.getElementsByTagName(DeepSkyFinding.XML_ELEMENT_LARGEDIAMETER);
		Angle largeDiameter = null;
		if (children != null) {
			if (children.getLength() == 1) {
				child = (Element) children.item(0);
				String value = child.getFirstChild().getNodeValue();
				String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
				largeDiameter = new Angle(Double.parseDouble(value), unit);
				this.setLargeDiameter(largeDiameter);
            } else if( children.getLength() > 1 ) {
				throw new SchemaException("DeepSkyFinding can only have one large diameter entry. ");
			}
		}

		// Get optional resolved attribute
		String resolved = finding.getAttribute(DeepSkyFinding.XML_ELEMENT_FINDING_ATTRIBUTE_RESOLVED);
        if(   (resolved != null)
           && (!"".equals(resolved.trim())) 
            ) {
			this.setResolved(new Boolean(resolved));
		}

		// Get optional stellar attribute
		String stellar =
			finding.getAttribute(
				DeepSkyFinding.XML_ELEMENT_FINDING_ATTRIBUTE_STELLAR);
        if(   (stellar != null)
           && (!"".equals(stellar.trim())) 
            ) {
			this.setStellar(new Boolean(stellar));
		}

		// Get optional mottled attribute
		String mottled =
			finding.getAttribute(
				DeepSkyFinding.XML_ELEMENT_FINDING_ATTRIBUTE_MOTTLED);
		if(   (mottled != null)
           && (!"".equals(mottled.trim())) 
            ) {
			this.setMottled(new Boolean(mottled));
		}

		// Get optional extended attribute
		String extended =
			finding.getAttribute(
				DeepSkyFinding.XML_ELEMENT_FINDING_ATTRIBUTE_EXTENDED);
		if(   (extended != null)
           && (!"".equals(extended.trim())) 
            ) {
			this.setExtended(new Boolean(extended));
		}		
		
	}

	// -------------------------------------------------------------------
	/**
	 * Constructs a new instance of a DeepSkyFinding.
	 * 
	 * @param description The description of the finding
	 * @param rating The rating of the finding
	 * @throws IllegalArgumentException if description was <code>null</code>
	 * or rating had a illegal value.
	 */
	public DeepSkyFinding(String description, int rating)
		throws IllegalArgumentException {

		super(description);

		this.setRating(rating);

	}

	
	
	
	// -------------
	// SchemaElement -----------------------------------------------------
	// -------------
    
    
	// -------------------------------------------------------------------
	/**
	 * Returns a display name for this element.<br>
	 * The method differs from the toString() method as toString() shows
	 * more technical information about the element. Also the formating of
	 * toString() can spread over several lines.<br>
	 * This method returns a string (in one line) that can be used as 
	 * displayname in e.g. a UI dropdown box.
	 * 
	 * @return Returns a String with a one line display name
	 * @see java.lang.Object.toString();
	 */ 		    	    
	public String getDisplayName() {
		
		return this.getDescription().substring(0, 7);
		
	}	
	
	
	
	
	// ------
	// Object ------------------------------------------------------------
	// ------

	// -------------------------------------------------------------------
	/**
	 * Overwrittes toString() method from java.lang.Object.<br>
	 * Returns the field values of this DeepSkyFinding.
	
	 * @return This DeepSkyValue field values
	 * @see java.lang.Object
	 */
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("DeepSkyFinding: Description=");
		buffer.append(super.getDescription());
		buffer.append(" Rating=");
		buffer.append(rating);

		if (stellar != -1) {
			buffer.append(" Stellar=");
			buffer.append(this.getStellar());
		}

		if (resolved != -1) {
			buffer.append(" Resolved=");
			buffer.append(this.getResolved());
		}

		if (mottled != -1) {
			buffer.append(" Mottled=");
			buffer.append(this.getMottled());
		}

		if (extended != -1) {
			buffer.append(" Extended=");
			buffer.append(this.getExtended());
		}		
		
		if (smallDiameter != null) {
			buffer.append(" small Diameter=");
			buffer.append(smallDiameter);
		}

		if (largeDiameter != null) {
			buffer.append(" large Diameter=");
			buffer.append(largeDiameter);
		}

		return buffer.toString();

	}

	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if this DeepSkyFinding and the given Object are equal. Two DeepSkyFindings
	 * are equal if both return the same string from their toString() method and their
	 * XSI type is equal.<br>
	 * @param obj The Object to compare this DeepSkyFinding with.
	 * @return <code>true</code> if both Objects are instances from class DeepSkyFinding,
	 * both XSI types are equal and
	 * their fields contain the same values. (Can be checked with calling and comparing 
	 * both objects toString() method)
	 * @see java.lang.Object
	 */
	public boolean equals(Object obj) {

		if (obj == null || !(obj instanceof DeepSkyFinding)) {
			return false;
		}

		// As we overwritte the toString() method and access all fields there,
		// two DeepSkyFindings are equal, if both objects return the same string 
		// from their toString() method.
		if (   (this.toString().equals(obj.toString()))
			&& (this.getXSIType().equals(((DeepSkyFinding)obj).getXSIType()))
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

		return DeepSkyFinding.XML_XSI_TYPE_VALUE;

	}

	
	
	
	// -------
	// Finding -----------------------------------------------------------
	// -------

	// -------------------------------------------------------------------
	/**
	 * Adds this DeepSkyFinding to an given parent XML DOM Element.
	 * The DeepSkyFinding Element will be set as a child element of
	 * the passed Element.
	 * 
	 * @param parent The parent element for this DeepSkyFinding
	 * @return Returns the Element given as parameter with this 
	 * DeepSkyFinding as child Element.<br>
	 * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */
	public Element addToXmlElement(Element parent) {

		if (parent == null) {
			return null;
		}

		Element e_Finding = this.createXmlFindingElement(parent);
		
		parent.appendChild(e_Finding);

		return parent;

	}

	
	
	
	// --------------
	// Public methods ----------------------------------------------------
	// --------------
	
	// -------------------------------------------------------------------       
	/**
	 * Returns the large visible diameter of the observed object.
	 * 
	 * @return The large visible diameter of the object as Angle.<br>
	 *         Might return <code>null</code> if large diameter was never set.
	 * @see de.lehmannet.om.Angle
	 */
	public Angle getLargeDiameter() {

		return largeDiameter;

	}

	// -------------------------------------------------------------------
	/**
	 * Returns the mottled value of this DeepSkyFinding.<br>
	 * A observed object is mottled when it can be seen with at least
	 * some structures.
	 * 
	 * @return <code>true</code> if the observed object could be
	 * seen with structures
	 * @throws IllegalStateException if mottled was not set by the user
	 * so the class cannot return <b>true</b> or <b>false</b>
	 */
	public boolean getMottled() throws IllegalStateException {

		if (mottled == -1) {
			throw new IllegalStateException(
				"Mottled value was never set for: " + this);
		}

		if (mottled == 1) {
			return true;
		} else {
			return false;
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Returns the rating of the observed object.<br>
	 * The rating scale is described at 
	 * <a href="http://www.naa.net/deepsky/">VdS - DeepSky Group</a>.
	 * A valid rating value is an integer between 1 and 7 or 99 if the value was unknown,
	 * for e.g. historical reasons. 
	 * 
	 * @return The rating that was given to the DeepSkyFinding during 
	 * observation
	 */
	public int getRating() {

		return rating;

	}

	// -------------------------------------------------------------------
	/**
	 * Returns <code>true</code> if the observed object could be seen resolved 
	 * during observation.<br>
	 * 
	 * @return <code>true</code> if the observed object could be seen
	 * resolved
	 * @throws IllegalStateException if resolved was not set by the user
	 * so the class cannot return <b>true</b> or <b>false</b>
	 */
	public boolean getResolved() throws IllegalStateException {

		if (resolved == -1) {
			throw new IllegalStateException(
				"Resolved value was never set for: " + this);
		}

		if (resolved == 1) {
			return true;
		} else {
			return false;
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Returns the small visible diameter of the observed object.
	 * 
	 * @return The small visible diameter of the object as Angle
	 * Might return <code>null</code> if large diameter was never set.
	 * @see de.lehmannet.om.Angle
	 */
	public Angle getSmallDiameter() {

		return smallDiameter;

	}

	// -------------------------------------------------------------------
	/**
	 * Returns <code>true</code> if the observed object appeard stellar
	 * during observation.
	 * 
	 * @return <code>true</code> if the observed object appeard stellar
	 * @throws IllegalStateException if stellar was not set by the user,
	 * so the class cannot return <b>true</b> or <b>false</b>
	 */
	public boolean getStellar() throws IllegalStateException {

		if (stellar == -1) {
			throw new IllegalStateException(
				"Stellar value was never set for: " + this);
		}

		if (stellar == 1) {
			return true;
		} else {
			return false;
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Returns <code>true</code> if the observed object appeard extended
	 * during observation.
	 * 
	 * @return <code>true</code> if the observed object appeard extended
	 * @throws IllegalStateException if extended was not set by the user,
	 * so the class cannot return <b>true</b> or <b>false</b>
	 */
	public boolean getExtended() throws IllegalStateException {

		if (extended == -1) {
			throw new IllegalStateException(
				"Extended value was never set for: " + this);
		}

		if (extended == 1) {
			return true;
		} else {
			return false;
		}

	}	
	
	// -------------------------------------------------------------------
	/**
	 * Sets the large visible diameter of the observed object.
	 * The passed Angle needs to have a positiv value.
	 * If the Angles value is negativ, the large diameter will not be set
	 * and the method returns <code>false</code>.
	 * 
	 * @param largeDiameter The large diameters (positiv) angle 
	 * @return <code>true</code> if the angle was set successfully. If 
	 * <code>false</code> is returned the angles value might have been 
	 * nagativ. 
	 * If <code>false</code> is returned the large diameter isn't set
	 * @see de.lehmannet.om.Angle
	 */
	public boolean setLargeDiameter(Angle largeDiameter) {

		if( largeDiameter != null ) {
			if( largeDiameter.getValue() < 0.0) {
				return false;
			}
		}

		this.largeDiameter = largeDiameter;

		return true;

	}

	// -------------------------------------------------------------------
	/**
	 * Sets the mottled value of this DeepSkyFinding.<br>
	 * A observed object is mottled when it can be seen with at least
	 * some structures.
	 * 
	 * @param mottled The mottled value to set for this DeepSkyFinding
	 * or <code>NULL</code> if the value should be not set at all.
	 */
	public void setMottled(Boolean mottled) {

		if( mottled == null ) {
			this.mottled = -1;
			return;
		}
		
		if (mottled.booleanValue() == true) {
			this.mottled = 1;
		} else {
			this.mottled = 0;
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Sets the rating of the observed object.<br>
	 * The rating scale is described at
	 * <a href="http://www.naa.net/deepsky/download/dsl_einfuehrung_v70.pdf">VdS - DeepSky Group</a>
	 * A valid rating value is an integer between 1 and 7 (including 1 and 7) or 99 if the value
	 * is unknown (should only be used for e.g. migration of old observations).
	 * If any other value is passed to the method throws an IllegalArgumentException.
	 * Explaination of rating scale:<br>
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
	 * <td>99</td>
	 * <td>unknown</td> 
	 * </tr>
	 * </table>
	 * 
	 * @param rating The rating value to set for this DeepSkyFinding.
	 * A valid rating value is an integer between 1 and 7 or 99 for unknown
	 * @throws IllegalArgumentException if rating > 7 or < 1 and also not 99
	 */
	public void setRating(int rating) throws IllegalArgumentException {

		if ((rating > 7) || (rating < 1)) {
			if( rating != 99 ) {
				throw new IllegalArgumentException("Rating value cannot be smaller then 1 or greater then 7. Or it must be 99 for unknown value.");	
			}			
		}

		if( rating == 7 ) {  // Object was not seen
			super.setSeen(false);
		} else {
			super.setSeen(true);  // Object was seen
		}
		
		this.rating = rating;

	}

	// -------------------------------------------------------------------
	/**
	 * Sets the resolved value for this DeepSkyFinding.<br>
	 * The value should be <code>true</code> if the observed
	 * object could be seen resolved during observation. 
	 * 
	 * @param resolved The resolved value for this DeepSkyFinding
	 * or <code>NULL</code> if the value should be not set at all.
	 */
	public void setResolved(Boolean resolved) {

		if( resolved == null ) {
			this.resolved = -1;
			return;
		}
		
		if (resolved.booleanValue() == true) {
			this.resolved = 1;
		} else {
			this.resolved = 0;
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Sets the small visible diameter of the observed object.
	 * The passed Angle needs to have a positiv value.
	 * If the Angles value is negativ, the small diameter will not be set
	 * and the method returns <code>false</code>.
	 * 
	 * @param smallDiameter The small diameters (positiv) angle 
	 * @return <code>true</code> if the angle was set successfully. If 
	 * <code>false</code> is returned the angles value might have been 
	 * nagativ 
	 * If <code>false</code> is returned the small diameter isn't set
	 * @see de.lehmannet.om.Angle
	 */
	public boolean setSmallDiameter(Angle smallDiameter) {

		if( smallDiameter != null ) {
			if( smallDiameter.getValue() < 0.0f ) {
				return false;
			}			
		}

		this.smallDiameter = smallDiameter;

		return true;

	}

	// -------------------------------------------------------------------
	/**
	 * Sets the stellar value for this DeepSkyFinding.<br>
	 * The value should be <code>true</code> if the observed object
	 * could only be seen stellar during observation. 
	 * 
	 * @param stellar The stellar value to set for this DeepkSkyFinding
	 * or <code>NULL</code> if the value should be not set at all.
	 */
	public void setStellar(Boolean stellar) {

		if( stellar == null ) {
			this.stellar = -1;
			return;
		}
		
		if (stellar.booleanValue() == true) {
			this.stellar = 1;
		} else {
			this.stellar = 0;
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Sets the extended value for this DeepSkyFinding.<br>
	 * The value should be <code>true</code> if the observed object
	 * could be seen extended during observation. 
	 * 
	 * @param extended The extended value to set for this DeepkSkyFinding
	 * or <code>NULL</code> if the value should be not set at all.
	 */
	public void setExtended(Boolean extended) {

		if( extended == null ) {
			this.extended = -1;
			return;
		}
		
		if (extended.booleanValue() == true) {
			this.extended = 1;
		} else {
			this.extended = 0;
		}

	}	
	

	
	
	// -----------------
	// Protected methods -------------------------------------------------
	// -----------------
	
	// -------------------------------------------------------------------	
	protected Element createXmlFindingElement(Element parent) {
				
		Document ownerDoc = parent.getOwnerDocument();

		Element e_Finding = super.createXmlFindingElement(parent);

		// Set XSI:Type
		e_Finding.setAttribute(IFinding.XML_XSI_TYPE,
							   DeepSkyFinding.XML_XSI_TYPE_VALUE);

		if (smallDiameter != null) {
			Element e_SmallDiameter =
				ownerDoc.createElement(XML_ELEMENT_SMALLDIAMETER);
			e_SmallDiameter = smallDiameter.setToXmlElement(e_SmallDiameter);

			e_Finding.appendChild(e_SmallDiameter);
		}

		if (largeDiameter != null) {
			Element e_LargeDiameter =
				ownerDoc.createElement(XML_ELEMENT_LARGEDIAMETER);
			e_LargeDiameter = largeDiameter.setToXmlElement(e_LargeDiameter);

			e_Finding.appendChild(e_LargeDiameter);
		}

		Element e_Rating = ownerDoc.createElement(XML_ELEMENT_RATING);
		Node n_RatingText = ownerDoc.createTextNode(Integer.toString(rating));
		e_Rating.appendChild(n_RatingText);
		e_Finding.appendChild(e_Rating);		
		
		if (stellar != -1) {
			e_Finding.setAttribute(
				XML_ELEMENT_FINDING_ATTRIBUTE_STELLAR,
				Boolean.toString(this.getStellar()));
		}

		if (resolved != -1) {
			e_Finding.setAttribute(
				XML_ELEMENT_FINDING_ATTRIBUTE_RESOLVED,
				Boolean.toString(this.getResolved()));
		}

		if (mottled != -1) {
			e_Finding.setAttribute(
				XML_ELEMENT_FINDING_ATTRIBUTE_MOTTLED,
				Boolean.toString(this.getMottled()));
		}

		if (extended != -1) {
			e_Finding.setAttribute(
				XML_ELEMENT_FINDING_ATTRIBUTE_EXTENDED,
				Boolean.toString(this.getExtended()));
		}				

		//return parent;
		return e_Finding;
		
	}
	
}