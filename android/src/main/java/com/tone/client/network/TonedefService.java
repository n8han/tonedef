package com.tone.client.network;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import tonedef.util.Parser;

import com.google.gson.Gson;

public class TonedefService {

	public interface StatusListener {
		
		void onUpdate(Object status);
		
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
					Object o = parser.parse(b.toString());
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

            HttpClient client = new DefaultHttpClient();
	    	HttpResponse resp = client.execute(p);
	    	resp.getEntity().consumeContent();
	    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void onUpdate(Object status) {
    	for(StatusListener l : listeners) {
    		l.onUpdate(status);
    	}
    }
}
