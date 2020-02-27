package luceneSearchEngine;

import java.io.IOException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;


/************************************************************************************************************
 * 
 * @author Esmond Dsouza
 * @version 1.0
 * @description Java class used to retrieve the most relevant documents based on the inputed query and the 
 * 				indices created by the Indexer.java file.
 *
 ***********************************************************************************************************/
public class Searcher {
	
	public ScoreDoc[] searchQuery(Query query, int hitsPerPage, Directory indexDir, Similarity similarity) throws Exception {
		try {
			IndexReader reader = DirectoryReader.open(indexDir);
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(similarity);
			TopDocs docs = searcher.search(query, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;
			reader.close();
			return hits;
		} catch (IOException e) {
			System.out.println("Failed to execute query" + query + " " + e.getMessage());
		}
		return null;
	}
}
