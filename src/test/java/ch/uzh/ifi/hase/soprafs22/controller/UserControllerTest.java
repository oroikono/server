package ch.uzh.ifi.hase.soprafs22.controller;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(user.getName())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

    // Status Code 201 add User
    @Test
    public void createUser_validInput_userCreated() throws Exception
    {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO1 = new UserPostDTO();
        userPostDTO1.setName("Test User");
        userPostDTO1.setUsername("testUsername2");

        // when
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO1));
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
  }
    // Status Code 409 add User failed because username already exists
    @Test
    public void createUser_usernameAlreadyTaken_userNotCreated() throws Exception
    {
        // given
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Test User");
        user1.setUsername("testUsername1");
        user1.setToken("1");
        user1.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO2 = new UserPostDTO();
        userPostDTO2.setName("Test User");
        userPostDTO2.setUsername("testUsername1");

        given(userService.createUser(Mockito.any())).willReturn(user1);

        // when
        MockHttpServletRequestBuilder postRequestU1 = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO2));

        // then
        mockMvc.perform(postRequestU1).andExpect(status().isCreated());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Test User");
        user2.setUsername("testUsername2");
        user2.setToken("1");
        user2.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO3 = new UserPostDTO();
        userPostDTO3.setName("Test User");
        userPostDTO3.setUsername("testUsername2");

        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when
        MockHttpServletRequestBuilder postRequestU2 = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO3));

        // then
        mockMvc.perform(postRequestU2)
                .andExpect(status().isConflict());

    }

    // Status Code 201 add User
    @Test
    public void givenUser_validLogIn() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO3 = new UserPostDTO();
        userPostDTO3.setName("Test User");
        userPostDTO3.setUsername("testUsername");

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO3));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isAccepted());
    }


  // 200 status code retrieve user profile with userId
    @Test
    public void givenUser_withCorrectId() throws Exception {
        // given
        User user = new User();
        user.setName("test User");
        user.setUsername("testUsername");
        user.setBirthday(null);
        user.setStatus(UserStatus.OFFLINE);
        user.setId(1L);

        given(userService.getUserById(1L)).willReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/users/1").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    // code 404 get / users/{userID}
    @Test
    public void getUser_userDoesNotExist() throws Exception {
        given(userService.getUserById(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder getRequest = get("/users/3")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    // 204 code update user profile
    @Test
    public void updateUser_succesfultUpdate() throws Exception{
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setName("testName");
        user.setPassword("testPassword");
        user.setBirthday(null);

        User updatedUser = new User();
        updatedUser.setUsername("new");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("new");

        doNothing().when(userService).updateUser(Mockito.anyLong(),Mockito.any());
        userService.updateUser(1L,updatedUser);

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

    }

    // 404 code put via logout
    @Test
    public void logoutUser_invalidCredentials() throws Exception
    {
        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setId(1L);
        userGetDTO.setUsername("testUsername");

        //given
        given(userService.logoutUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        //when
        MockHttpServletRequestBuilder putRequest = put("/users/logout/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO));

        //then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }


    /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}