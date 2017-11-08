package spring.webcrawler;

import java.net.URI;
import java.util.List;

public interface HttpParser {
	public List<URI> parseLinks(URI uri, String content);
}
