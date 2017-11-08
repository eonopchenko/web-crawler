package spring.webcrawler;

import java.net.URI;

public interface Indexing {
	public void createIndex(URI uri, String content);
}
