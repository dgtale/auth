package com.dg.sample.i18;

public interface MessageCode {
	
	/** System exception */
	String SYS001 = "SYS001";
	
	/** Invalid data */
	String BUS001 = "BUS001";
	
	/** Email address format not valid */
	String USR001 = "USR001";
	
	/** Password length is 8 chars */
	String USR002 = "USR002";
	
	/** Password pattern violation */
	String USR003 = "USR003";
	
	/** The user or password is wrong */
	String USR030 = "USR030";
	
	/** Unique Email Violation */
	String USR050 = "USR050";
	
	/** Account not created */
	String USR100 = "USR100";
}
