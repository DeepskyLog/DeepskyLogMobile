/* ====================================================================
 * /Session.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package org.deepskylog.util;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.deepskylog.util.util.DateConverter;
import org.deepskylog.util.util.SchemaException;

/**
 * A Session can be used to link several observations together. Typically a session would describe an observation night,
 * where several observations took place. Therefore an Session requires two mandatory fields: a start date and an end
 * date. All observations of the session should have a start date that is inbetween the sessions start and end date.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class Session extends SchemaElement implements ISession {

  // ------------------
  // Instance Variables ------------------------------------------------
  // ------------------

  // Start date of session
  private Calendar begin = Calendar.getInstance();

  // End date of session
  private Calendar end = Calendar.getInstance();

  // Site where session took place
  private ISite site = null;

  // Weather conditions of session
  private String weather = null;

  // Equipment used during session
  private String equipment = null;

  // Session comments
  private String comments = null;

  // Coobservers of the session
  @SuppressWarnings("rawtypes")
private LinkedList coObservers = new LinkedList();

  // Language (since 1.5)
  private String lang = null;

  // Relative paths to an image (list of String) (since 2.1)
  /**
   * @since 2.1
   */
  @SuppressWarnings("rawtypes")
private List images = new LinkedList();

  // ------------
  // Constructors ------------------------------------------------------
  // ------------

  // -------------------------------------------------------------------
  /**
   * Constructs a new Session instance from a given XML Schema Node. Normally this constructor is only used by
   * de.lehmannet.om.util.SchemaLoader<br>
   * Please mind: As a Session can have <coObserver> elements that link to <observer> elements somewhere else in the
   * xml, this method requires an array of IObservers to check whether the <coObserver> elements link to existing
   * <Obsever>s. If a Session Node has no <coObserver> elements, the second parameter can be <code>null</code>
   * 
   * @param session
   *          the XML Schema Node that represents this Session object
   * @param observers
   *          Needed if the Session Node has <coObserver> elements.
   * @throws IllegalArgumentException
   *           if parameter session is <code>null</code> or Node has <coObserver> elements, but no observer array way
   *           passed (or array was empty)
   * @throws SchemaException
   *           if the given Node does not match the XML Schema specifications
   */
  @SuppressWarnings("null")
public Session(Node session, IObserver[] observers, ISite[] sites) throws SchemaException, IllegalArgumentException {

    if (session == null) {
      throw new IllegalArgumentException("Parameter session node cannot be NULL. ");
    }

    // Cast to element as we need some methods from it
    Element sessionElement = (Element) session;

    // Helper classes
    Element child = null;
    NodeList children = null;

    // Getting data
    // First mandatory stuff and down below optional data

    // Get mandatory ID
    String ID = sessionElement.getAttribute(SchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
    if ((ID != null) && ("".equals(ID.trim()))) {
      throw new SchemaException("Session must have a ID. ");
    }
    super.setID(ID);

    // Get mandatory begin date
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_BEGIN);
    if ((children == null) || (children.getLength() != 1)) {
      throw new SchemaException("Session must have exact one begin date. ");
    }
    child = (Element) children.item(0);
    Calendar begin = null;
    if (child == null) {
      throw new SchemaException("Session must have begin date. ");
    } else {
      String ISO8601Begin = null;
      if (child.getFirstChild() != null) {
        ISO8601Begin = child.getFirstChild().getNodeValue();
      } else {
        throw new SchemaException("Session cannot have an empty begin date. ");
      }
      try {
        begin = DateConverter.toDate(ISO8601Begin);
        this.setBegin(begin);
      } catch (NumberFormatException nfe) {
        throw new SchemaException("Begin date is malformed. ", nfe);
      }
    }

    // Get mandatory end date
    child = null;
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_END);
    if ((children == null) || (children.getLength() != 1)) {
      throw new SchemaException("Session must have exact one end date. ");
    }
    child = (Element) children.item(0);
    Calendar end = null;
    if (child == null) {
      throw new SchemaException("Session must have end date. ");
    } else {
      String ISO8601End = child.getFirstChild().getNodeValue();
      try {
        end = DateConverter.toDate(ISO8601End);
        this.setEnd(end);
      } catch (NumberFormatException nfe) {
        throw new SchemaException("End date is malformed. ", nfe);
      }
    }

    // Get mandatory site
    child = null;
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_SITE);
    if ((children == null) || (children.getLength() != 1)) {
      throw new SchemaException("Session must have exact one site. ");
    }
    child = (Element) children.item(0);
    if (child == null) {
      throw new SchemaException("Session must have a site. ");
    } else {
      String siteID = child.getFirstChild().getNodeValue();
      if ((sites != null) || (sites.length > 0)) {
        boolean found = false;
        for (int j = 0; j < sites.length; j++) {
          if (sites[j].getID().equals(siteID)) {
            found = true;
            this.setSite(sites[j]);
            break;
          }
        }
        if (found == false) {
          throw new SchemaException("Sessions site links to not existing observer element. ");
        }
      } else {
        throw new IllegalArgumentException("Parameter ISite array is NULL or empty. ");
      }
    }

    // Get optional coObserver link
    child = null;
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_COOBSERVER);
    if (children != null) {
      for (int x = 0; x < children.getLength(); x++) {
        child = (Element) children.item(x);
        if (child != null) {
          String coObserverID = child.getFirstChild().getNodeValue();
          if ((observers != null) || (observers.length > 0)) {
            boolean found = false;
            for (int j = 0; j < observers.length; j++) {
              if (observers[j].getID().equals(coObserverID)) {
                found = true;
                this.addCoObserver(observers[j]);
                break;
              }
            }
            if (found == false) {
              throw new SchemaException("Sessions coobserver links to not existing observer element. ");
            }
          } else {
            throw new IllegalArgumentException("Parameter IObserver array is NULL or empty. ");
          }
        } else {
          throw new SchemaException("Problem retrieving coObserver from session. ");
        }
      }
    }

    // Get optional weather
    child = null;
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_WEATHER);
    String weather = "";
    if (children != null) {
      if (children.getLength() == 1) {
        child = (Element) children.item(0);
        if (child != null) {
          NodeList textElements = child.getChildNodes();
          if ((textElements != null) && (textElements.getLength() > 0)) {
            for (int te = 0; te < textElements.getLength(); te++) {
              weather = weather + textElements.item(te).getNodeValue();
            }
            // weather = child.getFirstChild().getNodeValue();
            this.setWeather(weather);
          }
        } else {
          throw new SchemaException("Problem while retrieving weather from session. ");
        }
      } else if (children.getLength() > 1) {
        throw new SchemaException("Session can have only one weather entry. ");
      }
    }

    // Get optional equipment
    child = null;
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_EQUIPMENT);
    String equipment = "";
    if (children != null) {
      if (children.getLength() == 1) {
        child = (Element) children.item(0);
        if (child != null) {
          NodeList textElements = child.getChildNodes();
          if ((textElements != null) && (textElements.getLength() > 0)) {
            for (int te = 0; te < textElements.getLength(); te++) {
              equipment = equipment + textElements.item(te).getNodeValue();
            }
            // equipment = child.getFirstChild().getNodeValue();
            this.setEquipment(equipment);
          }
        } else {
          throw new SchemaException("Problem while retrieving equipment from session. ");
        }
      } else if (children.getLength() > 1) {
        throw new SchemaException("Session can have only one equipment entry. ");
      }
    }

    // Get optional comments
    child = null;
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_COMMENTS);
    String comments = "";
    if (children != null) {
      if (children.getLength() == 1) {
        child = (Element) children.item(0);
        if (child != null) {
          // comments = child.getFirstChild().getNodeValue();
          NodeList textElements = child.getChildNodes();
          if ((textElements != null) && (textElements.getLength() > 0)) {
            for (int te = 0; te < textElements.getLength(); te++) {
              comments = comments + textElements.item(te).getNodeValue();
            }
            this.setComments(comments);
          }
        } else {
          throw new SchemaException("Problem while retrieving comment from session. ");
        }
      } else if (children.getLength() > 1) {
        throw new SchemaException("Session can have only one comment entry. ");
      }
    }

    // Get optional language
    String language = sessionElement.getAttribute(ISession.XML_ELEMENT_ATTRIBUTE_LANGUAGE);
    if ((language != null) && !("".equals(language.trim()))) {
      this.setLanguage(language);
    }

    // Get optional images
    child = null;
    children = sessionElement.getElementsByTagName(ISession.XML_ELEMENT_IMAGE);
    String image = "";
    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        child = (Element) children.item(i);
        if (child != null) {
          NodeList textElements = child.getChildNodes();
          if ((textElements != null) && (textElements.getLength() > 0)) {
            for (int te = 0; te < textElements.getLength(); te++) {
              image = image + textElements.item(te).getNodeValue();
            }
            // Make sure to use the local file separator character during runtime
            // When saving the file, we'll convert it back to /
            image = image.replace('/', File.separatorChar);
            image = image.replace('\\', File.separatorChar);
            this.addImage(image);
            image = "";
          }
          // image = child.getFirstChild().getNodeValue();
        } else {
          throw new SchemaException("Problem while retrieving image of session. ");
        }
      }
    }

  }

  // -------------------------------------------------------------------
  /**
   * Constructs a new instance of a Session.
   * 
   * @param begin
   *          The start date of the session
   * @param end
   *          The end date of the session
   * @param end
   *          The site of the session
   * @throws IllegalArgumentException
   *           if site, begin or end date is <code>null</code>
   */
  public Session(Calendar begin, Calendar end, ISite site) throws IllegalArgumentException {

    this.setBegin(begin);
    this.setEnd(end);
    this.setSite(site);

  }

  // -------------
  // SchemaElement -----------------------------------------------------
  // -------------

  // -------------------------------------------------------------------
  /**
   * Returns a display name for this element.<br>
   * The method differs from the toString() method as toString() shows more technical information about the element.
   * Also the formating of toString() can spread over several lines.<br>
   * This method returns a string (in one line) that can be used as displayname in e.g. a UI dropdown box.
   * 
   * @return Returns a String with a one line display name
   * @see java.lang.Object.toString();
   */
  public String getDisplayName() {

    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    df.setCalendar(this.getBegin());
    String result = df.format(this.getBegin().getTime()) + " - ";
    df.setCalendar(this.getEnd());
    result = result + df.format(this.getEnd().getTime());

    return result;

  }

  // ------
  // Object ------------------------------------------------------------
  // ------

  // -------------------------------------------------------------------
  /**
   * Overwrittes toString() method from java.lang.Object.<br>
   * Returns the field values of this Session.
   * 
   * @return This Sessions field values
   * @see java.lang.Object
   */
  @SuppressWarnings("rawtypes")
public String toString() {

    StringBuffer buffer = new StringBuffer();
    buffer.append("Session: Begin=");
    buffer.append(DateConverter.toISO8601(begin));

    buffer.append(" End=");
    buffer.append(DateConverter.toISO8601(end));

    buffer.append(" Site=");
    buffer.append(this.site);

    if (weather != null) {
      buffer.append(" Weather=");
      buffer.append(weather);
    }

    if (equipment != null) {
      buffer.append(" Equipment=");
      buffer.append(equipment);
    }

    if (comments != null) {
      buffer.append(" Comments=");
      buffer.append(comments);
    }

    if (coObservers != null) {
      buffer.append(" coobservers=");
      buffer.append(coObservers);
    }

    if ((this.images != null) && (this.images.size() > 0)) {
      buffer.append(" Images=");
      ListIterator imageIterator = images.listIterator();
      while (imageIterator.hasNext()) {
        buffer.append(imageIterator.next());
        if (imageIterator.hasNext()) {
          buffer.append(", ");
        }
      }
    }

    return buffer.toString();

  }

  // -------------------------------------------------------------------
  /**
   * Overwrittes equals(Object) method from java.lang.Object.<br>
   * Checks if this Session and the given Object are equal. The given object is equal with this Session, if it derives
   * from ISession and if its start and end date equals this Sessions start and end date.<br>
   * 
   * @param obj
   *          The Object to compare this Session with.
   * @return <code>true</code> if the given Object is an instance of ISession and its start and end date equals this
   *         Sessions start and end date.<br>
   * @see java.lang.Object
   */
  /*
   * public boolean equals(Object obj) {
   * 
   * if( obj == null || !(obj instanceof ISession) ) { return false; }
   * 
   * ISession session = (ISession)obj;
   * 
   * if( !begin.equals(session.getBegin()) ) { return false; }
   * 
   * if( !end.equals(session.getEnd()) ) { return false; }
   * 
   * if( !site.equals(session.getSite()) ) { return false; }
   * 
   * return false;
   * 
   * }
   */

  // --------------
  // Public methods ----------------------------------------------------
  // --------------

  // -------------------------------------------------------------------
  /**
   * Adds this Session to a given parent XML DOM Element. The Session element will be set as a child element of the
   * passed element.
   * 
   * @param parent
   *          The parent element for this Session
   * @return Returns the element given as parameter with this Session as child element.<br>
   *         Might return <code>null</code> if parent was <code>null</code>.
   * @see org.w3c.dom.Element
   */
  @SuppressWarnings("rawtypes")
public Element addToXmlElement(Element element) {

    if (element == null) {
      return null;
    }

    Document ownerDoc = element.getOwnerDocument();

    // Check if this element doesn't exist so far
    NodeList nodeList = element.getElementsByTagName(ISession.XML_ELEMENT_SESSION);
    if (nodeList.getLength() > 0) {
      Node currentNode = null;
      NamedNodeMap attributes = null;
      for (int i = 0; i < nodeList.getLength(); i++) { // iterate over all found nodes
        currentNode = nodeList.item(i);
        attributes = currentNode.getAttributes();
        Node idAttribute = attributes.getNamedItem(SchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if ((idAttribute != null) // if ID attribute is set and equals this objects ID, return existing element
            && (idAttribute.getNodeValue().trim().equals(super.getID().trim()))) {
          return element;
        }
      }
    }

    // Create the new session element
    Element e_Session = ownerDoc.createElement(XML_ELEMENT_SESSION);
    e_Session.setAttribute(XML_ELEMENT_ATTRIBUTE_ID, super.getID());

    element.appendChild(e_Session);

    Element e_Begin = ownerDoc.createElement(XML_ELEMENT_BEGIN);
    Node n_BeginText = ownerDoc.createTextNode(DateConverter.toISO8601(begin));
    e_Begin.appendChild(n_BeginText);
    e_Session.appendChild(e_Begin);

    Element e_End = ownerDoc.createElement(XML_ELEMENT_END);
    Node n_EndText = ownerDoc.createTextNode(DateConverter.toISO8601(end));
    e_End.appendChild(n_EndText);
    e_Session.appendChild(e_End);

    site.addAsLinkToXmlElement(e_Session);

    if ((coObservers != null) && !(coObservers.isEmpty())) {
      ListIterator iterator = coObservers.listIterator();
      IObserver coObserver = null;
      while (iterator.hasNext()) {
        coObserver = (IObserver) iterator.next();

        coObserver.addAsLinkToXmlElement(e_Session, ISession.XML_ELEMENT_COOBSERVER);
      }
    }

    if (weather != null) {
      Element e_Weather = ownerDoc.createElement(XML_ELEMENT_WEATHER);
      Node n_WeatherText = ownerDoc.createCDATASection(this.weather);
      e_Weather.appendChild(n_WeatherText);
      e_Session.appendChild(e_Weather);
    }

    if (equipment != null) {
      Element e_Equipment = ownerDoc.createElement(XML_ELEMENT_EQUIPMENT);
      Node n_EquipmentText = ownerDoc.createCDATASection(this.equipment);
      e_Equipment.appendChild(n_EquipmentText);
      e_Session.appendChild(e_Equipment);
    }

    if (comments != null) {
      Element e_Comments = ownerDoc.createElement(XML_ELEMENT_COMMENTS);
      Node n_CommentsText = ownerDoc.createCDATASection(this.comments);
      e_Comments.appendChild(n_CommentsText);
      e_Session.appendChild(e_Comments);
    }

    if ((this.images != null) && (!this.images.isEmpty())) {
      ListIterator imagesIterator = this.images.listIterator();
      Element e_currentImage = null;
      Node n_ImageText = null;
      String path = null;
      while (imagesIterator.hasNext()) {
        e_currentImage = ownerDoc.createElement(XML_ELEMENT_IMAGE);
        // Always write image path with / separators. While loading from XML, convert back to \ if necessary
        path = (String) imagesIterator.next();
        path = path.replace('\\', '/');
        n_ImageText = ownerDoc.createCDATASection(path);
        e_currentImage.appendChild(n_ImageText);
        e_Session.appendChild(e_currentImage);
      }
    }

    if (this.lang != null) {
      e_Session.setAttribute(ISession.XML_ELEMENT_ATTRIBUTE_LANGUAGE, this.lang);
    }

    return element;

  }

  // -------------------------------------------------------------------
  /**
   * Adds the session link to an given XML DOM Element The session element itself will be attached to given elements
   * ownerDocument if the passed boolean is <cod>true</code>. If the ownerDocument has no session container, it will be
   * created (in case the passed boolean is <cod>true</code>).<br>
   * Example:<br>
   * &lt;parameterElement&gt;<br>
   * <b>&lt;sessionLink&gt;123&lt;/sessionLink&gt;</b><br>
   * &lt;/parameterElement&gt;<br>
   * <i>More stuff of the xml document goes here</i><br>
   * <b>&lt;sessionContainer&gt;</b><br>
   * <b>&lt;session id="123"&gt;</b><br>
   * <i>session description goes here</i><br>
   * <b>&lt;/session&gt;</b><br>
   * <b>&lt;/sessionContainer&gt;</b><br>
   * <br>
   * 
   * @param element
   *          The element under which the the Session link is created
   * @param addElementToContainer
   *          if <code>true</code> it's ensured that the linked element exists in the corresponding container element.
   *          Please note, passing <code>true</code> slowes down XML serialization.
   * @return Returns the Element given as parameter with a additional Session link, and the session element under the
   *         session container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
   * @see org.w3c.dom.Element
   * @since 2.0
   */
  public Element addAsLinkToXmlElement(Element element, boolean addElementToContainer) {

    if (element == null) {
      return null;
    }

    Document ownerDoc = element.getOwnerDocument();

    // Create the link element
    Element e_Link = ownerDoc.createElement(XML_ELEMENT_SESSION);
    Node n_LinkText = ownerDoc.createTextNode(super.getID());
    e_Link.appendChild(n_LinkText);

    element.appendChild(e_Link);

    if (addElementToContainer) {
      // Get or create the container element
      Element e_Sessions = null;
      NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SESSION_CONTAINER);
      boolean created = false;
      if (nodeList.getLength() == 0) { // we're the first element. Create container element
        e_Sessions = ownerDoc.createElement(RootElement.XML_SESSION_CONTAINER);
        created = true;
      } else {
        e_Sessions = (Element) nodeList.item(0); // there should be only one container element
      }

      e_Sessions = this.addToXmlElement(e_Sessions);

      // If container element was created, add container here so that XML sequence fits forward references
      // Calling the appendChild in the if avbe would cause the session container to be located before
      // observers and sites container
      if (created) {
        ownerDoc.getDocumentElement().appendChild(e_Sessions);
      }
    }

    return element;

  }

  // -------------------------------------------------------------------
  /**
   * Adds the session link to an given XML DOM Element The session element itself will <b>NOT</b> be attached to given
   * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with parameters
   * <code>element, false</code><br>
   * Example:<br>
   * &lt;parameterElement&gt;<br>
   * <b>&lt;sessionLink&gt;123&lt;/sessionLink&gt;</b><br>
   * &lt;/parameterElement&gt;<br>
   * <br>
   * 
   * @param element
   *          The element under which the the session link is created
   * @return Returns the Element given as parameter with a additional session link Might return <code>null</code> if
   *         element was <code>null</code>.
   * @see org.w3c.dom.Element
   */
  public Element addAsLinkToXmlElement(Element element) {

    return this.addAsLinkToXmlElement(element, false);

  }

  // -------------------------------------------------------------------
  /**
   * Returns the start date of the session.<br>
   * 
   * @return Returns the start date of the session
   */
  public Calendar getBegin() {

    return (Calendar) begin.clone();

  }

  // -------------------------------------------------------------------
  /**
   * Returns a comment about this session.<br>
   * Might return <code>null</code> if no comment was set to this session.
   * 
   * @return Returns a comment about this session or <code>null</code> if no comment was set at all.
   */
  public String getComments() {

    return comments;

  }

  // -------------------------------------------------------------------
  /**
   * Returns the end date of the session.<br>
   * 
   * @return Returns the end date of the session
   */
  public Calendar getEnd() {

    return (Calendar) end.clone();

  }

  // -------------------------------------------------------------------
  /**
   * Returns the site of the session.<br>
   * 
   * @return Returns the site of the session
   */
  public ISite getSite() {

    return this.site;

  }

  // -------------------------------------------------------------------
  /**
   * Returns a string describing equipment which was used during this session.<br>
   * Typically one should add non optical equipment here like "Radio and a warm bottle of Tea."<br>
   * Might return <code>null</code> if no equipment was set to this session.
   * 
   * @return Returns string describing some equipment which was used during the session or <code>null</code> if no
   *         additional equipment was used at all.
   */
  public String getEquipment() {

    return equipment;

  }

  // -------------------------------------------------------------------
  /**
   * Returns a describtion of the weather conditions during the session.<br>
   * Might return <code>null</code> if no weather conditions were added to this session.
   * 
   * @return Returns a describtion of the weather conditions during the session or <code>null</code> if no weather
   *         conditions were added at all.
   */
  public String getWeather() {

    return weather;

  }

  // -------------------------------------------------------------------
  /**
   * Returns the language in which this session is described as ISO language string. E.g. de=German, fr=French, ...<br>
   * Might return <code>null</code> if no language was set for this session.
   * 
   * @return Returns a ISO language code that represents the sessions describtion language or <code>null</code> if no
   *         language was set at all.
   * @since 1.5
   */
  public String getLanguage() {

    return this.lang;

  }

  // -------------------------------------------------------------------
  /**
   * Returns a list of images (relativ path to images), taken at this session. Might return <code>null</code> if images
   * were set.
   * 
   * @return List of images or <code>null</code> if no images were set.
   */
  @SuppressWarnings("rawtypes")
public List getImages() {

    if ((this.images == null) || (this.images.isEmpty())) {
      return null;
    }

    return this.images;

  }

  // -------------------------------------------------------------------
  /**
   * Sets the start date of the session.<br>
   * 
   * @param begin
   *          The new start date of the session.
   * @throws IllegalArgumentException
   *           if new start date is <code>null</code>
   */
  public void setBegin(Calendar begin) throws IllegalArgumentException {

    if (begin == null) {
      throw new IllegalArgumentException("Start date cannot be null. ");
    }

    this.begin = (Calendar) begin.clone();

  }

  // -------------------------------------------------------------------
  /**
   * Sets a comment to the session.<br>
   * The old comment will be overwritten.
   * 
   * @param comments
   *          A new comment for the session
   */
  public void setComments(String comments) {

    if ((comments != null) && ("".equals(comments.trim()))) {
      this.comments = null;
      return;
    }

    this.comments = comments;

  }

  // -------------------------------------------------------------------
  /**
   * Sets the end date of the session.<br>
   * 
   * @param end
   *          The new end date of the session.
   * @throws IllegalArgumentException
   *           if new end date is <code>null</code>
   */
  public void setEnd(Calendar end) throws IllegalArgumentException {

    if (end == null) {
      throw new IllegalArgumentException("End date cannot be null. ");
    }

    this.end = (Calendar) end.clone();

  }

  // -------------------------------------------------------------------
  /**
   * Sets a equipment description to the session.<br>
   * Typically non optical equipment will should be stored here, e.g. "Red LED light and bottle of hot tea."<br>
   * The old equipment will be overwritten.
   * 
   * @param equipment
   *          The new equipment of the session
   */
  public void setEquipment(String equipment) {

    if ((equipment != null) && ("".equals(equipment.trim()))) {
      this.equipment = null;
      return;
    }

    this.equipment = equipment;

  }

  // -------------------------------------------------------------------
  /**
   * Sets a site (location) where the session took place.<br>
   * A session can only took place at one site.
   * 
   * @param site
   *          The site where the session took place.
   * @throws IllegalArgumentException
   *           if site is <code>null</code>
   */
  public void setSite(ISite site) throws IllegalArgumentException {

    if (site == null) {
      throw new IllegalArgumentException("Site cannot be null. ");
    }

    this.site = site;

  }

  // -------------------------------------------------------------------
  /**
   * Sets a new List of coobservers to this session.<br>
   * The old List of coobservers will be overwritten. If you want to add one ore more coobservers to the existing list
   * use addCoObservers(java.util.List) or addCoObserver(IObserver) instead.
   * 
   * @param coObservers
   *          The new List of coobservers of the session
   * @return <b>true</b> if the list could be set successfully, <b>false</b> if the operation fails, because e.g. one of
   *         the lists elements does not implement the IObserver interface. If <b>false</b> is returned the existing
   *         list is not changed at all.
   */
  @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public boolean setCoObservers(List coObservers) {

    if (coObservers == null) {
      this.coObservers = null;
      return true;
    }

    // Check if every single entry is a IObserver implementation (this might be a little paranoic...ok)
    ListIterator iterator = coObservers.listIterator();
    IObserver current = null;
    if (iterator.hasNext()) {
      try {
        current = (IObserver) iterator.next();
      } catch (ClassCastException cce) {
        return false;
      }

    }

    this.coObservers.clear();
    this.coObservers.addAll(coObservers);
    return true;

  }

  // -------------------------------------------------------------------
  /**
   * Adds a List of coobservers to this session.<br>
   * The old List of coobservers will be extended by the new List of coobservers.
   * 
   * @param coObservers
   *          A List of coobservers which will be added to the existing List of coobservers which is stored in the
   *          session
   * @return <b>true</b> if the list could be added to the existing list, <b>false</b> if the operation fails, because
   *         e.g. one of the lists elements does not implement the IObserver interface. If <b>false</b> is returned the
   *         existing list is not changed at all.
   */
  @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public boolean addCoObservers(List coObservers) {

    if (coObservers == null) {
      return true;
    }

    // Check if every single entry is a IObserver implementation (this might be a little paranoic...ok)
    ListIterator iterator = coObservers.listIterator();
    IObserver current = null;
    if (iterator.hasNext()) {
      try {
        current = (IObserver) iterator.next();
      } catch (ClassCastException cce) {
        return false;
      }
    }

    this.coObservers.addAll(coObservers);
    return true;

  }

  // -------------------------------------------------------------------
  /**
   * Adds a single coobserver to this session.<br>
   * 
   * @param coObserver
   *          A new coobserver who will be addded to the List of coobservers
   * @return <b>true</b> if the new coobserver could be added, <b>false</b> if the operation fails, because e.g. the
   *         given IObserver is <code>null</code>
   */
  @SuppressWarnings("unchecked")
public boolean addCoObserver(IObserver coObserver) {

    if (coObserver == null) {
      return false;
    }

    this.coObservers.add(coObserver);
    return true;

  }

  // -------------------------------------------------------------------
  /**
   * Returns a List of coobservers who joined this session.<br>
   * Might return <code>null</code> if no coobservers were added to this session.
   * 
   * @return Returns a List of coobserver or <code>null</code> if coobservers were never added.
   */
  @SuppressWarnings("rawtypes")
public List getCoObservers() {

    return this.coObservers;

  }

  // -------------------------------------------------------------------
  /**
   * Sets the weather conditions of the session.<br>
   * The weather conditions string should explain in some short sentences, how the weather conditions were like during
   * the session. E.g. "Small clouds at the first hour but then totally clear and cool, at about 4\u00b0C."
   * 
   * @param weather
   *          A string describing the weather conditions during the session
   */
  public void setWeather(String weather) {

    if ((weather != null) && ("".equals(weather.trim()))) {
      this.weather = null;
      return;
    }

    this.weather = weather;

  }

  // -------------------------------------------------------------------
  /**
   * Sets the language in which this session is described. String must be given as ISO language string. E.g. de=German,
   * fr=French, ...<br>
   * 
   * @param language
   *          ISO language string
   * @since 1.5
   */
  public void setLanguage(String language) {

    if ((language != null) && ("".equals(language.trim()))) {
      this.lang = null;
      return;
    }

    this.lang = language;

  }

  // -------------------------------------------------------------------
  /**
   * Adds a List of image paths (String) to this session.<br>
   * The new list of images will be added to the existing list of images belonging to this session. If you want to
   * replace the old images list use setImages(java.util.List).<br>
   * If the new list of images was successfully added to the old images list, the method will return <b>true</b>. If the
   * list is empty or <code>null</code>, the old result list will remain unchanged.
   * 
   * @param images
   *          A list (containing Strings) with additional images (path) for this session
   * @return <b>true</b> if the given list was successfully added to this session images list. <b>false</b> if the new
   *         list could not be added and the old list remains unchanged.
   * @see de.lehmannet.om.ISession#setResults(java.util.List images)
   */
  @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public boolean addImages(List images) {

    if ((images == null) || (images.isEmpty())) {
      return false;
    }

    // Check if every single entry is a String (this might be a little paranoic...ok)
    ListIterator iterator = images.listIterator();
    String current = null;
    while (iterator.hasNext()) {
      try {
        current = (String) iterator.next();
      } catch (ClassCastException cce) {
        return false;
      }
    }

    this.images.addAll(images);
    return true;

  }

  // -------------------------------------------------------------------
  /**
   * Adds a new image (path) to this session.<br>
   * 
   * @param imagePath
   *          A new image for this session
   */
  @SuppressWarnings("unchecked")
public void addImage(String imagePath) {

    if (imagePath == null) {
      return;
    }

    this.images.add(imagePath);

  }

  // -------------------------------------------------------------------
  /**
   * Sets a List of images (path as String) for this session.<br>
   * The old list of images will be overwritten. If you want to add one ore more images to the existing ones use
   * addImages(java.util.List) or addImage(String).<br>
   * If the new list of images was successfully attached to this session, the method will return <b>true</b>. If one of
   * the elements in the list isn't a java.lang.String object <b>false</b> is returned.<br>
   * If the new list is <code>null</code>, an IllegalArgumentException is thrown.
   * 
   * @param imagesList
   *          The new (String) list of images for this session
   * @return <b>true</b> if the given list was successfully set to this session. <b>false</b> if one of the lists
   *         elements wasn't a String
   * @see de.lehmannet.om.ISession#addImages(java.util.List images)
   * @see de.lehmannet.om.ISession#addImage(String image)
   * @throws IllegalArgumentException
   *           if new image list is <code>null</code>
   */
  @SuppressWarnings({ "rawtypes", "unused" })
public boolean setImages(List imagesList) throws IllegalArgumentException {

    if (imagesList == null) {
      throw new IllegalArgumentException("Images list cannot be null. ");
    }

    if (imagesList.isEmpty()) {
      this.images.clear();
      return true;
    }

    // Check if every single entry is a String (this might be a little paranoic...ok)
    ListIterator iterator = imagesList.listIterator();
    String current = null;
    if (iterator.hasNext()) {
      try {
        current = (String) iterator.next();
      } catch (ClassCastException cce) {
        return false;
      }
    }

    this.images = imagesList;
    return true;

  }

}
