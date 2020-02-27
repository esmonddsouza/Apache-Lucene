package parsers;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import helpers.UTILHelper;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/************************************************************************************************************
 * 
 * @author Esmond Dsouza
 * @version 1.0
 * @description Parser class used to read and parse the result, create the output file and save it at the 
 * 				mentioned path.
 *
 ***********************************************************************************************************/
public class ResultParser {

	private static final DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy@HH-mm");
	
	List<ResultWrapper> results;
	
	long currentTime;
	
	String filePath;
	String fileName;
	public ResultParser(List<ResultWrapper> results, String outputDir) throws IOException {
		File dir = new File(outputDir);
		UTILHelper.createDirIfNotExists(dir);
		this.results = results;
		this.currentTime = System.currentTimeMillis();
		Date date = new Date();
		this.fileName = "Result" + "_" + sdf.format(date);
		this.filePath = outputDir + "/" + fileName;
		File file = new File(filePath);
		file.createNewFile();
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	public String writeResults() {
		try (Writer writer = Files.newBufferedWriter(Paths.get(filePath))) {
			ColumnPositionMappingStrategy mappingStrategy= new ColumnPositionMappingStrategy(); 
			mappingStrategy.setType(ResultParser.ResultWrapper.class); 
	        String[] columns = new String[]{ "resultQueryNumber", "resultQuery", "resultDocId", "resultRank", "resultScore", "resultExp" }; 
	        mappingStrategy.setColumnMapping(columns); 
			StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
					.withSeparator(' ')
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
				    .withMappingStrategy(mappingStrategy)
					.build();
			beanToCsv.write(results);
			return this.fileName;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvDataTypeMismatchException e) {
			e.printStackTrace();
		} catch (CsvRequiredFieldEmptyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static class ResultWrapper {

		private String resultQueryNumber;
		private String resultQuery;
		private String resultDocId;
		private String resultRank;
		private String resultScore;
		private String resultExp;
		
		public ResultWrapper(String queryNumber, String q0, String docId, String rank, String score, String exp) {
			this.setResultQueryNumber(queryNumber);
			this.setResultQuery(q0);
			this.setResultDocId(docId);
			this.setResultRank(rank);
			this.setResultScore(score);
			this.setResultExp(exp);
		}

	
		public String getResultQueryNumber() {
			return resultQueryNumber;
		}


		public void setResultQueryNumber(String resultQueryNumber) {
			this.resultQueryNumber = resultQueryNumber;
		}


		public String getResultQuery() {
			return resultQuery;
		}


		public void setResultQuery(String resultQuery) {
			this.resultQuery = resultQuery;
		}


		public String getResultDocId() {
			return resultDocId;
		}


		public void setResultDocId(String resultDocId) {
			this.resultDocId = resultDocId;
		}


		public String getResultRank() {
			return resultRank;
		}


		public void setResultRank(String resultRank) {
			this.resultRank = resultRank;
		}


		public String getResultScore() {
			return resultScore;
		}


		public void setResultScore(String resultScore) {
			this.resultScore = resultScore;
		}


		public String getResultExp() {
			return resultExp;
		}


		public void setResultExp(String resultExp) {
			this.resultExp = resultExp;
		}
	}

}
