package edu.umd.cmsc434.ia07;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chiralcode.colorpicker.ColorPickerDialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class Doodle extends AppCompatActivity implements View.OnClickListener{

    private float smallBrush,mediumBrush,largeBrush;
    private Button drawBtn,clearBtn;
    private ImageButton colorBtn;
    private DrawingView drawingView;
    private Dialog brushDialog;
    private ColorPickerDialog colorPickerDialog;
    int curr_color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);
        curr_color = ContextCompat.getColor(this, R.color.colorPrimary);
        drawingView = (DrawingView)findViewById(R.id.canvas_view);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawBtn = (Button)findViewById(R.id.brush_btn);
        drawBtn.setOnClickListener(this);
        clearBtn = (Button)findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(this);
        colorBtn = (ImageButton)findViewById(R.id.color_btn);
        colorBtn.setOnClickListener(this);

        drawingView.setColor(curr_color);
        colorBtn.setBackgroundColor(curr_color);
        drawingView.setBrushSize(mediumBrush);

        brushDialog = setupBrushDialog();

        colorPickerDialog = new ColorPickerDialog(this, curr_color, new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                drawingView.setColor(color);
                colorBtn.setBackgroundColor(color);
                colorPickerDialog.dismiss();
            }
        });

        ToggleButton toggle = (ToggleButton) findViewById(R.id.erase_btn);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    drawingView.enableErase();
                } else {
                    // The toggle is disabled
                    drawingView.disableErase();
                }
            }
        });
        ToggleButton trace = (ToggleButton) findViewById(R.id.trace_btn);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                 InputStream image = null;
                    if (!displayTraceImage(image)){
                        makeText(getApplicationContext(), "Couldn't Load to Trace", Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);
                    }
                }else{
                    hideTraceImage();
                }
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
               brushDialog.show();
               break;
           case R.id.clear_btn:
               drawingView.clear();
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

    public boolean displayTraceImage(InputStream stream){
        if (stream == null) return false;
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Bitmap image = BitmapFactory.decodeStream(stream);
        imageView.setImageBitmap(image);
        drawingView.setBackgroundColor(Color.TRANSPARENT);
        return true;
    }

    public void hideTraceImage(){
        drawingView.setBackgroundColor(Color.WHITE);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        //Not sure how setImageBitmap works with null, uri claims to clear in that case
        imageView.setImageURI(null);
    }
}
