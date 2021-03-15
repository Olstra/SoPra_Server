package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE); // set to ON after creation since redirected to profile

        checkIfUserExists(newUser);

        // add creation date
        Date creationDate = new Date();
        newUser.setCreationDate(creationDate);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        User userByName = userRepository.findByName(userToBeCreated.getName());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null && userByName != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username and the name", "are"));
        }
        else if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
        }
        else if (userByName != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
        }
    }

    public User getUserWithUsername(String username){
        User user = userRepository.findByUsername(username);

        if(user != null){ return user; }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("couldn't find that user!", "username", "is"));
        }
    }

    public void logoutUser(User foundUser) {
        // TODO logout user
        foundUser.setStatus(UserStatus.OFFLINE);
    }

    public void changeUserValues(String username, User updatedInfo) {
        User currUsernameU = getUserWithUsername(username);
        User newUsernameU = userRepository.findByUsername(updatedInfo.getUsername());

        // first check if birthday was updated
        if(updatedInfo.getBirthday() != null){
            currUsernameU.setBirthday( updatedInfo.getBirthday() );
        }

        // check if username already taken (aka new user username was not null)
        if(newUsernameU != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken!");
        } else if(updatedInfo.getUsername() != null) {
            currUsernameU.setUsername(updatedInfo.getUsername());
        }

        // save changes
        userRepository.flush();
    }
}
