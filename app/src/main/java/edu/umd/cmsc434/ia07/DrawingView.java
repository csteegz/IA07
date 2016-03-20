package edu.umd.cmsc434.ia07;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class DrawingView extends View {

    private Paint _paintDoodle = new Paint();
    private Path _path = new Path();
    private int _color = R.color.colorPrimary;
    private float _brushSize , _storedSize;

    //Stuff for dithering
    private Paint _canvasPaint = new Paint();
    private Canvas _canvas;
    private Bitmap _canvasBitmap;

    public DrawingView(Context context) {
        super(context);
        init(null,0);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }


    private void init(AttributeSet attrs, int defStyle){
        _paintDoodle.setColor(_color);
        _paintDoodle.setAntiAlias(true);
        _paintDoodle.setStyle(Paint.Style.STROKE);
        _paintDoodle.setStrokeWidth(20);
        _paintDoodle.setStrokeJoin(Paint.Join.ROUND);
        _paintDoodle.setStrokeCap(Paint.Cap.ROUND);
        _canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w >0 && h > 0) {
            _canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            _canvas = new Canvas(_canvasBitmap);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(_canvasBitmap, 0, 0, _canvasPaint);
        canvas.drawPath(_path,_paintDoodle);
    }



    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                _path.moveTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                _path.lineTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_UP:
                _canvas.drawPath(_path,_paintDoodle);
                _path.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor){
        invalidate();
        _color = Color.parseColor(newColor);
        _paintDoodle.setColor(_color);
    }

    public void setBrushSize(float newSize){
        _brushSize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        _paintDoodle.setStrokeWidth(_brushSize);
        storeBrushSize(_brushSize);
    }

    public void storeBrushSize(float storeSize){
        _storedSize = storeSize;
    }

    public float retrieveSize(){
        return _storedSize;
    }

    public void clear(){
        _canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setColor(int newColor) {
        invalidate();
        _color = newColor;
        _paintDoodle.setColor(_color);
    }
}
