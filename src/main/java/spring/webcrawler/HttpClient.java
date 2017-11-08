package spring.webcrawler;

import java.net.URI;

public interface HttpClient {
	public boolean get(URI uri, StringBuilder content);
}
