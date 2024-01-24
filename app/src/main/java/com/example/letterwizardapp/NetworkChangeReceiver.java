package com.example.letterwizardapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import androidx.appcompat.app.AlertDialog;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isOnline(context)) {
            showNoInternetDialog(context);
        } else {
            dismissNoInternetDialog();
        }
    }
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private AlertDialog noInternetDialog;
    private void showNoInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.CustomAlertDialogStyle);
        builder.setMessage("No internet connection. Please check your network settings.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        noInternetDialog = builder.create();
        noInternetDialog.show();
    }
    private void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }
}
