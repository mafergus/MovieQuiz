package com.escalivadaapps.moviequiz.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final public class Level {

	final public String objectId;
	final public int levelId;
	final public String name;
	final public List<Movie> movies;

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

	@JsonCreator
	public Level(@JsonProperty("objectId")String objectId, 
			@JsonProperty("levelId")int levelId, 
			@JsonProperty("name")String name, 
			@JsonProperty("movies")final List<Movie> movies) {
		this.objectId = objectId;
		this.levelId = levelId;
		this.name = name;
		if (movies == null) {
			this.movies = new ArrayList<Movie>();
		} else {
			this.movies = movies;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + levelId;
		result = prime * result + ((movies == null) ? 0 : movies.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((objectId == null) ? 0 : objectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if ( !(o instanceof Level) ) {
			return false;
		}
		Level other = (Level)o;

		if (!other.objectId.equals(this.objectId) ||
				other.levelId != this.levelId ||
				!other.name.equals(this.name)) {
			return false;
		}
		Comparator<Movie> comparator = new Comparator<Movie>() {

			@Override
			public int compare(Movie lhs, Movie rhs) {
				if (lhs.mdid > rhs.mdid) {
					return 1;
				} else if (lhs.mdid < rhs.mdid) {
					return -1;
				} else {
					return 0;					
				}
			}
		};
		List<Movie> myMovies = new ArrayList<Movie>(this.movies);
		List<Movie> theirMovies = new ArrayList<Movie>(other.movies);
		Collections.sort(myMovies, comparator);
		Collections.sort(theirMovies, comparator);
		if (myMovies.size() != theirMovies.size()) {
			return false;
		}
		for (int i=0; i<myMovies.size(); i++) {
			if (! myMovies.get(i).equals( theirMovies.get(i) ) ) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Level [objectId=" + objectId + ", levelId=" + levelId
				+ ", name=" + name + ", movies=" + Arrays.toString(movies.toArray()) + "]";
	}


}
