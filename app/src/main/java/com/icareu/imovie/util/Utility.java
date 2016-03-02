package com.icareu.imovie.util;

/**
 * Created by Tony on 2016/2/25.
 */
public class Utility {
   public static final String TMDB_ID = "id";
   public static final String TMDB_TITLE = "title";
   public static final String TMDB_BACKDROP_PATH = "backdrop_path";
   public static final String TMDB_ORIGINAL_TITLE = "original_title";
   public static final String TMDB_POPULARITY = "popularity";
   public static final String TMDB_POSTER_PATH = "poster_path";
   public static final String TMDB_OVERVIEW = "overview";
   public static final String TMDB_RELEASE_DATE = "release_date";
   public static final String TMDB_VOTE_AVERAGE = "vote_average";
   public static final String TMDB_RUNTIME = "runtime";
   public static final String TMDB_RESULTS = "results";

   public enum MovieListTypeEnum {
       NOW_PLAYING("now_playing", 1),
       POPULAR("popular", 2),
       TOP_RATED("top_rated", 3)
      ,UPCOMING("upcoming", 4);
      // 成员变量
      private String name;
      private int index;
      // 构造方法
      private MovieListTypeEnum(String name, int index) {
          this.name = name;
          this.index = index;
      }

       @Override
       public String toString() {
           return this.name;
       }

       public int value(){
           return this.index;
       }

       /**
        * Convert int to MovieListTypeEnum
        * @param value int number to be converted
        * @return MovieListTypeEnum
        */
       public static MovieListTypeEnum valueOf(int value) {
           switch (value) {
               case 1:
                   return NOW_PLAYING;
               case 2:
                   return POPULAR;
               case 3:
                   return TOP_RATED;
               case 4:
                   return UPCOMING;
               default:
                   return null;
           }
       }
   }
}
