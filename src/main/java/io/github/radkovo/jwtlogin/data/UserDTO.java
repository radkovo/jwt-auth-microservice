/**
 * UserDTO.java
 *
 * Created on 17. 4. 2021, 20:01:02 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

/**
 * 
 * @author burgetr
 */
public class UserDTO
{
    private String username;
    
    private String password;
    
    private String name;
    
    private String email;
    
    public UserDTO()
    {
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

}
