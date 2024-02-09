/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slapStick;


import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

 

/**
 * Example Capture Activity.
 * 
 * @author Justin Wetherell (phishman3579@gmail.com)
 */
public class CaptureActivity extends DecoderActivity {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    Button vbtnTakePic = null;

 
    private View resultView = null;
    private boolean inScanMode = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.capture);
        Log.v(TAG, "onCreate()");
 


        inScanMode = false;
        // DO NOT USE OTHER PACKAGE NAMES IN THIS PROJECT! IT WILL CAUSE A MANIFEST MERGE CONFLICT!
        // DO NOT USE OTHER PACKAGE NAMES IN THIS PROJECT! IT WILL CAUSE A MANIFEST MERGE CONFLICT!
        // CHECK FOR CAMERA permissions are granted




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

 
    @Override
    protected void onResume() {
		try {
        super.onResume();
        Log.v(TAG, "onResume()");

        // CameraManager must be initialized here, not in onCreate().
        if (cameraManager == null) cameraManager = new CameraManager(getApplication());

	  cameraManager.setManualFramingRect(280, 280);

        if (viewfinderView == null) {
            viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
            viewfinderView.setCameraManager(cameraManager);
        }

        showScanner();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

		} catch(Exception e) {
        System.out.println("onResume.DecoderActivity.error: " + e);
		e.printStackTrace();
		}
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish(); 
	}
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void handleDecode(Bitmap barcode) {
 
    }

    protected void showScanner() {
        inScanMode = true;

        viewfinderView.setVisibility(View.VISIBLE);
    }
 

 


 

    // Put up our own UI for how to handle the decodBarcodeFormated contents.
    private void handleDecodeInternally(Bitmap barcode) {
        onPause();
        inScanMode = false;

 
                            Intent intent = new Intent();
                    // intent.putExtra("encdBmp", displayContents);
                    setResult(RESULT_OK, intent);
                    finish();
    }



    protected void takeAPicture() {
                System.out.println("takePicture");
cameraManager.doTakeAPicture(maPicture);
 
    }

 





    public Camera.PictureCallback maPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            System.out.println("PictureCallback onPictureTaken");
            try {
                String isdone = "noQvalue";
 
           
            } catch (Exception e) {
                System.out.println("onPictureTaken: " + e.toString());

            }
        }
    };




}
