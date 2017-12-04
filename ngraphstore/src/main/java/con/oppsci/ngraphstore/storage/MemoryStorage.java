package con.oppsci.ngraphstore.storage;


import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateRequest;

/**
 * In memory storage using plain jena models. <br/>
 * Only for testing purpose!
 * 
 * @author f.conrads
 *
 */
public class MemoryStorage {

	Model data = ModelFactory.createDefaultModel();
	
	public MemoryStorage(String ntFile) {
		data.read(ntFile);
	}
	
	public ResultSet select(String sparql) {
		System.out.println(sparql);
		Query query = QueryFactory.create(sparql);
		QueryExecution exec = QueryExecutionFactory.create(query, data);
		return exec.execSelect();
	}
	
	public void update(String query) {
		UpdateRequest update = new UpdateRequest();
		update.add(query);
		UpdateAction.execute(update, data);
				
	}

	public void putData(Model data) {
		this.data = data;
	}

	public void postData(Model data) {
		this.data.add(data);
	}
	
}
