package com.tone.client.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;

public class TonedefService {

	public interface StatusListener {
		
		void onUpdate(Object status);
		
	}

    private static String url = "http://10.0.2.2:8080/";
    
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
	    	URL u = new URL("http://10.0.2.2:8080/channel/foo");
			stream = u.openStream();    
			while (true) {
				int k = stream.read();
				if (k == -1) {
					continue;
				}
				char c = (char) k;
				if (c == '\n') {								
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
    
    private void onUpdate(Object status) {
    	for(StatusListener l : listeners) {
    		l.onUpdate(status);
    	}
    }
}
