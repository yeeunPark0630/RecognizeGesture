package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

public class GestureData {
    private String gestureName;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private DrawOneStroke drawOneStroke;

    public GestureData(String name, DrawOneStroke drawOneStroke){
        this.gestureName = name;
        this.drawOneStroke = drawOneStroke;
    }

    public String getGestureName() {
        return gestureName;
    }

    public DrawOneStroke getDrawOneStroke() {
        return drawOneStroke;
    }


}
