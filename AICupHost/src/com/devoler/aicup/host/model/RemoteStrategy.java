package com.devoler.aicup.host.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class RemoteStrategy implements Strategy {
	public static final String JSON_NAME_FIELD = "field";
	public static final String JSON_NAME_YOUR_SIDE = "yourSide";
	private final String host;

	public RemoteStrategy(final String host) {
		this.host = host;
	}

	@Override
	public Result getMove(Battlefield battlefield, Side yourSide) {
		try {
			JsonObject outJson = new JsonObject();
			outJson.addProperty(JSON_NAME_YOUR_SIDE, yourSide.name());
			outJson.add(JSON_NAME_FIELD, battlefield.toJson());

			byte[] outContent = outJson.toString().getBytes();
			URL url = new URL(host);
			URLConnection urlConnection = url.openConnection();
			if (!(urlConnection instanceof HttpURLConnection)) {
				throw new RuntimeException("Not an HTTP host: " + host);
			}

			HttpURLConnection connection = (HttpURLConnection) urlConnection;
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(outContent.length));
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			OutputStream os = connection.getOutputStream();
			long startTime = System.currentTimeMillis();
			os.write(outContent);
			os.flush();
			os.close();

			int responseCode = connection.getResponseCode();
			long endTime = System.currentTimeMillis();

			if (responseCode != 200) {
				throw new Throwable("Response code: " + responseCode);
			}

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try (InputStream is = connection.getInputStream()) {
				int nRead;
				byte[] data = new byte[256];
				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
			}

			String inJson = new String(buffer.toByteArray(), "utf-8");
			JsonElement root = new JsonParser().parse(inJson);
			return new Result(endTime - startTime, Move.fromJson(root.getAsJsonObject(), battlefield));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void main(String[] args) {
		System.out.println(new RemoteStrategy("http://localhost:8000/").getMove(Game.initBattlefield(), Side.RED));
	}
}
