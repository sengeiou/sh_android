package com.fav24.shootr.dao.domain;

public class Area extends Synchronized {

	private Long idArea;
	private Long idAreaOpta;
	private String name;

	public Area() {
	}

	public Area(Long idAreaOpta, String name) {
		this.idAreaOpta = idAreaOpta;
		this.name = name;
	}

	public Long getIdArea() {
		return idArea;
	}

	public void setIdArea(Long idArea) {
		this.idArea = idArea;
	}

	public Long getIdAreaOpta() {
		return idAreaOpta;
	}

	public void setIdAreaOpta(Long idAreaOpta) {
		this.idAreaOpta = idAreaOpta;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Area [idArea=").append(idArea).append(", idAreaOpta=").append(idAreaOpta).append(", name=").append(name).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idAreaOpta == null) ? 0 : idAreaOpta.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Area other = (Area) obj;
		if (idAreaOpta == null) {
			if (other.idAreaOpta != null)
				return false;
		} else if (!idAreaOpta.equals(other.idAreaOpta))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
