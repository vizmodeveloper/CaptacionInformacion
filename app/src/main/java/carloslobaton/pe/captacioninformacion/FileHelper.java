package carloslobaton.pe.captacioninformacion;

/**
 * Created by DanielRolando on 14/06/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileHelper {
    public static final String CONTENT_FOLDER_CACHE_NAME = "cache";
    public static final String CONTENT_FOLDER_NAME = "content";

    public static final int RESULT_OK = 0;
    public static final int RESULT_FILE_NOT_CREATED = 2;
    public static final int RESULT_TYPE_NOT_FOUND = 3;

    public static final int TYPE_FINGERPRINT = 0;
    public static final int TYPE_DNI_1 = 2;
    public static final int TYPE_DNI_2 = 3;
    public static final int TYPE_FACE = 4;
    public static final int TYPE_SIGNATURE = 5;
    public static final int TYPE_INFO = 6;

    private Context mContext;

    public FileHelper(Context mContext) {
        this.mContext = mContext;
    }

    private int saveFile(byte[] content, int type,String folderName){

        if (content==null || content.length==0){
            return RESULT_FILE_NOT_CREATED;
        } else if (type< TYPE_FINGERPRINT || type> TYPE_INFO){
            return RESULT_TYPE_NOT_FOUND;
        }
        try {
            String fileName = getFileName(type);
            File file = new File(folderName,fileName);
            FileOutputStream fop = new FileOutputStream(file);
            fop.write(content);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return RESULT_OK;

    }

    private String getFileName(int type) {
        switch(type){
            case TYPE_FINGERPRINT:
                return "fingerprint.bin";
            case TYPE_DNI_1:
                return "dni1.bin";
            case TYPE_DNI_2:
                return "dni2.bin";
            case TYPE_FACE:
                return "face.bin";
            case TYPE_SIGNATURE:
                return "signature.bin";
            case TYPE_INFO:
                return "info.bin";
        }

        return null;
    }

    private String getBaseFolder(){
//        File folder = new File(mContext.getFilesDir().getAbsolutePath(), CONTENT_FOLDER_NAME);
        File folder = new File(Environment.getExternalStorageDirectory()+"/Android/data/pe.onpe.captacioninformacion/files/"+CONTENT_FOLDER_NAME);

        if (!folder.exists()){
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    public String fullCacheImage(String name){
        File folder = new File(Environment.getExternalStorageDirectory()+"/Android/data/pe.onpe.captacioninformacion/files/"+CONTENT_FOLDER_CACHE_NAME);

        if (!folder.exists()){
            folder.mkdirs();
        }

        File file = new File(folder.getAbsolutePath() + "/" + name);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    public Bitmap getImageReduced(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int height = bitmap.getHeight(), width = bitmap.getWidth();

        Bitmap useBitmap;
        if (height > Constants.IMAGE_PHOTO_HEIGHT){
            float ratioHeight = (float)height/Constants.IMAGE_PHOTO_HEIGHT;
            int newWidth = (int)((float)width/ratioHeight);
            useBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, Constants.IMAGE_PHOTO_HEIGHT, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            useBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            try {
                File file = new File(path);
                FileOutputStream fop = new FileOutputStream(file);
                fop.write(baos.toByteArray());
                fop.flush();
                fop.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            useBitmap = bitmap;
        }
        return useBitmap;

    }

    public void saveAllInfo(String names, String document, String flastname, String mlastname, byte[] fingerprintData, Bitmap face, Bitmap dni1, Bitmap dni2, Bitmap signature, Bitmap fingerprintImage) {
        String folderName = getFolderName(document);
        saveTextInfo(folderName, names, document, flastname, mlastname,fingerprintData);
        saveImageInfo(folderName, TYPE_FACE, face);
        saveImageInfo(folderName, TYPE_DNI_1, dni1);
        saveImageInfo(folderName, TYPE_DNI_2, dni2);
        saveImageInfo(folderName, TYPE_SIGNATURE, signature);
        saveImageInfo(folderName, TYPE_FINGERPRINT, fingerprintImage);
    }

    private File createFolder(String folderName) {
        File folder = new File(getBaseFolder(), folderName);
        if(!folder.exists()){
            folder.mkdir();
        }
        return folder;
    }

    private void saveTextInfo(String folderName, String names, String document, String flastname, String mlastname, byte[] pattern) {
        File folder = createFolder(folderName);
        String patternString = pattern.toString();
        String info = names + System.getProperty("line.separator") +
                document + System.getProperty("line.separator") +
                flastname + System.getProperty("line.separator") +
                mlastname + System.getProperty("line.separator") + patternString;
        String encrypted = (AESCrypt.encryptText(info));
        saveFile(encrypted.getBytes(), TYPE_INFO, folder.getAbsolutePath());
    }

    private void saveImageInfo(String folderName,int type, Bitmap bitmap) {
        if (bitmap==null){
            return;
        }
        File folder = createFolder(folderName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        saveFile(baos.toByteArray(), type, folder.getAbsolutePath());
    }

    private String getFolderName(String document) {
        return document;
    }

    public void deleteFile(final String path){
        File file = new File(path);
        if (!file.exists()) {
        } else {
            file.delete();
        }
    }
}
