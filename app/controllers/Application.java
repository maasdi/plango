package controllers;

import play.mvc.*;

import views.html.index;
import views.html.error.notFound;

import java.util.regex.Pattern;

public class Application extends Controller {

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";

    @Security.Authenticated(Secured.class)
    public static Result index() {
        request().username();
        return ok(index.render("Your new application is ready."));
    }

    public static Result handlePathEndWithSlash(String path) {
        Pattern pattern = Pattern.compile(".*/$");
        if (pattern.matcher(path).matches()) {
            /*
             * remove slash at then of path and redirect
             */
            path = path.substring(0, path.length() - 1);
            return redirect("/" + path);
        }
        return ok(notFound.render(request().method(), path));
    }

}
