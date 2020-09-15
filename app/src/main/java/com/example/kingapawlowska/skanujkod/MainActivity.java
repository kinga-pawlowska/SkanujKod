package com.example.kingapawlowska.skanujkod;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    final Context context = this;
    private ZXingScannerView mScannerView;

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    DatabaseManager zb = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        }
    }

    public void onClick(View v) {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        mScannerView.stopCameraPreview();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //mScannerView.stopCamera();
        //mScannerView.stopCameraPreview();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mScannerView.startCamera();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handleResult(final Result result) {

        Log.w("handleResult", result.getText());

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Rezultat skanowania:");
        dialog.getWindow().setTitleColor(getResources().getColor(R.color.title_dialog));

        TextView text = (TextView) dialog.findViewById(R.id.text);

        text.setText(result.getText());

        SimpleDateFormat simpleDateHere = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        zb.addResult(result.getText(), simpleDateHere.format(new Date()));

        Button dialogButtonClose = (Button) dialog.findViewById(R.id.dialogButtonClose);
        //if button is clicked, close the custom dialog
        dialogButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogButtonSearch = (Button) dialog.findViewById(R.id.dialogButtonSearch);
        //if button is clicked, close the custom dialog
        dialogButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, result.getText());
                startActivity(intent);

                dialog.dismiss();
            }
        });

        Button dialogButtonWWW = (Button) dialog.findViewById(R.id.dialogButtonWWW);
        //if button is clicked, close the custom dialog
        dialogButtonWWW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = result.getText();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                dialog.dismiss();
            }
        });

        if (result.getText().startsWith("http://") || result.getText().startsWith("https://") ||
                result.getText().startsWith("www.")) {
            dialogButtonWWW.setEnabled(true);
            dialogButtonSearch.setEnabled(false);

        } else {
            dialogButtonWWW.setEnabled(false);
            dialogButtonSearch.setEnabled(true);
        }

        dialog.show();

        //Resume scanning
        //mScannerView.resumeCameraPreview(this);
        mScannerView.stopCamera();
        setContentView(R.layout.activity_main);
    }

    public void showAll(View view) {
        TextView tv = (TextView) findViewById(R.id.textView_main_search);

        Button buttonShow = (Button) findViewById(R.id.button_showSearch);
        buttonShow.setEnabled(false);
        Button buttonHide = (Button) findViewById(R.id.button_HideSearch);
        buttonHide.setEnabled(true);

        Cursor k = zb.giveAll();
        while (k.moveToNext()) {
            int nr = k.getInt(0);
            String wyszukanie = k.getString(1);
            String data = k.getString(2);
            tv.setText(tv.getText() + "\n" + nr + ".\t" + " " + wyszukanie + " - " + data);
        }
    }

    public void hideAll(View view) {
        TextView tv = (TextView) findViewById(R.id.textView_main_search);
        tv.setText("");
        Button buttonShow = (Button) findViewById(R.id.button_showSearch);
        buttonShow.setEnabled(true);
        Button buttonHide = (Button) findViewById(R.id.button_HideSearch);
        buttonHide.setEnabled(false);
    }
}
