package edu.umd.cmsc434.ia07;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.chiralcode.colorpicker.ColorPickerDialog;

import java.util.ArrayList;
import java.util.List;

public class Doodle extends AppCompatActivity implements View.OnClickListener{

    private float smallBrush,mediumBrush,largeBrush;
    private Button drawBtn,clearBtn;
    private ImageButton colorBtn;
    private DrawingView drawingView;
    private ColorPickerDialog colorPickerDialog;
    int curr_color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);
        curr_color = R.color.colorPrimary;
        drawingView = (DrawingView)findViewById(R.id.canvas_view);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawBtn = (Button)findViewById(R.id.brush_btn);
        drawBtn.setOnClickListener(this);
        clearBtn = (Button)findViewById(R.id.new_btn);
        clearBtn.setOnClickListener(this);
        colorBtn = (ImageButton)findViewById(R.id.color_btn);
        colorBtn.setOnClickListener(this);

        drawingView.setColor(curr_color);
        drawingView.setBrushSize(mediumBrush);

        colorPickerDialog = new ColorPickerDialog(this, curr_color, new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                drawingView.setColor(color);
                colorBtn.setBackgroundColor(color);
                colorPickerDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.color_btn:
               colorPickerDialog.show();
               break;
           case R.id.brush_btn:
               Dialog brushDialog = setupBrushDialog();
               brushDialog.show();
               break;
           case R.id.new_btn:
               //Clear Button Clicked;
               break;
           default:
              //should be unreachable
               break;
       }
    }

    private Dialog setupBrushDialog() {
        final Dialog brushDialog = new Dialog(this);
        int[] brushSizes = {R.id.large_brush,R.id.medium_brush,R.id.small_brush};
        View.OnClickListener brushListener = new View.OnClickListener() {
            //This would be so much cleaner in python
            @Override
            public void onClick(View v) {
                float brushSize = mediumBrush;
                switch (v.getId()){
                    case R.id.small_brush:
                        brushSize = smallBrush;
                        break;
                    case R.id.medium_brush:
                        brushSize = mediumBrush;
                        break;
                    case R.id.large_brush:
                        brushSize = largeBrush;
                        break;
                    default:
                        break;
                }
                drawingView.setBrushSize(brushSize);
                brushDialog.dismiss();
            }
        };
        brushDialog.setTitle("Brush Size:");
        brushDialog.setCancelable(true);
        brushDialog.setContentView(R.layout.brush_dialog);
        for (int id : brushSizes) {
            brushDialog.findViewById(id).setOnClickListener(brushListener);
        }
        return brushDialog;
    }
}
