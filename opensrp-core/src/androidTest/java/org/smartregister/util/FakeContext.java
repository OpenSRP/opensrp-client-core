package org.smartregister.util;

import org.smartregister.Context;
import org.smartregister.service.DrishtiService;
import org.smartregister.service.UserService;

import java.util.Date;

public class FakeContext {
    public static Context setupService(final DrishtiService drishtiService,
                                       final long numberOfMillisecondsAfterNowThatThisSessionEnds) {
        Context context = Context.setInstance(new Context() {
            @Override
            protected DrishtiService drishtiService() {
                return drishtiService;
            }
        });

        Session session = context.session().start(numberOfMillisecondsAfterNowThatThisSessionEnds);
        session.setPassword("password").setRepositoryName("drishti.db." + (new Date().getTime() - 1));

        return context;
    }

    public static Context setupService(final DrishtiService drishtiService, final FakeUserService userService,
                                       final int numberOfMillisecondsAfterNowThatThisSessionEnds) {
        Context context = Context.setInstance(new Context() {
            @Override
            protected DrishtiService drishtiService() {
                return drishtiService;
            }

            @Override
            public UserService userService() {
                return userService;
            }
        });

        Session properties = context.session().start(numberOfMillisecondsAfterNowThatThisSessionEnds);
        properties.setPassword("password").setRepositoryName("drishti.db." + (new Date().getTime() - 1));
        userService.setSession(context.session());

        return context;
    }
}
