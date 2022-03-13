package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    testUser.setCreation_date(new Date());
    testUser.setStatus(UserStatus.OFFLINE);
    testUser.setBirthday(new Date());

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

    @Test
    public void testLoginUser(){
        User testUser2 = new User();
        testUser2.setName("testName2");
        testUser2.setUsername("testUsername2");
        testUser2.setPassword("testPassword2");


        User createdUser2 = userService.createUser(testUser2);
        createdUser2.setStatus(UserStatus.ONLINE);

        userService.loginUser(createdUser2);

        assertEquals(userRepository.findByUsername("testUsername2").getStatus(), UserStatus.ONLINE);
     }


    @Test
    public void testLogoutUser(){
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");


        User test = userService.createUser(testUser);
        User loggedInUser = userService.loginUser(test);

        assertNotNull(loggedInUser);
        assertEquals(UserStatus.ONLINE, userRepository.findByUsername("testUsername").getStatus());

        userService.logoutUser(loggedInUser.getId());
        assertEquals(userRepository.findByUsername("testUsername").getStatus(), UserStatus.OFFLINE);
    }
    //@Test
    //    public void checkifUserExistsTest() {
    //        // given -> a first user has already been created
    //        userService.createUser(testUser);
    //
    //        // when -> setup additional mocks for UserRepository
    //        Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(testUser);
    //        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
    //
    //        // then -> attempt to create second user with same user -> check that an error
    //        // is thrown
    //        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    //    }

    @Test
    public void testGetUser(){
        assertNull(userRepository.findByUsername("testUsername5"));

        User testUser5 = new User();
        testUser5.setUsername("testUsername5");
        testUser5.setName("testName5");
        testUser5.setPassword("testPassword5");

        User createdUser5 = userService.createUser(testUser5);

        User recievedUser5 = userService.getUserById(createdUser5.getId());

        assertEquals(recievedUser5.getUsername(), "testUsername5");
        assertEquals(recievedUser5.getId(), createdUser5.getId());
    }
}


