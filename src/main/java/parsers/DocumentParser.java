package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import helpers.UTILHelper;


/************************************************************************************************************
 * 
 * @author Esmond Dsouza
 * @version 1.0
 * @description Parser class used to read the main document file cran.all.1400 and create individual 
 * 				document objects to be used for indexing.
 *
 ***********************************************************************************************************/
public class DocumentParser {
	public List<Document> fetchDocuments(String filePath) {
		List<Document> documents = new ArrayList<>();
		Integer documentCount = 0;
		String docId = "";
		StringBuilder textAbstract = new StringBuilder();
		String authors = "";
		String bibliography = "";
		StringBuilder content = new StringBuilder();

		String querySeparator = ".I";
		File file = new File(filePath);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String newSeparator = UTILHelper.checkForSeparators(line);
				if (newSeparator != null) {
					querySeparator = newSeparator;
					if(!newSeparator.equals(".I"))
						line = br.readLine();
				}
				if (".I".equals(querySeparator)) {

					docId = line.split(" ")[1];
					documentCount++;
					if (documentCount > 1) {
						Document doc = createDocumentWrapper(docId, textAbstract.toString(), authors, bibliography, 
								content.toString());
						documents.add(doc);
					}
					docId = "";
					textAbstract = new StringBuilder();
					authors = "";
					bibliography = "";
					content = new StringBuilder();
				} else if (querySeparator.equals(".T")) {
					textAbstract.append(line);
				} else if (querySeparator.equals(".A")) {
					authors += line;
				} else if (querySeparator.equals(".B")) {
					bibliography += line;
				} else if (querySeparator.equals(".W")) {
					content.append(line);
				}
			}
			br.close();
			Document doc = createDocumentWrapper(docId, textAbstract.toString(), authors, bibliography, content.toString());
			documents.add(doc);
			return documents;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	private Document createDocumentWrapper(String docId, String title, String authors, String bibliography, String content) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("docId", docId, Field.Store.YES));
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("author", authors, Field.Store.YES));
		doc.add(new StringField("bibliography", bibliography, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		return doc;
	}
}
