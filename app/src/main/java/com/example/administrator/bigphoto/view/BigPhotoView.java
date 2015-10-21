package com.example.administrator.bigphoto.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 15-10-21.
 */
public class BigPhotoView extends View implements View.OnTouchListener{
    private Rect rect;
    private int imageCenterX,imageCenterY;
    private int defaultWidth = 300;
    private BitmapRegionDecoder bitmapRegionDecoder;
    private static final  BitmapFactory.Options options = new BitmapFactory.Options();
    private GestureDetector gestureDetector;
    private static  final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;


    public BigPhotoView(Context context) {
        super(context);
        init();
    }

    public BigPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init(){
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        gestureDetector = new GestureDetector(getContext(),new GestureListener());
        setOnTouchListener(this);
        setEnabled(true);
        setLongClickable(true);
    }
    public void setImageInputStream(InputStream inputStream){
        try {
            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream,false);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            imageCenterX = options.outWidth/2;
            imageCenterY = options.outHeight/2;
            rect = new Rect();
            requestLayout();
            invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = bitmapRegionDecoder.decodeRegion(rect,options);
        canvas.drawBitmap(bitmap,0,0,null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth() == 0?defaultWidth:getMeasuredWidth();
        int height = getMeasuredHeight() == 0?defaultWidth:getMeasuredHeight();
        rect.left = imageCenterX - width /2;
        rect.right = imageCenterX+ width/2;
        rect.top = imageCenterY - height/2;
        rect.bottom = imageCenterY + height/2;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class  GestureListener extends GestureDetector.SimpleOnGestureListener{
        private GestureListener() {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                if(rect.right< imageCenterX*2) {
                    rect.left = (int) (rect.left + (e1.getX() - e2.getX()));
                    rect.right = (int) (rect.right + (e1.getX() - e2.getX()));
                    invalidate();
                }
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {

                if(rect.left>0){
                    rect.left = (int) (rect.left -(e2.getX() - e1.getX()));
                    rect.right = (int) (rect.right -(e2.getX() - e1.getX()));
                    invalidate();
                }
            } else if(e2.getY()-e1.getY()>FLING_MIN_DISTANCE && Math.abs(velocityY)>FLING_MIN_VELOCITY) {
                if(rect.top>0) {
                    rect.top = (int) (rect.top - (e2.getY() - e1.getY()));
                    rect.bottom = (int) (rect.bottom - (e2.getY() - e1.getY()));
                    invalidate();
                }
            } else if(e1.getY()-e2.getY()>FLING_MIN_DISTANCE && Math.abs(velocityY)>FLING_MIN_VELOCITY) {
                if(rect.bottom<imageCenterY*2) {
                    rect.top = (int) (rect.top + (e1.getY() - e2.getY()));
                    rect.bottom = (int) (rect.bottom + (e1.getY() - e2.getY()));
                    invalidate();
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
