package carloslobaton.pe.captacioninformacion;

/**
 * Created by DanielRolando on 20/05/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    public static final String CONTENT_FOLDER_CACHE_NAME = "cache";
    public static final String CONTENT_FOLDER_NAME = "content";
    public static final String CONTENT_FOLDER_TEST_NAME = "test";

    public static final int RESULT_OK = 0;
    public static final int RESULT_FOLDER_NOT_FOUND = 1;
    public static final int RESULT_FILE_NOT_CREATED = 2;
    public static final int RESULT_TYPE_NOT_FOUND = 3;
    public static final int RESULT_NULL_BITMAP = 4;

    public static final int TYPE_FINGERPRINT_1 = 0;
    public static final int TYPE_FINGERPRINT_2 = 1;
    public static final int TYPE_DNI_1 = 2;
    public static final int TYPE_DNI_2 = 3;
    public static final int TYPE_FACE = 4;
    public static final int TYPE_SIGNATURE = 5;
    public static final int TYPE_PATTERN = 6;

    private Context mContext;

    public FileHelper(Context mContext) {
        this.mContext = mContext;
    }

    private int saveFile(byte[] content, int type,String folderName){

        if (content==null || content.length==0){
            return RESULT_FILE_NOT_CREATED;
        } else if (type<TYPE_FINGERPRINT_1 || type>TYPE_PATTERN){
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
        Log.d("SAVEFILES", "PEOPLE REGISTER" + folderName + " - " + type);

        return RESULT_OK;

    }

    private String getFileName(int type) {
        switch(type){
            case TYPE_FINGERPRINT_1:
                return "fingerprint1.bin";
            case TYPE_FINGERPRINT_2:
                return "fingerprint2.bin";
            case TYPE_DNI_1:
                return "dni1.bin";
            case TYPE_DNI_2:
                return "dni2.bin";
            case TYPE_FACE:
                return "face.bin";
            case TYPE_SIGNATURE:
                return "signature.bin";
            case TYPE_PATTERN:
                return "patterns.bin";
        }

        return null;
    }

    public int saveImage(Bitmap bitmap, int type, String folderName){
        if (bitmap==null){
            return RESULT_NULL_BITMAP;
        } else if (type<TYPE_FINGERPRINT_1 || type>TYPE_SIGNATURE){
            return RESULT_TYPE_NOT_FOUND;
        }
        File folder = new File(getBaseFolder(), String.valueOf(folderName.hashCode()));
        if(!folder.exists()){
            folder.mkdir();
        }
        if (folder.exists()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return saveFile(baos.toByteArray(),type,folder.getAbsolutePath());
        } else{
            return RESULT_FOLDER_NOT_FOUND;
        }
    }

    private String getBaseFolder(){
        File folder = new File(mContext.getFilesDir().getAbsolutePath(), CONTENT_FOLDER_NAME);

        if (!folder.exists()){
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }

    public String fullCacheImage(String name){
        File folder = new File(Environment.getExternalStorageDirectory()+"/Android/data/pe.carloslobaton.captacioninformacion/files/"+CONTENT_FOLDER_CACHE_NAME);

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

    public String fullFileName(int type, String folderName){
        return getBaseFolder() + "/" + folderName.hashCode() + "/" + getFileName(type);
    }

    public byte[] getContent(int type, String folderName) {
        File folder = new File(getBaseFolder() + "/" + folderName.hashCode());
        if (!folder.exists()) {
        } else if (type < TYPE_FINGERPRINT_1 || type > TYPE_SIGNATURE) {
        } else {
            String fileName = getFileName(type);
            File file = new File(folder, fileName);
            if (!file.exists()) {
            } else {
                try {
                    return FileUtils.readFileToByteArray(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void deleteContent(int type, String folderName){
        File folder = new File(getBaseFolder() + "/" + folderName.hashCode());
        if (!folder.exists()) {
        } else if (type < TYPE_FINGERPRINT_1 || type > TYPE_SIGNATURE) {
        } else {
            String fileName = getFileName(type);
            File file = new File(folder, fileName);
            if (!file.exists()) {
            } else {
                file.delete();
            }
        }
    }

    public void deleteEmptyFolders() {
        File folder = new File(getBaseFolder());
        if (folder.isDirectory()){
            for (String folderName :folder.list()){
                File file = new File(folder,folderName);
                if (file.isDirectory() && file.list().length==0){
                    file.delete();
                }
            }
        }
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

    public void deleteFile(final String path){
        File file = new File(path);
        if (!file.exists()) {
        } else {
            file.delete();
        }
    }

    public void deleteAllFiles() {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),CONTENT_FOLDER_TEST_NAME);//new File(getBaseFolder());
        if (folder.isDirectory()){
            for (String folderName :folder.list()){
                File file = new File(folder,folderName);
                if (file.isDirectory()) {
                    String[] children = file.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(file, children[i]).delete();
                    }
                }
                file.delete();
            }
        }
    }

    public void savePatterns(String strPatterns) {
        saveFile(strPatterns.getBytes(),TYPE_PATTERN,getPatternsLocation(CONTENT_FOLDER_PATTERNS_NAME));
    }

    public byte[][] getValidPatterns (){
        byte[][] patterns = null;

        //File file = new File(getPatternsLocation(CONTENT_FOLDER_PATTERNS_NAME),"patterns.bin");
        File file2 = new File(getPatternsLocation(CONTENT_FOLDER_PATTERNS_NAME),"rawBytes.bin");
        //File file3 = new File(getPatternsLocation(CONTENT_FOLDER_PATTERNS_NAME),"test.bin");

        patterns = new byte[1][];
        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            byte[] bytes = IOUtils.toByteArray(fileInputStream);
//            fileInputStream.close();

//            patterns[0] = bytes;

            FileInputStream fileInputStream1 = new FileInputStream(file2);
            byte[] bytes1 = IOUtils.toByteArray(fileInputStream1);
            fileInputStream1.close();

            patterns[0] = bytes1;

//            FileInputStream fileInputStream2 = new FileInputStream(file3);
//            byte[] bytes2 = IOUtils.toByteArray(fileInputStream2);
//            fileInputStream2.close();

//            patterns[2] = bytes2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        List<String> strPatterns = new ArrayList<String>();
//        try{
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            for(String line; (line = br.readLine()) != null; ) {
//                strPatterns.add(line);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (strPatterns.size()>0){
//            patterns = new byte[strPatterns.size()][];
//            for (int i = 0;i<strPatterns.size();i++){
//                String pattern = strPatterns.get(i);
//                try {
//                    patterns[i] =  IOUtils.toByteArray(new StringReader(pattern), "UTF-8");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        return patterns;
    }

    public static final String CONTENT_FOLDER_PATTERNS_NAME = "patterns";

    public String getPatternsLocation(String name){
        File folder = new File(Environment.getExternalStorageDirectory().getPath());

        if (!folder.exists()){
            folder.mkdirs();
        }
        return folder.getAbsolutePath() + "/" + name;
    }

    public File getLog() {
        File folder  = new File(getBaseFolder() + "/Log" );
        if (!folder.exists()){
            folder.mkdir();
        }

        File log = new File(folder.getPath() + "/log.bin");
        if (!log.exists()){
            return null;
        } else {
            return log;
        }
    }

    public void deleteLog() {
        deleteFile(getLog().getPath());
    }
}
