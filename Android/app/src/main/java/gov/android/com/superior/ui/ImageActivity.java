package gov.android.com.superior.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.photoview.PhotoView;

import java.io.File;
import gov.android.com.superior.R;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);

        String url = getIntent().getStringExtra("url");
        String path = getIntent().getStringExtra("path");

        if (path != null && path.length() > 0) Glide.with(getBaseContext()).load(new File(path)).placeholder(R.mipmap.ic_default_attachment).into(photoView);
        else if (url != null && url.length() > 0) Glide.with(getBaseContext()).load(url).placeholder(R.mipmap.ic_default_attachment).into(photoView);
        else Glide.with(getBaseContext()).load(url).placeholder(R.mipmap.ic_default_attachment).into(photoView);

    }
}
