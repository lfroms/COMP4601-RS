package edu.carleton.comp4601.store;

import edu.carleton.comp4601.models.HypertextDocument;
import edu.carleton.comp4601.models.WebDocument;

public abstract class ApplicationSpecificMapper {
	protected static Class<? extends WebDocument> classForTypeName(String typeName) {
		switch (typeName) {
		case HypertextDocument.TYPE_NAME:
			return HypertextDocument.class;
		}

		throw new RuntimeException("No class available to deserialize from \"" + typeName + "\"");
	}
}
