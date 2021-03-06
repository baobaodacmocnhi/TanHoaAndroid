package vn.com.abc.tanhoaandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;

public class DangNhapActivity extends AppCompatActivity {

    //    CWebService ws = new CWebService();
    private AsyncTaskWS task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // permits to make a HttpURLConnection
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        ActivityCompat.requestPermissions(DangNhapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CContanstVariable.REQUEST_LOCATION_PERMISSION);
        startService(new Intent(DangNhapActivity.this, GPSService.class));

        Button btnDangNhap = (Button) findViewById(R.id.btnDangNhap);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText txtTaiKhoan = (EditText) findViewById(R.id.txtTaiKhoan);
                    EditText txtMatKhau = (EditText) findViewById(R.id.txtMatKhau);
                    Spinner cmbNam = (Spinner) findViewById(R.id.cmbNam);
                    Spinner cmbKy = (Spinner) findViewById(R.id.cmbKy);
                    Spinner cmbDot = (Spinner) findViewById(R.id.cmbDot);

                    if (txtTaiKhoan.getText().toString().matches("") || txtMatKhau.getText().toString().matches("")) {
                        Toast.makeText(DangNhapActivity.this, "Chưa nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    task = new AsyncTaskWS(DangNhapActivity.this);
                    SoapObject tbNguoiDung = (SoapObject) task.execute(new String[]{"DangNhap", txtTaiKhoan.getText().toString(), txtMatKhau.getText().toString()}).get();
                    if (tbNguoiDung != null) {
                        SoapObject nguoidung = (SoapObject) tbNguoiDung.getProperty(0);
                        CNguoiDung.MaND = nguoidung.getProperty("MaND").toString();
                        CNguoiDung.HoTen = nguoidung.getProperty("HoTen").toString();
                        CNguoiDung.May = nguoidung.getProperty("May").toString();
                        task = new AsyncTaskWS(DangNhapActivity.this);
                        CNguoiDung.tbDocSo = (SoapObject) task.execute(new String[]{"GetDSDocSo", cmbNam.getSelectedItem().toString(), cmbKy.getSelectedItem().toString(), cmbDot.getSelectedItem().toString(), CNguoiDung.May}).get();
                        task = new AsyncTaskWS(DangNhapActivity.this);
                        SoapObject tbCode = (SoapObject) task.execute(new String[]{"GetDSCode"}).get();
                        if (tbCode != null) {
                            CNguoiDung.cmbCodeValue = new HashMap<Integer, String>();
                            CNguoiDung.cmbCodeDisplay = new String[tbCode.getPropertyCount() + 2];

                            CNguoiDung.cmbCodeValue.put(0, "0");
                            CNguoiDung.cmbCodeDisplay[0] = "Tất Cả";

                            CNguoiDung.cmbCodeValue.put(1, "1");
                            CNguoiDung.cmbCodeDisplay[1] = "Chưa Ghi";

                            for (int i = 0; i < tbCode.getPropertyCount(); i++) {
                                SoapObject obj = (SoapObject) tbCode.getProperty(i);
                                CNguoiDung.cmbCodeValue.put(i + 2, obj.getProperty("CODE").toString());
                                CNguoiDung.cmbCodeDisplay[i + 2] = obj.getProperty("TTDHN").toString();
                            }
                        }
//                        Toast.makeText(DangNhapActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(DangNhapActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception ex) {
                    Toast.makeText(DangNhapActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
