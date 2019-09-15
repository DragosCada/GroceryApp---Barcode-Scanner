package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
//import android.speech.tts.TextToSpeech;
import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.FirebaseVision;

import java.util.List;
//import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private final int BARCODE_RECO_REQ_CODE=200;
    TextView rawdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rawdata=(TextView) findViewById(R.id.rawdata);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==BARCODE_RECO_REQ_CODE){
            if (resultCode==RESULT_OK){
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                barcodeRecognition(photo);
            }
        }
    }

    private void barcodeRecognition(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);

        // [START set_detector_options]
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_ALL_FORMATS
                                //, FirebaseVisionBarcode.FORMAT_QR_CODE,
                                // FirebaseVisionBarcode.FORMAT_AZTEC
                                )
                        .build();
        // [END set_detector_options]

        FirebaseVisionBarcodeDetector barcodeDetector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();
        Task<List<FirebaseVisionBarcode>> result = barcodeDetector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();
                            int valueType = barcode.getValueType();

                            if(rawValue.contains("06038304969")){
                                rawdata.setText("President's Choice Chocolate Chunk Cookies $2.97");
                            }
                            else {
                                rawdata.setText(rawValue);
                            }

                            //Toast.makeText(HomeActivity.this, rawValue,Toast.LENGTH_SHORT).show();

                            // See API reference for complete list of supported types
                            /*switch (valueType) {
                                case FirebaseVisionBarcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    break;
                                case FirebaseVisionBarcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    break;
                            }*/
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@Nullable Exception e) {
                        rawdata.setText("Something went wrong");
                        // Toast.makeText(HomeActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void barcodeReco(View view){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,BARCODE_RECO_REQ_CODE);
        rawdata.setText(""); //every time you click button, it resets text
    }




}
