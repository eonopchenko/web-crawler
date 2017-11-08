package spring.webcrawler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IndexingImpl implements Indexing {

	private static Logger logger = LoggerFactory
			.getLogger(IndexingImpl.class);
	
	@Override
	public void indexing(URI uri, String content) {
		IndexWriterConfig iwc;
		IndexWriter writer = null;
		Document doc = null;
		try {
			Directory directory = FSDirectory.open(new File(
					SpiderConstant.LUCENE_DAT_PATH));

			iwc = new IndexWriterConfig(Version.LUCENE_36,
					new StandardAnalyzer(Version.LUCENE_36));

			writer = new IndexWriter(directory, iwc);

			doc = new Document();
			doc.add(new Field(SpiderConstant.LUCENE_FIELD_URI, uri.toString(),
					Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field(SpiderConstant.LUCENE_FIELD_CONTENT, content,
					Field.Store.YES, Field.Index.ANALYZED));
			writer.addDocument(doc);

			logger.debug("Indexing is succesful for " + uri.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
