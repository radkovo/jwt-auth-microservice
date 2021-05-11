/**
 * CaptchaResource.java
 *
 * Created on 11. 5. 2021, 16:45:50 by burgetr
 */
package io.github.radkovo.jwtlogin.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.github.radkovo.jwtlogin.data.CaptchaResponse;
import io.github.radkovo.jwtlogin.data.MessageResponse;
import io.github.radkovo.jwtlogin.data.ResultResponse;
import io.github.radkovo.jwtlogin.data.TokenResponse;

/**
 * 
 * @author burgetr
 */
@Path("captcha")
public class CaptchaResource
{
    @Inject
    @ConfigProperty(name = "jwtauth.captcha.secret", defaultValue = "")
    String captchaSecret;
    
    @POST
    @Path("verify")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verifyCaptcha(TokenResponse data)
    {
        try {
            CaptchaResponse resp = checkToken(data.getToken());
            if (resp.isSuccess())
                return Response.ok(new ResultResponse("yes", resp.getRedirectTarget())).build();
            else
                return Response.ok(new ResultResponse("no", null)).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
        }
    }

    private CaptchaResponse checkToken(String token)
    {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.putSingle("secret", captchaSecret);
        map.putSingle("response", token);
        Client client = ClientBuilder.newClient();
        CaptchaResponse response = client.target("https://www.google.com/recaptcha/api/siteverify")
                .request().post(Entity.form(map), CaptchaResponse.class);
        return response;
    }
    
}
