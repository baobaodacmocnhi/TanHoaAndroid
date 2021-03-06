package vn.com.abc.tanhoaandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by user on 07/09/2017.
 */

public class AsyncTaskWS extends AsyncTask<String, Void, Object> {

    public interface AsyncResponse {
        void processFinish(Object output);
    }

    public AsyncResponse _delegate = null;

    private Context _context;
    private ProgressDialog _dialog;
    private CWebService _ws = new CWebService();

    public AsyncTaskWS(Context context, AsyncResponse delegate) {
        _context = context;
        _delegate=delegate;
    }

    public AsyncTaskWS(Context context) {
        _context = context;
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
            case "GetCurrentVersion":
                return _ws.GetCurrentVersion();
            case "DangNhap":
                return _ws.DangNhap(params[1], params[2]);
            case "GetDSDocSo":
                return _ws.GetDSDocSo(params[1], params[2], params[3], params[4]);
            case "GetDSCode":
                return _ws.GetDSCode();
            case "TinhTieuThu":
                return _ws.TinhTieuThu(params[1], params[2], params[3], params[4], params[5]);
            case "TinhTienNuoc":
                return _ws.TinhTienNuoc(params[1], params[2], params[3], params[4]);
            case "CapNhat":
                return _ws.CapNhat(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11]);
            case "ThemHinhDHN":
                return _ws.ThemHinhDHN(params[1], params[2], params[3], params[4], params[5]);

        };

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object object) {
        if (_dialog.isShowing()) {
            _dialog.dismiss();
        }
//        _delegate.processFinish(object);
        super.onPostExecute(object);

    }

}
