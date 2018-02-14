package com.example.natha.univeristyqr;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class StudentFrag1 extends Fragment {

    private SurfaceView cameraPreview;
    private TextView txtResult;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.student1, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraPreview = (SurfaceView) getView().findViewById(R.id.camPreview);
        txtResult = (TextView) getView().findViewById(R.id.result);


        //QR CAMERA CODING

        barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector)
                .setRequestedPreviewSize(480, 480)
                .setAutoFocusEnabled(true)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                startCam();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                txtResult = (TextView) getView().findViewById(R.id.result);
                if(((MainActivity) getActivity()).screenClicked) {//if true
                    ((MainActivity) getActivity()).screenClicked = false; //reset
                    //txtResult.setText("Tap to scan your QR Code");
                    if (qrcodes.size() != 0) {
                        //cameraSource.stop(); //pause camera
                        txtResult.post(new Runnable() {
                            @Override
                            public void run() {
                                //create vibration
                                cameraSource.stop(); //pause camera
                                displayAlertMessage("Do you want to continue to " + qrcodes.valueAt(0).displayValue + "? ", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //handle yes





                                        //start camera again
                                        startCam();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //handle no




                                        //start camera again
                                        startCam();
                                    }
                                });

                                //txtResult.setText(qrcodes.valueAt(0).displayValue);
                            }
                        });
                    }
                }
            }
        });
        //END OF QR CAMERA CODING
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startCam(){
        //start camera again
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
            return;
        }
        try {
            cameraSource.start(cameraPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener YesListen, DialogInterface.OnClickListener NoListen) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("Yes", YesListen)
                .setNegativeButton("No",NoListen)
                .create()
                .show();
    }
}
