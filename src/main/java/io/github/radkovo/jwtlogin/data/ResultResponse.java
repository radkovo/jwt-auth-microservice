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
    private Object param;
    
    public ResultResponse(String result, Object param)
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

    public Object getParam()
    {
        return param;
    }

    public void setParam(Object param)
    {
        this.param = param;
    }

}
