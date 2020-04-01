package edu.carleton.comp4601.helpers;

import org.apache.commons.codec.binary.Base64;

public final class Encoder {
	public final static String encodeAsBase64(String input) {
		byte[] bytesEncoded = Base64.encodeBase64(input.getBytes());
		
		return new String(bytesEncoded);
	}
	
	public final static String decodeFromBase64(String input) {
		byte[] valueDecoded = Base64.decodeBase64(input);

		return new String(valueDecoded);
	}
}
