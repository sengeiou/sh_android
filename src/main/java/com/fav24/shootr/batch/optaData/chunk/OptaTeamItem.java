package com.fav24.shootr.batch.optaData.chunk;

import java.util.ArrayList;
import java.util.List;

import com.fav24.shootr.dao.domain.Area;
import com.fav24.shootr.dao.domain.Team;

public class OptaTeamItem {
	private Area area;
	private List<Team> newTeams;

	public OptaTeamItem(Area area) {
		super();
		this.area = area;
		this.newTeams = new ArrayList<Team>();
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public List<Team> getNewTeams() {
		return newTeams;
	}

	public void setNewTeams(List<Team> newTeams) {
		this.newTeams = newTeams;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoadOptaTeamItem [area=").append(area).append(", newTeams=").append(newTeams).append("]");
		return builder.toString();
	}
}
