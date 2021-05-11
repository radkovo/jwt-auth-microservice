/**
 * CaptchaResponse.java
 *
 * Created on 11. 5. 2021, 16:50:26 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

/**
 * 
 * @author burgetr
 */
public class CaptchaResponse
{
    public boolean success;
    public String challenge_ts;
    public String hostname;
    public String redirectTarget;
    
    public boolean isSuccess()
    {
        return success;
    }
    
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
    
    public String getChallenge_ts()
    {
        return challenge_ts;
    }
    
    public void setChallenge_ts(String challenge_ts)
    {
        this.challenge_ts = challenge_ts;
    }
    
    public String getHostname()
    {
        return hostname;
    }
    
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public String getRedirectTarget()
    {
        return redirectTarget;
    }

    public void setRedirectTarget(String redirectTarget)
    {
        this.redirectTarget = redirectTarget;
    }
    
}
