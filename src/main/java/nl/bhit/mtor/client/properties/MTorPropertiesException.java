package nl.bhit.mtor.client.properties;

public class MTorPropertiesException extends Exception {
	private static final long serialVersionUID = -6589172612689962987L;

	public MTorPropertiesException(String message) {
        super(message);
    }
	
	public MTorPropertiesException(String message, Exception e) {
        super(message, e);
    }
}