package gov.android.com.superior;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

import uk.co.senab.photoview.PhotoView;

public class TransitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);

        String url = getIntent().getStringExtra("url");
        String path = getIntent().getStringExtra("path");

        if (path != null && path.length() > 0) Glide.with(getBaseContext()).load(new File(path)).placeholder(R.mipmap.background_attachment).into(photoView);
        else if (url != null && url.length() > 0) Glide.with(getBaseContext()).load(url).placeholder(R.mipmap.background_attachment).into(photoView);
        else Glide.with(getBaseContext()).load(url).placeholder(R.mipmap.background_attachment).into(photoView);

    }
}
