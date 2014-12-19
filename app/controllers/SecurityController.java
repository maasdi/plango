package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import security.AuthKey;
import views.html.login;

/**
 * SecurityController
 *
 * @author maasdianto
 *         create on 12/16/2014
 */
public class SecurityController extends Controller {

    public static User getUser() {
        return (User) Http.Context.current().args.get(AuthKey.AUTH_CONTEXT_USER);
    }

    private static Boolean isClientAuth() {
        String[] authModeHeaderValues = request().headers().get(AuthKey.AUTH_MODE_HEADER);
        if ((authModeHeaderValues != null) && (authModeHeaderValues.length == 1) && authModeHeaderValues[0] != null) {
            return AuthKey.AUTH_CLIENT_MODE.equals(authModeHeaderValues[0]);
        }
        return Boolean.FALSE;
    }

    public static Result login() {
        String username = session(AuthKey.AUTH_USERNAME);
        if (StringUtils.isNotBlank(username)) {
            if (User.find().byId(username) == null) {
                // invalidate session
                return logout();
            }
            return redirect(routes.Application.index());
        }
        return ok(login.render(Form.form(Login.class)));
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (isClientAuth()) {
            if (loginForm.hasErrors()) {
                ObjectNode errorJson = Json.newObject();
                errorJson.put(Application.FLASH_ERROR_KEY, Messages.get("login.required"));
                return badRequest(errorJson);
            }

            User user = User.auth(loginForm.get().username, loginForm.get().password);
            if (user == null) {
                ObjectNode errorJson = Json.newObject();
                errorJson.put(Application.FLASH_ERROR_KEY, Messages.get("login.invalid"));
                return badRequest(errorJson);
            }
            String authToken = user.createToken();
            ObjectNode authTokenJson = Json.newObject();
            authTokenJson.put(AuthKey.AUTH_TOKEN, authToken);
            return ok(authTokenJson);
        } else {
            if (loginForm.hasErrors()) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("login.required"));
                return badRequest(login.render(loginForm));
            }

            User user = User.auth(loginForm.get().username, loginForm.get().password);
            if (user == null) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("login.invalid"));
                return badRequest(login.render(loginForm));
            }

            session().put(AuthKey.AUTH_USERNAME, user.username);
            return redirect(routes.Application.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result logout() {
        if (isClientAuth()) {
            String username = request().username();
            User user = User.find().byId(username);
            if (user != null) {
                user.destroyToken();
            }
            ObjectNode okJson = Json.newObject();
            okJson.put(Application.FLASH_MESSAGE_KEY, Messages.get("logout.message"));
            return ok(okJson);
        } else {
            session().clear();
            flash(Application.FLASH_MESSAGE_KEY, Messages.get("logout.message"));
            return redirect(routes.SecurityController.login());
        }
    }

    public static class Login {
        @Constraints.Required
        public String username;
        @Constraints.Required
        public String password;
    }

}
