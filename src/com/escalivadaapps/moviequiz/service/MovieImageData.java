package com.escalivadaapps.moviequiz.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final public class MovieImageData {
	final public String movieTitle;
	final public int mdId;
	final public String url;

	@JsonCreator
	public MovieImageData(@JsonProperty("movieTitle")String title, 
			@JsonProperty("mdId")int mdId, 
			@JsonProperty("url")String url) {
		this.movieTitle = title;
		this.mdId = mdId;
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mdId;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		MovieImageData other = (MovieImageData) obj;
		if (mdId != other.mdId)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MovieImageData [movieTitle=" + movieTitle + ", mdId=" + mdId
				+ ", url=" + url + "]";
	}

}
