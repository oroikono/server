package ch.uzh.ifi.hase.soprafs22.rest.dto;
import javax.persistence.Column;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import java.util.Date;

public class UserPutDTO {

  private String username;
  private Date birthday;
  private String token;



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

  public Date getBirthday() {return birthday;}

  public void setBirthday(Date birthday) {this.birthday = birthday;}

}
