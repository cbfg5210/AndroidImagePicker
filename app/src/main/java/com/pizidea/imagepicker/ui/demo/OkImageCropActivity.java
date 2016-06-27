package com.pizidea.imagepicker.ui.demo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.pizidea.imagepicker.util.AndroidImagePicker;
import com.pizidea.imagepicker.widget.ClipImageLayout;
import com.pizidea.imagepickerDemo.R;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 *
 * @author zhy
 */
public class OkImageCropActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG="OkImageCropActivity";
    private ClipImageLayout mClipImageLayout;
    private TextView btnReChoose;
    private TextView btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_cropimage);

        btnOk = (TextView) findViewById(R.id.btn_pic_ok);
        btnReChoose = (TextView) findViewById(R.id.btn_pic_rechoose);
        btnOk.setOnClickListener(this);
        btnReChoose.setOnClickListener(this);

        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);

        String imagePath = getIntent().getStringExtra(AndroidImagePicker.KEY_PIC_PATH);
        Drawable imageDrawable=Drawable.createFromPath(imagePath);
        mClipImageLayout.setClipDrawable(imageDrawable);
    }

    @Override
    public void onClick(View view) {
        int viewId=view.getId();
        if(viewId==R.id.btn_pic_rechoose){
            finish();
        }else if(viewId==R.id.btn_pic_ok){
            Bitmap bitmap = mClipImageLayout.clip();
            finish();
            AndroidImagePicker.getInstance().notifyImageCropComplete(bitmap,0);
        }
    }
}

