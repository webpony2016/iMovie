package com.icareu.imovie.beans;

import java.io.Serializable;

/**
 * Created by Tony on 2016/2/26.
 * Refer to https://github.com/dagingaa/android-tmdb-example/blob/master/src/com/daginge/tmdbsearch/MovieResult.java
 */
public class Movie implements Serializable {
    private final String backdropPath;
    private final String originalTitle;
    private final int id;
    private final String popularity;
    private final String posterPath;
    private final String releaseDate;
    private final String title;
    private final String overview;
    private final Double vote_average;
    private final int runTime;
    private final String baseUrl = "http://image.tmdb.org/t/p/w500";

    private Movie(Builder builder) {
        backdropPath = builder.backdropPath;
        originalTitle = builder.originalTitle;
        id = builder.id;
        popularity = builder.popularity;
        posterPath = builder.posterPath;
        releaseDate = builder.releaseDate;
        title = builder.title;
        overview = builder.overview;
        vote_average = builder.vote_average;
        runTime = builder.runTime;
    }

    public static class Builder {
        private String backdropPath;
        private String originalTitle;
        private int id;
        private String popularity;
        private String posterPath;
        private String releaseDate;
        private String title;
        private String overview;
        private Double vote_average;
        private int runTime;

        public Builder(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public Builder setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
            return this;
        }

        public Builder setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setPopularity(String popularity) {
            this.popularity = popularity;
            return this;
        }

        public Builder setPosterPath(String posterPath) {
            this.posterPath = posterPath;
            return this;
        }

        public Builder setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder setVoteAverage(Double voteAverage) {
            this.vote_average = voteAverage;
            return this;
        }

        public Builder setRunTime(int runTime) {
            this.runTime = runTime;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }

    public static Builder newBuilder(int id, String title) {
        return new Builder(id, title);
    }

    public String getBackdropPath() {
        String baseUrl = "http://image.tmdb.org/t/p/w500";
        return baseUrl+ backdropPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public int getId() {
        return id;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return baseUrl + posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return vote_average;
    }

    public int getRunTime() {
        return runTime;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
