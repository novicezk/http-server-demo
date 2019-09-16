package com.novice.test.http.server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SocketChannelReader {
	private final SocketChannel channel;
	private final ByteBuffer byteBuffer;

	public SocketChannelReader(SocketChannel channel) {
		this.channel = channel;
		this.byteBuffer = ByteBuffer.allocate(1024);
	}

	public String readLine() throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			int i = 0;
			while ((this.channel.read(this.byteBuffer)) != -1 && i != 10) {
				this.byteBuffer.flip();
				while (this.byteBuffer.hasRemaining() && (i = this.byteBuffer.get()) != 10) {
					out.write(i);
				}
				this.byteBuffer.compact();
			}
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		}
	}
}
