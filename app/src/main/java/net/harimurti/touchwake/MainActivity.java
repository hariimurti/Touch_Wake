package net.harimurti.touchwake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootShell.RootShell;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!RootShell.isAccessGiven() || !Engine.isSupported()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.not_support)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ApplicationExit();
                        }
                    });
            builder.create().show();
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                CustomDialogReceiver, new IntentFilter("CustomDialog"));
    }

    private void ApplicationExit() {
        this.finish();
    }

    private BroadcastReceiver CustomDialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String value_feather = intent.getStringExtra("feather");
            if (value_feather != null) {
                TextView feather = (TextView) findViewById(R.id.value_feather);
                feather.setText(value_feather);
            }
            String value_timeout = intent.getStringExtra("timeout");
            if (value_timeout != null) {
                TextView timeout = (TextView) findViewById(R.id.value_timeout);
                timeout.setText(value_timeout);
            }
            String value_pressure = intent.getStringExtra("pressure");
            if (value_pressure != null) {
                TextView pressure = (TextView) findViewById(R.id.value_pressure);
                pressure.setText(value_pressure);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                CustomDialog.showAbout(this);
                return true;

            case R.id.action_exit:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            ConfigManager config = new ConfigManager(getContext());
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_advanced, container, false);

                TextView valueFeather = (TextView)  rootView.findViewById(R.id.value_feather);
                valueFeather.setText(String.format(Locale.getDefault(), "%d", Engine.getFeather()));
                TextView valueTimeout = (TextView)  rootView.findViewById(R.id.value_timeout);
                valueTimeout.setText(String.format(Locale.getDefault(), "%d", Engine.getTimeout()));
                TextView valuePressure = (TextView)  rootView.findViewById(R.id.value_pressure);
                valuePressure.setText(String.format(Locale.getDefault(), "%d", Engine.getPressure()));

                ImageView imgOption1 = (ImageView) rootView.findViewById(R.id.option1);
                imgOption1.setOnClickListener(this);
                ImageView imgOption2 = (ImageView) rootView.findViewById(R.id.option2);
                imgOption2.setOnClickListener(this);
                ImageView imgOption3 = (ImageView) rootView.findViewById(R.id.option3);
                imgOption3.setOnClickListener(this);
            } else {
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                TextView kernelInfo = (TextView) rootView.findViewById(R.id.kernel_info);
                String textInfo = Engine.getKernelInfo();
                if (textInfo != null)
                    kernelInfo.setText(textInfo);

                Switch doubletap = (Switch) rootView.findViewById(R.id.switch1);
                boolean dt2w = config.getBoolean("doubletap");
                if (dt2w != Engine.getDoubleTap())
                    showToast(getContext(), R.string.toast_dtw, dt2w);
                Engine.setDoubleTap(dt2w);
                doubletap.setChecked(dt2w);
                doubletap.setOnCheckedChangeListener(this);

                Switch sweep = (Switch) rootView.findViewById(R.id.switch2);
                boolean s2w = config.getBoolean("sweep");
                if (s2w != Engine.getSweep())
                    showToast(getContext(), R.string.toast_stw, s2w);
                Engine.setSweep(s2w);
                sweep.setChecked(s2w);
                sweep.setOnCheckedChangeListener(this);
            }
            return rootView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.option1:
                    CustomDialog.setValue(getContext(), "Set Feather", "feather");
                    break;
                case R.id.option2:
                    CustomDialog.setValue(getContext(), "Set Timeout", "timeout");
                    break;
                case R.id.option3:
                    CustomDialog.setValue(getContext(), "Set Pressure", "pressure");
                    break;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ConfigManager cm = new ConfigManager(getContext());
            switch (buttonView.getId()) {
                case R.id.switch1:
                    cm.setBoolean("doubletap", isChecked);
                    Engine.setDoubleTap(isChecked);
                    showToast(getContext(), R.string.toast_dtw, isChecked);
                    break;
                case R.id.switch2:
                    cm.setBoolean("sweep", isChecked);
                    Engine.setSweep(isChecked);
                    showToast(getContext(), R.string.toast_stw, isChecked);
                    break;
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show ... total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_main);
                case 1:
                    return getString(R.string.tab_advanced);
            }
            return null;
        }
    }

    private static void showToast(Context context, int stringId, boolean value) {
        Toast.makeText(context,
                String.format(context.getString(stringId), value ? "On" : "Off"),
                Toast.LENGTH_SHORT)
                .show();
    }
}
