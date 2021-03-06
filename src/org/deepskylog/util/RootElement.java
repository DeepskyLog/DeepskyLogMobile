/* ====================================================================
 * /RootElement.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */
 
 
package org.deepskylog.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.deepskylog.util.util.SchemaException;


/**
 * The RootElement element is the root element of a
 * schema element. All other schema elements are
 * grouped below RootElement.<br>
 * The object itself contains no astronomical data but 
 * provides XML namespaces, and schema element containers.
 * A schema element container groups multiple
 * schema elements of one and the same type.<br>
 * E.g.<br>
 * <observers><br>
 *   <observer><br>
 *     <name>Foo</name><br>
 *     <i>More observer stuff goes here</i><br>
 *   </observer><br>
 *   <observer><br>
 *     <name>Foo</name><br>
 *     <i>More observer stuff goes here</i><br>
 *   </observer><br>
 * </observers><br>
 * In this example <observers> is the container
 * element of multiple <observer> elements.<br>
 * Also the RootElement object contains the
 * serializeToSchema() method, that will create
 * a schema valid XML file.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class RootElement {

	// ---------
	// Constants ---------------------------------------------------------
	// ---------

    // XML Namespace of schema Key
    public static final String XML_NS_KEY = "xmlns:oal";

	// XML Namespace of schema
	public static final String XML_NS = "http://groups.google.com/group/openastronomylog";


    // XML SI Schema Key 
    public static final String XML_SI_KEY = "xmlns:xsi";

	// XML SI Schema 
	public static final String XML_SI = "http://www.w3.org/2001/XMLSchema-instance";
    

    // XML Schema Location Key
    public static final String XML_SCHEMA_LOCATION_KEY = "xsi:schemaLocation";

	// XML Schema Location 
	public static final String XML_SCHEMA_LOCATION = "http://groups.google.com/group/openastronomylog oal21.xsd";

    // XML Schema Version Key
    public static final String XML_SCHEMA_VERSION_KEY = "version";

    // XML Schema Location 
    public static final String XML_SCHEMA_VERSION = "2.1";



	// Schema container for <observation> objects
	public static final String XML_OBSERVATION_CONTAINER = "oal:observations";

	// Schema container for <session> objects
	public static final String XML_SESSION_CONTAINER = "sessions";

	// Schema container for <target> objects
	public static final String XML_TARGET_CONTAINER = "targets";
	
	// Schema container for <observer> objects
	public static final String XML_OBSERVER_CONTAINER = "observers";
	
	// Schema container for <site> objects
	public static final String XML_SITE_CONTAINER = "sites";	

	// Schema container for <scope> objects
	public static final String XML_SCOPE_CONTAINER = "scopes";

	// Schema container for <eyepiece> objects
	public static final String XML_EYEPIECE_CONTAINER = "eyepieces";

	// Schema container for <imager> objects
	public static final String XML_IMAGER_CONTAINER = "imagers";

	// Schema container for <filter> objects
	public static final String XML_FILTER_CONTAINER = "filters";	

	// Schema container for <lens> objects
	public static final String XML_LENS_CONTAINER = "lenses";		
	

	
	
	// ------------------
	// Instance Variables ------------------------------------------------
	// ------------------

	// All obervation objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList observationList = new ArrayList();

	// All observer objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList observerList = new ArrayList();

	// All site objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList siteList = new ArrayList();
	
	// All scope objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList scopeList = new ArrayList();
	
	// All eyepiece objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList eyepieceList = new ArrayList();
	
	// All imager objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList imagerList = new ArrayList();	
	
	// All session objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList sessionList = new ArrayList();
	
	// All target objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList targetList = new ArrayList();	

	// All filter objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList filterList = new ArrayList();	
	
	// All lens objects belonging to this RootElement group
	@SuppressWarnings("rawtypes")
	private ArrayList lensList = new ArrayList();
	
	
	
	// --------------
	// Public methods ----------------------------------------------------
	// --------------

    // -------------------------------------------------------------------  
    @SuppressWarnings("rawtypes")
	public Collection getObservations() {
        
        return this.observationList;
        
    }

    // -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Collection getEyepieceList() {
		
		return this.eyepieceList;
		
	}
	
    // -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Collection getImagerList() {
		
		return this.imagerList;
		
	}	
	
    // -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Collection getObserverList() {
		
		return this.observerList;
		
	}
	
    // -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Collection getScopeList() {
		
		return this.scopeList;
		
	}
	
    // -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Collection getSessionList() {
		
		return this.sessionList;
		
	}

    // -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Collection getSiteList() {
		
		return this.siteList;
		
	}
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("rawtypes")
	public Collection getTargetList() {
		
		return this.targetList;
		
	}    

    // -------------------------------------------------------------------	
	@SuppressWarnings("rawtypes")
	public Collection getFilterList() {
		
		return this.filterList;
		
	}    	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("rawtypes")
	public Collection getLensList() {
		
		return this.lensList;
		
	} 	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "unchecked" })
	public void addObservation(IObservation observation) throws SchemaException {
		
		if( observation != null ) {
			observationList.add(observation);
		} else {
			throw new SchemaException("Observation cannot be null. ");
		}
		
	}

    // -------------------------------------------------------------------	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addObservations(Collection observations) throws SchemaException {
		
		if( observations != null ) {
			observationList.addAll(observations);
		} else {
			throw new SchemaException("Observations cannot be null. ");
		}
		
	}	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "unchecked" })
	public void addEyepiece(IEyepiece eyepiece) throws SchemaException {
		
		if( eyepiece != null ) {
			this.eyepieceList.add(eyepiece);
		} else {
			throw new SchemaException("Eyepiece cannot be null. ");
		}
		
	}
		
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addEyepieces(Collection eyepieces) throws SchemaException {
		
		if( eyepieces != null ) {
			this.eyepieceList.addAll(eyepieces);
		} else {
			throw new SchemaException("Eyepieces cannot be null. ");
		}
		
	}	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addImager(IImager imager) throws SchemaException {
		
		if( imager != null ) {
			this.imagerList.add(imager);
		} else {
			throw new SchemaException("Imager cannot be null. ");
		}
		
	}		
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addImagers(Collection imagers) throws SchemaException {
		
		if( imagers != null ) {
			this.imagerList.addAll(imagers);
		} else {
			throw new SchemaException("Imagers cannot be null. ");
		}
		
	}		
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addSite(ISite site) throws SchemaException {
		
		if( site != null ) {
			this.siteList.add(site);
		} else {
			throw new SchemaException("Site cannot be null. ");
		}
		
	}
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addSites(Collection sites) throws SchemaException {
		
		if( sites != null ) {
			this.siteList.addAll(sites);
		} else {
			throw new SchemaException("Sites cannot be null. ");
		}
		
	}	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addScope(IScope scope) throws SchemaException {
		
		if( scope != null ) {
			this.scopeList.add(scope);
		} else {
			throw new SchemaException("Scope cannot be null. ");
		}
		
	}
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addScopes(Collection scopes) throws SchemaException {
		
		if( scopes != null ) {
			this.scopeList.addAll(scopes);
		} else {
			throw new SchemaException("Scopes cannot be null. ");
		}
		
	}	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addSession(ISession session) throws SchemaException {
		
		if( session != null ) {
			this.sessionList.add(session);
		} else {
			throw new SchemaException("Session cannot be null. ");
		}
		
	}
		
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addSessions(Collection sessions) throws SchemaException {
		
		if( sessions != null ) {
			this.sessionList.addAll(sessions);
		} else {
			throw new SchemaException("Sessions cannot be null. ");
		}
		
	}	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addObserver(IObserver observer) throws SchemaException {
		
		if( observer != null ) {
			this.observerList.add(observer);
		} else {
			throw new SchemaException("Observer cannot be null. ");
		}
		
	}
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addObservers(Collection observers) throws SchemaException {
		
		if( observers != null ) {
			this.observerList.addAll(observers);
		} else {
			throw new SchemaException("Observers cannot be null. ");
		}
		
	}	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addTarget(ITarget target) throws SchemaException {
		
		if( target != null ) {
			this.targetList.add(target);
		} else {
			throw new SchemaException("Target cannot be null. ");
		}
		
	}		
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addTargets(Collection targets) throws SchemaException {
		
		if( targets != null ) {
			this.targetList.addAll(targets);
		} else {
			throw new SchemaException("Targets cannot be null. ");
		}
		
	}		

    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addFilter(IFilter filter) throws SchemaException {
		
		if( filter != null ) {
			this.filterList.add(filter);
		} else {
			throw new SchemaException("Filter cannot be null. ");
		}
		
	}		
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addFilters(Collection filters) throws SchemaException {
		
		if( filters != null ) {
			this.filterList.addAll(filters);
		} else {
			throw new SchemaException("Filters cannot be null. ");
		}
		
	}	
	
    // -------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	public void addLens(ILens lens) throws SchemaException {
		
		if( lens != null ) {
			this.lensList.add(lens);
		} else {
			throw new SchemaException("Lens cannot be null. ");
		}
		
	}		
	
    // -------------------------------------------------------------------	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addLenses(Collection lenses) throws SchemaException {
		
		if( lenses != null ) {
			this.lensList.addAll(lenses);
		} else {
			throw new SchemaException("Lenses cannot be null. ");
		}
		
	}		
	
	// -------------------------------------------------------------------	
	public void serializeAsXml(File xmlFile) throws SchemaException {
		
        if( xmlFile == null ) {
            throw new SchemaException("File cannot be null. "); 
        }
        
		Document newSchema = this.getDocument();
		
		// Create OutputFormat for document, with default encoding (UTF-8) and pretty print (intent)
		OutputFormat outputFormat = new OutputFormat(newSchema,
                                                     "ISO-8859-1",
                                                     true);        
		XMLSerializer serializer = new XMLSerializer(outputFormat);
		try {
			FileOutputStream fos = new FileOutputStream(xmlFile);
			serializer.setOutputByteStream(fos);			
            serializer.serialize(newSchema);   
            fos.close();
		} catch(FileNotFoundException fnfe) {            
            throw new SchemaException("File not found: " + xmlFile.getAbsolutePath());
		} catch(IOException ioe) {
			throw new SchemaException("Error while serializing. Nested Exception is: ", ioe);            
       	}
		
	}

    @SuppressWarnings("rawtypes")
	public Document getDocument() throws SchemaException {
    
		Document newSchema = null;
		try {
			newSchema = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();			
		} catch(ParserConfigurationException pce) {
			throw new SchemaException("Unable to create new XML document. ", pce);
		}
		
		Element root = newSchema.createElement(RootElement.XML_OBSERVATION_CONTAINER);
        root.setAttribute(XML_NS_KEY, XML_NS);
        root.setAttribute(XML_SI_KEY, XML_SI);        
        root.setAttribute(XML_SCHEMA_LOCATION_KEY, XML_SCHEMA_LOCATION);
        root.setAttribute(XML_SCHEMA_VERSION_KEY, XML_SCHEMA_VERSION);

        newSchema.appendChild(root);
		
		// Add not linked elements
		// Don't change this as otherwise E&T cannot load the schema :-)
		root = this.addObserverToXmlElement(root);
		root = this.addSiteToXmlElement(root);		
		root = this.addSessionToXmlElement(root);
		root = this.addTargetToXmlElement(root);				
		root = this.addScopeToXmlElement(root);
		root = this.addEyepieceToXmlElement(root);
		root = this.addLensToXmlElement(root);
		root = this.addFilterToXmlElement(root);	
		root = this.addImagerToXmlElement(root);	
	        
        // This will persist all elements that are used by the observations
        // so almost all elements, except the not linked elements
		Iterator iterator = observationList.iterator();
		IObservation current = null;		
		while( iterator.hasNext() ) {
			current = (IObservation)iterator.next();
			current.addToXmlElement(root);
		}		
		
		return newSchema;
    	
    }	
    
	@SuppressWarnings("rawtypes")
	private Element addEyepieceToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Eyepiece = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_EYEPIECE_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Eyepiece = ownerDoc.createElement(RootElement.XML_EYEPIECE_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Eyepiece);
        } else {
        	e_Eyepiece = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.eyepieceList.iterator();
		IEyepiece current = null;        
		while( iterator.hasNext() ) {
			current = (IEyepiece)iterator.next();
			current.addToXmlElement(e_Eyepiece);			
		}		
		
		return root;
		
	}
	
	@SuppressWarnings("rawtypes")
	private Element addImagerToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Imager = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_IMAGER_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Imager = ownerDoc.createElement(RootElement.XML_IMAGER_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Imager);
        } else {
        	e_Imager = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.imagerList.iterator();
		IImager current = null;        
		while( iterator.hasNext() ) {
			current = (IImager)iterator.next();
			current.addToXmlElement(e_Imager);			
		}		
		
		return root;
		
	}	
	
	@SuppressWarnings("rawtypes")
	private Element addSiteToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Site = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SITE_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Site = ownerDoc.createElement(RootElement.XML_SITE_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Site);
        } else {
        	e_Site = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.siteList.iterator();
		ISite current = null;        
		while( iterator.hasNext() ) {
			current = (ISite)iterator.next();
			current.addToXmlElement(e_Site);			
		}		
		
		return root;		
		
	}
	
	@SuppressWarnings("rawtypes")
	private Element addObserverToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Observer = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_OBSERVER_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Observer = ownerDoc.createElement(RootElement.XML_OBSERVER_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Observer);
        } else {
        	e_Observer = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.observerList.iterator();
		IObserver current = null;        
		while( iterator.hasNext() ) {
			current = (IObserver)iterator.next();
			current.addToXmlElement(e_Observer);			
		}		
		
		return root;		
		
	}
	
	@SuppressWarnings("rawtypes")
	private Element addTargetToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Target = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Target = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Target);
        } else {
        	e_Target = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.targetList.iterator();
		ITarget current = null;        
		while( iterator.hasNext() ) {
			current = (ITarget)iterator.next();
			current.addToXmlElement(e_Target);			
		}		
		
		return root;
		
	}
	
	@SuppressWarnings("rawtypes")
	private Element addFilterToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Filter = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_FILTER_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Filter = ownerDoc.createElement(RootElement.XML_FILTER_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Filter);
        } else {
        	e_Filter = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.filterList.iterator();
		IFilter current = null;        
		while( iterator.hasNext() ) {
			current = (IFilter)iterator.next();
			current.addToXmlElement(e_Filter);			
		}		
		
		return root;
		
	}	
	
	@SuppressWarnings("rawtypes")
	private Element addLensToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Lens = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_LENS_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Lens = ownerDoc.createElement(RootElement.XML_LENS_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Lens);
        } else {
        	e_Lens = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.lensList.iterator();
		ILens current = null;        
		while( iterator.hasNext() ) {
			current = (ILens)iterator.next();
			current.addToXmlElement(e_Lens);			
		}		
		
		return root;
		
	}		
	
	@SuppressWarnings("rawtypes")
	private Element addSessionToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Session = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SESSION_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Session = ownerDoc.createElement(RootElement.XML_SESSION_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Session);
        } else {
        	e_Session = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.sessionList.iterator();
		ISession current = null;        
		while( iterator.hasNext() ) {
			current = (ISession)iterator.next();
			current.addToXmlElement(e_Session);			
		}		
		
		return root;		
		
	}
	
	@SuppressWarnings("rawtypes")
	private Element addScopeToXmlElement(Element root) {
		
    	if( root == null ) {
    		return null;
    	}
    
        Document ownerDoc = root.getOwnerDocument();				
		
        // Get or create the container element        
        Element e_Scope = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SCOPE_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
        	e_Scope = ownerDoc.createElement(RootElement.XML_SCOPE_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Scope);
        } else {
        	e_Scope = (Element)nodeList.item(0);  // there should be only one container element
        }		
		
		Iterator iterator = this.scopeList.iterator();
		IScope current = null;        
		while( iterator.hasNext() ) {
			current = (IScope)iterator.next();
			current.addToXmlElement(e_Scope);			
		}		
		
		return root;						
		
	}	
}
