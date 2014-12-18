package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import play.mvc.Security;
import util.FileUploader;
import views.html.upload.*;

/**
 * UploadFile
 *
 * @author maasdianto
 *         create on 12/14/2014
 */
@Security.Authenticated(Secured.class)
public class UploadFileController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileController.class);

    public static Result index() {
        return ok(index.render());
    }

    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filepart = body.getFile("file");

        if (filepart != null) {
            FileUploader uploader = new FileUploader(filepart);
            String path = Play.application().configuration().getString("upload.location");

            uploader.setFileName("demo");
            uploader.setDirectory(path);

            if (uploader.upload()) {
                flash(Application.FLASH_MESSAGE_KEY, Messages.get("success.upload"));
                return redirect(routes.UploadFileController.index());
            } else {
                flash(Application.FLASH_ERROR_KEY, Messages.get("error.upload"));
                return redirect(routes.UploadFileController.index());
            }
        } else {
            flash(Application.FLASH_ERROR_KEY, Messages.get("error.file.missing"));
            return redirect(routes.UploadFileController.index());
        }
    }

}
