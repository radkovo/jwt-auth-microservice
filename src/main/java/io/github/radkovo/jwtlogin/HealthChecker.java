/**
 * 
 */
package io.github.radkovo.jwtlogin;

import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import io.github.radkovo.jwtlogin.dao.UserService;

/**
 * A simple health check that tests the availability of the user database.
 *  
 * @author burgetr
 */
@Readiness
@Liveness
public class HealthChecker implements HealthCheck
{
    @Inject
    UserService userService;

    @Override
    public HealthCheckResponse call()
    {
        try {
            var list = userService.getUsers();
            if (list != null)
                return HealthCheckResponse.up("User service ready");
            else
                return HealthCheckResponse.down("User is db not available");
        } catch (Exception e) {
            return HealthCheckResponse.down(e.getMessage());
        }
    }
    
}
