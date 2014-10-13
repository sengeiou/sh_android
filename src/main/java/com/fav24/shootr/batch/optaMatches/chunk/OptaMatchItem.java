package com.fav24.shootr.batch.optaMatches.chunk;

import java.util.ArrayList;
import java.util.List;

import com.fav24.shootr.dao.domain.Match;

public class OptaMatchItem {
	private List<Match> newMatches;
	private List<Match> updateMatches;

	public OptaMatchItem(){
		newMatches = new ArrayList<Match>();
		updateMatches = new ArrayList<Match>();
	}
	
	public List<Match> getNewMatches() {
		return newMatches;
	}

	public void setNewMatches(List<Match> newMatches) {
		this.newMatches = newMatches;
	}

	public List<Match> getUpdateMatches() {
		return updateMatches;
	}

	public void setUpdateMatches(List<Match> updateMatches) {
		this.updateMatches = updateMatches;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoadOptaMatchItem [newMatches=").append(newMatches).append(", updateMatches=").append(updateMatches).append("]");
		return builder.toString();
	}

}
