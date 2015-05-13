package com.escalivadaapps.moviequiz.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final public class Movie {
	final public int mdid;
	final public String title;
	final public List<String> imageUrls;

	@JsonCreator
	public Movie(@JsonProperty("mdid")int movieId, 
			@JsonProperty("title")String title, 
			@JsonProperty("imageUrls")final List<String> imageUrls) {
		this.mdid = movieId;
		this.title = title;
		this.imageUrls = imageUrls;
	}

	@Override
	public String toString() {
		return "Movie [mdid=" + mdid + ", title=" + title + ", imageUrls="
				+ Arrays.toString(imageUrls.toArray()) + "]";
	}

	@Override
	public boolean equals(Object o) {
		if ( !(o instanceof Movie) ) {
			return false;
		}
		Movie other = (Movie)o;
		List<String> myUrls = new ArrayList<String>(imageUrls);
		List<String> theirUrls = new ArrayList<String>(other.imageUrls);
		Collections.sort(myUrls);
		Collections.sort(theirUrls);
		if (!myUrls.equals(theirUrls) ||
				this.mdid != other.mdid ||
				!this.title.equals(other.title)) {
			return false;
		}
		return true;
	}

}
