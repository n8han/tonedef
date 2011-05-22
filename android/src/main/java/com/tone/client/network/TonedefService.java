package com.tone.client.network;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import java.util.Map;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.HttpVersion;

import tonedef.util.Music;
import tonedef.util.Parser;

import com.google.gson.Gson;

public class TonedefService {

	public interface StatusListener {
		
		void onUpdate(String jsonStatus);
		
	}

    private static String url = "http://10.0.2.2:8080/";
    
    private Parser parser = new Parser();
    
    private InputStream stream;
    
    StringBuilder b = new StringBuilder();
    
    public TonedefService() {
		
    }
    
    private LinkedHashSet<StatusListener> listeners = new LinkedHashSet<StatusListener>();
    
    public void addListener(StatusListener listener) {
    	listeners.add(listener);
    }
    
    public void removeListener(StatusListener listener) {
    	listeners.remove(listener);
    }

    public void start(String id) {
        try {
	    	URL u = new URL("http://10.11.254.241:8080/channel/foo");
			stream = u.openStream();    
			while (true) {
				int k = stream.read();
				if (k == -1) {
					continue;
				}
				char c = (char) k;
				if (c == '\n') {
					try {
						System.out.println(b.toString());
						Music o = parser.parse(b.toString());
						System.out.println(o.name);
					} catch (Exception e) {
						e.printStackTrace();
					}
					onUpdate(b.toString());
					
					b.setLength(0);
				} else {
					b.append(c);
				}
			}
    	} catch(Exception e) {
    		e.printStackTrace();
            }
    }
    
    Gson gson = new Gson();
    
    public void push(Object obj) {
    	try {
    		HttpPost p = new HttpPost("http://10.11.254.241:8080/channel/foo");		
            p.setEntity(new StringEntity(gson.toJson(obj)));
            p.setHeader("Content-type", "application/json");
            BasicHttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpClient client = new DefaultHttpClient(params);
            BasicResponseHandler responseHandler = new BasicResponseHandler();
	    	String resp = client.execute(p, responseHandler);	    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void onUpdate(String status) {
    	for(StatusListener l : listeners) {
    		l.onUpdate(status);
    	}
    }
}
