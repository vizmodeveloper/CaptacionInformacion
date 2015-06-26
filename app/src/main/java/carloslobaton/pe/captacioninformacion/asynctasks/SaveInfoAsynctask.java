package carloslobaton.pe.captacioninformacion.asynctasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import carloslobaton.pe.captacioninformacion.activities.BaseActivity;
import carloslobaton.pe.captacioninformacion.utils;

/**
 * Created by DanielRolando on 14/06/2015.
 */
public class SaveInfoAsynctask extends AsyncTask {
    BaseActivity activity;
    utils.FileHelper fHelper;
    String names, document, flastname, mlastname;
    byte[] fingerprintData;
    Bitmap face,dni1,dni2,signature,fingerprintImage;

    public SaveInfoAsynctask(BaseActivity activity, utils.FileHelper fHelper, String names, String document, String flastname, String mlastname,
                             byte[] fingerprintData, Bitmap face, Bitmap dni1, Bitmap dni2, Bitmap signature, Bitmap fingerprintImage) {
        this.activity = activity;
        this.fHelper = fHelper;
        this.names = names;
        this.document = document;
        this.flastname = flastname;
        this.mlastname = mlastname;

        this.fingerprintData= fingerprintData;
        this.face = face;
        this.dni1 = dni1;
        this.dni2 = dni2;
        this.signature = signature;
        this.fingerprintImage = fingerprintImage;
    }

        @Override
    protected Object doInBackground(Object[] params) {
        fHelper.saveAllInfo(names,document,flastname,mlastname,fingerprintData,face,dni1,dni2,signature,fingerprintImage);
        return null;
    }
}
