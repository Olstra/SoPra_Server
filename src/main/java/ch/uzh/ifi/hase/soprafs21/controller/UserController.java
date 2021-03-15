package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // zusatzattribute ///////////////////////
        // creation date
        //Date cDate = new Date();
        //createdUser.setCreationDate(cDate);
        // online status
        //createdUser.setStatus(UserStatus.ONLINE);
        // birthday
        //Date bDate = new Date(0,0,0);
        //createdUser.setBirthday(bDate);
        /////////////////////////////////////////

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO authenticateUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // search and return user from DB
        User returnedUser = userService.getUserWithUsername(userInput.getUsername());

        // TODO check pw

        return DTOMapper.INSTANCE.convertEntityToUserGetDTO( returnedUser );
    }

    @GetMapping("/users/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserWithUsername(@PathVariable String username){
        User user = userService.getUserWithUsername(username);
        //System.out.println("hello the username: "+user.getUsername());
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @PutMapping("/logout/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void statusChange(@PathVariable String username){
        User foundUser = userService.getUserWithUsername(username);
        userService.logoutUser(foundUser);
    }

    @PutMapping("/users/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void changeProfile(@PathVariable String username, @RequestBody UserPutDTO userPutDTO){
        User updatedInfo = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        userService.changeUserValues(username, updatedInfo);
    }

}
