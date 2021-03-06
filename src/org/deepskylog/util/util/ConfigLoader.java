/* ====================================================================
 * /util/ConfigLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package org.deepskylog.util.util;
import android.annotation.SuppressLint;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 * The ConfigLoader is used to find config files inside the classpath (and the extension directory),
 * and if config files are found, it can provide easy access to the
 * config information.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
@SuppressLint("DefaultLocale")
public class ConfigLoader {

	// ---------
	// Constants ---------------------------------------------------------
	// ---------    
	// Extension of config files
	private static final String MANIFEST_FILENAME = "META-INF/SCHEMATYPE";
	// Config file XSI relation entry: XSI_Type target ending
	private static final String CONFIG_FILE_TARGETENTRY_TYPE_ENDING = "XSI_Relation_Type";
    // Config file XSI relation entry: XSI_Type target ending
    private static final String CONFIG_FILE_TARGETENTRY_CLASSNAME_ENDING = "XSI_Relation_Class";
	// Config file XSI relation entry: XSI_Type finding ending
	private static final String CONFIG_FILE_FINDINGENTRY_TYPE_ENDING = "XSI_Relation_Finding_Type";
    // Config file XSI relation entry: XSI_Type finding ending
    private static final String CONFIG_FILE_FINDINGENTRY_CLASSNAME_ENDING = "XSI_Relation_Finding_Class";    
    
    

	// ------------------
	// Instance variables ------------------------------------------------
	// ------------------
	// All target xsi:types as key and Java classname as value
	@SuppressWarnings("rawtypes")
	private static HashMap targets = new HashMap();
	// All finding xsi:types as key and Java classname as value
	@SuppressWarnings("rawtypes")
	private static HashMap findings = new HashMap();	
	// All target xsi:types as key and finding xsi:types as value
	@SuppressWarnings("rawtypes")
	private static HashMap target_findings = new HashMap();	
	
	
	
	// --------------
	// Public methods ----------------------------------------------------
	// --------------
	// -------------------------------------------------------------------
	/**
	 * Returns the java classname for a target that matches the given
	 * xsi:type attribute, which can be found at additional schema 
	 * elements<br>
	 * E.g.:<br>
	 * <target id="someID" <b>xsi:type="oal:deepSkyGX"</b>><br>
	 * 	 // More Target data goes here<br>
	 * </target><br>
	 * If for example the type "oal:deepSkyGX" would be passed to
	 * this method, it would return the classname: 
	 * "de.lehmannet.om.deepSky.DeepSkyTarget".
	 * The classname may then be used to load the corresponding java class 
	 * via java reflection API for a given schema element.
	 * 
	 * @param type The xsi:type value which can be found at additional 
	 * schema elements (can be a finding xsi:type or an target xsi_type)
	 * @return The corresponding target java classname for the given type, or
	 * <code>null</code> if the type could not be resolved.
	 * @throws ConfigException if problems occured during load of config
	 */
	@SuppressWarnings("rawtypes")
	public static String getTargetClassnameFromType(String type) throws ConfigException {
		if( type == null ) {
			return null;
		}
		synchronized( targets ) {
			if( targets.isEmpty() ) {
				loadConfig();
			}
		}
		type = ConfigLoader.checkAncestorTypes(type);
		
		if( !targets.containsKey(type) ) {   // Given type is finding type...try to get target type
			Collection c = target_findings.keySet();
			Iterator i = c.iterator();
			String currentKey = null;
			String currentValue = null;
			while( i.hasNext() ) {
				currentKey = (String)i.next();
				currentValue = (String)target_findings.get(currentKey);
				if( type.equals(currentValue) ) {
					type = currentKey;
					break;
				}
			}			
		}
		// Load classname from type
		String classname = (String)targets.get(type);
        if(   (classname == null)
           || ("".equals(classname.trim())) 
           ) {
        		throw new ConfigException("No target class defined for target type: " + type + ". Please check plugin Manifest files, or download new extension. ");
        }
     	return classname;						
	}
	
	// -------------------------------------------------------------------
	/**
	 * Returns the java classname for a finding that matches the given
	 * xsi:type attribute, which can be found at additional schema 
	 * elements<br>
	 * E.g.:<br>
	 * <result id="someID" <b>xsi:type="oal:findingsDeepSky"</b>><br>
	 * 	 // More finding data goes here<br>
	 * </result><br>
	 * If for example the type "oal:findingsDeepSky" would be passed to
	 * this method, it would return the classname: 
	 * "de.lehmannet.om.extension.deepSky.DeepSkyFinding".
	 * The classname may then be used to load the corresponding java class 
	 * via java reflection API for a given schema element.
	 * 
	 * @param type The xsi:type value which can be found at additional 
	 * schema elements (can be a finding xsi:type or an target xsi_type)
	 * @return The corresponding finding java classname for the given type, or
	 * <code>null</code> if the type could not be resolved.
	 * @throws ConfigException if problems occured during load of config
	 */
	@SuppressWarnings("rawtypes")
	public static String getFindingClassnameFromType(String type) throws ConfigException {
		if( type == null ) {
			return null;
		}
		synchronized( findings ) {
			if( findings.isEmpty() ) {
				loadConfig();
			}
		}
		type = ConfigLoader.checkAncestorTypes(type);
		
		if( !findings.containsKey(type) ) {   // Given type is target type...try to get finding type
			Collection c = target_findings.keySet();
			Iterator i = c.iterator();
			String currentKey = null;
			String currentValue = null;
			while( i.hasNext() ) {
				currentKey = (String)i.next();
				currentValue = (String)target_findings.get(currentKey);
				if( type.equals(currentKey) ) {
					type = currentValue;
					break;
				}
			}			
		}
		// Load classname from type
		String classname = (String)findings.get(type);
        if(   (classname == null)
           || ("".equals(classname.trim())) 
           ) {
        		throw new ConfigException("No findings class defined for findings type: " + type + ". Please check plugin Manifest files, or download new extension. ");
        }
     	return classname;						
	}	
	
	
	// -------------------------------------------------------------------
	/**
	 * Scans the java classpath again for valid configfile.
	 * 
	 * @throws ConfigException if problems occured during load of config
	 */
	public static void reloadConfig() throws ConfigException {
		// Delete old entries
		targets.clear();
		findings.clear();
		target_findings.clear();
		
		// Load entries again
		loadConfig();
	}
	
	
	// ---------------
	// Private methods ---------------------------------------------------
	// ---------------
	// -------------------------------------------------------------------
	private static void loadConfig() throws ConfigException {
		// Add fixed generic elements (no extenstion package required) 
		ConfigLoader.addGenericElements();
		
		// Get JARs from classpath
		String sep = System.getProperty("path.separator"); 
		String path = System.getProperty("java.class.path");		
		StringTokenizer tokenizer = new StringTokenizer(path, sep);
		File token = null;
		while (tokenizer.hasMoreTokens()) { 
			token = new File(tokenizer.nextToken());
			if(   ( token.isFile() ) 
		  	   && ( token.getName().endsWith(".jar") ) 
 			   ){
					scanJarFile(token); 	
			}						
		}
		// Get JARs under extension path
		String extPath = System.getProperty("java.ext.dirs");
		File ext = new File(extPath);
		if( ext.exists() ) {
			File[] jars = ext.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
               	 
               	 if( name.toLowerCase().endsWith(".jar") ) 
               	   return true;
               	 
               	 return false;
                }
            });
			if( jars != null ) {
				for(int i=0; i < jars.length; i++ ) {
					scanJarFile(jars[i]); 
				}			
			}			
		}
		
	}	

	// -------------------------------------------------------------------
	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "resource", "rawtypes" })
	private static void scanJarFile(File jar) throws ConfigException {
		ZipFile archive = null;
		try {
			archive = new ZipFile(jar);
		} catch(ZipException zipEx) {
			throw new ConfigException("Error while accessing JAR file. ", zipEx);
		} catch(IOException ioe) {
			throw new ConfigException("Error while accessing JAR file. ", ioe);
		}
		Enumeration enu = archive.entries();
		while( enu.hasMoreElements() ) {			
			ZipEntry entry = (ZipEntry)enu.nextElement();
			String name = entry.getName();		
			if( name.toUpperCase().equals(MANIFEST_FILENAME) ) {
				InputStream in = null;
				try { 
			
					in = archive.getInputStream(entry);
					Properties prop = new Properties();
					prop.load(in);			
					addConfig(prop);				
				} catch (IOException ioe) {
					throw new ConfigException("Error while accessing entry from JAR file. ", ioe); 					
		        } finally {
        	  		if( in != null ) {
        	  			try {
							in.close();
        	  			} catch(IOException ioe) {
        	  				// we can't do anything here	
        	  			}
        	  		}
        		}               				
			}
		}
	}

	// -------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addConfig(Properties newProperties) throws ConfigException {          
		Iterator keys = newProperties.keySet().iterator();
		String currentKey = null;
        String prefix = null;
        String target_classname = null;
        String target_type = null;
        String finding_classname = null;
        String finding_type = null;
        
        while( keys.hasNext() ) {
            currentKey = (String)keys.next();
            // Check if key ends with target TYPE ending
            if( currentKey.endsWith(ConfigLoader.CONFIG_FILE_TARGETENTRY_TYPE_ENDING) ) {
                // Get target TYPE value
                target_type = newProperties.getProperty(currentKey);
                
                // Get prefix (everything that is before our target TYPE ending) 
                prefix = currentKey.substring(0, currentKey.lastIndexOf(ConfigLoader.CONFIG_FILE_TARGETENTRY_TYPE_ENDING));
                // Use prefix and CLASS ending to get target class property
                target_classname = newProperties.getProperty(prefix + ConfigLoader.CONFIG_FILE_TARGETENTRY_CLASSNAME_ENDING);
                // Use prefix and CLASS ending to get finding class property
                finding_type = newProperties.getProperty(prefix + ConfigLoader.CONFIG_FILE_FINDINGENTRY_TYPE_ENDING);
                // Use prefix and CLASS ending to get finding class property
                finding_classname = newProperties.getProperty(prefix + ConfigLoader.CONFIG_FILE_FINDINGENTRY_CLASSNAME_ENDING);                
                
                // Add type and classname to our list of known types
                synchronized(targets) {
                	targets.put(target_type, target_classname);                                        
                }
                // Add type and classname to our list of known types
                synchronized(findings) {
                	findings.put(finding_type, finding_classname);                                        
                }                                          
                
                // Add target type and finding type                 
                synchronized(target_findings) {
                	target_findings.put(target_type, finding_type);                                        
                }                                          
                
            }            
        }
        			
	}
	
	// -------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private static void addGenericElements() {
		
		// This is the most simple element relation
		final String target_type = "oal:observationTargetType";
		final String target_classname = "de.lehmannet.om.GenericTarget";
		final String finding_type = "oal:findingsType";
		final String finding_classname = "de.lehmannet.om.GenericFinding";
		
        // Add type and classname to our list of known types
        synchronized(targets) {
        	targets.put(target_type, target_classname);                                        
        }
        // Add type and classname to our list of known types
        synchronized(findings) {
        	findings.put(finding_type, finding_classname);                                        
        }                                          
        
        // Add target type and finding type                 
        synchronized(target_findings) {
        	target_findings.put(target_type, finding_type);                                        
        } 		
		
        // This is a simple star
		final String starTarget_type = "oal:starTargetType";
		final String starTarget_classname = "de.lehmannet.om.TargetStar";
		final String starTarget_finding_type = "oal:findingsType";
		final String starTarget_finding_classname = "de.lehmannet.om.GenericFinding";
		
        // Add type and classname to our list of known types
        synchronized(targets) {
        	targets.put(starTarget_type, starTarget_classname);                                        
        }
        // Add type and classname to our list of known types
        synchronized(findings) {
        	findings.put(starTarget_finding_type, starTarget_finding_classname);                                        
        }                                          
        
        // Add target type and finding type                 
        synchronized(target_findings) {
        	target_findings.put(starTarget_type, starTarget_finding_type);                                        
        } 	        
        
	}
	
	// -------------------------------------------------------------------
	// Check on old xsi types/names (before OAL 2.0)
	private static String checkAncestorTypes(String type) {		
		
		if( type.startsWith("fgca") ) {
			type = type.replaceAll("fgca", "oal");
		} 			
		
		return type;
		
	}
  
}
