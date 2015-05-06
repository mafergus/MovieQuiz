package com.escalivadaapps.moviequiz.service;

import java.util.List;

final public class Level {
	
	final public int levelId;
	final public String name;
	final public List<Movie> movies; 
	
	public Level(int levelId, String name, final List<Movie> movies) {
		this.levelId = levelId;
		this.name = name;
		this.movies = movies;
	}

}
