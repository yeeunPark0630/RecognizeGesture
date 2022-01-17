package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DrawOneStroke extends View {
    private Paint paint = new Paint();
    private Path path = new Path();
    private Canvas canvas;
    private Bitmap bitmap;
    private Boolean isDraw = false;
    private Boolean isReplace = false;
    float length = 0;
    private Path savePath;
    float startX = 0 , startY = 0;
    ArrayList<PointF> orgPoint;
    ArrayList<PointF> resample;
    float[] rotate = new float[256];
    Boolean isFirst = true;

    public DrawOneStroke(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(20);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        orgPoint =  new ArrayList<>(128);
        resample = new ArrayList<>(128);
        savePath = new Path();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h , Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawPath(path, paint);
        invalidate();
    }

    protected void drawForSave(Canvas canvas){
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawPath(savePath, paint);
    }

    public boolean onTouchEvent(MotionEvent event){
        isDraw = true;
       // if(!isFirst) return false;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                canvasClear();
                path.moveTo(x, y);
                savePath.moveTo(x, y);
                canvas.drawColor(Color.WHITE);
                orgPoint.add(new PointF(x, y));
                // for calculate path length
                length = 0;
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                savePath.lineTo(x, y);
                orgPoint.add(new PointF(x, y));
                // add length
                float dx = x - startX;
                float dy = y - startY;
                float distance = (float)Math.sqrt((dx * dx) + (dy*dy));
                length += distance;
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                break;
            default:
                return false;
        }
        //invalidate();
        return true;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Path getPath() {
        return savePath;
    }

    public Boolean getDraw() {
        return isDraw;
    }

    public Bitmap getBitmap()
    {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);


        return bmp;
    }

    public void setIsFirst(Boolean b){
        isFirst = b;
    }

    public void setIsReplace(){
        isReplace = true;
    }

    public Paint getPaint() {
        return paint;
    }

    public Boolean getReplace() {
        return isReplace;
    }

    public void setIsDraw(Boolean b){
        isDraw = b;
    }

    public void calResample() {
        resample.add(orgPoint.get(0));
        int point = 1;
        for(int i = 0; i < 126; ++i){
            if(point == orgPoint.size()) break;
            float interval = length / 128;
            PointF startP = resample.get(i);
            PointF endP = orgPoint.get(point);
            float distance = distance(startP, endP);
            while(distance < interval){
                ++point;
                if(point == orgPoint.size()) break;
                endP = orgPoint.get(point);
                distance = distance(startP, endP);
            }
            float ratio = interval/distance;

            float x = startP.x + (endP.x - startP.x)*(ratio);
            float y = startP.y + (endP.y - startP.y)*ratio;

            resample.add(new PointF(x, y));

            //paint.setColor(Color.BLACK);
            //canvas.drawCircle(startP.x + interval, y, 5, paint);

        }
        resample.add(orgPoint.get(orgPoint.size()-1));
        //System.out.println(resample.size());
    }

    public PointF getCenterPoint(ArrayList<PointF> point){
        float sumX = 0, sumY = 0;
        for(int i = 0; i < point.size(); ++i){
            sumX += point.get(i).x;
            sumY += point.get(i).y;
        }
        PointF centerP = new PointF();
        centerP.x = sumX / 128;
        centerP.y = sumY / 128;
        return centerP;
    }
    public void rotate(){
        PointF centerP = getCenterPoint(resample); // let A
        PointF zeroP = new PointF(centerP.x + 1, centerP.y); // let B
        PointF startP = resample.get(0); // let C
        float AB = (float)Math.sqrt(Math.pow(centerP.x-zeroP.x, 2)+ Math.pow(centerP.y - zeroP.y, 2));
        float BC = (float)Math.sqrt(Math.pow(zeroP.x-startP.x, 2)+ Math.pow(zeroP.y - startP.y, 2));
        float AC = (float)Math.sqrt(Math.pow(startP.x-centerP.x, 2)+ Math.pow(startP.y - centerP.y, 2));
        float ratio = (AB * AB + AC * AC - BC * BC) /( 2 * AC * AB);
        double degree =Math.acos(ratio)*(180/Math.PI);

        float[] resampleArray = new float[256];
        int pos = 0;
        float minX = canvas.getWidth(), maxX = 0, minY = canvas.getHeight(), maxY = 0;
        for(int i = 0; i < resample.size(); ++i){
            if(pos >= 256) break;
           resampleArray[pos] = resample.get(i).x;
           resampleArray[pos+1] = resample.get(i).y;
           pos += 2;
           if(resample.get(i).x < minX) minX = resample.get(i).x;
           if(resample.get(i).x > maxX) maxX = resample.get(i).x;
           if(resample.get(i).y < minY) minY = resample.get(i).y;
           if(resample.get(i).y > maxY) maxY = resample.get(i).y;
        }

        float width = maxX - minX;
        float height = maxY - minY;
        float scaleX = (float)500/width;
        float scaleY = (float)500/height;

        Matrix matrix = new Matrix();

        matrix.postRotate((float)degree, centerP.x, centerP.y);
        matrix.postTranslate(-centerP.x, -centerP.y);
        matrix.postScale(scaleX, scaleY);

        matrix.mapPoints(resampleArray);

        pos = 0;
        for(int i = 0; i < resampleArray.length; i+=2){
            if(pos >= resample.size()) break;
            resample.get(pos).x = resampleArray[i];
            resample.get(pos).y = resampleArray[i+1];
            pos++;
        }

    }



    public void canvasClear(){
        savePath = new Path();
        startX = 0;
        startY = 0;
        orgPoint = new ArrayList<>();
        resample = new ArrayList<>();
        length = 0;
    }

    public float distance(PointF start, PointF end){
        float sx = start.x;
        float sy = start.y;
        float eX = end.x;
        float eY = end.y;
        return (float)Math.sqrt(Math.pow(sx-eX, 2) + Math.pow(sy-eY, 2));
    }

    public ArrayList<PointF> getResample() {
        return resample;
    }

    public void setPath(Path path){
        this.savePath = path;
    }

}
