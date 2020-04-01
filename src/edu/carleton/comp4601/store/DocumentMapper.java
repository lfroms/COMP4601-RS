package edu.carleton.comp4601.store;

import java.lang.reflect.InvocationTargetException;

import edu.carleton.comp4601.models.Identifiable;

public interface DocumentMapper<External extends Identifiable, Internal> {
	
	/**
	 * 
	 * @param input
	 * @return A type that is compatible with the storage provider being mapped for.
	 */
	Internal serialize(External input);
	
	/**
	 * 
	 * @param input
	 * @return An application-compatible type.
	 */
	External deserialize(Internal input) 
			throws 
			InstantiationException, 
			IllegalAccessException, 
			IllegalArgumentException, 
			InvocationTargetException, 
			NoSuchMethodException, 
			SecurityException ;

}
