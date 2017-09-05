package vn.com.abc.docsoandroid;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChupHinhDocSoActivity extends Fragment {

    private View _rootView;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private String _imageFileName;
    private Uri _imgUri;
    private Bitmap _image;
    private CWebService ws = new CWebService();
    private LocationManager locationManager;
private LocationListener locationListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.activity_chup_hinh_doc_so, container, false);

        Button btnChupHinh = (Button) _rootView.findViewById(R.id.btnChupHinh);
        Button btnLuu = (Button) _rootView.findViewById(R.id.btnLuu);
        final ImageView imageView = (ImageView) _rootView.findViewById(R.id.imageView);

        btnChupHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri()); // put uri file khi mà mình muốn lưu ảnh sau khi chụp như thế nào  ?
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, REQUEST_ID_IMAGE_CAPTURE);
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
                    if (CNguoiDung.DanhBo != "" && CNguoiDung.MaND != "") {
                        String imgString = Base64.encodeToString(getBytesFromBitmap(_image), Base64.NO_WRAP);
                        String result = ws.ThemHinhDHN(CNguoiDung.DanhBo, CNguoiDung.MaND, imgString);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        return _rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                ImageView imageView = (ImageView) _rootView.findViewById(R.id.imageView);
                imageView.setImageURI(getImgUri());
                _image = BitmapFactory.decodeFile(this._imageFileName);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Action canceled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Action Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri setImageUri() {
        File photoFile = createFile();
        if (Build.VERSION.SDK_INT < 21) {
            // Từ android 5.0 trở xuống. khi ta sử dụng FileProvider.getUriForFile() sẽ trả về ngoại lệ FileUriExposedException
            // Vì vậy mình sử dụng Uri.fromFile đề lấy ra uri cho file ảnh
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            photoFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "" + timeStamp + ".jpg");
            this._imgUri = Uri.fromFile(photoFile);
        } else {
            // từ android 5.0 trở lên ta có thể sử dụng Uri.fromFile() và FileProvider.getUriForFile() để trả về uri file sau khi chụp.
            // Nhưng bắt buộc từ Android 7.0 trở lên ta phải sử dụng FileProvider.getUriForFile() để trả về uri cho file đó.
            Uri photoURI = FileProvider.getUriForFile(getActivity(), "camera_fullsize", photoFile);
            this._imgUri = photoURI;
        }
        this._imageFileName = photoFile.getAbsolutePath();
        return this._imgUri;
    }

    private File createFile() {
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

    public Uri getImgUri() {
        return this._imgUri;
    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }




}
