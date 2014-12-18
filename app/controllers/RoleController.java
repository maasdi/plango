package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.Role;
import models.User;

import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import views.html.role.*;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Roles
 *
 * @author maasdianto
 *         create on 12/10/2014
 */
@Security.Authenticated(Secured.class)
@Restrict(@Group("ADMIN"))
public class RoleController extends Controller {

    private static String labelRole = Messages.get("label.role");

    public static Result index() {
        List<Role> roles = Role.find().all();
        return ok(list.render(roles));
    }

    public static Result add() {
        Role role = new Role();
        role.id = UUID.randomUUID().toString();
        Form<Role> roleForm = Form.form(Role.class).fill(role);
        return ok(add.render(roleForm));
    }

    public static Result edit(String id) {
        Role role = Role.find().byId(id);
        if (role == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("error.record.notfound"));
            return redirect(routes.RoleController.index());
        }
        Form<Role> roleForm = Form.form(Role.class).fill(role);
        return ok(edit.render(id, roleForm));
    }

    public static Result save() {
        Form<Role> roleForm = Form.form(Role.class).bindFromRequest();
        if (roleForm.hasErrors()) {
            return badRequest(add.render(roleForm));
        }
        Role role = roleForm.get();
        if (Role.find().filter("name", role.name).get() != null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("message.role.exists", role.name));
            return badRequest(add.render(roleForm));
        }
        role.insert();
        flash(Application.FLASH_MESSAGE_KEY, Messages.get("success.create", labelRole, role.name));
        return redirect(routes.RoleController.index());
    }

    public static Result update(String id) {
        Form<Role> roleForm = Form.form(Role.class).bindFromRequest();
        if (roleForm.hasErrors()) {
            return badRequest(edit.render(id, roleForm));
        }
        Role role = roleForm.get();
        role.update();
        flash(Application.FLASH_MESSAGE_KEY, Messages.get("success.update", labelRole, role.name));
        return redirect(routes.RoleController.index());
    }

    public static Result search() {
        String search = request().getQueryString("search");
        List<Role> roles;
        if (StringUtils.isNotBlank(search)) {
            roles = Role.find().filter("name", Pattern.compile("^"+search, Pattern.CASE_INSENSITIVE)).asList();
        } else {
            roles = Role.find().all();
        }
        return ok(list.render(roles));
    }

    public static Result delete(String id) {
        Role role = Role.find().byId(id);
        if (role == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("error.record.notfound"));
            return redirect(routes.RoleController.index());
        }
        List<User> users = User.find().filter("user_roles in", role).asList();
        if (!users.isEmpty()) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("error.role.refs", role.name));
            return redirect(routes.RoleController.index());
        }
        role.delete();
        flash(Application.FLASH_MESSAGE_KEY, Messages.get("success.delete", labelRole, role.name));
        return redirect(routes.RoleController.index());
    }

}
