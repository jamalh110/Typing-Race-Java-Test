package game;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreProtocolPNames;

public class Main {

	private static Scanner key = new Scanner(System.in);

	public static void main(String[] args) {
		try {
			apache();
			
			// sendSocket();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	static void apache() throws Exception{
		String url = "http://localhost:8080/?message=yough";

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", CoreProtocolPNames.USER_AGENT);
		while(true) {
		HttpResponse response = client.execute(request);

		System.out.println("Response Code : "
		                + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		System.out.print(result);
		Thread.sleep(40000);
		}
	}
	private static void sendGet() throws Exception {

		String url = "http://localhost:8080/?message=yava";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		System.out.println("\nSending 'GET' request to URL : " + url);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			print("read");
			Thread.sleep(100);

		}
		in.close();
		con.disconnect();
		Thread.sleep(20000);
		
		
		System.out.println(response.toString());
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		 in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		 response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			print("read");
			Thread.sleep(100);

		}
		in.close();
		con.disconnect();
		Thread.sleep(10000);
	}

	static void print(String s) {
		System.out.println(s);
	}

	private static void sendSocket() {
		try {

			Socket s = new Socket("127.0.0.1", 8080);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

			out.println("hi");

			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				
				Thread.sleep(1000);
				print(response.toString());
				
			}
			in.close();
			s.close();
			
			System.out.println(response.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}