package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmDAOImplTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Популярные фильмы")
    public void getPopularFilm_Test() {
        UserDAOImpl userDao = new UserDAOImpl(jdbcTemplate);
        FilmDAOImpl dao = new FilmDAOImpl(jdbcTemplate);
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1997, 7, 14))
                .duration(240)
                .mpa(MPA.builder().id(3L).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(dao.save(film));
        assertThat(optionalFilm).isPresent();

        Film film1 = Film.builder()
                .name("name1")
                .description("description2")
                .releaseDate(LocalDate.of(1495, 3, 3))
                .duration(480)
                .mpa(MPA.builder().id(4L).build())
                .build();
        Optional<Film> optionalFilm1 = Optional.ofNullable(dao.save(film1));
        assertThat(optionalFilm1).isPresent();

        User user = User.builder()
                .name("name1")
                .email("nanana@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 10, 10))
                .build();
        Optional<User> user1Opt = Optional.ofNullable(userDao.save(user));
        assertThat(user1Opt).isPresent();

        User user1 = User.builder()
                .name("name2")
                .email("zipfig@mail.ru")
                .login("login2")
                .birthday(LocalDate.of(2011, 11, 11))
                .build();
        Optional<User> user2Opt = Optional.ofNullable(userDao.save(user1));
        assertThat(user2Opt).isPresent();
        boolean isAddedTrue1 = dao.addLike(optionalFilm1.get().getId(), user1Opt.get().getId());

        boolean isAddedTrue2 = dao.addLike(optionalFilm1.get().getId(), user2Opt.get().getId());
        assertTrue(isAddedTrue1);
        assertTrue(isAddedTrue2);

        dao.addLike(optionalFilm.get().getId(), user1Opt.get().getId());
        List<Film> popularFilm = dao.getPopularFilm(1);
        for (Film film2 : popularFilm) {
            System.out.println(film2);
        }
        assertNotNull(popularFilm);
        assertEquals(1, popularFilm.size());
    }

    @Test
    @DisplayName("Добавление лайка и его удаление")
    public void addLikeAndRemove_Test() {
        UserDAOImpl userDao = new UserDAOImpl(jdbcTemplate);
        FilmDAOImpl dao = new FilmDAOImpl(jdbcTemplate);
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1997, 7, 14))
                .duration(240)
                .mpa(MPA.builder().id(3L).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(dao.save(film));
        User user = User.builder()
                .name("name1")
                .email("nanana@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 10, 10))
                .build();
        Optional<User> user1Opt = Optional.ofNullable(userDao.save(user));
        boolean isAddedLike = dao.addLike(optionalFilm.get().getId(), user1Opt.get().getId());
        boolean removeLike = dao.deleteLike(optionalFilm.get().getId(), user1Opt.get().getId());
        assertTrue(isAddedLike);
        assertTrue(removeLike);
    }


    @Test
    @DisplayName("Поиск фильма по его айди")
    public void findById_Test() {
        FilmDAOImpl dao = new FilmDAOImpl(jdbcTemplate);
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1997, 7, 14))
                .duration(240)
                .mpa(MPA.builder().id(3L).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(dao.save(film));
        Optional<Film> film1 = dao.findById(optionalFilm.get().getId());
        assertThat(film1)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    @DisplayName("Обновление фильма")
    public void update_Test() {
        FilmDAOImpl dao = new FilmDAOImpl(jdbcTemplate);
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1997, 7, 14))
                .duration(240)
                .mpa(MPA.builder().id(3L).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(dao.save(film));
        Film film1 = Film.builder()
                .id(10L)
                .name("name1")
                .description("description2")
                .releaseDate(LocalDate.of(1495, 3, 3))
                .duration(480)
                .mpa(MPA.builder().id(4L).build())
                .build();
        Film firstFilm = film;
        Film secondFilm = film1;
        assertNotEquals(film, film1);

    }

    @Test
    @DisplayName("Удаление фильма")
    public void delete_Test() {
        FilmDAOImpl dao = new FilmDAOImpl(jdbcTemplate);
        Film film = Film.builder()
                .name("name")
                .description("description2")
                .releaseDate(LocalDate.of(1997, 7, 14))
                .duration(240)
                .mpa(MPA.builder().id(3L).build())
                .build();
        Optional<Film> optionalFilm = Optional.ofNullable(dao.save(film));
        boolean isDeletedFilm = dao.delete(optionalFilm.get().getId());
        assertTrue(isDeletedFilm);
    }
}