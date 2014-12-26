package com.devoler.aicup.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.LocalStrategy;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.RemoteStrategy;
import com.devoler.aicup.host.model.Side;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RemoteStrategyAdapter {
	public RemoteStrategyAdapter(final LocalStrategy localStrategy, final int port) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				try (InputStream is = exchange.getRequestBody()) {
					int nRead;
					byte[] data = new byte[256];
					while ((nRead = is.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}
					buffer.flush();
				}
				String inJson = new String(buffer.toByteArray());
				// System.out.println(inJson);
				JsonObject root = new JsonParser().parse(inJson).getAsJsonObject();
				Side yourSide = Side.valueOf(root.get(RemoteStrategy.JSON_NAME_YOUR_SIDE).getAsString().toUpperCase());
				Battlefield battlefield = Battlefield.fromJson(root.get(RemoteStrategy.JSON_NAME_FIELD)
						.getAsJsonObject());

				Move move = localStrategy.getMoveLocally(battlefield, yourSide);
				String outJson = move.toJson().toString();
				byte[] outContent = outJson.getBytes();

				exchange.sendResponseHeaders(200, outContent.length);
				exchange.getResponseBody().write(outContent);
				exchange.getResponseBody().close();
			}
		});
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
	}

	public static void main(String[] args) {
		try {
			new RemoteStrategyAdapter(new SimpleMinimaxStrategy(), 8080);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
