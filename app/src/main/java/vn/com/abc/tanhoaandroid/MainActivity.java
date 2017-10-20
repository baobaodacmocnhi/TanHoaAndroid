package vn.com.abc.tanhoaandroid;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private AsyncTaskWS task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnDangNhap = (Button) findViewById(R.id.btnDangNhap);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DangNhapActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        Button btnDocSo = (Button) findViewById(R.id.btnDocSo);
        btnDocSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DocSoActivity.class);
                startActivity(intent);
            }
        });

        Button btnDangXuat = (Button) findViewById(R.id.btnDangXuat);
        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CNguoiDung.Clear();
                Button btnDangNhap = (Button) findViewById(R.id.btnDangNhap);

                btnDangNhap.setEnabled(true);
                btnDangNhap.callOnClick();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int CurrentVersionApp = packageInfo.versionCode;
            task = new AsyncTaskWS(MainActivity.this);
            try {

                String CurrentVersionServer = (String) task.execute(new String[]{"GetCurrentVersion"}).get();
                if (CurrentVersionApp != Integer.parseInt(CurrentVersionServer)) {
//                    new DownloadFileFromURL().execute("https://113.161.88.180:1989/app/tanhoa.apk");
                    DownloadFileFromURL downloadFileFromURL=new DownloadFileFromURL();
                    downloadFileFromURL.execute("https://113.161.88.180:1989/app/tanhoa.apk");
                }

            } catch (InterruptedException e) {
                Toast.makeText(MainActivity.this,  e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (CNguoiDung.MaND == "") {
            Button btnDangNhap = (Button) findViewById(R.id.btnDangNhap);

            btnDangNhap.callOnClick();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Button btnDangNhap = (Button) findViewById(R.id.btnDangNhap);

                btnDangNhap.setEnabled(false);
            }
        }
    }

    private ProgressDialog pDialog;
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String root = Environment.getExternalStorageDirectory().toString();

                System.out.println("Downloading");
                URL url = new URL(f_url[0]);

                HttpURLConnection conection = (HttpURLConnection) url.openConnection();//Open Url Connection
                conection.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                OutputStream output = new FileOutputStream(root+"/downloadedfile.apk");
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Toast.makeText(MainActivity.this,  e.toString(), Toast.LENGTH_SHORT).show();
//                Log.e("Error: ", e.getMessage());
            }

            return null;
        }



        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");

            pDialog.dismiss();
        }

    }
        
        
    
}
