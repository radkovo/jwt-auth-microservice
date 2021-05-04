/**
 * CorsFilter.java
 *
 * Created on 15. 12. 2020, 20:54:25 by burgetr
 */
package io.github.radkovo.jwtlogin;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * 
 * @author burgetr
 */
@Provider
public class CorsFilter implements ContainerResponseFilter
{
    @Inject
    @ConfigProperty(name = "jwtauth.cors.origin")
    private Optional<String> corsOrigin;
    
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException
    {
        if (corsOrigin.isPresent())
        {
            final var headers = responseContext.getHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
    }
}
