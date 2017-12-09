package com.oppsci.ngraphstore.web.rest.rdf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.oppsci.ngraphstore.processor.UpdateProcessor;

/**
 * The Controller for SPARQL Update queries.
 * 
 * @author f.conrads
 *
 */
public class UpdateRestController {
	
	@Autowired
	private UpdateProcessor directProcessor;

	/**
	 * Processes an update query either using a POST or PUT method
	 * 
	 * @param query the SPARQL update query
	 * @param isPost true if POST, false if PUT
	 * @return true if succeeded, otherwise false
	 */
	public Boolean processUpdate(String query, boolean isPost) {
		// TODO Auto-generated method stub
		
		//TODO do not forget to set graph in query if no default is
		return null;
	}


	public String exchangeTriples(JSONObject parse) {
		JSONArray updates = (JSONArray) parse.get("update");
		for(int i=0;i<updates.size();i++) {
			JSONObject update = (JSONObject) updates.get(i);
			JSONObject currentQuad = (JSONObject)update.get("current");
			String[] currentTerms = new String[4];
			currentTerms[0] = currentQuad.get("subject").toString();
			currentTerms[1] = currentQuad.get("predicate").toString();
			currentTerms[2] = currentQuad.get("object").toString();
			currentTerms[3] = currentQuad.get("graph").toString();
			JSONObject newQuad = (JSONObject)update.get("new");
			String[] newTerms = new String[4];
			newTerms[0] = newQuad.get("subject").toString();
			newTerms[1] = newQuad.get("predicate").toString();
			newTerms[2] = newQuad.get("object").toString();
			newTerms[3] = newQuad.get("graph").toString();
			directProcessor.quadUpdate(currentTerms, newTerms);
		}
		return "";
	}
	
	public String exchangeTriples(JSONObject old, JSONObject newTriples) {
		JSONArray olds = (JSONArray) old.get("graph");
		JSONArray news = (JSONArray) newTriples.get("graph");
		JSONObject updates = new JSONObject();
		JSONArray updateArr = new JSONArray();
		for(int i=0;i<olds.size();i++) {
			JSONObject updateObject = new JSONObject();
			if(!checkJSONQuadEquals((JSONObject)olds.get(i), (JSONObject)news.get(i))) {
				updateObject.put("current", olds.get(i));
				updateObject.put("new", news.get(i));
				updateArr.add(updateObject);
			}
		}
		updates.put("update", updateArr);
		exchangeTriples(updates);
		return newTriples.toJSONString();
	}
	
	private boolean checkJSONQuadEquals(JSONObject obj1, JSONObject obj2) {
		boolean equals = obj1.get("subject").equals(obj2.get("subject"));
		equals = equals && obj1.get("predicate").equals(obj2.get("predicate"));
		equals = equals && obj1.get("object").equals(obj2.get("object"));
		equals = equals && obj1.get("graph").equals(obj2.get("graph"));
		return equals;
	}
	
}
