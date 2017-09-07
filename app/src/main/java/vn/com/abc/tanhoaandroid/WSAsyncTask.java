package vn.com.abc.tanhoaandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by user on 07/09/2017.
 */

public class WSAsyncTask extends AsyncTask<String,Void,Object> {

    public interface AsyncResponse {
        void processFinish(SoapObject output);
    }

    public AsyncResponse _delegate = null;

    private Context _context;
    private ProgressDialog _dialog;
private CWebService _ws=new CWebService();

    public WSAsyncTask(Context context) {
        _context=context;
    }

    @Override
    protected void onPreExecute() {
        _dialog = new ProgressDialog(_context);
        _dialog.setTitle("Thông Báo");
        _dialog.setMessage("Đang Xử Lý...");
//        _dialog.setIndeterminate(true);
//        _dialog.setCancelable(false);
        _dialog.show();
        super.onPreExecute();

    }

    @Override
    protected Object doInBackground(String... params) {

            switch (params[0]) {
                case "DangNhap":
                    return _ws.DangNhap(params[1], params[2]);
                case "GetDSDocSo":
                    return _ws.GetDSDocSo(params[1], params[2], params[3], params[4]);
                case "GetDSCode":
                    return _ws.GetDSCode();
                case "TinhTieuThu":
                    return _ws.TinhTieuThu(params[1], params[2], params[3], params[4],params[5]);
                case "TinhTienNuoc":
                    return _ws.TinhTienNuoc(params[1], params[2], params[3], params[4],params[5]);
                case "CapNhat":
                    return _ws.CapNhat(params[1], params[2], params[3], params[4],params[5], params[6],params[7]);
                case "ThemHinhDHN":
                    return _ws.ThemHinhDHN(params[1], params[2], params[3], params[4],params[5]);

            };


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object soapObject) {
        if (_dialog.isShowing()) {
            _dialog.dismiss();
        }
//        _delegate.processFinish(soapObject);
        super.onPostExecute(soapObject);

    }

}
