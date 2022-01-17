package ca.uwaterloo.cs349;

import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.View.OnClickListener;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdditionFragment extends Fragment {

    private SharedViewModel mViewModel;
    private DrawOneStroke drawOneStroke;
    private Button okButton, clearButton, addButton, cancelButton;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_addition, container, false);
        drawOneStroke = root.findViewById(R.id.drawOneStroke);


        final Dialog addDialog = new Dialog(getLayoutInflater().getContext());
        okButton = root.findViewById(R.id.button3);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawOneStroke.getDraw()) {
                    drawOneStroke.calResample();
                    drawOneStroke.rotate();
                    final Path path = drawOneStroke.getPath();

                    addDialog.setContentView(R.layout.add_dialog);
                    addDialog.setTitle("Add a gesture");

                    addDialog.show();

                    final EditText name = addDialog.findViewById(R.id.gesture_name);
                    addButton = addDialog.findViewById(R.id.addButton);
                    addButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (name.getText().toString().trim().length() > 0) {
                                String gestureName = name.getText().toString();

                                drawOneStroke.setIsFirst(false);
                                drawOneStroke.setIsDraw(true);
                                final Bitmap bitmap = Bitmap.createBitmap(drawOneStroke.getWidth(), drawOneStroke.getHeight(), Bitmap.Config.ARGB_8888);
                                final Canvas canvas = new Canvas(bitmap);

                                if (drawOneStroke.getDraw() == true) mViewModel.addPath(gestureName, drawOneStroke);

                                Canvas canvas2 = drawOneStroke.getCanvas();
                                canvas2.drawColor(Color.WHITE);
                                drawOneStroke.setIsDraw(false);

                                addDialog.dismiss();


                            }
                        }
                    });

                    cancelButton = addDialog.findViewById(R.id.cancel_button);
                    cancelButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addDialog.dismiss();
                        }
                    });


                }
            }
        });

        clearButton = root.findViewById(R.id.button4);
        clearButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawOneStroke.canvasClear();
                    Canvas canvas = drawOneStroke.getCanvas();
                    canvas.drawColor(Color.WHITE);
                    drawOneStroke.setIsDraw(false);
                }
        });


        return root;
    }

}