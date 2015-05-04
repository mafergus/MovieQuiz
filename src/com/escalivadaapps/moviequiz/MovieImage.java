package com.escalivadaapps.moviequiz;

import android.graphics.drawable.Drawable;

public class MovieImage {
	public Drawable drawable;
	public int movieId;

	public MovieImage(final Drawable d, int movieId) {
		this.drawable = d;
		this.movieId = movieId;
	}

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
		MovieImage other = (MovieImage) obj;
		if (movieId != other.movieId)
			return false;
		return true;
	}

}
