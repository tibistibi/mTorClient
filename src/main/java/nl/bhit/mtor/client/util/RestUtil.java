package nl.bhit.mtor.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.bhit.mtor.client.model.ClientMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class RestUtil {
	protected static final Log LOG = LogFactory.getLog(RestUtil.class);

	/**
	 * Get client messages from server via REST GET in JSON format
	 */
	public static List<ClientMessage> getClientMessages(Long userId, String serverUrl) {
		String url = serverUrl + "/services/api/messages/" + userId + ".json";
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

		MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(messageConverter);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(messageConverters);
		
		try {
			ResponseEntity<ClientMessage[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ClientMessage[].class);
			ClientMessage[] result = responseEntity.getBody();
			return Arrays.asList(result);
			
		} catch (RestClientException e) {
			LOG.warn("no connection", e);
		}
		return null;
	}
	

	/**
	 * Save a client message to server via REST PUT in JSON format
	 */
	public static void saveClientMessage(ClientMessage message, String serverUrl) throws RestClientException, Exception {
		String url = serverUrl + "/services/api/messages/saveclientmessage";
		
		ObjectMapper mapper = new ObjectMapper();
		HttpHeaders headers = new HttpHeaders();
		
		try {
			headers.setContentType(MediaType.APPLICATION_JSON);
			// TODO: authentication...
			String jsonMessage = mapper.writeValueAsString(message);
			HttpEntity<String> entity = new HttpEntity<String>(jsonMessage, headers);
			RestTemplate restTemplate = new RestTemplate();
			LOG.trace("Saving client message...");
			restTemplate.put(url, entity);
			LOG.trace("Saved client message!");

		} catch (RestClientException e) {
			LOG.warn("Exception in REST client: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			LOG.warn("General exception while preparing REST connection: " + e.getMessage());
			throw e;
		}
	}

}
