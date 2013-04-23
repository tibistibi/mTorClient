package nl.bhit.mtor.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {
	
	private ReflectionUtils() {
	}
	
	/**
	 * Sets a different value for a final static field of a specified class. This method only should
	 * be used for testing purposes.
	 * 
	 * @param className				Class name that contains the final static field to modify.
	 * @param fieldName				Field name that we want to modify.
	 * @param newValue				New value to set.
	 * 
	 * @throws SecurityException				
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void modifyFinalStaticLongValue(final String className, final String fieldName, final long longValue) throws SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		final Field field = Class.forName(className).getDeclaredField(fieldName);
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setLong(field, field.getModifiers() & ~Modifier.FINAL);
		field.setLong(null, longValue);
		modifiersField.setLong(field, field.getModifiers() & Modifier.FINAL);
	}
	
}
