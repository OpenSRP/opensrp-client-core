package org.smartregister.view.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.ConfigurationInstancesHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseConfigurableRegisterActivityModel implements BaseRegisterContract.Model {

    private ModuleConfiguration moduleConfiguration;

    public BaseConfigurableRegisterActivityModel(@NonNull ModuleConfiguration moduleConfiguration) {
        this.moduleConfiguration = moduleConfiguration;
    }

    public ModuleConfiguration getModuleConfiguration() {
        return moduleConfiguration;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            getModuleConfiguration().getConfigurableViewsLibrary().registerViewConfigurations(viewIdentifiers);
        }
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            getModuleConfiguration().getConfigurableViewsLibrary().unregisterViewConfigurations(viewIdentifiers);
        }
    }

    @Override
    public void saveLanguage(String language) {
        // Empty implementation
    }

    @Nullable
    @Override
    public String getLocationId(@Nullable String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Nullable
    @Override
    public HashMap<Client, List<Event>> processRegistration(String jsonString, FormTag formTag) {
        try {
            return ConfigurationInstancesHelper.newInstance(getModuleConfiguration().getFormProcessorClass()).extractEventClient(jsonString, null, formTag);
        } catch (JSONException e) {
            Timber.e(e);
            return null;
        }
    }

    @Nullable
    @Override
    public HashMap<String, String> getInjectedFieldValues(CommonPersonObjectClient client) {
        HashMap<String, String> injectableFields = ConfigurationInstancesHelper.newInstance(getModuleConfiguration().getFormProcessorClass()).getInjectableFields();
        if (injectableFields != null) {
            HashMap<String, String> injectableFieldValues = new HashMap<>();
            for (String formKey : injectableFields.keySet()) {
                injectableFieldValues.put(formKey, client.getDetails().get(injectableFields.get(formKey)));
            }
            return injectableFieldValues;
        }
        return null;
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws JSONException {
        return getFormAsJson(formName, entityId, currentLocationId, null);
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId,
                                    String currentLocationId, @Nullable HashMap<String, String> injectedValues) throws JSONException {
        try {
            JSONObject form = FormUtils.getInstance(CoreLibrary.getInstance().context().applicationContext())
                    .getFormJson(formName);
            if (form != null) {
                return ConfigurationInstancesHelper.newInstance(getModuleConfiguration().getFormProcessorClass())
                        .getFormAsJson(form, formName, entityId, currentLocationId, injectedValues);
            }
        } catch (Exception ex) {
            Timber.e(ex);
        }
        return null;
    }

    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }

}
