package luceneSearchEngine;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;


/************************************************************************************************************
 * 
 * @author Esmond Dsouza
 * @version 1.0
 * @description Java class used to index the documents using Apache Lucene and save the indices at the 
 * 				mentioned path to be used by searcher.
 *
 ***********************************************************************************************************/
public class Indexer {
	public void createIndexes(Analyzer analyzer, IndexWriterConfig.OpenMode openMode, Similarity similarity, Directory indexDir, List<Document> docs) throws Exception {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(openMode);
		config.setSimilarity(similarity);
		try (IndexWriter indexWriter = new IndexWriter(indexDir, config)) {
			indexWriter.addDocuments(docs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
