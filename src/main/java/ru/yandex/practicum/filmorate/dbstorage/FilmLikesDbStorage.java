package ru.yandex.practicum.filmorate.dbstorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.FilmLikesStorageDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.Map.Entry;

@Component
public class FilmLikesDbStorage implements FilmLikesStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmLikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "select count(id_user) AS likes, f.*, m.name_rate " +
                "from film_likes l " +
                "RIGHT join film f ON f.film_id = l.id_film " +
                "inner join mpa m on f.mpa = m.id " +
                "GROUP BY f.film_id, m.id " +
                "ORDER BY likes DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, count);
    }

    @Override
    public List<Film> getLikedFilms(int idUser1, int idUser2) {
        String sql = "select * from film as f" +
                " inner join mpa as m on f.mpa = m.id" +
                " inner join film_likes as f_l on f.film_id = f_l.id_film" +
                " where (f_l.id_user = ? and f_l.id_film =" +
                " (select f_l.id_film from film_likes where film_likes.id_user = ?))" +
                " order by f.rate desc";
        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, idUser1, idUser2);
    }

    @Override
    public List<Film> getPopularFilmsWithGenre(int count, int idGenre) {
        String sql = "select * from film as f inner join mpa as m on f.mpa = m.id " +
                "where (film_id = (select id_film from film_genre where genre_id = ?)) order by rate desc limit ?";
        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, idGenre, count);
    }

    @Override
    public List<Film> getPopularFilmsWithYear(int count, int year) {
        String sql = "select * from film as f inner join mpa as m on f.mpa = m.id " +
                "where (select extract (year from cast (release_data as date)) = ?) " +
                "order by rate desc limit ?";
        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, year, count);
    }

    @Override
    public List<Film> getPopularFilmsWithGenreAndYear(int count, int idGenre, int year) {
        String sql = "select * from film as f inner join mpa as m on f.mpa = m.id " +
                "where (select extract (year from cast (release_data as date)) = ?" +
                "and film_id = (select id_film from film_genre where genre_id = ?)) order by rate desc limit ?";
        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, year, idGenre, count);
    }

    @Override
    public void deleteLike(int idFilm, int idUser) {
        String sql = "delete from film_likes " +
                "where (id_film = ? and id_user = ?)";
        jdbcTemplate.update(sql, idFilm, idUser);
        String sqlRate = "update film set rate = (rate -1) where film_id = ?";
        jdbcTemplate.update(sqlRate, idFilm);
    }

    @Override
    public List<Film> getRecommendations(int idUser) {
        Map<Integer, Map<Integer, Integer>> diff = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();
        Map<Integer, HashMap<Integer, Integer>> outputData = new HashMap<>();
        HashMap<Integer, Integer> uPred = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> uFreq = new HashMap<Integer, Integer>();
        Map<Integer, HashMap<Integer, Integer>> inputData = new HashMap<>();
        Set<Integer> idFilm = new TreeSet<>();
        String sqlForInputData = "select * from film_likes";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlForInputData);
        Map<Integer, List<Integer>> userLikes = new HashMap<>();
        for (Map<String, Object> map : result) {
            if (!userLikes.containsKey((Integer) map.get("id_user"))) {
                List<Integer> newList = new ArrayList<>();
                newList.add((Integer) map.get("id_film"));
                userLikes.put(((Integer) map.get("id_user")), newList);
            } else {
                userLikes.get((Integer) map.get("id_user")).add((Integer) map.get("id_film"));
            }
        }
        if (userLikes.isEmpty()) {
            return new ArrayList<>();
        } else {
            for (Integer integer : userLikes.keySet()) {
                for (Integer integer1 : userLikes.get(integer))
                    idFilm.add(integer1);
            }
        }
        for (Integer integer : userLikes.keySet()) {
            HashMap<Integer, Integer> likes = new HashMap<>();
            for (Integer integer1 : idFilm) {
                likes.put(integer1, 0);
            }
            for (int i = 0; i < userLikes.get(integer).size(); i++) {
                likes.put(userLikes.get(integer).get(i), 1);
                if (i + 1 == userLikes.get(integer).size()) {
                    inputData.put(integer, likes);
                }
            }
        }
        for (HashMap<Integer, Integer> user : inputData.values()) {
            for (Entry<Integer, Integer> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Integer, Integer>());
                    freq.put(e.getKey(), new HashMap<Integer, Integer>());
                }
                for (Entry<Integer, Integer> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    int oldDiff = 0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    int observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Integer j : diff.keySet()) {
            for (Integer i : diff.get(j).keySet()) {
                int oldValue = diff.get(j).get(i).intValue();
                int count = freq.get(j).get(i).intValue();
                diff.get(j).put(i, oldValue / count);
            }
        }
        for (Integer j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0);
        }
        for (Entry<Integer, HashMap<Integer, Integer>> e : inputData.entrySet()) {
            for (Integer j : e.getValue().keySet()) {
                for (Integer k : diff.keySet()) {
                    try {
                        Integer predictedValue = diff.get(k).get(j).intValue() + e.getValue().get(j).intValue();
                        Integer finalValue = predictedValue * freq.get(k).get(j).intValue();
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j).intValue());
                    } catch (NullPointerException e1) {
                        throw new NullPointerException("Ошибка в обработке рекомендаций");
                    }
                }
            }
            HashMap<Integer, Integer> clean = new HashMap<Integer, Integer>();
            for (Integer j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j).intValue() / uFreq.get(j).intValue());
                }
            }
            for (Integer j : idFilm) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1);
                }
            }
            outputData.put(e.getKey(), clean);
        }
        List<Integer> idRecommendation = new ArrayList<>();
        List<Integer> idFilmUser = userLikes.get(idUser);
        for (Integer integer : outputData.get(idUser).keySet()) {
            if (outputData.get(idUser).get(integer) != -1) {
                if (!idFilmUser.contains(integer)) {
                    idRecommendation.add(integer);
                }
            }
        }
        String sqlForFilm = "select * from film as f inner join mpa as m on f.mpa = m.id where film_id = ?";
        List<Film> filmRecommendation = new ArrayList<>();
        for (Integer integer : idRecommendation) {
            filmRecommendation.add(jdbcTemplate.queryForObject(sqlForFilm, FilmDbStorage::makeFilm, integer));
        }
        return filmRecommendation;
    }

    @Override
    public void addLikes(int idFilm, int idUser) {
        String sql = "insert into film_likes (id_film, id_user) " +
                "values (?, ?)";
        jdbcTemplate.update(sql, idFilm, idUser);
        String sqlRate = "update film set rate = (rate + 1) where film_id = ?";
        jdbcTemplate.update(sqlRate, idFilm);
    }
}
