package com.escalivadaapps.moviequiz.service;

import java.util.List;

final public class Movie {
	final public int mdid;
	final public String title;
	final public List<String> imageUrls;
	
	public Movie(int movieId, String title, final List<String> imageUrls) {
		this.mdid = movieId;
		this.title = title;
		this.imageUrls = imageUrls;
	}

}
