package vn.com.abc.docsoandroid;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GhiChiSoActivity extends Fragment {

    private View _rootView;
    private Integer _index = 0;
    private String _ID = "";
    private String _Nam = "";
    private String _Ky = "";
    private String _ChiTiet="";
    private  String _TienNuoc="";
    private Spinner cmbCode;
    private CWebService ws = new CWebService();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.activity_ghi_chi_so, container, false);

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                _ID = bundle.getString("ID");
                GetDanhBo(_ID);
            }
        } catch (Exception ex) {
        }

        Button btnTruoc = (Button) _rootView.findViewById(R.id.btnTruoc);
        btnTruoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_index == 0) {
                    Toast.makeText(getActivity(), "Đầu Danh Sách", Toast.LENGTH_SHORT).show();
                    return;
                }
                _index--;
                GetDanhBo(_index);
            }
        });

        Button btnSau = (Button) _rootView.findViewById(R.id.btnSau);
        btnSau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_index == CNguoiDung.tbDocSoFilter.getPropertyCount()) {
                    Toast.makeText(getActivity(), "Cuối Danh Sách", Toast.LENGTH_SHORT).show();
                    return;
                }
                _index++;
                GetDanhBo(_index);
            }
        });

        Button btnTinhTien = (Button) _rootView.findViewById(R.id.btnTinhTien);
        btnTinhTien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText txtDanhBo = (EditText) _rootView.findViewById(R.id.txtDanhBo);
                    cmbCode = (Spinner) _rootView.findViewById(R.id.cmbCode);
                    EditText txtChiSo = (EditText) _rootView.findViewById(R.id.txtChiSo);
                    EditText txtTieuThu = (EditText) _rootView.findViewById(R.id.txtTieuThu);
                    EditText txtGB = (EditText) _rootView.findViewById(R.id.txtGB);
                    EditText txtDM = (EditText) _rootView.findViewById(R.id.txtDM);
                    EditText txtTongTien = (EditText) _rootView.findViewById(R.id.txtTongTien);

                    String CodeMoi = "";
                    if (cmbCode.getSelectedItem().toString().matches("Chưa Ghi") == false && cmbCode.getSelectedItem().toString().matches("Đã Ghi") == false)
                        CodeMoi = CNguoiDung.cmbCodeValue.get(cmbCode.getSelectedItemPosition());
                    if (CodeMoi != "") {
                        String TieuThu = ws.TinhTieuThu(txtDanhBo.getText().toString().replace(" ", ""), _Nam, _Ky, CodeMoi, txtChiSo.getText().toString());
                        String resultTienNuoc = ws.TinhTienNuoc(txtDanhBo.getText().toString().replace(" ", ""), txtGB.getText().toString(), txtDM.getText().toString(), TieuThu, _ChiTiet);

                        String[]temp=resultTienNuoc.replace("[", "").replace("]", "").split(",");
                        _TienNuoc=temp[0];
                        Double TongTien=Double.parseDouble(temp[0])*1.15;
                        Long TongTienRound=Math.round(TongTien);
                        _ChiTiet=temp[1];

                        txtTieuThu.setText(TieuThu);
                        txtTongTien.setText((TongTienRound).toString());
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCapNhat = (Button) _rootView.findViewById(R.id.btnCapNhat);
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText txtDanhBo = (EditText) _rootView.findViewById(R.id.txtDanhBo);
                    cmbCode = (Spinner) _rootView.findViewById(R.id.cmbCode);
                    EditText txtChiSo = (EditText) _rootView.findViewById(R.id.txtChiSo);
                    EditText txtTieuThu = (EditText) _rootView.findViewById(R.id.txtTieuThu);

                    String CodeMoi = "";
                    if (cmbCode.getSelectedItem().toString().matches("Chưa Ghi") == false && cmbCode.getSelectedItem().toString().matches("Đã Ghi") == false)
                        CodeMoi = CNguoiDung.cmbCodeValue.get(cmbCode.getSelectedItemPosition());
                    if (CodeMoi != "") {
                        String result = ws.CapNhat(_ID,CodeMoi,cmbCode.getSelectedItem().toString(),txtChiSo.getText().toString(),txtTieuThu.getText().toString(),_TienNuoc,_ChiTiet);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnIn = (Button) _rootView.findViewById(R.id.btnIn);
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return _rootView;
    }

    private void GetDanhBo(String ID) {
        try {
            for (int i = 0; i < CNguoiDung.tbDocSoFilter.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) CNguoiDung.tbDocSoFilter.getProperty(i);
                if (obj.getProperty("DocSoID").toString().matches(ID) == true) {
                    _index = i;
                    FillData(obj);
                    break;
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void GetDanhBo(Integer position) {
        try {
            SoapObject obj = (SoapObject) CNguoiDung.tbDocSoFilter.getProperty(_index);
            _index = position;
            FillData(obj);

        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void FillData(SoapObject obj) {
        try {
            try {
                cmbCode = (Spinner) _rootView.findViewById(R.id.cmbCode);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, CNguoiDung.cmbCodeDisplay);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cmbCode.setAdapter(adapter);
            } catch (Exception ex) {
                Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
            }

            EditText txtDanhBo = (EditText) _rootView.findViewById(R.id.txtDanhBo);
            EditText txtMLT = (EditText) _rootView.findViewById(R.id.txtMLT);
            EditText txtHoTen = (EditText) _rootView.findViewById(R.id.txtHoTen);
            EditText txtDiaChi = (EditText) _rootView.findViewById(R.id.txtDiaChi);
            EditText txtGB = (EditText) _rootView.findViewById(R.id.txtGB);
            EditText txtDienThoai = (EditText) _rootView.findViewById(R.id.txtDienThoai);
            EditText txtSoThan = (EditText) _rootView.findViewById(R.id.txtSoThan);
            EditText txtDM = (EditText) _rootView.findViewById(R.id.txtDM);
            EditText txtVT = (EditText) _rootView.findViewById(R.id.txtVT);
            EditText txtCo = (EditText) _rootView.findViewById(R.id.txtCo);
            EditText txtHieu = (EditText) _rootView.findViewById(R.id.txtHieu);
            EditText txtCT = (EditText) _rootView.findViewById(R.id.txtCT);
            EditText txtTongTien = (EditText) _rootView.findViewById(R.id.txtTongTien);
            EditText txtTT = (EditText) _rootView.findViewById(R.id.txtTT);
            EditText txt3T = (EditText) _rootView.findViewById(R.id.txt3T);
            EditText txtCC = (EditText) _rootView.findViewById(R.id.txtCC);
            EditText txtCode3 = (EditText) _rootView.findViewById(R.id.txtCode3);
            EditText txtCode2 = (EditText) _rootView.findViewById(R.id.txtCode2);
            EditText txtCode1 = (EditText) _rootView.findViewById(R.id.txtCode1);
            EditText txtChiSo3 = (EditText) _rootView.findViewById(R.id.txtChiSo3);
            EditText txtChiSo2 = (EditText) _rootView.findViewById(R.id.txtChiSo2);
            EditText txtChiSo1 = (EditText) _rootView.findViewById(R.id.txtChiSo1);
            EditText txtTieuThu3 = (EditText) _rootView.findViewById(R.id.txtTieuThu3);
            EditText txtTieuThu2 = (EditText) _rootView.findViewById(R.id.txtTieuThu2);
            EditText txtTieuThu1 = (EditText) _rootView.findViewById(R.id.txtTieuThu1);
            EditText txtChiSo = (EditText) _rootView.findViewById(R.id.txtChiSo);
            EditText txtTieuThu = (EditText) _rootView.findViewById(R.id.txtTieuThu);

            _ID = obj.getProperty("DocSoID").toString();
            _Nam = obj.getProperty("Nam").toString();
            _Ky = obj.getProperty("Ky").toString();
CNguoiDung.DanhBo=obj.getProperty("DanhBa").toString();

            String DanhBo = new StringBuilder(obj.getProperty("DanhBa").toString()).insert(obj.getProperty("DanhBa").toString().length() - 7, " ").toString();
            DanhBo = new StringBuilder(DanhBo).insert(DanhBo.length() - 4, " ").toString();
            txtDanhBo.setText(DanhBo);

            String MLT = new StringBuilder(obj.getProperty("MLT1").toString()).insert(obj.getProperty("MLT1").toString().length() - 7, " ").toString();
            MLT = new StringBuilder(MLT).insert(MLT.length() - 5, " ").toString();
            txtMLT.setText(MLT);

//                    txtHoTen.setText(obj.getProperty("HoTen").toString());
            txtDiaChi.setText(obj.getProperty("SoNhaCu").toString() + " " + obj.getProperty("Duong").toString());
            txtGB.setText(obj.getProperty("GB").toString());
            txtDienThoai.setText(obj.getProperty("SDT").toString());
            txtSoThan.setText(obj.getProperty("SoThanCu").toString());
            txtDM.setText(obj.getProperty("DM").toString());
            txtVT.setText(obj.getProperty("ViTriCu").toString());
            txtHieu.setText(obj.getProperty("HieuCu").toString());
            txtCT.setText(obj.getProperty("ChiThanCu").toString());
            txtTongTien.setText(obj.getProperty("TongTien").toString());
//                    txtTT.setText(obj.getProperty("").toString());
            txtCC.setText(obj.getProperty("ChiCoCu").toString());
//            Integer TT = 0;//tiêu thụ
//            Integer TB = 0;//trung bình cộng
            try {
                txtCode3.setText(obj.getProperty("CodeCu3").toString());
                txtChiSo3.setText(obj.getProperty("CSCu3").toString());
                txtTieuThu3.setText(obj.getProperty("TieuThuCu3").toString());
//                TT += Integer.parseInt(obj.getProperty("TieuThuCu3").toString());
//                TB++;
            } catch (Exception ex) {
            }

            try {
                txtCode2.setText(obj.getProperty("CodeCu2").toString());
                txtChiSo2.setText(obj.getProperty("CSCu2").toString());
                txtTieuThu2.setText(obj.getProperty("TieuThuCu2").toString());
//                TT += Integer.parseInt(obj.getProperty("TieuThuCu2").toString());
//                TB++;
            } catch (Exception ex) {
            }

            try {
                txtCode1.setText(obj.getProperty("CodeCu").toString());
                txtChiSo1.setText(obj.getProperty("CSCu").toString());
                txtTieuThu1.setText(obj.getProperty("TieuThuCu").toString());
//                TT += Integer.parseInt(obj.getProperty("TieuThuCu").toString());
//                TB++;
            } catch (Exception ex) {
            }

//            TT = TT / TB;
            txt3T.setText(obj.getProperty("TBTT").toString());

            try {
                Integer index = 0;
                for (Map.Entry<Integer, String> entry : CNguoiDung.cmbCodeValue.entrySet()) {
                    if (entry.getValue().equals(obj.getProperty("CodeMoi").toString())) {
                        index = entry.getKey();
                        break; //breaking because its one to one map
                    }
                }
                cmbCode.setSelection(index);
            } catch (Exception ex) {
                cmbCode.setSelection(0);
            }
            txtChiSo.setText(obj.getProperty("CSMoi").toString());
            txtTieuThu.setText(obj.getProperty("TieuThuMoi").toString());
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
