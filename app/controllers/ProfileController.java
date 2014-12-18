package controllers;

import models.Profile;
import models.User;
import play.Play;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.FileUploader;
import views.html.user.profile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * ProfileController
 *
 * @author maasdianto
 *         create on 12/16/2014
 */
@Security.Authenticated(Secured.class)
public class ProfileController extends Controller {

    private static final String BASEPATH = Play.application().path().getAbsolutePath() + File.separator + "public" + File.separator;
    public static final String PROFILE_DIR = "picture" + File.separator;
    // Supported formats
    private static final List<String> FORMATS = Arrays.asList(new String[]{"png", "gif", "jpg", "jpeg"});

    public static Result index() {
        User user = User.find().byId(request().username());
        Form<User> userForm = Form.form(User.class).fill(user);
        return ok(profile.render(userForm));
    }

    public static Result updateProfile() {
        Form<User> userForm = Form.form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(profile.render(userForm));
        }
        User user = User.find().byId(request().username());
        if (user == null) {
            flash(Application.FLASH_ERROR_KEY, Messages.get("message.user.notexists", userForm.get().username));
            return badRequest(profile.render(userForm));
        }
        // set update field
        user.name = userForm.get().name;
        user.email = userForm.get().email;

        // check whether user update profile picture
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filepart = body.getFile("profile_picture");
        if (filepart != null) {
            FileUploader uploader = new FileUploader(filepart);

            String path = BASEPATH + File.separator + PROFILE_DIR;
            uploader.setFileName(user.username);
            uploader.setDirectory(path);
            if (!FORMATS.contains(uploader.getFileExtension().toLowerCase())) {
                flash(Application.FLASH_ERROR_KEY, Messages.get("message.profile.invalidformat", FORMATS));
                return badRequest(profile.render(userForm));
            }
            if (uploader.upload()) {
                // update profile image
                if (user.profile == null) {
                    user.profile = new Profile();
                }
                user.profile.profile_img = uploader.getFileName();
            } else {
                flash(Application.FLASH_ERROR_KEY, Messages.get("error.upload"));
                return badRequest(profile.render(userForm));
            }
        }
        user.update();
        flash(Application.FLASH_MESSAGE_KEY, Messages.get("message.profile.update"));
        return redirect(routes.ProfileController.index());
    }

}
