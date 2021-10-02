/**
 * PasswordDTO.java
 *
 * Created on 2. 10. 2021, 20:22:22 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

/**
 * 
 * @author burgetr
 */
public class PasswordDTO
{
    private String oldPassword;
    private String newPassword;
    
    public PasswordDTO()
    {
    }

    public String getOldPassword()
    {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

}
