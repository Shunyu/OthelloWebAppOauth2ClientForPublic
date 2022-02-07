package com.beautifulsetouchi.AiOthelloGameClientServer.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultResponseResource {
	private long id;
	
	private long winnum;
	
	private long drawnum;
	
	private long losenum;
	
	public ResultResponseResource() {		
	}
	
	public ResultResponseResource(long id, long winnum, long drawnum, long losenum) {
		this.id = id;
		this.winnum = winnum;
		this.drawnum = drawnum;
		this.losenum = losenum;
	}
}
