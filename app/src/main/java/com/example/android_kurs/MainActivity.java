package com.example.android_kurs;

import android.Manifest;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;
import java.io.IOException;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView coordinatesTextView;
    private TextView signalInfoTextView;
    private TelephonyManager telephonyManager;
    private LocationManager locationManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatesTextView = findViewById(R.id.coordinatesTextView);
        signalInfoTextView = findViewById(R.id.signalInfoTextView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // Настройка Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.10:3000") // Замените на IP вашего ноутбука
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Запрос разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.INTERNET
            }, 1);
            return; // Выход из метода, пока разрешения не получены
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        getSignalInfo();
    }

    private void getSignalInfo() {
        // Получаем информацию о сигнале
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            if (cellInfoList != null && !cellInfoList.isEmpty()) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                        CellSignalStrengthLte signalStrength = cellInfoLte.getCellSignalStrength();

                        int rsrp = signalStrength.getDbm();
                        int rsrq = signalStrength.getRsrq();
                        int rssi = signalStrength.getRssi();
                        int asuLevel = signalStrength.getAsuLevel();
                        int level = signalStrength.getLevel(); // Общее качество сигнала



                        String operatorName = telephonyManager.getNetworkOperatorName();
                        String mcc = telephonyManager.getNetworkOperator();
                        String mnc = mcc.length() > 3 ? mcc.substring(3) : "";
                        String bandwidth = "Неизвестно"; // Можно добавить логику для получения ширины полосы, если доступно

                        signalInfoTextView.setText("RSRP: " + rsrp + " dBm\n" +
                                "RSRQ: " + rsrq + " dB\n" +
                                "RSSI: " + rssi + " dBm\n" +
                                "ASU Level: " + asuLevel + "\n" +
                                "Signal Level: " + level +
                                "\nOperatorName: " + operatorName +
                                "\nmcc: " + mcc +
                                "\nmnc: " + mnc +
                                "\nШирина полосы: " + bandwidth);


                        // Получаем местоположение
                        double latitude = 0;
                        double longitude = 0;
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                        SignalData signalData = new SignalData(rsrp, rsrq, rssi, asuLevel, level, operatorName, mnc, mcc, bandwidth, latitude, longitude);

                        // Отправляем данные на сервер
                        sendSignalDataToServer(signalData);
                    }
                }
            }
        }
    }

    private void sendSignalDataToServer(SignalData signalData) {
        apiService.sendSignalData(signalData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Данные отправлены на сервер", Toast.LENGTH_SHORT).show();
                } else {
                    // Логируем и показываем сообщение об ошибке
                    String errorMessage = "Ошибка отправки данных: " + response.code();
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("Retrofit Error", errorBody);
                        errorMessage += "\nОтвет от сервера: " + errorBody;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Выводим более подробную информацию о возникшей ошибке
                Log.e("Retrofit Failure", t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Листенер для получения местоположения устройства
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            coordinatesTextView.setText("Широта: " + latitude + "\nДолгота: " + longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Toast.makeText(MainActivity.this, "GPS отключен", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    getSignalInfo();
                }
            } else {
                Toast.makeText(this, "Разрешение не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
