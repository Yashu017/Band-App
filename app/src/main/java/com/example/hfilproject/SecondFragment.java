package com.example.hfilproject;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    CircleImageView pass;
    private double lat1, long1;
    TextView address;
    UpdateBackgroundLocation service=null;
    boolean mbound=false;
    Button locUpdates;
    private final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            UpdateBackgroundLocation.LocalBinder binder=(UpdateBackgroundLocation.LocalBinder)iBinder;
            service=binder.getService();
            mbound=true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service=null;
            mbound=false;

        }
    };



    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_second, container, false);


        address = view.findViewById(R.id.originalAddressFrag);
        pass = view.findViewById(R.id.openMAp);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);


            }
        });


        Dexter.withActivity(getActivity())
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                locUpdates=view.findViewById(R.id.locUp);
                locUpdates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        service.requestLocationUpdates();
                    }
                });

                startService(Common.requestLoctionUpdates(getContext()));
                getActivity().bindService(new Intent(getContext(),UpdateBackgroundLocation.class),mServiceConnection,Context.BIND_AUTO_CREATE);


            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();
      return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(getContext()).
        registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        if(mbound)
        {
            getActivity().unbindService(mServiceConnection);
            mbound=false;
        }
        PreferenceManager.getDefaultSharedPreferences(getContext()).
                unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(Common.KEY_REQUESTING_LOCATION_UPDATES))
        {
            startService(sharedPreferences.getBoolean(Common.KEY_REQUESTING_LOCATION_UPDATES,false));

        }
    }

    private void startService(boolean aBoolean) {

        if(aBoolean)
        {
           // locUpdates.setEnabled(false);
        }
        else
        {
            //locUpdates.setEnabled(true);
        }



    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocation event)
    {
        if(event!=null)
        {
            double latitude=event.getLocation().getLatitude();
            double longitude=event.getLocation().getLongitude();
            String data= new StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude()).toString();

            Toast.makeText(getContext(),data,Toast.LENGTH_LONG).show();


            Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
                String address1=addressList.get(0).getAddressLine(0);
                String area=addressList.get(0).getLocality();
                String city=addressList.get(0).getAdminArea();
                String country=addressList.get(0).getCountryName();
                String postalCode=addressList.get(0).getPostalCode();
                String fulladdress= address1 + ", " + area+", " + city + ", " + country + ", "+ postalCode;
                address.setText(fulladdress);
                locUpdates.setVisibility(View.GONE);
            }
            catch (Exception e)
            {
                e.printStackTrace();

            }


        }

    }

}
