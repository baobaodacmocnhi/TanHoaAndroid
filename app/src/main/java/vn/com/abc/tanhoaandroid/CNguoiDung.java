package vn.com.abc.tanhoaandroid;

import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;

/**
 * Created by user on 23/08/2017.
 */

public class CNguoiDung {
    public static  String DanhBo="";
    public static String MaND = "";
    public static String HoTen = "";
    public static String May = "";
    public static SoapObject tbDocSo = null;
    public static SoapObject tbDocSoFilter = null;
    public static String[] cmbCodeDisplay;
    public static HashMap<Integer, String> cmbCodeValue;

    public static void Clear()
    {
        DanhBo="";
        MaND="";
        HoTen="";
        May="";
        tbDocSo=null;
        tbDocSoFilter=null;
        cmbCodeDisplay=null;
        cmbCodeValue=null;
    }

}
