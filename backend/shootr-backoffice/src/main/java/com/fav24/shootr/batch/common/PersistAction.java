package com.fav24.shootr.batch.common;

public class PersistAction {
	public final static int CREATE = 1;
	public final static int UPDATE = 2;
	public final static int IGNORE = 3;
	
	public Long domainId;
	public int op;

	public PersistAction(Long idMatch, int op) {
		super();
		this.domainId = idMatch;
		this.op = op;
	}

	public PersistAction(int op) {
		super();
		this.op = op;
	}
}