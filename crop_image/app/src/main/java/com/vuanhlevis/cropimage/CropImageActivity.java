package com.vuanhlevis.cropimage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vuanhlevis.cropimage.adapter.GridAdapter;
import com.vuanhlevis.cropimage.base.ActivityBase;
import com.vuanhlevis.cropimage.util.MyUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vuanhlevis.cropimage.MainActivity.base64_data;

public class CropImageActivity extends ActivityBase {

    private static final String TAG = CropImageActivity.class.getSimpleName();
    private GridAdapter adapter;
    private ArrayList<String> arrFilePath;
    private String currentUri = "";


    @BindView(R.id.iv_crop)
    CropImageView iv_crop;

    @BindView(R.id.tv_hide_gallery)
    TextView tv_hide_gallery;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.tv_done)
    TextView tv_done;

    @BindView(R.id.gv_list_gallery)
    GridView gridView;

    @BindView(R.id.lnl_hide)
    LinearLayout lnl_hide;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);

        String uri = getIntent().getStringExtra("uri");
        Log.d(TAG, "onActivityResult uri: " + uri + "--");
        currentUri = uri;


        arrFilePath = new ArrayList<>();
        arrFilePath = getFilePaths();

        Log.e(TAG, "onCreate: " + arrFilePath.size());

        if (arrFilePath.size() > 0) {
            Log.e(TAG, "onCreate: " + arrFilePath.get(0));
        }

        adapter = new GridAdapter(this, arrFilePath);
        gridView.setAdapter(adapter);

        iv_crop.setImageUriAsync(Uri.parse("file://" + currentUri));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentUri = arrFilePath.get(i);
//                MyUtils.rotateImage(currentUri, currentUri, false);
                iv_crop.setImageUriAsync(Uri.parse("file://" + currentUri));

            }
        });

        tv_hide_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideGallery();
            }
        });

        lnl_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGallery();
            }
        });
//
//
//        tv_done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                // need to save bitmap
//
//
////                Log.d(TAG, "onClick: crop getCropRect  left - " + iv_crop.getCropRect().left + "- end - " + iv_crop.getCropRect().right);
////                Log.d(TAG, "onClick: crop getCropRect  top - " + iv_crop.getCropRect().top + "- bottom - " + iv_crop.getCropRect().bottom);
////                Log.d(TAG, "onClick: crop getCropRect " + iv_crop.getCropRect());
//
////                showProgressDialog();
//            }
//        });

        iv_crop.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                // done -> send bit map \
                Bitmap cropped = result.getBitmap();

                base64_data = convertImageToByte(cropped);
//                result.getUri();
                Log.e(TAG, "onCropImageComplete: " + base64_data);

                Intent intent = new Intent();
//                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//                cropped.compress(Bitmap.CompressFormat.PNG, 100, bStream);
//                byte[] byteArray = bStream.toByteArray();
//                intent.putExtra("bitmap", base64_data);
                setResult(RESULT_OK, intent);

                finish();
            }
        });


    }

    public String convertImageToByte(Bitmap bitmap) {
        String imgString = "";
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imgString = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
            // dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgString;
    }

    @OnClick({R.id.tv_done, R.id.iv_crop, R.id.iv_back})
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_done:
                // getbit map image crop
                iv_crop.getCroppedImageAsync();

//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
////                        iv_crop.getCroppedImage();
//                        iv_crop.getCroppedImageAsync();
////                        Bitmap cropped = iv_crop.getCroppedImage();
////                        Log.d(TAG, "onClick: " + cropped.toString());
////                        Intent intent = new Intent();
////                        intent.putExtra("bitmap", cropped);
////                        setResult(RESULT_OK, intent);
//                    }
//                });
//                thread.run();

                break;
            case R.id.iv_crop:
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }


    int height = 600;
    int height_after = 900;
    boolean isStart = true;

    public void hideGallery() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                iv_crop.getLayoutParams();
        if (params.height != -1 && isStart) {
            height = params.height;
            isStart = false;
        }

//        Log.d(TAG, "hideGallery: " + params.weight);
        Log.d(TAG, "hideGallery: " + params.height + "---" + height_after);
        Log.d(TAG, "hideGallery: " + params.width);
        if (params.height > height_after) {
            height_after = params.height;
        }

        if (params.height != height_after) {
            params.height = LinearLayout.LayoutParams.MATCH_PARENT - 1;
            tv_hide_gallery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_black_24dp, 0);
        } else {
            tv_hide_gallery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
            params.height = height - 1;
        }

        Log.e(TAG, "hideGallery: height change ->" + params.height);

        iv_crop.setLayoutParams(params);
    }

    public ArrayList<String> getFilePaths() {

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();

        String[] directories = null;
        if (uri != null) {
            c = this.getContentResolver().query(uri, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();

                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                    ) {


                        String path = imagePath.getAbsolutePath();
                        resultIAV.add(path);

                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resultIAV;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d(TAG, "onActivityResult: " + resultUri);
//                MyUtils.rotateImage(currentUri, currentUri, false);
                iv_crop.setImageUriAsync(Uri.parse("file://" + currentUri));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
