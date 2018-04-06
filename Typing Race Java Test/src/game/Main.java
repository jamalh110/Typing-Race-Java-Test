package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

public class Main {

	private static Scanner key = new Scanner(System.in);
	int loginPort = 8080;
	int gamePort = 9090;
	HttpClient gameClient;
	static ArrayBlockingQueue<String> updatePassage = new ArrayBlockingQueue<String>(100);
	static ArrayBlockingQueue<String> updateStatus = new ArrayBlockingQueue<String>(100);
	public static void main(String[] args) {
		Interface frame = new Interface();
		frame.display();
		Main main = new Main();
		frame.setMain(main);
		
		while(true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			String passage = updatePassage.poll();
			if(passage!=null) {
				frame.passage.setText(passage);
			}
			
			String status = updateStatus.poll();
			if(status!=null) {
				frame.matchStatus.setText(status);
			}
		}
		
	}
	public Main() {
		gameClient = HttpClientBuilder.create().build();
	}
	
	public String getOpponentName(UUID sessionID, UUID matchID) {
		return sendGameMessage(sessionID, matchID, "oppName");
	}
	public String getPassage(UUID sessionID, UUID matchID) {
		return sendGameMessage(sessionID, matchID, "getPassage");
	}
	public String getMatchStatus(UUID sessionID, UUID matchID) {
		return sendGameMessage(sessionID, matchID, "matchStatus");
	}
	public String sendWord(UUID sessionID, UUID matchID, String word) {
		return sendGameMessageWithContent(sessionID, matchID, "word",word);
	}
	
	//log in to the login server. should return the login key/cookie.
	public String login(String username, String password) {
		HttpClient client = HttpClientBuilder.create().build();
		int port = loginPort;
		String url = "http://localhost:"+port+"/TypingRaceLoginServer/Login?";
		url+="username="+username;
		url+="&password="+password;
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", CoreProtocolPNames.USER_AGENT);
		HttpResponse response = null;
		try {
			response = client.execute(request);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		}
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		}

		StringBuffer result = new StringBuffer();
		String line = "";
		try {
			while ((line = rd.readLine()) != null) {
				result.append(line);
				//System.out.println(result);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		}
		return result.toString();
	}
	public UUID matchRequest(String loginKey) {
		HttpClient client = HttpClientBuilder.create().build();
		int port = loginPort;
		String url = "http://localhost:"+port+"/TypingRaceLoginServer/MakeMatch?";
		url+="loginKey="+loginKey;
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", CoreProtocolPNames.USER_AGENT);
		HttpResponse response = null;
		try {
			response = client.execute(request);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		StringBuffer result = new StringBuffer();
		String line = "";
		try {
			while ((line = rd.readLine()) != null) {
				result.append(line);
				//System.out.println(result);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return UUID.fromString(result.toString());
	}
	public String queryMatch(UUID sessionID) {
		try {
		int port = gamePort;
		String url = "http://localhost:"+port+"?";
		url+="type=getMatch&";
		url+="sessionID="+sessionID.toString();
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", CoreProtocolPNames.USER_AGENT);
		HttpResponse response = null;
		try {
			response = gameClient.execute(request);
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return null;
		}
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return null;
		}

		StringBuffer result = new StringBuffer();
		String line = "";
		try {
			while ((line = rd.readLine()) != null) {
				result.append(line);
				//System.out.println(result);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return null;
		}
		if(!result.toString().equals("no match")){
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return result.toString();
		}
		else {
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return null;
		}
		}catch(Exception e) {
			
			return null;
		}
	}
	public boolean connectToMatch(UUID sessionID, UUID matchID, UUID threadID) {
		try {
		//gamlient = HttpClientBuilder.create().build();
		int port = gamePort;
		String url = "http://localhost:"+port+"?";
		url+="type=connect&";
		url+="sessionID="+sessionID.toString()+"&";
		url+="matchID="+matchID.toString()+"&";
		url+="threadID="+threadID.toString();
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", CoreProtocolPNames.USER_AGENT);
		HttpResponse response = null;
		try {
			response = gameClient.execute(request);
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return false;
		}
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return false;
		}

		StringBuffer result = new StringBuffer();
		String line = "";
		try {
			while ((line = rd.readLine()) != null) {
				result.append(line);
				//System.out.println(result);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return false;
		}
		if(result.toString().equals("Connection Success")) {
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return true;
		}
		else {
			System.err.print("failed: " + result.toString());
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return false;
		}
		}catch(Exception e) {
			return false;
		}
	}
	
	//helper method
	String sendGameMessage(UUID sessionID, UUID matchID, String message) {
		try {
			
			int port = gamePort;
			String url = "http://localhost:"+port+"?";
			url+="type=message&";
			url+="sessionID="+sessionID.toString()+"&";
			url+="matchID="+matchID.toString()+"&";
			url+="message="+message;
			HttpGet request = new HttpGet(url);
			request.addHeader("User-Agent", CoreProtocolPNames.USER_AGENT);
			HttpResponse response = null;
			try {
				response = gameClient.execute(request);
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			}
			BufferedReader rd = null;
			try {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			}

			StringBuffer result = new StringBuffer();
			String line = "";
			try {
				while ((line = rd.readLine()) != null) {
					result.append(line);
					//System.out.println(result);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			}
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return result.toString();
		}catch(Exception e) {
			
			return null;
		}
	}
	
	String sendGameMessageWithContent(UUID sessionID, UUID matchID, String message, String content) {
		try {
			
			int port = gamePort;
			String url = "http://localhost:"+port+"?";
			url+="type=message&";
			url+="sessionID="+sessionID.toString()+"&";
			url+="matchID="+matchID.toString()+"&";
			url+="message="+message+"&";
			url+="content="+content;
			HttpGet request = new HttpGet(url);
			request.addHeader("User-Agent", CoreProtocolPNames.USER_AGENT);
			HttpResponse response = null;
			try {
				response = gameClient.execute(request);
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			}
			BufferedReader rd = null;
			try {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			}

			StringBuffer result = new StringBuffer();
			String line = "";
			try {
				while ((line = rd.readLine()) != null) {
					result.append(line);
					//System.out.println(result);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			}
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			return result.toString();
		}catch(Exception e) {
			
			return null;
		}
	}
}