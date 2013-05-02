package nl.bhit.mtor.client;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import nl.bhit.mtor.client.exceptions.MTorPropertiesException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MTorProperties {
	private static final transient Logger LOG = Logger.getLogger(MTorProperties.class);
	
	private static final String FILENAME_DEFAULT = "mTor.default.properties";
	private static final String FILENAME_OVERRIDE = "mTor.properties";
	
	private static final String MTOR_PROPERTYNAME_PROJECT_ID = "mTor.project.id";
    private static final String MTOR_PROPERTYNAME_SERVER_URL = "mTor.server.url";
    private static final String MTOR_PROPERTYNAME_SERVER_URL_SAVECLIENTMESSAGE = "mTor.server.url.saveclientmessage";
    private static final String MTOR_PROPERTYNAME_SERVER_USERNAME = "mTor.server.username";
    private static final String MTOR_PROPERTYNAME_SERVER_PASSWORD = "mTor.server.password";
    private static final String MTOR_PROPERTYNAME_PACKAGES = "mTor.packages";

	private final String defaultBasePackage = "nl.bhit.mtor";
	
	private String projectId;
	private String serverUrl;
	private String serverUrlSaveclientmessage;
	private String serverUsername;
	private String serverPassword;
	private String packages;
    
    Properties properties;
    
    MTorProperties() throws MTorPropertiesException {
    	properties = new Properties();
		loadProperties(FILENAME_DEFAULT);
		loadProperties(FILENAME_OVERRIDE);
		checkRequiredPropertiesPresent();
    }
    
	private void loadProperties(String propertiesFile) {
		try {
			properties.load(this.getClass().getResourceAsStream("/" + propertiesFile));
			projectId = properties.getProperty(MTOR_PROPERTYNAME_PROJECT_ID);
			serverUrl = properties.getProperty(MTOR_PROPERTYNAME_SERVER_URL);
			serverUrlSaveclientmessage = properties.getProperty(MTOR_PROPERTYNAME_SERVER_URL_SAVECLIENTMESSAGE);
			serverUsername = properties.getProperty(MTOR_PROPERTYNAME_SERVER_USERNAME);
			serverPassword = properties.getProperty(MTOR_PROPERTYNAME_SERVER_PASSWORD);
			packages = properties.getProperty(MTOR_PROPERTYNAME_PACKAGES);
		} catch (Exception e) {
			LOG.warn("Properties could not be loaded. Make sure the following properties file is on the path: " + propertiesFile);
			LOG.trace("stacktrace for above error:", e);
		}
	}
	
	private void checkRequiredPropertiesPresent() throws MTorPropertiesException {
		String errorPrefix = "Required property not found: "; 
		if (projectId == null) {
			throw new MTorPropertiesException(errorPrefix + "mTor.project.id");
		}
		if (serverUrl == null) {
			throw new MTorPropertiesException(errorPrefix + "mTor.server.url");
		}
		if (serverUrlSaveclientmessage == null) {
			throw new MTorPropertiesException(errorPrefix + "mTor.server.url.saveclientmessage");
		}
		if (serverUsername == null) {
			throw new MTorPropertiesException(errorPrefix + "mTor.server.username");
		}
		if (serverPassword == null) {
			throw new MTorPropertiesException(errorPrefix + "mTor.server.password");
		}
	}

	protected Long getProjectId() {
		Long projectId = null;
		try {
			projectId = new Long(this.projectId);
		} catch (Exception e) {
			LOG.warn("could not read the projectId so message can not be send, no monitoring possible!", e);
		}
		LOG.debug("using projectId:" + projectId);
		return projectId;
	}

	protected String getServerUrl() {
		return serverUrl;
	}
	
	protected String getServerUrlSaveclientmessage() {
		return serverUrlSaveclientmessage;
	}
    
    protected String getServerUsername() {
        return serverUsername;
    }
    
    protected String getServerPassword() {
        return serverPassword;
    }

	protected Set<String> getPackages() {
		Set<String> result = new HashSet<String>();
		result.add(defaultBasePackage);
		String[] pieces = StringUtils.split(packages, ",");
		if (pieces != null) {
			for (int i = 0; i < pieces.length; i++) {
				result.add(StringUtils.trim(pieces[i]));
			}
		}
		return result;
	}

	
}
