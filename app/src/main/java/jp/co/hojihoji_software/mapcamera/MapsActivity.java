package jp.co.hojihoji_software.mapcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMyLocationButtonClickListener, LocationSource {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    private OnLocationChangedListener onLocationChangedListener = null;

    private int priority[] = {LocationRequest.PRIORITY_HIGH_ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_NO_POWER};
    private int locationPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //LocationRequestを生成して精度、インターバルを設定
        locationRequest = LocationRequest.create();

        //測位の精度、消費電力の優先度
        locationPriority = priority[1];

        if(locationPriority == priority[0]){
            //位置情報の精度を優先する場合
            locationRequest.setPriority(locationPriority);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(16);
        }else if(locationPriority == priority[1]){
            //消費電力を考慮する場合
            locationRequest.setPriority(locationPriority);
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(16);
        }else if(locationPriority == priority[2]){
            //都市レベルの精度
            locationRequest.setInterval(locationPriority);
        }else{
            //外部からのトリガーでの測位のみ
            locationRequest.setPriority(locationPriority);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    //onResumeフェーズに入ったら接続
    @Override
    protected void onResume(){
        super.onResume();
        mGoogleApiClient.connect();
    }

    //onPauseで切断
    protected void onPause(){
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public  void onMapReady(GoogleMap googleMap){
        //パーミッションのチェック
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d("debug", "permission granted");

            //FusedLocationApi
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            //fusedLocationClient


        }else{
            Log.d("debug", "permission error");
            return;
        }
    }

}
