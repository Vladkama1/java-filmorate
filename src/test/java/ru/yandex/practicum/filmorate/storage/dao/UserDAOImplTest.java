package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserDAOImplTest {
    final JdbcTemplate jdbcTemplate;
    User newUser = User.builder().build();

    @BeforeEach
    public void startMethod() {
        newUser = User.builder()
                .id(1L)
                .name("name")
                .login("login")
                .email("qwertyu4@email.ru")
                .birthday(LocalDate.of(1974, 6, 28))
                .build();
    }

    @Test
    @DisplayName("Поиск пользователя по айди")
    public void findById_Test() {

        UserDAOImpl dao = new UserDAOImpl(jdbcTemplate);
        dao.save(newUser);
        User savedUser = dao.findById(newUser.getId()).orElse(null);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    @DisplayName("Поиск всех пользователей")
    public void findAll_Test() {
        UserDAOImpl dao = new UserDAOImpl(jdbcTemplate);
        User user = User.builder()
                .id(2L)
                .name("name1")
                .login("login1")
                .email("name1@mail.ru")
                .birthday(LocalDate.of(1917, 3, 28))
                .build();
        User bebra = User.builder()
                .id(2L)
                .name("name1")
                .login("login2")
                .email("qwer@mail.ru")
                .birthday(LocalDate.of(1917, 3, 28))
                .build();
        dao.save(user);
        dao.save(bebra);
        List<User> users = dao.findAll();
        assertEquals(users.size(), 2);
    }

    @Test
    @DisplayName("Обновление пользователя")
    public void update_Test() {
        UserDAOImpl dao = new UserDAOImpl(jdbcTemplate);
        User user = User.builder()
                .id(2L)
                .name("name")
                .login("login")
                .email("email@mail.ru")
                .birthday(LocalDate.of(1917, 3, 28))
                .build();
        dao.save(user);
        user.toBuilder()
                .id(2L)
                .name("name")
                .email("email@mail.ru")
                .login("login2")
                .birthday(LocalDate.of(1930, 1, 1));
        dao.update(user);
        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    @DisplayName("получение списка друзей")
    public void getAllFriends_Test() {
        UserDAOImpl dao = new UserDAOImpl(jdbcTemplate);
        User nuser = User.builder()
                .name("name")
                .login("login")
                .email("email@mail.ru")
                .birthday(LocalDate.of(1917, 3, 28))
                .build();
        Optional<User> friendsOPtional = Optional.ofNullable(dao.save(nuser));
        assertThat(friendsOPtional).isPresent();
        List<User> emptyList = dao.getAllFriends(friendsOPtional.get().getId());
        assertNotNull(emptyList);
        assertEquals(0, emptyList.size());
        User friend = User.builder()
                .name("name")
                .email("email@mail.ru")
                .login("login")
                .birthday(LocalDate.of(1995, 4, 7))
                .build();
        Optional<User> userOptional = Optional.ofNullable(dao.save(friend));
        assertThat(userOptional).isPresent();
        dao.addFriend(nuser.getId(), friend.getId());
        List<User> userList = new ArrayList<>(dao.getAllFriends(nuser.getId()));
        assertNotNull(userList);
        assertEquals("name", userList.get(0).getName());
    }

    @Test
    @DisplayName("получение общих друзей")
    public void getAllMutualFriends_Test() {
        UserDAOImpl dao = new UserDAOImpl(jdbcTemplate);
        User userFriend = User.builder()
                .name("name")
                .email("yandex@mail.ru")
                .login("login")
                .birthday(LocalDate.of(1987, 3, 3))
                .build();
        Optional<User> optionalUser = Optional.ofNullable(dao.save(userFriend));
        assertThat(optionalUser).isPresent();

        User user4 = User.builder()
                .name("name4")
                .email("name4@mail.ru")
                .login("nameLogin4")
                .birthday(LocalDate.of(1945, 5, 9))
                .build();
        Optional<User> userOptional = Optional.ofNullable(dao.save(user4));
        assertThat(userOptional).isPresent();

        User user6 = User.builder()
                .name("name5")
                .email("name@tmail.ru")
                .login("nameLogin5")
                .birthday(LocalDate.of(2000, 6, 6))
                .build();
        Optional<User> user6Opt = Optional.ofNullable(dao.save(user6));
        assertThat(user6Opt).isPresent();
        dao.addFriend(optionalUser.get().getId(), user6Opt.get().getId());
        dao.addFriend(userOptional.get().getId(), user6Opt.get().getId());
        List<User> friendsOfUser = List.of(user6);
        List<User> comoonFriends = dao.getAllMutualFriends(optionalUser.get().getId(), userOptional.get().getId());
        assertArrayEquals(friendsOfUser.toArray(), comoonFriends.toArray());
    }
}