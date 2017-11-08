import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import spring.webcrawler.HttpClientImpl;
import spring.webcrawler.HttpParser;
import spring.webcrawler.HttpParserImpl;
import spring.webcrawler.IndexingImpl;
import spring.webcrawler.Search;
import spring.webcrawler.SearchImpl;
import spring.webcrawler.Spider;
import spring.webcrawler.SpiderImpl;
import spring.webcrawler.UrlBean;

public class CrawlerViewController {

	private static Search searcher = new SearchImpl();
	private static Spider spider = new SpiderImpl();
	List<UrlBean> urlBeans = new ArrayList<UrlBean>();

    @FXML
    private Label lblSite;

    @FXML
    private TextField txtSite;

    @FXML
    private TextField txtCrawt;

    @FXML
    private Label lblCrawt;

    @FXML
    private Label lblSearch;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView<UrlBean> tblSearch;

    @FXML
    void onActionSearch(ActionEvent event) {
		try {
			((SpiderImpl)spider).setCrawler(new HttpClientImpl());
			HttpParser httpParser = new HttpParserImpl();
			((HttpParserImpl)httpParser).setIndexCreator(new IndexingImpl());
			((SpiderImpl)spider).setParser(httpParser);
			spider.startCrawl(new URI(txtSite.getText()),
					Integer.parseInt(txtCrawt.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tblSearch.setEditable(true);
		urlBeans = searcher.search(txtSearch.getText());
    	
    	ObservableList<UrlBean> data = tblSearch.getItems();
		
		for(UrlBean bean : urlBeans) {
			data.add(bean);
			System.out.println(bean.getContent());
			System.out.println(bean.getTitle());
			System.out.println(bean.getUrl());
		}
    }
}
