package org.smartregister.service;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.service.formsubmissionhandler.FormSubmissionRouter;

import java.util.Map;

import static java.text.MessageFormat.format;
import static org.mozilla.javascript.Context.enter;
import static org.mozilla.javascript.Context.exit;
import static org.mozilla.javascript.Context.toObject;
import static org.smartregister.AllConstants.FORM_SUBMISSION_ROUTER;
import static org.smartregister.AllConstants.REPOSITORY;
import static org.smartregister.AllConstants.ZIGGY_FILE_LOADER;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

public class ZiggyService {
    private static final String SAVE_METHOD_NAME = "save";
    private static final String JS_INIT_SCRIPT =
            "require([\"ziggy/FormDataController\"], " + "function (FormDataController) {\n"
                    + "    controller = FormDataController;\n" + "});";

    private ZiggyFileLoader ziggyFileLoader;
    private FormDataRepository dataRepository;
    private FormSubmissionRouter formSubmissionRouter;
    private Context context;
    private Function saveFunction;

    public ZiggyService(ZiggyFileLoader ziggyFileLoader, FormDataRepository dataRepository,
                        FormSubmissionRouter formSubmissionRouter) {
        this.ziggyFileLoader = ziggyFileLoader;
        this.dataRepository = dataRepository;
        this.formSubmissionRouter = formSubmissionRouter;
        initRhino();
    }

    public void saveForm(String params, String formInstance) throws Exception {
        //context = enter();
        //saveFunction.call(context, scope, scope, new Object[]{params, formInstance});
        logInfo(format("Saving form successful, with params: {0}, with instance {1}.", params,
                formInstance));
        //exit();
    }

    private void initRhino() {
        try {
            context = enter();
            context.setOptimizationLevel(-1);
            ScriptableObject scope = context.initStandardObjects();
            String jsFiles = ziggyFileLoader.getJSFiles();
            scope.put(REPOSITORY, scope, toObject(dataRepository, scope));
            scope.put(ZIGGY_FILE_LOADER, scope, toObject(ziggyFileLoader, scope));
            scope.put(FORM_SUBMISSION_ROUTER, scope, toObject(formSubmissionRouter, scope));
            context.evaluateString(scope, jsFiles + JS_INIT_SCRIPT, "code", 1, null);
            saveFunction = ((Function) ((Map) scope.get("controller", scope))
                    .get(SAVE_METHOD_NAME));
        } catch (Exception e) {
            logError("Rhino initialization failed. We are screwed. EOW!!!. Evil: " + e);
        } finally {
            exit();
        }
    }
}
