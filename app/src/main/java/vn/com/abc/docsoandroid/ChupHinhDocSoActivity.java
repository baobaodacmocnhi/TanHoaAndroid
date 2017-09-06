package vn.com.abc.docsoandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 200;
    private String _imageFileName;
    private Uri _imgUri;
    private CWebService _ws = new CWebService();

    private static double Latitude, Longitude;

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
                        Bitmap image = BitmapFactory.decodeFile(_imageFileName);
                        String imgString = Base64.encodeToString(getBytesFromBitmap(image), Base64.NO_WRAP);

                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                        GPSTracker gpsTracker = new GPSTracker(getContext());
                        Location location = gpsTracker.getLocation();
                        if( location != null){
                            Latitude = location.getLatitude();
                            Longitude = location.getLongitude();
                        }else {
                            Toast.makeText(getContext(),"GPS unable to get Value",Toast.LENGTH_SHORT).show();
                        }

                        String result = _ws.ThemHinhDHN(CNguoiDung.DanhBo, CNguoiDung.MaND, imgString, String.valueOf(Latitude), String.valueOf(Longitude));
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
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
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                ImageView imageView = (ImageView) _rootView.findViewById(R.id.imageView);
                imageView.setImageURI(getImgUri());
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
            _imgUri = Uri.fromFile(photoFile);
        } else {
            // từ android 5.0 trở lên ta có thể sử dụng Uri.fromFile() và FileProvider.getUriForFile() để trả về uri file sau khi chụp.
            // Nhưng bắt buộc từ Android 7.0 trở lên ta phải sử dụng FileProvider.getUriForFile() để trả về uri cho file đó.
            Uri photoURI = FileProvider.getUriForFile(getActivity(), "camera_fullsize", photoFile);
            _imgUri = photoURI;
        }
        _imageFileName = photoFile.getAbsolutePath();
        return _imgUri;
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
