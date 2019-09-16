package com.novice.test.http.server;

import com.novice.test.http.server.util.InputStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpServer {

	public static void main(String[] args) throws IOException {
		ExecutorService service = Executors.newFixedThreadPool(100);
		ServerSocket serverSocket = new ServerSocket(8000);
		AtomicInteger count = new AtomicInteger(0);
		while (true) {
			Socket client = serverSocket.accept();
			service.execute(() -> {
				try {
					int i = count.addAndGet(1);
					System.out.println("客户端" + i + "连接: ");
					InputStream in = client.getInputStream();
					String line = InputStreamUtils.readLine(in);
					while (!line.trim().equals("")) {
						System.out.println(line);
						line = InputStreamUtils.readLine(in);
					}
					Thread.sleep(20L);
					try (PrintStream out = new PrintStream(client.getOutputStream(), true)) {
						out.println("HTTP/1.1 200 OK");
						out.println("Content-Type: text/html;charset=utf-8");
						out.println();
						out.println("Hello World!");
//						InputStream inputStream = HttpServer.class.getResourceAsStream("/test.html");
//						int byteCount;
//						byte[] bytes = new byte[1024 * 1024];
//						while ((byteCount = inputStream.read(bytes)) != -1) {
//							out.write(bytes, 0, byteCount);
//						}
//						inputStream.close();
					}
					client.close();
					System.out.println("客户端" + i + "断开----------------\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

}
