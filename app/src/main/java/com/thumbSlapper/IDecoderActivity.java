package com.thumbSlapper;

import android.graphics.Bitmap;
import android.os.Handler;

 

public interface IDecoderActivity {

    public ViewfinderView getViewfinder();

    public Handler getHandler();

    public CameraManager getCameraManager();

    public void handleDecode(Bitmap barcode);
}
