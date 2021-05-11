/**
 * ResultResponse.java
 *
 * Created on 11. 5. 2021, 20:47:20 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

/**
 * 
 * @author burgetr
 */
public class ResultResponse
{
    private String result;
    private String param;
    
    public ResultResponse(String result, String param)
    {
        this.result = result;
        this.param = param;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getParam()
    {
        return param;
    }

    public void setParam(String param)
    {
        this.param = param;
    }

}
