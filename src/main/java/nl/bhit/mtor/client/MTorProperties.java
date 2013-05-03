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
	private static final String MTOR_PROPERTYNAME_DISKSPACE_PATH = "mTor.diskspace.path";
	private static final String MTOR_PROPERTYNAME_DISKSPACE_WARNLIMIT_GB = "mTor.diskspace.warnlimit.gb";
	private static final String MTOR_PROPERTYNAME_DISKSPACE_ERRORLIMIT_GB = "mTor.diskspace.errorlimit.gb";
	private static final String MTOR_PROPERTYNAME_FREEMEMORY_WARNLIMIT_MB = "mTor.freememory.warnlimit.mb";
	private static final String MTOR_PROPERTYNAME_FREEMEMORY_ERRORLIMIT_MB = "mTor.freememory.errorlimit.mb";

	private static final String DEFAULT_PACKAGE = "nl.bhit.mtor";

	private static PropertiesConfiguration defaultProperties;
	private static PropertiesConfiguration overrideProperties;

	public static void initialize() throws MTorPropertiesException {
		try {
			defaultProperties = new PropertiesConfiguration(FILENAME_DEFAULT);
		} catch (ConfigurationException e) {
			throw new MTorPropertiesException("Default properties could not be loaded!");
		}
		
		try {
			overrideProperties = new PropertiesConfiguration(FILENAME_OVERRIDE);
			overrideProperties.setReloadingStrategy(new FileChangedReloadingStrategy());
		} catch (ConfigurationException e) {
			throw new MTorPropertiesException("Properties could not be loaded. Make sure the following properties file is on the path: "
					+ FILENAME_OVERRIDE);
		}

		checkRequiredPropertiesPresent();
	}

	private static void checkRequiredPropertiesPresent()
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

	public static Long getProjectId() {
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

	public static String getServerUrl() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_URL);
	}

	public static String getServerUrlSaveclientmessage() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_URL_SAVECLIENTMESSAGE);
	}

	public static String getServerUsername() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_USERNAME);
	}

	public static String getServerPassword() {
		return getProperty(MTOR_PROPERTYNAME_SERVER_PASSWORD);
	}
	
	public static String getDiskspacePath() {
		return getProperty(MTOR_PROPERTYNAME_DISKSPACE_PATH);
	}
	
	public static Long getDiskspaceWarnlimit() {
		Long limit = null;
		try {
			limit = GbToByte(new Long(getProperty(MTOR_PROPERTYNAME_DISKSPACE_WARNLIMIT_GB)));
		} catch (Exception e) {
			LOG.warn("Could not read the limit number for property: " + MTOR_PROPERTYNAME_DISKSPACE_WARNLIMIT_GB);
		}
		return limit;
	}
	
	public static Long getDiskspaceErrorlimit() {
		Long limit = null;
		try {
			limit = GbToByte(new Long(getProperty(MTOR_PROPERTYNAME_DISKSPACE_ERRORLIMIT_GB)));
		} catch (Exception e) {
			LOG.warn("Could not read the limit number for property: " + MTOR_PROPERTYNAME_DISKSPACE_ERRORLIMIT_GB);
		}
		return limit;
	}
	
	public static Long getFreememoryWarnlimit() {
		Long limit = null;
		try {
			limit = MbToByte(new Long(getProperty(MTOR_PROPERTYNAME_FREEMEMORY_WARNLIMIT_MB)));
		} catch (Exception e) {
			LOG.warn("Could not read the limit number for property: " + MTOR_PROPERTYNAME_FREEMEMORY_WARNLIMIT_MB);
		}
		return limit;
	}
	
	public static Long getFreememoryErrorlimit() {
		Long limit = null;
		try {
			limit = MbToByte(new Long(getProperty(MTOR_PROPERTYNAME_FREEMEMORY_ERRORLIMIT_MB)));
		} catch (Exception e) {
			LOG.warn("Could not read the limit number for property: " + MTOR_PROPERTYNAME_FREEMEMORY_ERRORLIMIT_MB);
		}
		return limit;
	}

	public static Set<String> getPackages() {
		Set<String> result = new HashSet<String>();
		result.add(DEFAULT_PACKAGE);
		String[] pieces = StringUtils.split(
				getProperty(MTOR_PROPERTYNAME_PACKAGES), ",");
		if (pieces != null) {
			for (int i = 0; i < pieces.length; i++) {
				result.add(StringUtils.trim(pieces[i]));
			}
		}
		return result;
	}

	private static String getProperty(String key) {
		String property = getPropertyFromConfiguration(key, overrideProperties);
		if (property == null) {
			property = getPropertyFromConfiguration(key, defaultProperties);
		}
		if (property == null) {
			LOG.debug("Property with key \"" + key + "\" is requested but not found!");
		}
		return property;
	}
	
	private static String getPropertyFromConfiguration(String key, PropertiesConfiguration properties) {
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

	private static Long GbToByte(Long Gb) {
		return Gb * 1024 /*MB*/ * 1024 /*KB x MB*/ * 1024 /*Byte x KB*/;
	}
	
	private static Long MbToByte(Long Mb) {
		return Mb * 1024 /*KB x MB*/ * 1024 /*Byte x KB*/;
	}
}
