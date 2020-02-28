package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import helpers.UTILHelper;


/************************************************************************************************************
 * 
 * @author Esmond Dsouza
 * @version 1.0
 * @description Parser class used to read and create queries objects from the .qry file present at 
 * 				the mentioned path.
 *
 ***********************************************************************************************************/
public class QueryParser {

	MultiFieldQueryParser multiFieldQP;

	public List<Query> loadQueries(String filePath, Analyzer analyzer) {
		List<Query> queries = new ArrayList<>();
		Integer queryCount = 0;
		HashMap<String,Float> boosts = new HashMap<String,Float>();
		boosts.put("title", 0.30f);
		//boosts.put("author", 1f);
		boosts.put("content", 0.70f);
		this.multiFieldQP = new MultiFieldQueryParser(new String[] {"title","content"}, analyzer, boosts);		
		
		String queryId = "";
		StringBuilder queryStr = new StringBuilder();
		String querySeparator = ".I";
		File file = new File(filePath);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = UTILHelper.deleteSpecialCharacters(line);

				String newSeparator = UTILHelper.checkForSeparators(line);
				if (newSeparator != null) {
					querySeparator = newSeparator;
					if (!newSeparator.equals(".I"))
						line = br.readLine();
				}
				if (querySeparator.equals(".I")) {
					queryId = line.split(" ")[1];
					queryCount++;
					if (queryCount > 1) {
						Query query = createQuery(queryId, queryStr.toString());
						queries.add(query);
					}
					queryId = "";
					queryStr = new StringBuilder();
				} else if (querySeparator.equals(".W")) {
					queryStr.append(line);
				}
			}
			Query query = createQuery(queryId, queryStr.toString());
			queries.add(query);
			return queries;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private Query createQuery(String queryId, String queryStr) throws IOException, ParseException {
		Query q = null;
		q = multiFieldQP.parse(queryStr);
		return q;
	}
}
