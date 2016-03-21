package edu.umd.cmsc434.ia07;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chiralcode.colorpicker.ColorPickerDialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.widget.Toast.makeText;

public class Doodle extends AppCompatActivity implements View.OnClickListener{

    private float smallBrush,mediumBrush,largeBrush;
    private Button drawBtn,clearBtn,saveBtn,loadBtn;
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

        //set this up as listener for buttons
        drawBtn = (Button)findViewById(R.id.brush_btn);
        drawBtn.setOnClickListener(this);
        clearBtn = (Button)findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(this);
        colorBtn = (ImageButton)findViewById(R.id.color_btn);
        colorBtn.setOnClickListener(this);
        saveBtn = (Button)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        loadBtn = (Button)findViewById(R.id.load_btn);
        loadBtn.setOnClickListener(this);

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
        trace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                 InputStream image = getInputStreamFromGallery();
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

    private InputStream getInputStreamFromGallery() {
        return null;
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
           case R.id.load_btn:
               InputStream img = getInputStreamFromGallery();
               if(!loadCanvas(img)){
                   makeText(getApplicationContext(), "Couldn't Load", Toast.LENGTH_LONG).show();
               }
               break;
           case R.id.save_btn:
               saveCanvas();
               break;
           default:
              //should be unreachable
               break;
       }
    }

    private Dialog setupBrushDialog() {
        final Dialog brushDialog = new Dialog(this);
        SeekBar.OnSeekBarChangeListener brushListener = new SeekBar.OnSeekBarChangeListener() {
            int opacity,brushSize;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()){
                    case (R.id.brush_seek_bar):
                        brushSize = progress;
                        break;
                    case (R.id.opacity_seek_bar):
                        opacity = progress;
                        break;
                }
                drawNewBrush();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Don't Worry about this.
                return;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (seekBar.getId()){
                    case (R.id.brush_seek_bar):
                        drawingView.setBrushSize(largeBrush/100 * brushSize);
                        break;
                    case (R.id.opacity_seek_bar):
                        drawingView.setOpacity(opacity);
                        break;
                }
            }
        };

        brushDialog.setTitle("Brush Details");
        brushDialog.setCancelable(true);
        brushDialog.setContentView(R.layout.brush_dialog);

        SeekBar bar = (SeekBar) brushDialog.findViewById(R.id.brush_seek_bar);
        bar.setOnSeekBarChangeListener(brushListener);
        bar = (SeekBar) brushDialog.findViewById(R.id.opacity_seek_bar);
        bar.setOnSeekBarChangeListener(brushListener);
        return brushDialog;
    }

    private void drawNewBrush() {
        // redraws the brush in the dialog at the new size and opacity.
        //TODO: Maybe implement?
        return;
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

    public void saveCanvas(){
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                drawingView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawingView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "drawing");
                if(imgSaved!=null){
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                drawingView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    public boolean loadCanvas(InputStream img){
        //TODO: Make work.
        return img != null;
    }
}
