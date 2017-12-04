package con.oppsci.ngraphstore.query;

import java.util.List;

import com.oppsci.ngraphstore.sparql.elements.BGPElement;
import com.oppsci.ngraphstore.sparql.elements.Filter;

public class Query {

	private List<BGPElement> bgpelements;
	private List<Filter> filter;
	private List<String> vars;

	public Query(String query) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the bgpelements
	 */
	public List<BGPElement> getBGPElements() {
		return bgpelements;
	}

	/**
	 * @param bgpelements the bgpelements to set
	 */
	public void setBGPElements(List<BGPElement> bgpelements) {
		this.bgpelements = bgpelements;
	}

	/**
	 * @return the filter
	 */
	public List<Filter> getFilter() {
		return filter;
	}

	/**
	 * @param filter the filters to set
	 */
	public void setFilter(List<Filter> filter) {
		this.filter = filter;
	}

	public List<String> getVars() {
		return vars;
	}
}
