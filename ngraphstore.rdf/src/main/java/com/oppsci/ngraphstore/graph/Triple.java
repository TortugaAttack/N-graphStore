package com.oppsci.ngraphstore.graph;

public class Triple<T> {

	private T subject;
	private T predicate;
	private T object;
	private T graph;
	/**
	 * @return the subject
	 */
	public T getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(T subject) {
		this.subject = subject;
	}
	/**
	 * @return the predicate
	 */
	public T getPredicate() {
		return predicate;
	}
	/**
	 * @param predicate the predicate to set
	 */
	public void setPredicate(T predicate) {
		this.predicate = predicate;
	}
	/**
	 * @return the object
	 */
	public T getObject() {
		return object;
	}
	/**
	 * @param object the object to set
	 */
	public void setObject(T object) {
		this.object = object;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Triple<?>) {
			Triple<T> objTriple = (Triple<T>)obj;
			boolean equals = subject.equals(objTriple.getSubject());
			equals = equals && predicate.equals(objTriple.getPredicate());
			equals = equals && object.equals(objTriple.getObject());
			equals = equals && graph.equals(objTriple.getGraph());
			return equals;
		}
		return false; 
	}
	
	public T getGraph() {
		return graph;
	}
	
	public void setGraph(T graph) {
		this.graph = graph;
	}
	
}
