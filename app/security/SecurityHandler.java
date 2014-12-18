package security;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import controllers.SecurityController;
import controllers.routes;
import org.apache.commons.lang3.StringUtils;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Results;
import play.mvc.SimpleResult;
import views.html.error.forbidden;

/**
 * SecurityHandler
 *
 * @author maasdianto
 *         create on 12/17/2014
 */
public class SecurityHandler extends AbstractDeadboltHandler {

    @Override
    public F.Promise<SimpleResult> beforeAuthCheck(Http.Context context) {
        if (StringUtils.isNotBlank(context.request().username())) {
            return F.Promise.pure(null);
        } else {
            return F.Promise.promise(new F.Function0<SimpleResult>() {
                @Override
                public SimpleResult apply() throws Throwable {
                    return redirect(routes.SecurityController.login());
                }
            });
        }
    }

    @Override
    public F.Promise<SimpleResult> onAuthFailure(final Http.Context context, final String content) {
        return F.Promise.promise(new F.Function0<SimpleResult>(){
            @Override
            public SimpleResult apply() throws Throwable {
                return Results.forbidden(forbidden.render(context.request().method(), context.request().path()));
            }
        });
    }

    @Override
    public Subject getSubject(Http.Context context) {
        return (Subject) context.args.get(SecurityController.AUTH_CONTEXT_USER);
    }
}
