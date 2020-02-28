package luceneSearchEngine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilterFactory;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.ClassicTokenizerFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import helpers.UTILHelper;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import parsers.DocumentParser;
import parsers.QueryParser;
import parsers.ResultParser;


/************************************************************************************************************
 * 
 * @author Esmond Dsouza
 * @version 1.0
 * @description Main java class used for accepting the arguments and running the entire project.
 *
 ***********************************************************************************************************/
public class App {
	
	public static String DOCUMENT_PATH = "";
	public static String INDEX_PATH = "";
	public static String QUERIES_PATH = "";
	public static String RESULTS_PATH = "";
	public static final String[] SEPERATORS = new String[]{".I",".T",".A",".B", ".W"};
	
	public static void main(String[] args) throws Exception {
		
		if(args.length != 7)
			System.out.println(args.length +" Please mention all the parameters\n1. DOCUMENT PATH\n2. INDEX PATH\n3. QUERIES PATH\n"
					+ "4. RESULTS PATH\n5. ANALYZER\n6. SIMILARITY\n7. HITS PER PAGE");
		
		else {
			System.out.println("Successfully read arguments");
			DOCUMENT_PATH = args[0];
			INDEX_PATH = args[1];
			QUERIES_PATH = args[2];
			RESULTS_PATH = args[3];
			int selectedAnalyzerType = Integer.valueOf(args[4]);
			int selectedSimilarity = Integer.valueOf(args[5]);
			int hitspp = Integer.valueOf(args[6]);

			Analyzer analyzer = null;
			Similarity similarity = null;

	        switch(selectedAnalyzerType) {
		        case 1:
					analyzer = new WhitespaceAnalyzer(); //0.2524  0.1868
					break;
		        case 2:
					analyzer = new EnglishAnalyzer(UTILHelper.createStopWordsCharArray()); //0.3601 BM25 0.3697
					break;
		        case 3:
					analyzer = new SimpleAnalyzer();
					break;
		        case 4:
					analyzer = new StopAnalyzer(UTILHelper.createStopWordsCharArray()); //0.3110
					break;
		        case 5:
					analyzer = createCustomAnalyzer();
					break;
		        case 6:
					analyzer = new StandardAnalyzer(); //0.3417
					break;
				default:
					analyzer = new StandardAnalyzer(); //0.3417
					break;
	        }
	        
	        switch(selectedSimilarity) {
		        case 1:
		        	similarity = new ClassicSimilarity(); // TFIDFSimilarity implementation 0.3035
					break;
		        case 2:
		        	similarity = new BooleanSimilarity();
					break;
		        case 3:
		        	Similarity[] similarities = new Similarity[3];
		        	similarities[0] = new BM25Similarity(1.2f, 0.75f);
		        	similarities[1] = new BooleanSimilarity();
		        	similarities[2] = new ClassicSimilarity();
		        	similarity = new MultiSimilarity(similarities); //0.3332
					break;
		        case 4:
		        	similarity = new BM25Similarity(1.2f, 0.75f); //default values
					break;
		        case 5:
		        	similarity = new BM25Similarity(1.2f, 0.95f); 
					break;
				default:
					similarity = new BM25Similarity(1.2f, 0.95f); //0.3388
	        }
	        
			DocumentParser docParser = new DocumentParser();
			List<Document> documents = docParser.fetchDocuments(DOCUMENT_PATH);
			System.out.println("Documents fetched...");
			
			Directory indexDir = FSDirectory.open(Paths.get(INDEX_PATH));

			Indexer indexer = new Indexer();
			indexer.createIndexes(analyzer, IndexWriterConfig.OpenMode.CREATE, similarity, indexDir, documents);
			System.out.println("Documents indexed...");

			QueryParser queryParser = new QueryParser();
			List<Query> queries = queryParser.loadQueries(QUERIES_PATH, analyzer);
			System.out.println("Queries fetched...");

			List<ResultParser.ResultWrapper> results = new ArrayList<>();
			Searcher searchEngine = new Searcher();
			int queryCount = 1;
			for (Query query : queries) {
				ScoreDoc[] hits = searchEngine.searchQuery(query, hitspp, indexDir, similarity);
				for (int i = 0; i < hits.length; ++i) {
					int documentId = hits[i].doc;
					double score = hits[i].score;
					ResultParser.ResultWrapper resultWrapper = new ResultParser.ResultWrapper(queryCount + "", "0", (documentId + 1) + "", (i + 1) + "", score + "", "exp");
					results.add(resultWrapper);
				}
				queryCount++;
			}
			String resultFileName = "";
			try {
				System.out.println("Queries fired...");
				resultFileName = new ResultParser(results, RESULTS_PATH).writeResults();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Completed Successfully. Result file '" + resultFileName + "' created and saved at result path.");
		}
	}
	
	private static Analyzer createCustomAnalyzer() throws IOException {
        return CustomAnalyzer.builder()
                .withTokenizer(ClassicTokenizerFactory.class)
                .addTokenFilter(EnglishPossessiveFilterFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
    			.addTokenFilter(StopFilterFactory.class)
    			.addTokenFilter(SnowballPorterFilterFactory.class)
    			.addTokenFilter(EnglishMinimalStemFilterFactory.class)
                .build();
    }
}

/*
 * 
 * THINGS TO CHANGE
 * 
 * 1. USE SWITCH CASE FOR USER INPUT ON SIMILARITY AND ANALYZER done
 * 2. CHANGE PACKAGE STRUCTURE done
 * 3. CHANGE CODE done
 * 4. DELETE UNWANTED CLASSES done
 * 5. CHANGE VARIABLE NAMES
 * 6. CHANGE SEARCH FILE 
 * 
 */
