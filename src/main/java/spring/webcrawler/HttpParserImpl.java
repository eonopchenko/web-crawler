package spring.webcrawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.jerry.JerryFunction;
import jodd.lagarto.LagartoParser;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.LagartoDOMBuilderTagVisitor;
import jodd.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParserImpl implements HttpParser {

	private static Logger logger = LoggerFactory
			.getLogger(HttpParserImpl.class);
	private Indexing indexCreator;
	private List<URI> uris;
	private URI uri;

	public Indexing getIndexCreator() {
		return indexCreator;
	}

	public void setIndexCreator(Indexing indexCreator) {
		this.indexCreator = indexCreator;
	}

	public List<URI> parseLinks(URI uri, String content) {
		this.uri = uri;
		uris = new LinkedList<URI>();
		
		getBodyText(content);

		if (samePageCheckByTitle(content)) {
			logger.debug("Already visited because of same title" + uri.toString());
		} else {
			if (1 == SpiderImpl.currentDepth) {
				logger.debug("Stop parsing href because current depth is 1.");
			} else {
				getLinks(content);
			}
		}
		
		logger.debug("Found (" + uris.size() + ") links in [" + uri.toString()
				+ "]");

		return uris;
	}

	private void getBodyText(String content) {
		JerryParser jerryParser = Jerry.jerry();

		LagartoDOMBuilder lagartoDOMBuilder = (LagartoDOMBuilder) jerryParser
				.getDOMBuilder();

		LagartoDOMBuilderTagVisitor tagVisitor = new LagartoDOMBuilderTagVisitor(
				lagartoDOMBuilder);

		lagartoDOMBuilder.enableHtmlMode();

		final StringBuilder result = new StringBuilder();
		LagartoParser parser = new LagartoParser(content, false);

		parser.parse(new TagAdapter(tagVisitor) {

			public void text(CharSequence text) {

				String str = text.toString();
				str = StringUtil.removeChars(str, "\r\n\t\b");
				if (str.trim().length() != 0) {
					result.append(str);
				}
			}

		});

		indexCreator.indexing(uri, result.toString());
	}

	private boolean samePageCheckByTitle(String content) {
		int start = content.indexOf("<title>") + 7;
		int end = content.indexOf("</title>");

		if (start < 0 || end < 0 || start > end) {
			return false;
		}

		String title = content.substring(start, end);
		if (null != SpiderImpl.titles
				&& !SpiderImpl.titles.contains(title)) {
			SpiderImpl.titles.add(title);
			return false;
		}
		return true;
	}

	private void getLinks(String content) {

		Jerry doc = Jerry.jerry(content);
		doc.$("a").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {

				URI uri;
				String link = $this.attr("href");

				if (null == link || "".equals(link.trim())) {
					logger.debug("Blank href!");
					return true;
				}

				if (!link.startsWith(SpiderConstant.PREFIX_HTTP)
						&& !link.startsWith(SpiderConstant.PREFIX_HTTPS)
						&& !link.startsWith(SpiderConstant.PREFIX_SLASH)) {
					logger.debug("Not a http href:" + link);
					return true;
				} else if (link.startsWith(SpiderConstant.PREFIX_SLASH)) {
					link = SpiderConstant.PREFIX_HTTP + SpiderImpl.HOST
							+ link;
				}

				try {
					uri = new URI(link);
					if (uris.contains(uri)) {
						logger.debug("Already existing href:" + link);
						return true;
					}

					if (!uri.getHost().contains(SpiderImpl.HOST_KEY)) {
						logger.debug("Other website href:" + link);
						return true;
					}
					
					if (!SpiderImpl.toVisit.contains(uri)) {
						SpiderImpl.toVisit.add(uri);
						uris.add(uri);
					}
				} catch (URISyntaxException e) {
					logger.error("URISyntaxException with href:" + link);
					return true;
				}

				return true;
			}
		});
	}
}
