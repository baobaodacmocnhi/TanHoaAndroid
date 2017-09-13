package vn.com.abc.tanhoaandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChupHinhDocSoActivity extends Fragment {

    private View _rootView;

    private String _imageFileName="";
    private Bitmap _image=null;
    private WSAsyncTask _task;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.activity_chup_hinh_doc_so, container, false);

        Button btnChupHinh = (Button) _rootView.findViewById(R.id.btnChupHinh);
        Button btnLuu = (Button) _rootView.findViewById(R.id.btnLuu);

        btnChupHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri()); // put uri file khi mà mình muốn lưu ảnh sau khi chụp như thế nào  ?
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, CContanstVariable.REQUEST_IMAGE_CAPTURE_PERMISSION);
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (CNguoiDung.DanhBo != "" && CNguoiDung.MaND != ""&&_image!=null) {
                        Bitmap reizeImage = Bitmap.createScaledBitmap(_image, 1024, 1024, false);
                        String imgString = Base64.encodeToString(getBytesFromBitmap(reizeImage), Base64.NO_WRAP);

                            _task = new WSAsyncTask(getActivity());
                            String result = (String) _task.execute(new String[]{"ThemHinhDHN", CNguoiDung.DanhBo, CNguoiDung.MaND, imgString, String.valueOf(CContanstVariable.Latitude), String.valueOf(CContanstVariable.Longitude)}).get();
                            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();

                            Clear();
                            ImageView imageView = (ImageView) _rootView.findViewById(R.id.imageView);
                            imageView.setImageDrawable(null);
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return _rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CContanstVariable.REQUEST_IMAGE_CAPTURE_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                ImageView imageView = (ImageView) _rootView.findViewById(R.id.imageView);
                _image = BitmapFactory.decodeFile(_imageFileName);
                _image = imageOreintationValidator(_image, _imageFileName);
                imageView.setImageBitmap(_image);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Action canceled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Action Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Clear()
    {
        _imageFileName="";
        _image=null;
    }

    public Uri setImageUri() {
        Uri uri;
        File photoFile = createFile();
        if (Build.VERSION.SDK_INT < 21) {
            // Từ android 5.0 trở xuống. khi ta sử dụng FileProvider.getUriForFile() sẽ trả về ngoại lệ FileUriExposedException
            // Vì vậy mình sử dụng Uri.fromFile đề lấy ra uri cho file ảnh
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            photoFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "" + timeStamp + ".jpg");
            uri = Uri.fromFile(photoFile);
        } else {
            // từ android 5.0 trở lên ta có thể sử dụng Uri.fromFile() và FileProvider.getUriForFile() để trả về uri file sau khi chụp.
            // Nhưng bắt buộc từ Android 7.0 trở lên ta phải sử dụng FileProvider.getUriForFile() để trả về uri cho file đó.
            Uri photoURI = FileProvider.getUriForFile(getActivity(), "camera_fullsize", photoFile);
            uri = photoURI;
        }
        _imageFileName = photoFile.getAbsolutePath();
        return uri;
    }

    public File createFile() {
        File filesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            file = File.createTempFile(timeStamp, ".jpg", filesDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

}
