package com.example.android.receiptreader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.security.auth.callback.Callback;

public class SecondCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    Camera camera;
    SurfaceView cameraView;
    SurfaceHolder surfaceHolder;
    boolean camCondition = false;
    Button capture_camera_button;
    Long whenReceiptPictureTakenTime = null;
    String receiptJPGImagePathAfterCaptureButtonTrigger = null;
//    private boolean checkCameraHardware(Context context) {
//        // this device has a camera
//        // no camera on this device
//        rturn context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
//

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_fragment_camera);
        System.out.println("MADE IT SECOND CAMERA ACTIVITY");
        // getWind() to get widnow and set its pizel format with is UNKNOWNy
//        getWindow().setFormat(PixelFormat.getPixelFormatInfo(Format.pix););
        // referring the id of surface view

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, R.string.camera_permission_not_grated, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        else {
            cameraView = (SurfaceView) findViewById(R.id.camera_view);
            // getting access to the surface of surfaceView and return it
            surfaceHolder = cameraView.getHolder();
            // adding call back to this context means SecondCameraActivity
            surfaceHolder.addCallback(this);
            // to set surface type
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
            capture_camera_button = (Button) findViewById(R.id.capture_button);
            capture_camera_button.setOnClickListener((v) -> {
                // TODO auto-generated method stub
                // calling a method of camera class take picture by passing our call back
                camera.takePicture(null, null, null, mPictureCallback);
            });

            // when the users clicks thet capture image button the once null whenReceiptPictureTakenTime variable
            // is now not null and we will access the jpg image with its description
            if (whenReceiptPictureTakenTime != null) {
                // accessing the image to run the ocr on and update jsom
                try {
                    SmartAlgorithms.indiaBazaarSmartAlgorithm(receiptJPGImagePathAfterCaptureButtonTrigger);
                } catch (IOException e) {
                    Toast.makeText(this, R.string.couldnt_analyze_receipt, Toast.LENGTH_LONG);
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, R.string.couldnt_analyze_receipt, Toast.LENGTH_LONG);
            }
        }

    }

    Camera.PictureCallback mPictureCallback = (data, c) -> {
        FileOutputStream outStream = null;
        try{
            // creating a new jpg file to store the receipt image to be passed to tess base api to analyze in smart algorithms
            whenReceiptPictureTakenTime = System.currentTimeMillis();
            receiptJPGImagePathAfterCaptureButtonTrigger = "src\\main\\java\\images" + whenReceiptPictureTakenTime + "jpg";
            outStream = new FileOutputStream(receiptJPGImagePathAfterCaptureButtonTrigger);
            outStream.write(data);
            outStream.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally{

        }
    };

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        // stop the camera
        if(camCondition){
            camera.stopPreview();
            camCondition = false;
        }
        if(camera != null){
            try{
                Camera.Parameters parameters = camera.getParameters();
                parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();

                camCondition = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();
        } catch(Exception exception){
            System.out.println("SURFACE CREATED ERROR");
            Toast.makeText(this, R.string.couldnt_connect_to_camera, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camCondition = false;
    }


}