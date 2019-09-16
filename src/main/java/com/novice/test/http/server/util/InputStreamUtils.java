package com.novice.test.http.server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class InputStreamUtils {

	private InputStreamUtils() {
	}

	public static String readLine(InputStream inputStream) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			int i;
			while ((i = inputStream.read()) != 10 && i != -1) {
				out.write(i);
			}
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		}
	}
}
