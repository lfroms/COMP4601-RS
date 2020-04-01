package edu.carleton.comp4601.store;

import java.util.Optional;

import edu.carleton.comp4601.models.Identifiable;

public interface Storable<DataType extends Identifiable> {
	
	/**
	 * 
	 * @param input
	 */
	public void upsert(DataType input);
	
	/**
	 * 
	 * @param id
	 * @return The object.
	 */
	public Optional<DataType> find(Integer id);
	
	/**
	 * 
	 * @param id
	 */
	public void delete(Integer id);
	
	/**
	 * Resets all data.
	 */
	public void reset();
	
}
