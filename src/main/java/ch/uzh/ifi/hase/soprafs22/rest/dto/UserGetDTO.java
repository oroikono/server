package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import java.util.Date;

public class UserGetDTO {

  private Long id;
  private String name;
  private String username;
  private String token;
  private Date birthday;
  private Date creation_date;
  private UserStatus status;
  private boolean logged_in;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {this.username = username;}

  public String getToken() {
        return token;
    }

  public void setToken(String token) {
        this.token = token;
    }

  public UserStatus getStatus() {return status;}
  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public boolean getLogged_in() {return logged_in;}
  public void setLogged_in(boolean logged_in) {
        this.logged_in = logged_in;
    }

  public Date getCreation_date() {return creation_date;}

  public void setCreation_date(Date creation_date) {this.creation_date = creation_date;}

  public Date getBirthday() {return birthday;}

  public void setBirthday(Date birthday) {this.birthday = birthday;}

}
