/**
 * RegisterUserDTO.java
 *
 * Created on 4. 10. 2021, 11:04:31 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

/**
 * 
 * @author burgetr
 */
public class RegisterUserDTO extends UserDTO
{
    private String captchaToken;
    
    public RegisterUserDTO()
    {
    }

    public String getCaptchaToken()
    {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken)
    {
        this.captchaToken = captchaToken;
    }

}
