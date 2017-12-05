package com.oppsci.ngraphstore.graph;

public class Triple<T> {

	private T subject;
	private T predicate;
	private T object;
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
	
}
