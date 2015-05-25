package com.escalivadaapps.moviequiz.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final public class Level {

	final public String objectId;
	final public int levelId;
	final public String name;
	final public boolean isRandom;
	final public String imageUrl;
	//	final public List<Movie> movies;
	final public List<MovieImageData> movieImages;

	static class LevelComparator implements Comparator<Level> {

		@Override
		public int compare(Level lhs, Level rhs) {
			if (lhs.levelId > rhs.levelId) {
				return 1;
			} else if (lhs.levelId < rhs.levelId) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	final private class MoviePair {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + mdId;
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
			MoviePair other = (MoviePair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (mdId != other.mdId)
				return false;
			return true;
		}
		final public int mdId;
		final public String title;
		public MoviePair(int mdId, String title) { this.mdId = mdId; this.title = title; }
		private Level getOuterType() {
			return Level.this;
		}
		
	}
	
	public List<Movie> getMovieList() {
		Map<MoviePair, List<String> > movieUrls = new HashMap<MoviePair, List<String> >();
		for (MovieImageData mid : movieImages) {
			MoviePair key = new MoviePair(mid.mdId, mid.movieTitle);
			List<String> urls = movieUrls.get(key);
			if (urls == null) { 
				urls = new ArrayList<String>();
				movieUrls.put(key, urls);
			}
			urls.add(mid.url);
		}
		
		List<Movie> movies = new ArrayList<Movie>();
		Set<MoviePair> keys = movieUrls.keySet();
		for (MoviePair mp : keys) {
			final Movie m = new Movie(mp.mdId, mp.title, movieUrls.get(mp));
			movies.add(m);
		}
		return movies;
	}

	@JsonCreator
	public Level(@JsonProperty("objectId")String objectId, 
			@JsonProperty("levelId")int levelId, 
			@JsonProperty("name")String name, 
			@JsonProperty("movieImages")final List<MovieImageData> movieImages,
			@JsonProperty("isRandom")boolean isRandom,
			@JsonProperty("imageUrl")String imageUrl) {
		this.objectId = objectId;
		this.imageUrl = imageUrl;
		this.levelId = levelId;
		this.name = name;
		if (movieImages == null) {
			this.movieImages = new ArrayList<MovieImageData>();
		} else {
			this.movieImages = movieImages;
		}
		this.isRandom = isRandom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isRandom ? 1231 : 1237);
		result = prime * result + levelId;
		result = prime * result
				+ ((movieImages == null) ? 0 : movieImages.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((objectId == null) ? 0 : objectId.hashCode());
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
		Level other = (Level) obj;
		if (isRandom != other.isRandom)
			return false;
		if (levelId != other.levelId)
			return false;
		if (movieImages == null) {
			if (other.movieImages != null)
				return false;
		} else if (!movieImages.equals(other.movieImages))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Level [objectId=" + objectId + ", levelId=" + levelId
				+ ", name=" + name + ", isRandom=" + isRandom + ", imageUrl " + imageUrl + ", movies=" + Arrays.toString(movieImages.toArray()) + "]";
	}


}
