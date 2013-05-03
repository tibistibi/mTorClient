package nl.bhit.mtor.client.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.codec.binary.Base64;

public final class RestUtil {
	private static final String USERNAME_PASSWORD_ENCODING = "US-ASCII";

	private static final transient Logger LOG = Logger.getLogger(RestUtil.class);

	private static final int STATUS_CODE_NO_CONTENT = 204;
	
	/**
	 * Get a list of objects from a REST service that works in JSON format
	 * 
	 * @param <T>
	 * @param objectType
	 * 			type of object you want to return in the Array List
	 * @param url
	 * 			url to the service to get JSON objects from
	 * 			example: https://mtor.bhit.nl/services/api/messages/1.json
	 * 			where 1 is the user id
	 * @param username
	 * 			username for the basic authentication
	 * @param password
	 * 			password for the basic authentication
	 * 
	 */
	public static <T> List<T> getObjectsFromServer(Class<T[]> objectType, String url, String username, String password) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if (username != null & password != null) {
			headers.set("Authorization", getAuthorizationHeader(username, password));
		}
		HttpEntity<?> requestEntity = new HttpEntity<Object>(headers);

		MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(messageConverter);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(messageConverters);
		
		try {
			ResponseEntity<T[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, objectType);
			T[] result = responseEntity.getBody();
			return Arrays.asList(result);
			
		} catch (RestClientException e) {
			LOG.warn("Exception in REST client: " + e.getMessage());
			return null;
		} catch (Exception e) {
			LOG.warn("General exception while preparing REST connection: " + e.getMessage());
			return null;
		}
	}
	

	/**
	 * Save an object to an REST service that works in JSON format
	 * 
	 * @param object
	 * 			object you want to save
	 * @param url
	 * 			url to the service that receives the JSON objects
	 * 			example: https://mtor.bhit.nl/services/api/messages/saveclientmessage
	 * @param username
	 * 			username for the basic authentication
	 * @param password
	 * 			password for the basic authentication
	 */
	public static void putObjectInServer(Object object, String url, String username, String password) {
		
		ObjectMapper mapper = new ObjectMapper();
		HttpHeaders headers = new HttpHeaders();
		
		try {
			headers.setContentType(MediaType.APPLICATION_JSON);
			if (username != null & password != null) {
				headers.set("Authorization", getAuthorizationHeader(username, password));
			}
			String jsonMessage = mapper.writeValueAsString(object);
			HttpEntity<String> entity = new HttpEntity<String>(jsonMessage, headers);
			RestTemplate restTemplate = new RestTemplate();

			LOG.trace("Trying to save object via REST PUT request to URL: " + url);
			int statusCode = restTemplate.exchange(url, HttpMethod.PUT, entity, null).getStatusCode().value();
			if (statusCode != STATUS_CODE_NO_CONTENT) {
				LOG.warn("Saving object didn't return expected status code 204 (No Content), object probably not saved! Status code was " + statusCode);
			} else {
				LOG.trace("Saved object!");				
			}
			
		} catch (RestClientException e) {
			LOG.warn("Exception in REST client: " + e.getMessage());
		} catch (Exception e) {
			LOG.warn("General exception while preparing REST connection: " + e.getMessage());
		}
	}

	private static String getAuthorizationHeader(String username, String password) {
		String usernamePassword = username + ":" + password;
		byte[] usernamePasswordBytes = usernamePassword.getBytes(Charset.forName(USERNAME_PASSWORD_ENCODING));
		String authorizationHeader = "";
		try {
			String basic = new String(Base64.encodeBase64(usernamePasswordBytes), USERNAME_PASSWORD_ENCODING);
			authorizationHeader = "Basic " + basic;
		} catch (UnsupportedEncodingException e) {
			LOG.warn("Unsupported encoding: " + USERNAME_PASSWORD_ENCODING);
		}
		return authorizationHeader;
	}

	private RestUtil() {
	}
}


