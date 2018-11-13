package com.greendislike;

import com.greendislike.controller.types.UserSearch;
import com.greendislike.persistence.model.User;
import com.greendislike.persistence.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BstRestTaskApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void postCreateUserOk() {

        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(new UserRequest("Ivan", "Ivanov", "ivan@gmail.com", "12345"));

        ResponseEntity<String> response = restTemplate.postForEntity("/user", requestEntity, String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.OK,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "{\"message\":\"User Ivan Ivanov with id = 1 successfully created\"}",
                response.getBody());

        User user = userRepository.findById(1L).orElse(null);

        Assert.assertNotNull(user);
        Assert.assertEquals("Invalid first name in db", "Ivan", user.getFirstName());
        Assert.assertEquals("Invalid lst name in db", "Ivanov", user.getLastName());
        Assert.assertEquals("Invalid email in db", "ivan@gmail.com", user.getEmail());
        Assert.assertEquals("Invalid password in db", true, BCrypt.checkpw("12345", user.getPassword()));
    }

    @Test
    public void getAllUsersOk() {

        User user1 = new User("Ivan", "Ivanov", "ivan@gmail.com", "12345");
        User user2 = new User("Ivan", "NeIvanov", "ivanNeIvanov@gmail.com", "123");
        User user3 = new User("Nuar", "Ivanov", "ivan@mail.ru", "111");

        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);

        ResponseEntity<String> response = restTemplate.getForEntity("/user", String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.OK,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "[{\"id\":1,\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"email\":\"ivan@gmail.com\",\"password\":\"12345\"},"
                + "{\"id\":2,\"firstName\":\"Ivan\",\"lastName\":\"NeIvanov\",\"email\":\"ivanNeIvanov@gmail.com\",\"password\":\"123\"},"
                + "{\"id\":3,\"firstName\":\"Nuar\",\"lastName\":\"Ivanov\",\"email\":\"ivan@mail.ru\",\"password\":\"111\"}]",
                response.getBody());
    }

    @Test
    public void getUserById() {
        User user1 = new User("Ivan", "Ivanov", "ivan@gmail.com", "12345");
        userRepository.saveAndFlush(user1);

        ResponseEntity<String> response = restTemplate.getForEntity("/user/1", String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.OK,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "{\"id\":1,\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"email\":\"ivan@gmail.com\",\"password\":\"12345\"}",
                response.getBody());
    }

    @Test
    public void getUserByIdNotFound() {

        ResponseEntity<String> response = restTemplate.getForEntity("/user/47", String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.NOT_FOUND,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "{\"code\":\"404 NOT_FOUND\",\"description\":\"Could not find user with Id = 47\"}",
                response.getBody());
    }

    @Test
    public void getByEmail() {

        User user1 = new User("Ivan", "Ivanov", "ivan@gmail.com", "12345");
        userRepository.save(user1);

        ResponseEntity<String> response = restTemplate.postForEntity("/user/searches", new UserSearch("ivan@gmail.com"), String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.OK,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "{\"id\":1,\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"email\":\"ivan@gmail.com\",\"password\":\"12345\"}",
                response.getBody());
    }

    @Test
    public void putEditUserOk() {

        User user1 = new User("Ivan", "Ivanov", "ivan@gmail.com", "12345");
        userRepository.save(user1);

        HttpEntity<User> requestEntity = new HttpEntity<>(new User("Ivan", "Ivanov", "newIvan@gmail.com", "123456"));

        ResponseEntity<String> response = restTemplate.exchange("/user/1", HttpMethod.PUT,
                requestEntity, String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.OK,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "{\"message\":\"User Ivan Ivanov with id = 1 successfully edited\"}",
                response.getBody());

        User user = userRepository.findById(1L).orElse(null);

        Assert.assertNotNull(user);
        Assert.assertEquals("Invalid first name in db", "Ivan", user.getFirstName());
        Assert.assertEquals("Invalid lst name in db", "Ivanov", user.getLastName());
        Assert.assertEquals("Invalid email in db", "newIvan@gmail.com", user.getEmail());
        Assert.assertEquals("Invalid password in db", true, BCrypt.checkpw("123456", user.getPassword()));

    }

    @Test
    public void putEditUserNotFound() {

        HttpEntity<User> requestEntity = new HttpEntity<>(new User("Ivan", "Ivanov", "newIvan@gmail.com", "123456"));

        ResponseEntity<String> response = restTemplate.exchange("/user/1", HttpMethod.PUT,
                requestEntity, String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.NOT_FOUND,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "{\"code\":\"404 NOT_FOUND\",\"description\":\"Could not find user with Id = 1\"}",
                response.getBody());

    }

    @Test
    public void putEditUserNotEquals() {

        User user1 = new User("Ivan", "Ivanov", "ivan@gmail.com", "12345");
        userRepository.save(user1);

        HttpEntity<User> requestEntity = new HttpEntity<>(new User("NeIvan", "Ivanov", "newIvan@gmail.com", "123456"));

        ResponseEntity<String> response = restTemplate.exchange("/user/1", HttpMethod.PUT,
                requestEntity, String.class);

        Assert.assertEquals("Server returned invalid HTTP code",
                HttpStatus.BAD_REQUEST,
                response.getStatusCode());

        Assert.assertEquals("Server returned invalid response",
                "{\"code\":\"400 BAD_REQUEST\",\"description\":\"User with id = 1 Ivan Ivanov not equals NeIvan Ivanov\"}",
                response.getBody());

    }

}
