package com.escalivadaapps.moviequiz.service;

final public class MovieData {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + movieId;
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
		MovieData other = (MovieData) obj;
		if (movieId != other.movieId)
			return false;
		return true;
	}

	final public int movieId;
	
	public MovieData(int movieId) {
		this.movieId = movieId;
	}
	
	
}
