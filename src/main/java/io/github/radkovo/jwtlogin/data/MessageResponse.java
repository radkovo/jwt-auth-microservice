/**
 * MessageResponse.java
 *
 * Created on 17. 4. 2021, 20:19:24 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

/**
 * 
 * @author burgetr
 */
public class MessageResponse
{
    private String message;

    public MessageResponse(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
