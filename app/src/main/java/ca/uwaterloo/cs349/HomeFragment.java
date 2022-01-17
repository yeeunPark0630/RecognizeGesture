package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Array;
import java.util.*;


public class HomeFragment extends Fragment {

    private SharedViewModel mViewModel;
    private DrawOneStroke drawOneStroke;
    ArrayList<GestureData> gesture;
    ArrayList<PointF> recPoint;
    Map<Float ,GestureData> score;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        drawOneStroke = root.findViewById(R.id.homedraw);

        Button recognize = root.findViewById(R.id.recognize);
        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawOneStroke.calResample();
                drawOneStroke.rotate();
                gesture = mViewModel.getGestureData();
                recPoint = drawOneStroke.getResample();
                score = new TreeMap<Float, GestureData>();
                calScore();
                showGesture(root);
            }
        });


        return root;
    }

    private void calScore(){

        for(int i = 0; i < gesture.size(); ++i){
            GestureData library = gesture.get(i);
            ArrayList<PointF> stored = library.getDrawOneStroke().getResample();
            float sum = 0;
            for(int j = 0; j < stored.size(); ++j){
                if(j == recPoint.size()) break;
                float dataX = (float)Math.pow(recPoint.get(j).x - stored.get(j).x, 2);
                float dataY = (float)Math.pow(recPoint.get(j).y - stored.get(j).y,2);
                sum += (float)Math.sqrt(dataX + dataY);
            }
            float d = sum / stored.size();
            score.put(d, library);
        }
    }

    private void showGesture(View rowView){
        int count = 1;

        for(Map.Entry<Float, GestureData> entry : score.entrySet()){
            if(count > 3) break;

            GestureData g = entry.getValue();
            ImageView imageView;
            PathShape ps;
            ShapeDrawable shapeDrawable;
            TextView textView;

            switch (count){
                case 1 :
                    imageView = rowView.findViewById(R.id.homeImage);
                    imageView.setVisibility(rowView.VISIBLE);

                    ps = new PathShape(g.getDrawOneStroke().getPath(), 1141, 1290);
                    shapeDrawable = new ShapeDrawable(ps);
                    shapeDrawable.getPaint().setColor(Color.BLACK);
                    shapeDrawable.getPaint().setStrokeWidth(10);
                    shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                    imageView.setBackgroundDrawable(shapeDrawable);
                    imageView.setImageDrawable(shapeDrawable);

                    textView = rowView.findViewById(R.id.textView);
                    textView.setText(g.getGestureName());
                    break;
                case 2:
                    imageView = rowView.findViewById(R.id.homeImage2);

                    ps = new PathShape(g.getDrawOneStroke().getPath(), 1141, 1290);
                    shapeDrawable = new ShapeDrawable(ps);
                    shapeDrawable.getPaint().setColor(Color.BLACK);
                    shapeDrawable.getPaint().setStrokeWidth(10);
                    shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                    imageView.setBackgroundDrawable(shapeDrawable);
                    imageView.setImageDrawable(shapeDrawable);

                    textView = rowView.findViewById(R.id.textView2);
                    textView.setText(g.getGestureName());
                    break;
                case 3:
                    imageView = rowView.findViewById(R.id.homeImage3);

                    ps = new PathShape(g.getDrawOneStroke().getPath(), 1141, 1290);
                    shapeDrawable = new ShapeDrawable(ps);
                    shapeDrawable.getPaint().setColor(Color.BLACK);
                    shapeDrawable.getPaint().setStrokeWidth(10);
                    shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                    imageView.setBackgroundDrawable(shapeDrawable);
                    imageView.setImageDrawable(shapeDrawable);

                    textView = rowView.findViewById(R.id.textView3);
                    textView.setText(g.getGestureName());
                    break;
                default:
                    break;
            }
            ++count;
        }

    }
}