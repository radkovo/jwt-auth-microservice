package io.github.radkovo.jwtlogin;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

/**
 * Configures a JAX-RS endpoint.
 *
 * @author burgetr
 */
@ApplicationPath("/")
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({ "admin", "user" })
public class JAXRSConfiguration extends Application {

}
