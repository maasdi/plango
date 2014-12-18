package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.Role;
import models.User;

import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Security;
import play.mvc.Result;

import util.Pagination;
import views.html.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Users
 *
 * @author maasdianto
 *         create on 12/8/2014
 */
@Security.Authenticated(Secured.class)
@Restrict(@Group("ADMIN"))
public class UserController extends Controller {

    private static String labelUser = Messages.get("label.user");

    public static Result index(int page) {
        Pagination<User> pagging = new Pagination<User>(page, User.find());
        return ok(list.render(pagging));
    }

    public static Result add() {
        Form<User> userForm = Form.form(User.class).fill(new User());
        return ok(add.render(userForm));
    }

    public static Result save() {
        Form<User> userForm = Form.form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(add.render(userForm));
        }
        User user = userForm.get();
        removeEmptyRole(user);
        if (user.getRoles().isEmpty()) {
            userForm.reject("roles", "validation.required");
            return badRequest(add.render(userForm));
        }
        // check exist
        if (User.find().byId(user.username) != null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("message.user.exists", user.username));
            return badRequest(add.render(userForm));
        }
        // insert
        user.insert();
        flash(Application.FLASH_MESSAGE_KEY, Messages.get("success.create", labelUser, user.name));
        return redirect(routes.UserController.index(1));
    }

    public static Result edit(String id) {
        User user = User.find().byId(id);
        if (user == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("error.record.notfound"));
            return redirect(routes.UserController.index(1));
        }
        Form<User> userForm = Form.form(User.class).fill(User.find().byId(id));
        return ok(edit.render(id, userForm));
    }

    public static Result update(String id) {
        Form<User> userForm = Form.form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(edit.render(id, userForm));
        }
        User user = userForm.get();
        removeEmptyRole(user);
        if (user.getRoles().isEmpty()) {
            userForm.reject("roles", "validation.required");
            return badRequest(edit.render(id, userForm));
        }
        // update
        user = User.find().byId(userForm.get().username);
        if (user == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("message.user.notexists", userForm.get().username));
            return badRequest(edit.render(id, userForm));
        }
        user.name = userForm.get().name;
        user.email = userForm.get().email;
        user.enabled = userForm.get().enabled;
        user.user_roles = userForm.get().user_roles;
        user.update();
        flash(Application.FLASH_MESSAGE_KEY, Messages.get("success.update", labelUser, user.name));
        return redirect(routes.UserController.index(1));
    }

    public static Result search(int page, String search) {
        if (StringUtils.isNotBlank(search)) {

            Pagination<User> pagging = new Pagination<User>(page
                    , User.find().filter("username", Pattern.compile("^" + search, Pattern.CASE_INSENSITIVE)));

            return ok(list.render(pagging));
        } else {
            Pagination<User> pagging = new Pagination<User>(page
                    , User.find());
            return ok(list.render(pagging));
        }
    }

    public static Result delete(String id) {
        User user = User.find().byId(id);
        if (user == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("error.record.notfound"));
            return redirect(routes.UserController.index(1));
        }
        user.delete();
        flash(Application.FLASH_MESSAGE_KEY, Messages.get("success.delete", labelUser, user.name));
        return redirect(routes.UserController.index(1));
    }

    /*
     * Filter roles gets from view.
     */
    private static void removeEmptyRole(User user) {
        List<Role> fix = new ArrayList<Role>();
        for (Role role : user.user_roles) {
            if (role != null && role.id != null) {
                fix.add(role);
            }
        }
        user.user_roles = fix;
    }

}
