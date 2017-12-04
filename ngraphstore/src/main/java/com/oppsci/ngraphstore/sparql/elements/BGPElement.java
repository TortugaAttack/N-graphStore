package com.oppsci.ngraphstore.sparql.elements;

public class BGPElement implements Element {

	private String subject;
	private String predicate;
	private String object;
	private boolean subjectVar;
	private boolean objectVar;
	private boolean predicateVar;
	
	public BGPElement(String[] triple) {
		subject = triple[0];
		setPredicate(triple[1]);
		setObject(triple[2]);
		setSubjectVar(subject.startsWith("?"));
		setPredicateVar(subject.startsWith("?"));
		setObjectVar(subject.startsWith("?"));
	}
	
	/**
	 * @return the predicate
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param predicate the predicate to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the predicate
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate the predicate to set
	 */
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	/**
	 * @return the object
	 */
	public String getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * @return the subjectVar
	 */
	public boolean isSubjectVar() {
		return subjectVar;
	}

	/**
	 * @param subjectVar the subjectVar to set
	 */
	public void setSubjectVar(boolean subjectVar) {
		this.subjectVar = subjectVar;
	}

	/**
	 * @return the predicateVar
	 */
	public boolean isPredicateVar() {
		return predicateVar;
	}

	/**
	 * @param predicateVar the predicateVar to set
	 */
	public void setPredicateVar(boolean predicateVar) {
		this.predicateVar = predicateVar;
	}

	/**
	 * @return the objectVar
	 */
	public boolean isObjectVar() {
		return objectVar;
	}

	/**
	 * @param objectVar the objectVar to set
	 */
	public void setObjectVar(boolean objectVar) {
		this.objectVar = objectVar;
	}

}
