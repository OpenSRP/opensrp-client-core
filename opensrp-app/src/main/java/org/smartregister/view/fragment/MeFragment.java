package org.smartregister.view.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.contract.MeContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class MeFragment extends Fragment implements MeContract.View {
    protected MeFragmentActionHandler meFragmentActionHandler = new MeFragmentActionHandler();
    protected MeContract.Presenter presenter;

    private TextView initials;
    private TextView userName;

    private RelativeLayout me_location_section;
    private RelativeLayout setting_section;
    private RelativeLayout logout_section;
    private LocationPickerView facilitySelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
        setClickListeners();
        presenter.updateInitials();
        presenter.updateName();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateLocationText();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocationText();
    }

    protected void setUpViews(View view) {
        initials = view.findViewById(R.id.initials);
        userName = view.findViewById(R.id.user_name);
        me_location_section = view.findViewById(R.id.me_location_section);
        setting_section = view.findViewById(R.id.setting_section);
        logout_section = view.findViewById(R.id.logout_section);
        facilitySelection = view.findViewById(R.id.facility_selection);

        if (me_location_section != null && !Utils.getBooleanProperty(AllConstants.PROPERTY.DISABLE_LOCATION_PICKER_VIEW)) {
            facilitySelection.init();
            facilitySelection.setTextColor(getResources().getColor(R.color.text_black));
        }
        TextView application_version = view.findViewById(R.id.application_version);
        if (application_version != null) {
            try {
                application_version.setText(String.format(getString(R.string.app_version), Utils.getVersion(getActivity()), presenter.getBuildDate()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        TextView synced_data = view.findViewById(R.id.synced_data);
        if (synced_data != null) {
            //Todo Update this to the values after the sync functionality is added.
            synced_data.setText(String.format(getString(R.string.data_synced), new SimpleDateFormat("dd MMM yyyy", Utils.getDefaultLocale()).format(new
                    Date()), new SimpleDateFormat("hh:mm a", Utils.getDefaultLocale()).format(new Date())));
        }
    }

    protected void setClickListeners() {
        me_location_section.setOnClickListener(meFragmentActionHandler);
        setting_section.setOnClickListener(meFragmentActionHandler);
        logout_section.setOnClickListener(meFragmentActionHandler);
    }

    protected abstract void initializePresenter();

    @Override
    public void updateInitialsText(String userInitials) {
        if (initials != null) {
            initials.setText(userInitials);
        }
    }

    @Override
    public void updateNameText(String name) {
        if (userName != null) {
            userName.setText(name);
        }
    }

    protected void updateLocationText() {
        if (facilitySelection != null) {
            facilitySelection.setText(LocationHelper.getInstance().getOpenMrsReadableName(facilitySelection.getSelectedItem()));
            String locationId = LocationHelper.getInstance().getOpenMrsLocationId(facilitySelection.getSelectedItem());
            CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.CURRENT_LOCATION_ID, locationId);
        }
    }

    protected abstract void onViewClicked(View view);

    /**
     * Handles the Click actions on any of the section in the page.
     */
    private class MeFragmentActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            /*if (view.getId() == R.id.setting_section) {
                //ToDO Add the functionality for the setting page after that is decided.
            } else */
            if (view.getId() == R.id.me_location_section) {
                if (facilitySelection != null) {
                    LocationPickerView locationPickerView = new LocationPickerView(getContext());
                    locationPickerView.init();
                    locationPickerView.onClick(facilitySelection);
                }
            } else {
                onViewClicked(view);
            }
        }

    }
}
