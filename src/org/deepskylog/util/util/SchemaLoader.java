/* ====================================================================
 * /util/SchemaLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */
 
package org.deepskylog.util.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.deepskylog.util.Eyepiece;
import org.deepskylog.util.Filter;
import org.deepskylog.util.IEyepiece;
import org.deepskylog.util.IFilter;
import org.deepskylog.util.IFinding;
import org.deepskylog.util.IImager;
import org.deepskylog.util.ILens;
import org.deepskylog.util.IObservation;
import org.deepskylog.util.IObserver;
import org.deepskylog.util.IScope;
import org.deepskylog.util.ISession;
import org.deepskylog.util.ISite;
import org.deepskylog.util.ITarget;
import org.deepskylog.util.Lens;
import org.deepskylog.util.OALException;
import org.deepskylog.util.Observation;
import org.deepskylog.util.Observer;
import org.deepskylog.util.RootElement;
import org.deepskylog.util.Scope;
import org.deepskylog.util.Session;
import org.deepskylog.util.Site;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * The SchemaLoader provides loading facilities to
 * load (parse) a XML Schema file.<br> 
 * You can see this as a Factory of the Schema 
 * Objects.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class SchemaLoader {

    // ---------
    // Constants --------------------------------------------------------------
    // ---------
	
	// XML Schema Filenames
	public static final String[] VERSIONS = new String[] {"comast14.xsd", "comast15.xsd", "comast16.xsd", "comast17.xsd", "oal20.xsd", "oal21.xsd"};
	
	
	
	
    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Array of all obervations that have been found in the XML Document
	private IObservation[] observations = null;

    // Array of all session that have been found in the XML Document
	private ISession[] sessions = null;
    
    // Array of all targets that have been found in the XML Document    
	private ITarget[] targets = null;
    
    // Array of all observers that have been found in the XML Document    
	private IObserver[] observers = null;
    
    // Array of all sites that have been found in the XML Document    
	private ISite[] sites = null;
    
    // Array of all scopes that have been found in the XML Document    
	private IScope[] scopes = null;
    
    // Array of all eyepieces that have been found in the XML Document    
	private IEyepiece[] eyepieces = null;

    // Array of all filters that have been found in the XML Document    
	private IFilter[] filters = null;	
	
    // Array of all imager that have been found in the XML Document    
	private IImager[] imagers = null;
	
    // Array of all lenses that have been found in the XML Document    
	private ILens[] lenses = null;		
	
	// Add doublicate catalog targets in here
	// Key is the doublicate target entry, value is the "new" target which 
	// will be used to replace the doublicate target in the corsp. observations
    @SuppressWarnings("rawtypes")
	private HashMap doublicateTargets = new HashMap(); 
    
    // List of additional classLoader which can be used to find classes using reflection
    @SuppressWarnings("rawtypes")
	private static ArrayList extensionClassLoaders = new ArrayList();
	
	

    // ---------------------
    // Public Static Methods ---------------------------------------------
    // ---------------------

    // -------------------------------------------------------------------  
    /**
     * Gets a ITarget object (e.g. DeepSkyTarget) from a given xsiType.
     * 
     * @param xsiType The unique xsi:Type that identifies the object/element
     * @param currentNode The XML Node that represents the object 
     * e.g. <target>...</target>
     * @param observers A array of Observers that are needed to instanciate
     * a object of type Target
     * @return A ITarget that represents the given node as Java object
     * @throws SchemaException if the given node is not well formed according
     * to the Schema specifications
     */       
    public static ITarget getTargetFromXSIType(String xsiType,
                                              Node currentNode,
                                              IObserver[] observers) throws SchemaException {
    
        return (ITarget)SchemaLoader.getObjectFromXSIType(xsiType, currentNode, observers, SchemaElementConstants.TARGET);        
    
    }

    // -------------------------------------------------------------------  
    /**
     * Gets a IFinding object (e.g. DeepSkyFinding) from a given xsiType.
     * 
     * @param xsiType The unique xsi:Type that identifies the object/element
     * @param currentNode The XML Node that represents the object 
     * e.g. <result>...</result>
     * @return A IFinding that represents the given node as Java object
     * @throws SchemaException if the given node is not well formed according
     * to the Schema specifications
     */       
    public static IFinding getFindingFromXSIType(String xsiType,
                                                 Node currentNode) throws SchemaException {
    
        return (IFinding)SchemaLoader.getObjectFromXSIType(xsiType, currentNode, null, SchemaElementConstants.FINDING);        
    
    }
    
    
    // -------------------------------------------------------------------  
    /**
     * Gets a IImager object (e.g. CCDImager) from a given xsiType.
     * 
     * @param xsiType The unique xsi:Type that identifies the object/element
     * @param currentNode The XML Node that represents the object 
     * e.g. <imager>...</imager>
     * @return A IImager that represents the given node as Java object
     * @throws SchemaException if the given node is not well formed according
     * to the Schema specifications
     */       
    public static IImager getImagerFromXSIType(String xsiType,
                                               Node currentNode) throws SchemaException {
    
        return (IImager)SchemaLoader.getObjectFromXSIType(xsiType, currentNode, null, SchemaElementConstants.IMAGER);        
    
    }
    
    
    
    
    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    // -------------------------------------------------------------------
    public IObservation[] getObservations() {
    	   	
    	return this.observations;
    	
    }
    
    // -------------------------------------------------------------------
    public ISession[] getSessions() {
    	
    	return this.sessions;
    	
    }
    
    // -------------------------------------------------------------------
    public ITarget[] getTargets() {
    	
    	return this.targets;
    	
    }
    
    // -------------------------------------------------------------------
    public IObserver[] getObservers() {
    	
    	return this.observers;
    	
    }
    
    // -------------------------------------------------------------------
    public ISite[] getSites() {
    	
    	return this.sites;
    	
    }
    
    // -------------------------------------------------------------------
    public IScope[] getScopes() {
    	
    	return this.scopes;
    	
    }
	
    // -------------------------------------------------------------------
    public IEyepiece[] getEyepieces() {
    	
    	return this.eyepieces;
    	
    }	       

    // -------------------------------------------------------------------
    public IFilter[] getFilters() {
    	
    	return this.filters;
    	
    }	           

    // -------------------------------------------------------------------
    public ILens[] getLenses() {
    	
    	return this.lenses;
    	
    }	            
    
    // -------------------------------------------------------------------
    public IImager[] getImagers() {
    	
    	return this.imagers;
    	
    }    
    
    // -------------------------------------------------------------------
    /**
     * Loads/parses a XML File
     * 
     * @param schemaPath The path to the XML Schemas
     * @throws OALException if schema File cannot be accessed
     * @throws SchemaException if XML File is not valid 
     */    	
	@SuppressWarnings("unused")
	public RootElement load(FileInputStream xmlFile,
			                File schemaPath) throws OALException, 
	                                         		SchemaException {
		
        // Check if file is OK
		if(    (xmlFile == null)
		   ) {
			throw new OALException("XML file is null, does not exist or is directory. ");
		}
		
		File schemaFile = this.getSchemaFile(xmlFile, schemaPath);
		
        // Try to parse and validate file
		try {            		
			
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			dbf.setValidating(true);
//			dbf.setNamespaceAware(true);
//			dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
//			dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaFile.getAbsoluteFile());			
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Validator handler = new Validator();
			db.setErrorHandler(handler); 
			URL url = new URL(
					"http://www.ster.kuleuven.be/~wim/wim.oal");
			Document doc = db.parse(new InputSource(url.openStream())); //xmlFile); //, schemaFile.getAbsolutePath());
						                        
			return this.load(doc);
            			
		} catch(SAXException sax) {
			throw new SchemaException("Unable to parse xml file : " + sax);
		} catch(IOException ioe) {
			throw new OALException("Unable to access xml file: " + ioe);
		}
		
		catch(ParserConfigurationException pce) {
			throw new SchemaException("Parser configuration wrong: " + pce);
		}
		
	}
	
	
    // -------------------------------------------------------------------
    /**
     * Adds a new classloader to the SchemaLoader.<br>
     * Additional classloaders will be used in case a requested class cannot be
     * found on the default classloaders search path.
     * 
     * @param classloader A new classloader
     */  
	@SuppressWarnings("unchecked")
	public static void addClassloader(ClassLoader classloader) {
		
		if( classloader != null ) {
			SchemaLoader.extensionClassLoaders.add(classloader);
		}
		
	}
	

    // -------------------------------------------------------------------
    /**
     * Loads/parses a XML Document
     * 
     * @param doc The XML Document which should be parsed
     * @throws OALException if doc is <code>NULL</code> or empty
     * @throws SchemaException if XML File is not valid 
     */  	
	public RootElement load(Document doc) throws OALException, 
	                                      SchemaException {

        // Check if document is OK
		if(   (doc == null) 
           || (!doc.hasChildNodes()) 
           ) {
			throw new OALException("XML Schema is NULL or has no child nodes. ");
		}		
		       
        Element rootElement = doc.getDocumentElement();               
               
		// Get elements here
		// Don't change the sequence of retrieving the elements, or we might
		// run in dependecy problems!
        
        // This might be parallelize in a future release, as some elements, have no dependencies
		
		Node element = null;
		NodeList elementContainer = null;

        // --------- Observer -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_OBSERVER_CONTAINER);
        if( elementContainer.getLength() != 1 ) {
            throw new OALException("Schema XML can only have one " + RootElement.XML_OBSERVER_CONTAINER + " element. ");
        }
        element = elementContainer.item(0);     
        observers = createObserverElements(element);
        
        
        // --------- Target -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if( elementContainer.getLength() != 1 ) {
            throw new OALException("Schema XML can only have one " + RootElement.XML_TARGET_CONTAINER + " element. ");
        }
		element = elementContainer.item(0);
		targets = createTargetElements(element, observers); 


        // --------- Site -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_SITE_CONTAINER);
        if( elementContainer.getLength() != 1 ) {
            throw new OALException("Schema XML can only have one " + RootElement.XML_SITE_CONTAINER + " element. ");
        }
        element = elementContainer.item(0);     		
		sites = createSiteElements(element);


        // --------- Scope -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_SCOPE_CONTAINER);
        if( elementContainer.getLength() != 1 ) {
            throw new OALException("Schema XML can only have one " + RootElement.XML_SCOPE_CONTAINER + " element. ");
        }
        element = elementContainer.item(0);             		
		scopes = createScopeElements(element);

		
        // --------- Lens -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_LENS_CONTAINER);
        if( elementContainer.getLength() > 1 ) {	//  <-- All XML files prio 1.7 won't have a lens element, so 0 is ok
            throw new OALException("Schema XML can only have one " + RootElement.XML_LENS_CONTAINER + " element. ");
        }
        element = elementContainer.item(0);		
		lenses = createLensElements(element);		
		

        // --------- Eyepiece -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_EYEPIECE_CONTAINER);
        if( elementContainer.getLength() != 1 ) {
            throw new OALException("Schema XML can only have one " + RootElement.XML_EYEPIECE_CONTAINER + " element. ");
        }
        element = elementContainer.item(0);		
		eyepieces = createEyepieceElements(element);	

        // --------- Filter -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_FILTER_CONTAINER);
        if( elementContainer.getLength() > 1 ) {   //  <-- All XML files prio 1.5 won't have a filter element, so 0 is ok
            throw new OALException("Schema XML can only have one " + RootElement.XML_FILTER_CONTAINER + " element. ");
        }
        element = elementContainer.item(0);		
		filters = createFilterElements(element);			
		
        // --------- Imager -----------
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_IMAGER_CONTAINER);
        if( elementContainer.getLength() > 1 ) {
            throw new OALException("Schema XML can only have one " + RootElement.XML_IMAGER_CONTAINER + " element. ");
        } else if( elementContainer.getLength() == 1 ) {
            element = elementContainer.item(0);		
    		imagers = createImagerElements(element);
        }		
        
        // --------- Session -----------        
        elementContainer = rootElement.getElementsByTagName(RootElement.XML_SESSION_CONTAINER);
        if( elementContainer.getLength() != 1 ) {
            throw new OALException("Schema XML can only have one " + RootElement.XML_SESSION_CONTAINER + " element. ");
        }
        element = elementContainer.item(0);        
		sessions = createSessionElements(element);


        // --------- Observation -----------	
		this.observations = createObservationElements(rootElement);
        
		// Bugfix from 0.516 to 0.617
		// Remove all doublicate catalog targets
		// Bugfix from 0.617 to 0.717
		// Also used for fixing catalog datasource strings		
		this.removeDoublicateTargets();
		
        RootElement obs = new RootElement();
        for(int i=0; i < observations.length; i++) {
            obs.addObservation(observations[i]);
        }     
        
        return obs;
										
	}
    
    
    
    
    // ----------------------
    // Private Static Methods --------------------------------------------
    // ----------------------

    // -------------------------------------------------------------------
    /**
     * Loads objects for a given xsiType via reflection
     * 
     * @param xsiType The xsiType that specifies the Object
     * @param currentNode The XML node that represents the Object 
     * e.g. <target>...</target>
     * @param observers Needed for Target Objects, can be <code>null</code>
     * for Findings
     */     
    @SuppressWarnings("rawtypes")
	private static Object getObjectFromXSIType(String xsiType,
                                               Node currentNode,
                                               IObserver[] observers,
                                               int schemaElementType) throws SchemaException {
    
        // Resolve xsiType to java classname
        String classname = null;
        try {
        	if( SchemaElementConstants.FINDING == schemaElementType ) {
        		classname = ConfigLoader.getFindingClassnameFromType(xsiType);	
        	} else {		// TARGETs and all other extenable schemaElements can be found in Targetable of ConfigLoader 
        		classname = ConfigLoader.getTargetClassnameFromType(xsiType);
        	}
        } catch(ConfigException ce) {
            throw new SchemaException("Unable to get classname from xsi:type.\n" + ce.getMessage());
        }                                                          
            
        // Get Java class            
        Class currentClass = null;
        try {		// First try default ClassLoader
            currentClass = Class.forName(classname);
        } catch(ClassNotFoundException cnfe) { 
        	// Default ClassLoader cannot find it...so try extension ClassLoaders if there are any
        	if( !SchemaLoader.extensionClassLoaders.isEmpty() ) {
        		ListIterator iterator = SchemaLoader.extensionClassLoaders.listIterator();
        		while( iterator.hasNext() ) {
        			try {	
        				currentClass = ((ClassLoader)iterator.next()).loadClass(classname);
        				break;	// Class was found
                	} catch(ClassNotFoundException cnfe2) {     
                		// Do nothing...just try next classLoader
                	}                	
        		}
        		if( currentClass == null ) {
        			throw new SchemaException("Unable to load class for classname:" + classname);
        		}
        	}
        }        
        
        // Get constructors for class
        Constructor[] constructors = currentClass.getConstructors();
        Object object = null;       
        if( constructors.length > 0 ) {                     
            try {
                Class[] parameters = null;
                for(int i=0; i < constructors.length; i++) {
                    parameters = constructors[i].getParameterTypes();
                    if( observers == null ) {   // create IFinding (Constructor has one parameter)
                        if(   (parameters.length == 1)
                           && (parameters[0].isInstance(currentNode)) 
                           ) {
                               object = constructors[i].newInstance(new Object[] {currentNode});                           
                               break;                           
                        }                        
                    } else {                    // create ITarget (Constructor takes 2 parameters)
                        if(   (parameters.length == 2)
                           && (parameters[0].isInstance(currentNode)) 
                           && (parameters[1].isInstance(observers))
                           ) {
                               object = constructors[i].newInstance(new Object[] {currentNode, observers});                           
                               break;                           
                        }                         
                    }
                }                   
            } catch(InstantiationException ie) {
                throw new SchemaException("Unable to instantiate class: " + classname + "\n" + ie.getMessage());                
            } catch(InvocationTargetException ite) {
                throw new SchemaException("Unable to invocate class: " + classname + "\n" + ite.getMessage());                  
            } catch(IllegalAccessException iae) {
                throw new SchemaException("Unable to access class: " + classname + "\n" + iae.getMessage());                    
            }               
        } else {
            throw new SchemaException("Unable to load class: " + classname + "\nMaybe class has no default constructor. ");
        }           
    
        return object;
    
    }
    	
        


    // ---------------
    // Private Methods ---------------------------------------------------
    // ---------------

    // -------------------------------------------------------------------        
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IObservation[] createObservationElements(Node observations) 
	                                                 throws SchemaException {

        Element e = (Element)observations;                                        
        NodeList observationList = e.getElementsByTagName(IObservation.XML_ELEMENT_OBSERVATION);

        // Cannot use array here as loading of observation might fail (target loading might fail cause of XSI type, 
        // so this might cause observation loading to fail as well....
		ArrayList obs = new ArrayList(observationList.getLength());

		for(int i=0; i < observationList.getLength(); i++) {	

			try {
            obs.add(new Observation(observationList.item(i),
		                            this.targets,
		                            this.observers,
		                            this.sites,
		                            this.scopes,
		                            this.sessions,
		                            this.eyepieces,
									this.filters,
									this.imagers,
									this.lenses
		                            ));
			} catch(SchemaException se) {
				System.err.println(se + "\n\nContinue loading next observation...\n\n");
				continue;
			} catch(IllegalArgumentException iae) {
				System.err.println(iae + "\n\nContinue loading next observation...\n\n");
				continue;
			}
            
        }
		                           	
		return (IObservation[])obs.toArray(new IObservation[] {});	                                                 	
	                                                 	
	}


    // -------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ITarget[] createTargetElements(Node targets, IObserver[] observers) 
										   throws SchemaException {
									
        Element e = (Element)targets;                                     	  
		NodeList targetList = e.getElementsByTagName(ITarget.XML_ELEMENT_TARGET);

		// As loading of target might fail (unknown XSI type) we do not know the amount of successfuly loaded elements..
		ArrayList targetElements = new ArrayList(targetList.getLength());

        // Helper classes
        Node currentNode = null;
        Node attribute = null;
	
		for(int i=0; i < targetList.getLength(); i++) {
			
			currentNode = targetList.item(i);

			// Get classname from xsi:type
            NamedNodeMap attributes = currentNode.getAttributes();
            if(   (attributes != null)
               && (attributes.getLength() != 0) 
               ) {
               attribute = attributes.getNamedItem(ITarget.XML_XSI_TYPE);   
               if( attribute != null ) {
                   String xsiType = attribute.getNodeValue();
                   
                   Object object = null;
                   try {
                   	 object = SchemaLoader.getTargetFromXSIType(xsiType, currentNode, observers);
                   } catch(SchemaException se) {
                   		System.err.println(se + "\n\nContinue with next target element...\n\n");
                   		continue;
                   }
                   if( object != null ) {
                       ITarget currentTarget = null;
                       if( object instanceof ITarget ) {
                           currentTarget = (ITarget)object;
                           // Make sure catalog targets are unique (fixes Bug that might occur with files from 0.516)
                         //  if( currentTarget.getDatasource() != null ) {   // Target is catalog object
	                           int index = targetElements.indexOf(currentTarget);  
	                           if( index != -1 ) {  // Target already in catalog
	                        	   this.doublicateTargets.put(currentTarget, targetElements.get(index));
	                           }
                         //  }
                           // Add target (doublicate targets will be removed later when we've the observations)
                       	   targetElements.add(currentTarget);                                                                 
                       } else {
                           throw new SchemaException("Unable to load class of type: " + xsiType + "\nClass seems not to be of type ITarget. ");                 
                       }                        
                   } else {
                       throw new SchemaException("Unable to load class of type: " + xsiType);
                   }                                                         
               } else {
                   throw new SchemaException("No attribute specified: " + ITarget.XML_XSI_TYPE);
               } 
            } else {
                throw new SchemaException("No attribute specified: " + ITarget.XML_XSI_TYPE);                
            }
			
																
		}												   	
												   	
		return (ITarget[])targetElements.toArray(new ITarget[] {});										   	
	}									   	


    // -------------------------------------------------------------------
	private ISession[] createSessionElements(Node sessions) 
											 throws SchemaException {											
					
        Element e = (Element)sessions;                                        
        NodeList sessionList = e.getElementsByTagName(ISession.XML_ELEMENT_SESSION);                                        

		ISession[] sessionElements = new ISession[sessionList.getLength()];

		for(int i=0; i < sessionList.getLength(); i++) {			
            sessionElements[i] = new Session(sessionList.item(i),
                                             this.observers,
											 this.sites);
        }
        
		return sessionElements;											 	
											 	
	}


    // -------------------------------------------------------------------
	private IObserver[] createObserverElements(Node observers) 
											   throws SchemaException {
											   	
        Element e = (Element)observers;                                                
                                                
		NodeList observerList = e.getElementsByTagName(IObserver.XML_ELEMENT_OBSERVER);

		IObserver[] observerElements = new IObserver[observerList.getLength()];

		for(int i=0; i < observerList.getLength(); i++) {		
			observerElements[i] = new Observer(observerList.item(i));
        }
		
		return observerElements;
											   	
	}
	
	
    // -------------------------------------------------------------------    
	private ISite[] createSiteElements(Node sites) 
									   throws SchemaException {

        Element e = (Element)sites;                                                
                                            
        NodeList siteList = e.getElementsByTagName(ISite.XML_ELEMENT_SITE);

		ISite[] siteElements = new ISite[siteList.getLength()];

		for(int i=0; i < siteList.getLength(); i++) {			
            siteElements[i] = new Site(siteList.item(i));
        }
        
        return siteElements;
           
	}


    // -------------------------------------------------------------------
	private IScope[] createScopeElements(Node scopes) 
				  					     throws SchemaException {

        Element e = (Element)scopes;                                                
                                            
        NodeList scopeList = e.getElementsByTagName(IScope.XML_ELEMENT_SCOPE);		        

		IScope[] scopeElements = new IScope[scopeList.getLength()];
		
		for(int i=0; i < scopeList.getLength(); i++) {			
			scopeElements[i] = new Scope(scopeList.item(i));
        }
				
		return scopeElements;

	}


    // -------------------------------------------------------------------
	private IEyepiece[] createEyepieceElements(Node eyepieces) 
											   throws SchemaException {

        Element e = (Element)eyepieces;                                                
                                            
        NodeList eyepieceList = e.getElementsByTagName(IEyepiece.XML_ELEMENT_EYEPIECE);

		IEyepiece[] eyepieceElements = new IEyepiece[eyepieceList.getLength()];
	
		for(int i=0; i < eyepieceList.getLength(); i++) {			
            eyepieceElements[i] = new Eyepiece(eyepieceList.item(i));
        }
            
   		return eyepieceElements;										
			   	
	}

	
    // -------------------------------------------------------------------
	private ILens[] createLensElements(Node lenses) 
								       throws SchemaException {

		// For < 1.7 compatibility reasons
        if( lenses == null ) {
        	return new ILens[0];
        }		
		
        Element e = (Element)lenses;                                                
                                            
        NodeList lensesList = e.getElementsByTagName(ILens.XML_ELEMENT_LENS);

		ILens[] lensElements = new ILens[lensesList.getLength()];
	
		for(int i=0; i < lensesList.getLength(); i++) {			
			lensElements[i] = new Lens(lensesList.item(i));
        }
            
   		return lensElements;										
			   	
	}	
	
	
    // -------------------------------------------------------------------
	private IFilter[] createFilterElements(Node filters) 
										   throws SchemaException {

		// For < 1.5 compatibility reasons
        if( filters == null ) {
        	return new IFilter[0];
        }
		
		Element e = (Element)filters;                                                
                                            
        NodeList filterList = e.getElementsByTagName(IFilter.XML_ELEMENT_FILTER);

		IFilter[] filterElements = new IFilter[filterList.getLength()];
	
		for(int i=0; i < filterList.getLength(); i++) {			
			filterElements[i] = new Filter(filterList.item(i));
        }
            
   		return filterElements;										
			   	
	}	
	
	
    // -------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IImager[] createImagerElements(Node imagers) 
											   throws SchemaException {

        Element e = (Element)imagers;                                                
                                            
        NodeList imagerList = e.getElementsByTagName(IImager.XML_ELEMENT_IMAGER);

		// As loading of imagers might fail (unknown XSI type) we do not know the amount of successfuly loaded elements..
		ArrayList imagerElements = new ArrayList(imagerList.getLength());

        // Helper classes
        Node currentNode = null;
        Node attribute = null;
	
		for(int i=0; i < imagerList.getLength(); i++) {
			
			currentNode = imagerList.item(i);

			// Get classname from xsi:type
            NamedNodeMap attributes = currentNode.getAttributes();
            if(   (attributes != null)
               && (attributes.getLength() != 0) 
               ) {
               attribute = attributes.getNamedItem(IImager.XML_XSI_TYPE);   
               if( attribute != null ) {
                   String xsiType = attribute.getNodeValue();
                   
                   Object object = null;
                   try {
                   	 object = SchemaLoader.getImagerFromXSIType(xsiType, currentNode);
                   } catch(SchemaException se) {
                   		System.err.println(se + "\n\nContinue with next imager element...\n\n");
                   		continue;
                   }
                   if( object != null ) {
                       IImager currentImager = null;
                       if( object instanceof IImager ) {
                    	   currentImager = (IImager)object;
                    	   imagerElements.add(currentImager);                                                                 
                       } else {
                           throw new SchemaException("Unable to load class of type: " + xsiType + "\nClass seems not to be of type IImager. ");                 
                       }                        
                   } else {
                       throw new SchemaException("Unable to load class of type: " + xsiType);
                   }                                                         
               } else {
                   throw new SchemaException("No attribute specified: " + IImager.XML_XSI_TYPE);
               } 
            } else {
                throw new SchemaException("No attribute specified: " + IImager.XML_XSI_TYPE);                
            }
			
																
		}												   	
												   	
		return (IImager[])imagerElements.toArray(new IImager[] {});										
			   	
	}	

	
    // -------------------------------------------------------------------
	private File getSchemaFile(FileInputStream xmlFile, File schemaPath) throws OALException {
	
		byte[] buffer = new byte[500];
		try {
			// Only load the 500 first characters of the file
			xmlFile.read(buffer, 0, 500);
		} catch( IOException ioe ) {
			throw new OALException("Cannot read XML file to determine schema version. File " + xmlFile + "\n" + ioe);	
		}
		
		// Check if in the first 500 characters of the XML file a known SchemaFile name is persent. 
		// If so load the Schemafile for validation
		for(int i=0; i < SchemaLoader.VERSIONS.length; i++ ) {
			int index = new String(buffer).indexOf(SchemaLoader.VERSIONS[i]);
			if( index != -1 ) {
				return new File(schemaPath.getAbsolutePath() + File.separatorChar + SchemaLoader.VERSIONS[i]);
			}			
		}
		
		throw new OALException("Cannot determine schema version from XML file: " + xmlFile + "\n");
		
	}
	
	
	// -------------------------------------------------------------------
	// Remove doublicate catalog targets
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void removeDoublicateTargets() {
			
		if(   (this.doublicateTargets.isEmpty()) 
		   || (this.observations.length <= 0) 
		   ) {
			return;
		}
		
		Iterator keyIterator = null;
		ITarget current = null;		
		Object dT = null;
        for(int i=0; i < this.observations.length; i++) {
            current = observations[i].getTarget();           
        	if( current.getDatasource() == null ) {  // Check if target is catalog target
       // 		continue;
            }        	
        	
        	keyIterator = this.doublicateTargets.keySet().iterator();
        	while( keyIterator.hasNext() ) {
        		dT = keyIterator.next();
        		if( current.equalsID(dT) ) {  // Replace target with ID is equal (calling equal won't work here!)
        			observations[i].setTarget((ITarget)this.doublicateTargets.get(dT));
        		}
        	}
        }  
        
        // Remove targets from targets array (cache) 
        ArrayList targetElements = new ArrayList(Arrays.asList(this.targets));
        ListIterator iterator = targetElements.listIterator();
        while( iterator.hasNext() ) {
        	current = (ITarget)iterator.next();
        	keyIterator = this.doublicateTargets.keySet().iterator();
        	while( keyIterator.hasNext() ) {
        		dT = keyIterator.next();
        		if( current.equalsID(dT) ) {  // Check targetID is equal (calling equal won't work here!)
        			iterator.remove();
        		}
        	}        	
        }
        // Set clean targets array
        this.targets = (ITarget[])targetElements.toArray(new ITarget[] {});	
		
	}
	
	
	
	
	// -------------
	// Inner Classes ----------------------------------------------------------
	// -------------
	
	private class Validator extends DefaultHandler {
				
		public void error(SAXParseException exception) throws SAXException {
		                 
			System.err.println("XML Schema error: " + exception);
		   
		}  
		
		public void fatalError(SAXParseException exception) throws SAXException {
			
			System.err.println("XML Schema fatal error: " + exception);
		   
		}		    
		  
		public void warning(SAXParseException exception) throws SAXException { 
			
			System.out.println("XML Schema warning: " + exception);
			
		}
		  
	}		
	
}