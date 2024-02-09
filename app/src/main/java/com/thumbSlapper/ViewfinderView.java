/*
 * Copyright (C) 2008 ZXing authors
 * 
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

package com.thumbSlapper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;




import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
 


// import com.google.zxing.ResultPoint;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View  {
    private static final String TAG = ViewfinderView.class.getSimpleName();
    private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };
    private static final long ANIMATION_DELAY = 100L;
    private static final long ANIMATION_PDELAY = 1000L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    private CameraManager cameraManager;
    private final Paint paint;
	Paint transparentPaint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    int frameColor;
	int intPicTaken;
    int slapCount;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    BitmapDrawable ball;
    BitmapDrawable slaplogo;

    Bitmap theBall;
    Bitmap theSkewedBall;
    Bitmap theSlapLogo;
    Bitmap currSlapLogo;
    Bitmap theCurrBall;
 MediaPlayer mp;
        int ballx = -1;
 	Context tContext;
         int bally = -1;
        boolean isSlapped;
        boolean hasSlap;
	  boolean isTouched;
        private int xVelocity = 10;

        private int yVelocity = 5;
   private float previousX;
   private float previousY;

   private float ballSpeedX = 5;  // Ball's speed (x,y)
   private float ballSpeedY = 3;
    Camera.PreviewCallback prevCallBack;
   //  private List<ResultPoint> possibleResultPoints;
   //  private List<ResultPoint> lastPossibleResultPoints;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
 		tContext = context;
        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
	intPicTaken = 0;
      isTouched = false;
       isSlapped = false;
	hasSlap = false;
	    this.setFocusableInTouchMode(true);
	   slapCount = 0;
        // possibleResultPoints = new ArrayList<ResultPoint>(5);
        // lastPossibleResultPoints = null;
            mp = new MediaPlayer();
		mp = MediaPlayer.create(this.getContext(), R.raw.slap);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                    // mp=null;
                }

            });
	  ball = (BitmapDrawable) context.getResources().getDrawable(R.raw.mask_small);
	  slaplogo = (BitmapDrawable) context.getResources().getDrawable(R.drawable.slaplogo_small);
        theBall = boost(ball.getBitmap(), (float)7);
        theCurrBall = theBall;
        theSlapLogo = slaplogo.getBitmap();
        currSlapLogo = null;

         theSkewedBall = getScewedBitmap(theBall, 5, theBall.getWidth(), theBall.getHeight());
/*
    transparentPaint = new Paint();
    transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
    transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
*/

        // Initialize the result bitmap to be blank
        resultBitmap = null;
        // cameraManager = null; // Wait until onSizeChanged() to set cameraManager
         // Camera.Parameters parameters = camera.getParameters();
         prevCallBack = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

                System.out.println("PreviewCallback onPreviewFrame: " + intPicTaken);
                try {
                    System.out.println("PreviewCallback onPreviewFrame: " + intPicTaken);
/*

                 if(intPicTaken == 0) {
					drawSlapBmp(theSlapLogo);
			}
                if(intPicTaken == 2) {
undrawSlapBmp();
   Camera.Parameters parameters = cameraManager.getCamera().getParameters();
    int width = parameters.getPreviewSize().width;
    int height = parameters.getPreviewSize().height;


int previewFormat = 0;


Rect apreviewRect = cameraManager.getFramingRectInPreview();
int pwidth = apreviewRect.width();
int pheight = apreviewRect.height();


  Bitmap bitmap;

    int[] previewPixels = new int[width * height];
    decodeYUV420SP(previewPixels, data, width, height);
    bitmap = Bitmap.createBitmap(previewPixels, width, height, Bitmap.Config.RGB_565);


        Matrix rotrmatrix = new Matrix();
        rotrmatrix.postRotate(1);

        Bitmap bmprrotated = Bitmap.createBitmap(bitmap, apreviewRect.left, apreviewRect.top, pwidth, pheight);


     // drawResultBitmap(getScewedBitmap(getCircularBitmap(changeToLight(bmprrotated)),15,pwidth,pheight));
	// drawResultBitmap(bmprrotated);


              }
              if(intPicTaken == 6) {

              cameraManager.getCamera().setPreviewCallback(null);

		   intPicTaken = 0;
               isTouched = false;
		   drawViewfinder();

                }



            intPicTaken++;

*/

                    doSlapSkew(data);

                } catch (Exception e) {
                    System.out.println("onPreviewFrame: " + e.toString());
                    e.printStackTrace();
                    cameraManager.getCamera().setPreviewCallback(null);
                    intPicTaken = 0;
                }
            }
        };
    }


 


    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {

		try {

        Rect frame = cameraManager.getFramingRect();

        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

	  System.out.println("onDraw: ");




Random rand = new Random();
int r = rand.nextInt(255);
int g = rand.nextInt(255);
int b = rand.nextInt(255);
 frameColor = Color.rgb(r,g,b);





            // Draw a two pixel solid black border inside the framing rect


            paint.setColor(frameColor);

            canvas.drawRect(frame.left, frame.top, frame.left + 50, frame.top + 2, paint);
            canvas.drawRect(frame.right - 50, frame.top, frame.right +1, frame.top + 2, paint);

            canvas.drawRect(frame.right - 1, frame.top, frame.right +1, frame.top + 50, paint);

            canvas.drawRect(frame.right - 1, frame.bottom -50, frame.right +1, frame.bottom - 1, paint);
            canvas.drawRect(frame.right - 50, frame.bottom, frame.right +1, frame.bottom -1, paint);
            canvas.drawRect(frame.left, frame.bottom - 1, frame.left + 50, frame.bottom + 1, paint);
            canvas.drawRect(frame.left, frame.bottom - 50, frame.left + 1, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + 1, frame.top + 50, paint);

 

            // canvas.drawCircle((frame.width() / 2) + frame.left, (frame.height() / 2) + frame.top, (float)20, transparentPaint);





        if(isSlapped) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(180);
            canvas.drawBitmap(theSlapLogo, ballx, bally, paint);
	  	System.out.println("currSlapLogo redraw: ");
         
        }

         if (hasSlap) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(180);
		// canvas.drawBitmap(resultBitmap,  ballx, bally, paint);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
		canvas.drawBitmap(theSkewedBall, ballx, bally, null);
	  	System.out.println("resultBitmap redraw: ");
         
        }
	/*		
        if (currSlapLogo != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(180);
            canvas.drawBitmap(currSlapLogo, null, frame, paint);
	  	System.out.println("currSlapLogo redraw: ");
         
        }

 
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(180);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
	  	System.out.println("resultBitmap redraw: ");
            // drawViewfinder();
        } else {

	*/

		if(isTouched) {

            // Draw a red "laser scanner" line through the middle to show
            // decoding is active
            paint.setColor(laserColor);
            // paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            // scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;
            // canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            Rect previewFrame = cameraManager.getFramingRectInPreview();
            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            // List<ResultPoint> currentPossible = possibleResultPoints;
            // List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;




             if (ballx<0 && bally <0) {

                 ballx = width/2;

                 bally = height/2;

             } else {

                 ballx += xVelocity;

                 bally += yVelocity;

                 if ((ballx > width - ball.getBitmap().getWidth()) || (ballx < 0)) {

                         xVelocity = xVelocity*-1;

                 }

                 if ((bally > height - ball.getBitmap().getHeight()) || (bally < 0)) {

                         yVelocity = yVelocity*-1;

                 }

            }

            canvas.drawBitmap(theSkewedBall, ballx, bally, null);

		}  
            postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE, frame.top - POINT_SIZE, frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);



        // }

		} catch(Exception e) {
        System.out.println("onDraw.error: " + e);
		e.printStackTrace();
		}


    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        this.theCurrBall = null;
        if (theCurrBall != null) {
            theCurrBall.recycle();
        }
        theCurrBall = theBall;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     * 
     * @param barcode
     *            An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
	  hasSlap = true;	
	  refreshTimer();
        invalidate();

    }



    public void undrawSlapBmp() {
        Bitmap currSlapLogo = this.currSlapLogo;
        this.currSlapLogo = null;
        if (currSlapLogo != null) {
            currSlapLogo.recycle();
        }
        invalidate();
    }

    public void drawSlapBmp(Bitmap theBMP) {
        currSlapLogo = theBMP;
        invalidate();

    }




/*
    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (point) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }
*/

   @Override
   public boolean onTouchEvent(MotionEvent event) {

/*
      cameraManager.getCamera().setPreviewCallback(null);
	isTouched = true;
	drawViewfinder();
	undrawSlapBmp();

*/

      float initialX, initialY;

            initialX = event.getRawX();
            initialY = event.getRawY();
    switch (event.getAction()) {
 
        case MotionEvent.ACTION_DOWN:
	isTouched = true;
       ballx = Math.round(event.getRawX());
      bally = Math.round(event.getRawY());

            initialX = event.getRawX();
            initialY = event.getRawY();
             System.out.println("initialRawX: " + initialX + " :: initialRawY: " + initialY);

            Log.d(TAG, "Action was DOWN");
            break;
 
        case MotionEvent.ACTION_MOVE:
 
       ballx = Math.round(event.getRawX());
      bally = Math.round(event.getRawY());

            initialX = event.getRawX();
            initialY = event.getRawY();

             System.out.println("initialRawX: " + initialX + " :: initialRawY: " + initialY);

            Log.d(TAG, "Action was MOVE");
            break;
 
        case MotionEvent.ACTION_UP:
    
            float finalX = event.getRawX();
            float finalY = event.getRawY();
 
            Log.d(TAG, "Action was UP");
 
            if (initialX < finalX) {
                Log.d(TAG, "Left to Right swipe performed");
            }
 
            if (initialX > finalX) {
                Log.d(TAG, "Right to Left swipe performed");
            }
 
            if (initialY < finalY) {
                Log.d(TAG, "Up to Down swipe performed");
            }
 
            if (initialY > finalY) {
                Log.d(TAG, "Down to Up swipe performed");
            }
      	// isTouched = false;
	      // drawSlapLogo(theSlapLogo);
		doSlap(ballx, bally);
            break;
        case MotionEvent.ACTION_CANCEL:
            Log.d(TAG,"Action was CANCEL");
 		isTouched = false;
            break;
 
        case MotionEvent.ACTION_OUTSIDE:
		isTouched = false;
            Log.d(TAG, "Movement occurred outside bounds of current screen element");
            break;
    }
 
 
      return true;  // Event handled

   }



    public void playScream() {
 
            mp = new MediaPlayer();
		mp = MediaPlayer.create(this.getContext(), R.raw.brayf);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                    mp=null;
                }

            });

 

            mp.start();
 
   }


   public void playSound() {

 


 		slapCount++;

             if(slapCount == 3) {
            slapCount = 0;
            playScream();
		} else {
            mp = new MediaPlayer();
		mp = MediaPlayer.create(this.getContext(), R.raw.slap);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                    mp=null;


 

                }

            });

 

            mp.start();
		}
 
   }
 

public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

   final int frameSize = width * height;

   for (int j = 0, yp = 0; j < height; j++) {
     int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
     for (int i = 0; i < width; i++, yp++) {
       int y = (0xff & ((int) yuv420sp[yp])) - 16;
       if (y < 0)
         y = 0;
       if ((i & 1) == 0) {
         v = (0xff & yuv420sp[uvp++]) - 128;
         u = (0xff & yuv420sp[uvp++]) - 128;
       }

       int y1192 = 1192 * y;
       int r = (y1192 + 1634 * v);
       int g = (y1192 - 833 * v - 400 * u);
       int b = (y1192 + 2066 * u);

       if (r < 0)
         r = 0;
       else if (r > 262143)
         r = 262143;
       if (g < 0)
         g = 0;
       else if (g > 262143)
         g = 262143;
       if (b < 0)
         b = 0;
       else if (b > 262143)
         b = 262143;

       rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
     }
   }
 
 }    


public Bitmap getScewedBitmap(Bitmap output, int intScewAng, int oldw, int oldh) {
float d_up = oldh / intScewAng; 
float d_down = d_up + intScewAng;
Matrix matrix2 = new   Matrix();
float[] src2 = new float[] { 0, 0, oldw, 0, oldw, oldh, 0, oldh };
float[] dst2 = new float[] { 0, d_up, oldw, 0, oldw, oldh, 0, oldh - d_down };
matrix2.setPolyToPoly(src2, 0, dst2, 0, src2.length >> 1);
Bitmap bMatrix2 = Bitmap.createBitmap(output,0,0,oldw,oldh,matrix2, true);   
return bMatrix2;
}


    public static Bitmap roundCorner(Bitmap src, float round) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        //Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        // paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }






 	public static Bitmap changeToOil(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		int color = 0;
		int radio = 0;
		
		Random random = new Random();
		int iModel = 4;
		int i = width - iModel;
		
		while (i > 1) { 
			int j = height - iModel;
			while (j > 1) {
				int iPos = random.nextInt(100000000) % iModel;
				color = bitmap.getPixel(i + iPos, j + iPos);
				returnBitmap.setPixel(i, j, color);
				j--;
			}
			i--;
		}
		
		return returnBitmap;
	}

 

	public static Bitmap changeToLight(Bitmap bitmap) {
		final int width = bitmap.getWidth();  
        final int height = bitmap.getHeight();  
        Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
          
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
          
        int centerX = width / 3;  
        int centerY = height / 3;  
        int radius = Math.min(centerX, centerY);  
          
        final float strength = 150F; // 光照强度 100~150  
        int[] pixels = new int[width * height];  
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);  
        int pos = 0;  
        for (int i = 1, length = height - 1; i < length; i++)  // y
        {   
            for (int k = 1, len = width - 1; k < len; k++) // x 
            {  
                pos = i * width + k;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;  
                  
                // 计算当前点到光照中心的距离，平面座标系中求两点之间的距离  
                int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(centerX - k, 2));  
                if (distance < radius * radius)  
                {  
                    // 按照距离大小计算增加的光照值  
                    int result = (int) (strength * (1.0 - Math.sqrt(distance) / radius));  
                    newR = pixR + result;  
                    newG = pixG + result;  
                    newB = pixB + result;  
                }  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
            }  
        }  
          
        returnBitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return returnBitmap;  
	}


 

    public static Bitmap applyGaussianBlur(Bitmap src) {
        double[][] GaussianBlurConfig = new double[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(GaussianBlurConfig);
        convMatrix.Factor = 16;
        convMatrix.Offset = 0;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }




	public static Bitmap changeToGray(Bitmap bitmap) {
		int width, height;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
			
		Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(grayBitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true); // 设置抗锯齿
			
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(10);
			
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
			
		paint.setColorFilter(filter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
			
		return grayBitmap;
	}



    public static Bitmap boost(Bitmap src, float percent) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int pixel;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                    R = (int) (R * (1 + percent));
                    if (R > 255) R = 255;
    
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }


    public Bitmap doMask(Bitmap bmpToMask, int iw, int ih) {
    Bitmap bmpBG = Bitmap.createBitmap(iw, ih, Bitmap.Config.ARGB_8888);

    BitmapDrawable bmpDrwMask = (BitmapDrawable) tContext.getResources().getDrawable(R.raw.mask_small);
    Bitmap bmpMask = bmpDrwMask.getBitmap();
    Canvas cnvMask = new Canvas(bmpBG); 
    Paint maskPaint = new Paint();
    // maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    Paint imagePaint = new Paint();
    imagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

             if(slapCount == 2) {
    Matrix matrix = new Matrix();
    matrix.preScale(1.0f, -1.0f);
    Bitmap bmpTmpToMask = Bitmap.createBitmap(bmpBG, 0, 0, iw, ih, matrix, true);
    bmpMask = bmpTmpToMask;
	}


    cnvMask.drawBitmap(bmpToMask, 0, 0, imagePaint);
    cnvMask.drawBitmap(bmpMask, ballx, bally, maskPaint);

    return bmpBG;
    }


    public Bitmap doClipMask(Bitmap bmpToMask, Bitmap bmpTheMask, int iw, int ih) {
     Bitmap bmpBG = Bitmap.createBitmap(bmpToMask.getWidth(), bmpToMask.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas cnvMask = new Canvas(bmpBG); 
    Paint maskPaint = new Paint();
 
 
    Matrix matrix = new Matrix();
    matrix.preScale(1.0f, -1.0f);
    Bitmap bmpTmpToMask = Bitmap.createBitmap(bmpTheMask, 0, 0, bmpTheMask.getWidth(), bmpTheMask.getHeight(), matrix, true);
    bmpTheMask = bmpTmpToMask;
 


    cnvMask.drawBitmap(bmpToMask, 0, 0, maskPaint);
    cnvMask.drawBitmap(bmpTheMask, iw, ih, maskPaint);

    return bmpBG;
    }


private void refreshTimer() {
        Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
 
                        hasSlap = false;
            }
        }, 600L);
}




     public void doSlapSkew(byte[] data) {
   Camera.Parameters parameters = cameraManager.getCamera().getParameters();
    int width = parameters.getPreviewSize().width;
    int height = parameters.getPreviewSize().height;

		Rect frame = cameraManager.getFramingRect();
int previewFormat = 0;


Rect apreviewRect = cameraManager.getFramingRectInPreview();
int pwidth = apreviewRect.width();
int pheight = apreviewRect.height();


  Bitmap bitmap;
 
    int[] previewPixels = new int[width * height];
    decodeYUV420SP(previewPixels, data, width, height);
    bitmap = Bitmap.createBitmap(previewPixels, width, height, Bitmap.Config.RGB_565);



       // Bitmap bmpZSlapped = Bitmap.createBitmap(bitmap, bally, ballx, bally + 60, ballx + 60);

       //  Bitmap bmpFF = doMask(bmprrotated, pwidth,pheight);
      // Bitmap bmpFF = roundCorner(changeToGray(bmpZSlapped),(float)2);
      //  Bitmap bmpFFA = doClipMask(bitmap,bmpFF,bally,ballx);


        Bitmap bmprrotated = Bitmap.createBitmap(bitmap, apreviewRect.left, apreviewRect.top, pwidth, pheight);


        Matrix rotrmatrix = new Matrix();
        rotrmatrix.postRotate(1);

    rotrmatrix.preScale(1.0f, -1.0f);
    Bitmap bmpTmpToMask = Bitmap.createBitmap(bmprrotated, 0, 0, bmprrotated.getWidth(), bmprrotated.getHeight(), rotrmatrix, true);
 


       Bitmap bmpFF = getScewedBitmap(bmpTmpToMask,29,pwidth,pheight);
        // Bitmap bmpFF = applyFleaEffect(getScewedBitmap(bmprrotated,15,pwidth,pheight));
       Bitmap bmpAFF = roundCorner(changeToGray(bmpFF),(float)221);

        isSlapped = false;
	  drawResultBitmap(bmpAFF); 
	}

     public void doSlap(int thex, int they) {
            isSlapped = true;
		try {
		playSound();
		} catch(Exception e) {
            System.out.println("doSlap:Error playing sound: " + e);
        }

		isTouched = false;
         try {
		cameraManager.getCamera().setOneShotPreviewCallback(prevCallBack);
    } catch(Exception e) {
        System.out.println("doSlap:Error cameraManager: " + e);
    }
	}

}
