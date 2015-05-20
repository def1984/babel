package com.example.ai.babel.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ai.babel.R;
import com.soundcloud.android.crop.Crop;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends BaseActivity {
    private CircleImageView resultView;

    private String filename; //图片名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        resultView = (CircleImageView) findViewById(R.id.profile_image);
        Toolbar mToolbar = getActionBarToolbar();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("个人设置");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        resultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.setImageDrawable(null);
                Crop.pickImage(ProfileActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        filename = format.format(date);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputImage = new File(path,filename+".jpg");
        try {
            if(outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch(IOException e) {
            e.printStackTrace();
        }
        //将File对象转换为Uri并启动照相程序
        Uri destination = Uri.fromFile(outputImage);
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            resultView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
