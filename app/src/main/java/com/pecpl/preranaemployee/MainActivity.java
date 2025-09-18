package com.pecpl.preranaemployee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.app.ActivityCompat;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.content.DialogInterface;


public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Force the activity to be in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mWebView = findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient()); // to handle URL redirects in the app
        mWebView.getSettings().setJavaScriptEnabled(true); // to enable JavaScript on web pages
        mWebView.getSettings().setGeolocationEnabled(true); // to enable GPS location on web pages
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Load the web page if location permission is granted
            mWebView.loadUrl(url);
        }
//        mWebView.loadUrl("https://www.google.com");

        if (com.example.firstapp.NetworkUtils.isNetworkAvailable(this)) {
            mWebView.loadUrl("https://www.google.com");
//            webView.loadUrl("https://www.example.com"); // Load your desired URL
        } else {
            // Handle no internet connection:
            Toast.makeText(this, "No internet connection available.", Toast.LENGTH_LONG).show();
            showExitDialog();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // Check GPS status every time the activity comes to the foreground
        checkGpsStatus();
    }

    private void checkGpsStatus() {
        if (!isGpsEnabled()) {
            showGpsAlert();
        } else {
            // GPS is enabled, you can now start your app's main functionality
            // For example:
            // startLocationUpdates();
            // loadMaps();
//            Toast.makeText(this, "Enable GPS First", Toast.LENGTH_LONG).show();
//            showExitDialog();

        }
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showGpsAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Location Services Disabled")
                .setMessage("Please enable GPS to use this app.")
                .setCancelable(false) // Prevent the dialog from being dismissed by tapping outside
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Exit App", (dialog, which) -> {
                    finish(); // Close the activity
                })
                .show();
    }

    public class myWebViewclient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);


            final String urls = url;
            if (urls.contains("mailto") || urls.contains("whatsapp") || urls.contains("tel") || urls.contains("sms") || urls.contains("facebook") || urls.contains("truecaller") || urls.contains("")) {
                mWebView.stopLoading();
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(urls));
                startActivity(i);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mWebView.loadUrl(url); // to load the web page after location permission is granted
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showExitDialog() {
        // Show the toast message
        Toast.makeText(this, "No internet connection detected.", Toast.LENGTH_LONG).show();

        // Build and show the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Application");
        builder.setMessage("The application requires an internet connection and will now close.");
        builder.setCancelable(false); // User must choose the exit option

        // Define what happens when the "Exit" button is clicked
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user chose to exit, so finish the activity and close the app
//                finish();
                System.exit(0);
            }
        });

        // Create and show the dialog
        AlertDialog alert = builder.create();
        alert.show();
    }


}