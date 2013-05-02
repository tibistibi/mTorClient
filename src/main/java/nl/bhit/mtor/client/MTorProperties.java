package nl.bhit.mtor.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.bhit.mtor.client.exceptions.MTorPropertiesException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MTorProperties {
	private static final transient Logger LOG = Logger
			.getLogger(MTorProperties.class);

	private static final String FILENAME_DEFAULT = "mTor.default.properties";
	private static final String FILENAME_OVERRIDE = "mTor.properties";

	private static final String MTOR_PROPERTYNAME_PROJECT_ID = "mTor.project.id";
	private static final String MTOR_PROPERTYNAME_SERVER_URL = "mTor.server.url";
	private static final String MTOR_PROPERTYNAME_SERVER_URL_SAVECLIENTMESSAGE = "mTor.server.url.saveclientmessage";
	private static final String MTOR_PROPERTYNAME_SERVER_USERNAME = "mTor.server.username";
	private static final String MTOR_PROPERTYNAME_SERVER_PASSWORD = "mTor.server.password";
	private static final String MTOR_PROPERTYNAME_PACKAGES = "mTor.packages";

	private final String defaultBasePackage = "nl.bhit.mtor";

	PropertiesConfiguration defaultProperties;
	PropertiesConfiguration overrideProperties;

	MTorProperties() throws MTorPropertiesException {
		try {
			defaultProperties = new PropertiesConfiguration(FILENAME_DEFAULT);
		} catch (ConfigurationException e) {
			LOG.warn("Default properties could not be loaded!");
		}
		
		try {
			overrideProperties = new PropertiesConfiguration(FILENAME_OVERRIDE);
			overrideProperties.setReloadingStrategy(new FileChangedReloadingStrategy());
		} catch (ConfigurationException e) {
			LOG.warn("Properties could not be loaded. Make sure the following properties file is on the path: "
					+ FILENAME_OVERRIDE);
			LOG.trace("stacktrace for above error:", e);
		}

		checkRequiredPropertiesPresent();
	}

	private void checkRequiredPropertiesPresent()
			throws MTorPropertiesException {
		String errorPrefix = "Required property not found: ";
		if (getProperty(MTOR_PROPERTYNAME_PROJECT_ID) == null) {
			throw new MTorPropertiesException(errorPrefix + "mTor.project.id");
		}
		if (getProperty(MTOR_PROPERTYNAME_SERVER_URL) == null) {
			throw new MTorPropertiesException(errorPrefix + "mTor.server.url");
		}
		if (getProperty(MTOR_PROPERTYNAME_SERVER_URL_SAVECLIENTMESSAGE) == null) {
			throw new MTorPropertiesException(errorPrefix
					+ "mTor.server.url.saveclientmessage");
		}
		if (getProperty(MTOR_PROPERTYNAME_SERVER_USERNAME) == null) {
			throw new MTorPropertiesException(errorPrefix
					+ "mTor.server.username");
		}
		if (getProperty(MTOR_PROPERTYNAME_SERVER_PASSWORD) == null) {
			throw new MTorPropertiesException(errorPrefix
					+ "mTor.server.password");
		}
	}

	protected Long getProjectId() {
		Long projectId = null;
		try {
			projectId = new Long(getProperty(MTOR_PROPERTYNAME_PROJECT_ID));
		} catch (Exception e) {
			LOG.warn(
					"could not read the projectId so message can not be send, no monitoring possible!",
					e);
		}
		LOG.debug("using projectId:" + projectId);
		return projectId;
	}

	protected String getServerUrl() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_URL);
	}

	protected String getServerUrlSaveclientmessage() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_URL_SAVECLIENTMESSAGE);
	}

	protected String getServerUsername() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_USERNAME);
	}

	protected String getServerPassword() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_PASSWORD);
	}

	protected Set<String> getPackages() {
		Set<String> result = new HashSet<String>();
		result.add(defaultBasePackage);
		String[] pieces = StringUtils.split(
				getProperty(MTOR_PROPERTYNAME_PACKAGES), ",");
		if (pieces != null) {
			for (int i = 0; i < pieces.length; i++) {
				result.add(StringUtils.trim(pieces[i]));
			}
		}
		return result;
	}

	public String getProperty(String key) {
		String property = getPropertyFromConfiguration(key, overrideProperties);
		if (property == null) {
			property = getPropertyFromConfiguration(key, defaultProperties);
		}
		if (property == null) {
			LOG.debug("Property with key \"" + key + "\" is requested but not found!");
		}
		return property;
	}
	
	public String getPropertyFromConfiguration(String key, PropertiesConfiguration properties) {
		String property = null;
		if (properties.getProperty(key) instanceof String) {
			property = (String) properties.getProperty(key);
		} else if (properties.getProperty(key) instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) properties.getProperty(key);
			property = list.get(list.size() - 1);
		}
		return property;
	}

}
