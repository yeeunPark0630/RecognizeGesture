package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;
   // private MutableLiveData<ArrayList<Path>> mPath;
    //private ArrayList<Path> paths;
    //private HashMap<String, Path> paths;
    private ArrayList<GestureData> gestureData;

    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shared model");
        gestureData = new ArrayList<GestureData>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public ArrayList<GestureData> getGestureData(){ return gestureData;}

    public void addPath(String name, DrawOneStroke drawOneStroke){
      //  System.out.println(gestureData.size());
        gestureData.add(new GestureData(name, drawOneStroke));
    }

    public void replacePath(GestureData gesture, int position){ gestureData.set(position, gesture);}

    public int length(){
        return gestureData.size();
    }
}