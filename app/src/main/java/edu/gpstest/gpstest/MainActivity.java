package edu.gpstest.gpstest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView keidoText;
    private TextView idoText;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keidoText = findViewById(R.id.keidoText);
        idoText = findViewById(R.id.idoText);

    }

    public void onGpsLogggerButtonClick(View v) {
        Toast.makeText(this, "GPSロガーボタンが押されました",Toast.LENGTH_SHORT).show();
        //keidoText.setText("35.000000000000");
        //idoText.setText("40.000000000000");

        //パーミッションの確認
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //パーミッションがない場合、パーミッションのリクエストを表示する
            ActivityCompat.requestPermissions(this,
               new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            Toast.makeText(this, "リクエストが終了しました。",Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "許可されています。",Toast.LENGTH_SHORT).show();
            locationStart();
        }

        Toast.makeText(this, "GPSロガーボタンの動作を終了しました",Toast.LENGTH_SHORT).show();

    }


    //ロケーションの実行
    private void locationStart() {
        Toast.makeText(this, "ロケーションスタートメソッド実行",Toast.LENGTH_SHORT).show();
        Log.d("debug","locationStart()");

        //ロケーションマネージャインスタンス生成
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //端末の設定で位置情報が"高精度"か"GPS"になっていることを確認
        if(locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug","Location Manager Enabled");
        } else {
            //GPS利用不可に設定している場合、位置情報を"GPS"か"高精度"に設定するよう促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug","not gpsEnable, startActivity");
        }

        //
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            Log.d("debug","checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,50,this);


    }

    //パーミッション使用許可結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull  String[] permissions,@NonNull int[] grantResults) {
        if (requestCode == 1000) {
            //使用が許可された
            Toast.makeText(this, "GPSのアクセス権限が承諾されました",Toast.LENGTH_SHORT).show();
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //位置測定を始めるコードで飛ぶ
                locationStart();
            }
        } else {
            //拒否された場合
            Toast.makeText(this, "GPSのアクセス権限が拒否されました",Toast.LENGTH_SHORT).show();

        }
    }

    public void onGoogleMapButtonClick(View v) {
        Toast.makeText(this, "GoogleMap表示ボタンが押されました",Toast.LENGTH_SHORT).show();

        //TextViewから経度・緯度の取得
        String keido = keidoText.getText().toString();
        String ido = idoText.getText().toString();

        if (keido == "" || ido == "") {
            Toast.makeText(this, "位置情報が正しく取得されていません。処理を中止します。",Toast.LENGTH_SHORT).show();
        }

        //URLを指定しGoogleMapを開く場合の処理
//        Uri uri = Uri.parse("geo:"+keido+","+ido+"z=26");
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }

        //Maps SDK for Androidを使って開く場合
        Intent intent = new Intent(getApplication(), MapsActivity.class);
        intent.putExtra("keido",keido);
        intent.putExtra("ido",ido);
        startActivity(intent);

//        Toast.makeText(this, "GoogleMap表示ボタンの動作を終了しました",Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onStatusChanged(String provider,int status , Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {

        String keido;
        String ido;

        keido = String.format("%.6f",location.getLatitude());
        ido = String.format("%.6f",location.getLongitude());

        Log.d("debug","【位置情報が変化しました】");
        Log.d("debug","Latitude:"+keido);
        Log.d("debug","Longitude:"+ido);

        //経度・緯度の表示
        keidoText.setText(keido);
        idoText.setText(ido);

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
