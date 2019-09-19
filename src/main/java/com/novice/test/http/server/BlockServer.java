package com.novice.test.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		ServerSocket serverSocket = new ServerSocket(8000);
		int i = 0;
		while (true) {
			Socket client = serverSocket.accept();
			i++;
			System.out.println("客户端" + i + "连接: ");
			InputStream in = client.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (!line.trim().equals("")) {
				System.out.println(line);
				line = br.readLine();
			}
			Thread.sleep(20L);
			try (PrintStream out = new PrintStream(client.getOutputStream(), true)) {
				out.println("HTTP/1.1 200 OK");
				out.println("Content-Type: text/html;charset=utf-8");
				out.println();
				out.println("Hello World!");
			}
			client.close();
			System.out.println("客户端" + i + "断开----------------\n");
		}
	}

}
