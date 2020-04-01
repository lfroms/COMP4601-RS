package edu.carleton.comp4601.store;

import java.util.Objects;
import java.util.function.Supplier;

import edu.carleton.comp4601.models.Identifiable;

public abstract class MappableProvider<External extends Identifiable, Internal> {
	protected final Supplier<? extends DocumentMapper<External, Internal>> mapperConstructor;
	
	public MappableProvider(Supplier<? extends DocumentMapper<External, Internal>> mapperConstructor) {
		this.mapperConstructor = Objects.requireNonNull(mapperConstructor);
	}
}
