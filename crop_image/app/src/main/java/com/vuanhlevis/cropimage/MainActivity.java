package com.vuanhlevis.cropimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vuanhlevis.cropimage.base.ActivityBase;
import com.vuanhlevis.cropimage.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends ActivityBase {

    private Context mContext;

    public static String base64_data = "";


    private static final int CROP_CODE = 10;
    private static final int PERMISSION_REQUEST_CAMERA = 100;
    private final static int TAKE_CAMERA = 1;
    private final static int CHOOSE_GALLERY = 2;

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.iv_add_crop)
    ImageView iv_add;

    @BindView(R.id.rl_save)
    RelativeLayout rl_save;

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.tv_path_save)
    TextView tv_path_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;


//        test();

    }

    private void test() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference("Email");
//
//        String key = myRef.push().getKey();
//
//        myRef.child(key).setValue("Hello, World!").addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "onFailure: "  + e.getMessage());
//            }
//        });

    }

    private void configTxtPathsave() {

        tv_path_save.setVisibility(mCurrentPhotoPath.trim().isEmpty() ? View.GONE : View.VISIBLE);

        if (!mCurrentPhotoPath.trim().isEmpty()) {
            String temp = "Your image has been save in: ";
            tv_path_save.setText(temp + mCurrentPhotoPath);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.iv_add_crop, R.id.rl_save})
    public void clickView(View v) {
        hideKeyboard(MainActivity.this);
        switch (v.getId()) {
            case R.id.iv_add_crop:
                showPopUp();

                break;
            case R.id.rl_save:

                if (!base64_data.trim().isEmpty()) {
                    // save file
                    showPopUpEmail();
                }

                Log.e(TAG, "clickView: " + "save file");


                break;

            default:

                break;
        }
    }

    private void showPopUpEmail() {
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Enter your email below to contact with us!");
        View view = inflater.inflate(R.layout.layout_dialog_email, null);
        EditText et_email = view.findViewById(R.id.etEmail);
        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int which) {

                        hideKeyboard(MainActivity.this);

                        if (et_email.getText().toString().trim().isEmpty()) {

//                            Toast.makeText(mContext, "Bạn cần nhập đúng thông tin mail!", Toast.LENGTH_SHORT).show();
                            Snackbar.make(coordinator, "Bạn cần nhập đúng thông tin mail ", Snackbar.LENGTH_SHORT).show();
//
                        } else {
//                            showProgress(true);
                            // send data to firebase

                            // save image

                            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Email");
                            String key = myRef.push().getKey();

                            myRef.child(key).setValue(et_email.getText().toString()).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: " + e.getMessage());
                                }
                            });

                            activeSaveButton(false);
                            showProgress(true);
                            saveFileImage(base64_data);

                            dialog.dismiss();
                        }
                    }
                });
        alertDialog.show();
    }

    private void showProgress(boolean isShow) {
        if (isShow) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            progressBar.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


    private void showPopUp() {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.layout_popup_choice_image);

//        dialog.setContentView(R.layout.layout_popup_change_ava);
        TextView tv_title = dialog.findViewById(R.id.tv_title_change_ava);
        LinearLayout lnl_choice = dialog.findViewById(R.id.lnl_choice);
        LinearLayout lnl_chup_anh = dialog.findViewById(R.id.lnl_chup_anh);
        tv_title.setText("Phương thức chọn ảnh");

        LinearLayout bt_huy = dialog.findViewById(R.id.lnl_huy);

        bt_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        lnl_choice.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: " + "choose image");
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);

                dialog.dismiss();

                // need change log --> getUserOrAdmin
            }
        });

        lnl_chup_anh.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: " + "take photo");
                if (!checkPermission()) {
                    requestPermission(PERMISSION_REQUEST_CAMERA);
                } else
                    take_Photo();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_GALLERY:
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = mContext.getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();

                    Intent intent = new Intent(mContext, CropImageActivity.class);
                    intent.putExtra("uri", picturePath);
                    startActivityForResult(intent, CROP_CODE);
//                    mContext.startActivity(intent);
                    break;

                case TAKE_CAMERA:
                    File f;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri imageUri = Uri.parse(mCurrentPhotoPath);
                        f = new File(imageUri.getPath());
                    } else {
                        f = new File(Environment.getExternalStorageDirectory().toString());
                        for (File temp : f.listFiles()) {
                            if (temp.getName().equals("photo.jpg")) {
                                f = temp;
                                break;
                            }
                        }
                    }

                    String uri = f.getAbsolutePath();
                    Log.d(TAG, "onActivityResult: " + uri);
                    Intent intent2 = new Intent(mContext, CropImageActivity.class);
                    intent2.putExtra("uri", uri);

                    startActivityForResult(intent2, CROP_CODE);

                    break;

                case CROP_CODE:

//                    byte[] byteData = data.getByteArrayExtra("bitmap");

//                    String base64_data = data.getStringExtra("bitmap");
//                    bitmap = BitmapFactory.decodeByteArray(byteData,0,byteData.length);

                    if (!base64_data.trim().isEmpty()) {
                        Log.e(TAG, "onActivityResult: " + base64_data);

                    } else
                        Log.e(TAG, "onActivityResult: " + "null");


                    // save file

                    activeSaveButton(true);

                    //

                    break;
                default:
                    break;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void activeSaveButton(boolean active) {
        if (active) {
            rl_save.setClickable(true);
            rl_save.setBackground(mContext.getDrawable(R.drawable.bg_button_save_can_click));
        } else {
            rl_save.setClickable(false);
            rl_save.setBackground(mContext.getDrawable(R.drawable.bg_bt_save_not_click));
        }
    }

    public String photoFileName = "photo.jpg";
    File photoFile;
    String mCurrentPhotoPath;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermission(int PERMISSION_REQUEST_CODE) {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void take_Photo() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = getPhotoFileUri(photoFileName);
                Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.vuanhlevis.cropimage", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);
            }
        } catch (Exception ex) {
            String et = ex.toString();
        }
    }

    public File getPhotoFileUri(String fileName) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = new File(
                    this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
            }
            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
            mCurrentPhotoPath = file.getAbsolutePath();
            return file;
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeAccepted && cameraAccepted)
                        take_Photo();
                    else {
//                        Toast.makeText(mContext, "Bạn sẽ không dùng đươc chức năng này nếu không cho phép", Toast.LENGTH_SHORT).show();
                        Snackbar.make(coordinator, "Bạn sẽ không dùng đươc chức năng này nếu không cho phép", Snackbar.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }


    private void saveFileImage(String base64) {
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void run() {

                new AsyncTask() {


                    @Override
                    protected Object doInBackground(Object[] objects) {
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        File pictureFile = getOutputMediaFile();
                        if (pictureFile == null) {
                            Log.d(TAG,
                                    "Error creating media file, check storage permissions: ");// e.getMessage());
                            return null;
                        }

                        mCurrentPhotoPath = pictureFile.getAbsolutePath();
                        Log.e(TAG, "saveFileImage: " + pictureFile.getAbsolutePath());


                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            decodedByte.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();

                            base64_data = "";

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showProgress(false);

                                    configTxtPathsave();

                                    Toast.makeText(mContext, "Đã lưu file", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d(TAG, "Error accessing file: " + e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {

                        showProgress(false);
                        super.onPostExecute(o);
                    }
                }.execute();

            }
        }, 1000);
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}
