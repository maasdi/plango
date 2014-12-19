package controllers;

import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import security.AuthKey;

/**
 * Secured
 *
 * @author maasdianto
 *         create on 12/15/2014
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context context) {
        User user = null;
        String[] authModeHeaderValues = context.request().headers().get(AuthKey.AUTH_MODE_HEADER);
        if ((authModeHeaderValues != null) && (authModeHeaderValues.length == 1) && authModeHeaderValues[0] != null) {
            if (AuthKey.AUTH_CLIENT_MODE.equals(authModeHeaderValues[0])) {
                String[] authTokenHeaderValues = context.request().headers().get(AuthKey.AUTH_TOKEN_HEADER);
                if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
                    user = User.findByAuthToken(authTokenHeaderValues[0]);
                    if (user != null) {
                        context.args.put(AuthKey.AUTH_CONTEXT_USER, user);
                        return user.username;
                    }
                }
            }
        }

        String authUsernameValue = context.session().get(AuthKey.AUTH_USERNAME);
        if (authUsernameValue != null) {
            user = User.find().byId(authUsernameValue);
            if (user != null) {
                context.args.put(AuthKey.AUTH_CONTEXT_USER, user);
                return user.username;
            }
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context context) {
        return redirect(routes.SecurityController.login());
    }
}
