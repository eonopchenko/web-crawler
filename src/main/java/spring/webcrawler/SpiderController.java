package spring.webcrawler;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@EnableAutoConfiguration
public class SpiderController {

	private static Logger logger = LoggerFactory
			.getLogger(SpiderController.class);
	private static BeanFactory factory;
	private static Search search;
	private static Spider spider;

	List<UrlBean> urlBeans = new ArrayList<UrlBean>();

	public SpiderController() {
		factory = new ClassPathXmlApplicationContext("spring.xml");
		spider = factory.getBean("spider", SpiderImpl.class);
		search = factory.getBean("search", SearchImpl.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpiderController.class, args);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/search")
	public String search(CrawlerBean crawlerBean, Map<String, Object> model) {

		if ("".equals(crawlerBean.getSearchKey().trim())) {
			crawlerBean.setMessage("Please input key you want to search.");
			return "search";
		}

		urlBeans = search.search(crawlerBean.getSearchKey());

		model.put("urlBeans", urlBeans);

		logger.debug("Search is successful!");
		return "search";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/crawl")
	public String crawl(CrawlerBean crawlerBean) {
		try {

			spider.startCrawl(new URI(crawlerBean.getUri()),
					crawlerBean.getDepth());
			logger.debug("Crawler is successful!");

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return "search";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/spider")
	public String webcrawler(Map<String, Object> model) {
		return "spider";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/search")
	public String search(Map<String, Object> model) {
		return "search";
	}
}
