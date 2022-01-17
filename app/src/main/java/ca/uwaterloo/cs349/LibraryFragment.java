package ca.uwaterloo.cs349;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private SharedViewModel mViewModel;
    private DrawOneStroke drawOneStroke;
    ImageView imageView;
    TextView nameView;
    ImageButton replaceButton ,deleteButton;

    //final ArrayList<GestureData> items ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        final ArrayList<GestureData> items = mViewModel.getGestureData();

        ListView listView = root.findViewById(R.id.list);
        final MyAdapter adapter = new MyAdapter(getLayoutInflater().getContext(), items) ;
        listView.setAdapter(adapter);


        return root;
    }

    public class MyAdapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<GestureData> gesture;
        DrawOneStroke drawOneStroke;

        public MyAdapter(Context c, ArrayList<GestureData> gesture){
            this.mContext = c;
            this.gesture = gesture;
            this.mLayoutInflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return gesture.size();
        }

        @Override
        public Object getItem(int position) {
            return gesture.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View rowView = mLayoutInflater.inflate(R.layout.list, null);

            imageView = rowView.findViewById(R.id.imageView);
            float width = gesture.get(position).getDrawOneStroke().getCanvas().getWidth();
            float height = gesture.get(position).getDrawOneStroke().getCanvas().getHeight();
            PathShape ps = new PathShape(gesture.get(position).getDrawOneStroke().getPath(), width, height);
            ShapeDrawable shapeDrawable = new ShapeDrawable(ps);
            shapeDrawable.getPaint().setColor(Color.BLACK);
            shapeDrawable.getPaint().setStrokeWidth(10);
            shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
            imageView.setBackgroundDrawable(shapeDrawable);

            nameView = rowView.findViewById(R.id.nameGesture);
            nameView.setText(gesture.get(position).getGestureName());
            replaceButton = rowView.findViewById(R.id.replace);
            replaceButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog replaceDialog = new Dialog(getLayoutInflater().getContext());
                    replaceDialog.setContentView(R.layout.fragment_addition);
                    replaceDialog.setTitle("Replace a gesture");
                    drawOneStroke = replaceDialog.findViewById(R.id.drawOneStroke);

                    Button okButton = replaceDialog.findViewById(R.id.button3);
                    okButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Path path = drawOneStroke.getPath();
                            String gestureName = gesture.get(position).getGestureName();
                            drawOneStroke.setIsReplace();

                            final Bitmap bitmap =  Bitmap.createBitmap(drawOneStroke.getWidth(),drawOneStroke.getHeight(), Bitmap.Config.ARGB_8888);
                            final Canvas canvas = new Canvas(bitmap);
                           // imageView = rowView.findViewById(R.id.imageView);

                            if (drawOneStroke.getReplace() == true) {
                                gesture.get(position).getDrawOneStroke().setPath(path);
                                Canvas canvas2 = drawOneStroke.getCanvas();
                                canvas2.drawColor(Color.WHITE);
                                drawOneStroke.setIsDraw(false);
                            }

                            imageView = rowView.findViewById(R.id.imageView);
                            float width = gesture.get(position).getDrawOneStroke().getCanvas().getWidth();
                            float height = gesture.get(position).getDrawOneStroke().getCanvas().getHeight();
                            PathShape ps = new PathShape(gesture.get(position).getDrawOneStroke().getPath(), width, height);
                            ShapeDrawable shapeDrawable = new ShapeDrawable(ps);
                            shapeDrawable.getPaint().setColor(Color.BLACK);
                            shapeDrawable.getPaint().setStrokeWidth(10);
                            shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                            imageView.setBackgroundDrawable(shapeDrawable);
                            replaceDialog.dismiss();
                        }

                    });

                    Button clearButton = replaceDialog.findViewById(R.id.button4);
                    clearButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawOneStroke.canvasClear();
                            Canvas canvas = drawOneStroke.getCanvas();
                            canvas.drawColor(Color.WHITE);
                            drawOneStroke.setIsDraw(false);
                        }
                    });
                    replaceDialog.show();
                }
            });


            deleteButton = rowView.findViewById(R.id.delete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gesture.remove(position);
                    notifyDataSetChanged();
                }
            });

            return rowView;
        }
    }

}