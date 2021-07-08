package org.smartregister.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.smartregister.R;
import org.smartregister.adapter.PagerAdapter;
import org.smartregister.view.contract.StatsContract;
import org.smartregister.view.fragment.StatsFragment;

public class StatsActivity extends AppCompatActivity implements StatsContract.View {

    private ProgressDialog progressDialog;
    protected StatsFragment mBaseFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        Toolbar toolbar = this.findViewById(R.id.summary_toolbar);
        toolbar.setTitle(R.string.return_to_register);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupViews();
    }

    protected void setupViews() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
    }

    protected ViewPager setupViewPager(ViewPager viewPager) {
        mBaseFragment = new StatsFragment();
        Fragment[] otherFragments = getOtherFragments();
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mBaseFragment, otherFragments);
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    @Override
    public void showProgressDialog(int titleIdentifier) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(titleIdentifier);
        progressDialog.setMessage(getString(R.string.please_wait_message));

        if (!isFinishing())
            progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected Fragment[] getOtherFragments() {
        Fragment[] fragments = new Fragment[1];
        fragments[0] = new StatsFragment();
        return fragments;
    }
}
