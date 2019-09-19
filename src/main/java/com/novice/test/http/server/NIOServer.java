package com.novice.test.http.server;

import com.novice.test.http.server.util.SocketChannelReader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NIOServer {
	private static final ExecutorService SERVICE = Executors.newCachedThreadPool();
	private static final AtomicInteger COUNT = new AtomicInteger(0);

	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.socket().bind(new InetSocketAddress(8000));
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		while (true) {
			if (selector.selectNow() == 0) {
				continue;
			}
			Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = ite.next();
				ite.remove();
				if (key.isAcceptable()) {
					acceptKey(selector, key);
				} else if (key.isReadable()) {
					readKey(key);
				} else if (key.isWritable()) {
					writeKey(key);
				}
			}
		}
	}

	private static void acceptKey(Selector selector, SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel channel = server.accept();
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}

	private static void readKey(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		int i = COUNT.addAndGet(1);
		System.out.println("客户端" + i + "连接: ");
		SERVICE.execute(() -> {
			try {
				SocketChannelReader reader = new SocketChannelReader(channel);
				String line = reader.readLine();
				while (!line.trim().equals("")) {
					System.out.println(line);
					line = reader.readLine();
				}
				Thread.sleep(20L);
				key.attach(new Attachment(i, "Hello World!"));
				key.interestOps(SelectionKey.OP_WRITE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
	}

	private static void writeKey(SelectionKey key) throws IOException {
		Attachment attachment = (Attachment) key.attachment();
		SocketChannel socketChannel = null;
		try {
			socketChannel = (SocketChannel) key.channel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			String message = "HTTP/1.1 200 OK\nContent-Type: text/html;charset=utf-8\n\n" + attachment.result;
			buffer.put(message.getBytes(StandardCharsets.UTF_8));
			buffer.flip();
			socketChannel.write(buffer);
		} finally {
			if (socketChannel != null) {
				socketChannel.shutdownInput();
				socketChannel.close();
				System.out.println("客户端" + attachment.i + "断开----------------\n");
			}
		}
	}

	private static class Attachment {
		private int i;
		private String result;

		private Attachment(int i, String result) {
			this.i = i;
			this.result = result;
		}
	}

}
