package spring.webcrawler;

import java.net.URI;

public interface Spider {
	public void startCrawl(URI uri, int depth);
}
