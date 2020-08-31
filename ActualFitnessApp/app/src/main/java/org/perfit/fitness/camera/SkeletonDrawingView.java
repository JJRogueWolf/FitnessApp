package org.perfit.fitness.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.perfit.fitness.tflite.SkeletonPoint;

public class SkeletonDrawingView extends View {
    // defines paint and canvas
    private Paint drawPaint;
    // stores next circle
    private final Path path = new Path();

    public SkeletonDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawPaint = new Paint();
        // setup initial color
        int paintColor = Color.RED;
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }
        // Force a view to draw again
        postInvalidate();
        return true;
    }

    public void drawSkeleton(Canvas canvas, SkeletonPoint[] skelPoints, Models model, int exerciseId) {

        postInvalidate();
        if(skelPoints == null) {
            return;
        }

        float xScaling, yScaling;
        if(model == Models.Google) {
            xScaling = 4f;
            yScaling = 4f;
        }
        else {
            xScaling = getWidth();
            yScaling = getHeight() ;
        }


        for(SkeletonPoint keypoint : skelPoints) {
            if(keypoint != null)
                if (exerciseId == 0 || exerciseId == 3) {
                    canvas.drawCircle(getWidth() - keypoint.getPosition().x * xScaling, keypoint.getPosition().y * yScaling, 10, drawPaint);
                } else {
                    canvas.drawCircle(getWidth() - keypoint.getPosition().y * xScaling, getHeight() - keypoint.getPosition().x * yScaling, 10, drawPaint);
                }
        }
    }


}
