package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;

public class UserPostDTO {

    private String name;
    private String username;
    private String password;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // getter and setter for password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
