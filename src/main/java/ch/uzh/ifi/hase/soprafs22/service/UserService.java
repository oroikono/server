package ch.uzh.ifi.hase.soprafs22.service;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService
{
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public List<User> getUsers()
    {
        return this.userRepository.findAll();
    }

    //create user service and adding it into the database...
    public User createUser(User newUser)
    {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setLogged_in(true);
        newUser.setCreation_date(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));

        checkIfUserExists(newUser);
        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    //login user
    public User loginUser(User userInput)
    {
        User userByUsername = userRepository.findByUsername(userInput.getUsername());

        if(userByUsername == null)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The username is not correct or the user does not exist");
        }
        else if(!userByUsername.getPassword().equals(userInput.getPassword()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The password is not correct");
        }
        userByUsername.setStatus(UserStatus.ONLINE);
        userByUsername.setLogged_in(true);
        userRepository.save(userByUsername);

        log.debug("Logged in User: {}", userByUsername);
        return userByUsername;
    }

    //logout user
    public User logoutUser(Long id)
    {
        User userById = userRepository.findUserById(id);

        if (userById == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The token is not correct or the token does not exist");
        }
        userById.setStatus(UserStatus.OFFLINE);
        userById.setLogged_in(false);
        return userById;
    }

    //gets the user by its corresponding id and return him/her
    public User getUserById(Long userId)
    {
        User userById = userRepository.findUserById(userId);

        if (userById == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The id is not correct or the id does not exist");
        }

        log.debug("Found User by Id: {}", userById);
        return userById;
    }

    //updates a user's birthday or/and username
    public void updateUser(Long id, User userInput)
    {
        User userForUpdate = getUserById(id);

        if (userRepository.findByUsername(userInput.getUsername())!=null)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not use that username because it is not unique");
        }
        if (userInput.getUsername() != null)
        {
            userForUpdate.setUsername(userInput.getUsername());
        }

        userForUpdate.setBirthday(userInput.getBirthday());
        userRepository.save(userForUpdate);
        userRepository.flush();
    }
    /*
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws SopraServiceException
     * @see User
     */
    void checkIfUserExists(User userToBeCreated)
    {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }

    }

}
