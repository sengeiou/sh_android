package com.fav24.shootr.batch.optaData.rest.DTO;

public class SeasonDTO {

	private Long season_id;
	private String name;

	private String start_date;
	private String end_date;

	private Long service_level; // no
	private String last_updated;

	public SeasonDTO() {
	}

	public SeasonDTO(Long season_id, String name, String start_date, String end_date, Long service_level, String last_updated) {
		this.season_id = season_id;
		this.name = name;
		this.start_date = start_date;
		this.end_date = end_date;
		this.service_level = service_level;
		this.last_updated = last_updated;
	}

	public Long getSeason_id() {
		return season_id;
	}

	public void setSeason_id(Long season_id) {
		this.season_id = season_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public Long getService_level() {
		return service_level;
	}

	public void setService_level(Long service_level) {
		this.service_level = service_level;
	}

	public String getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(String last_updated) {
		this.last_updated = last_updated;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SeasonDTO [season_id=").append(season_id).append(", name=").append(name).append(", start_date=").append(start_date).append(", end_date=").append(end_date)
				.append(", service_level=").append(service_level).append(", last_updated=").append(last_updated).append("]");
		return builder.toString();
	}

}
