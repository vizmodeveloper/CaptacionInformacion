package carloslobaton.pe.captacioninformacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.morpho.capture.MorphoTabletFPSensorDevice;
import com.morpho.dao.AuthBfdCap;

import java.io.File;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements AuthBfdCap {
    private static final int REQUEST_FINGERPRINT_CAPTURE= 1;
    private static final int REQUEST_SIGNATURE_CAPTURE = 2;
    private static final int REQUEST_FACE_CAPTURE = 3;
    private static final int REQUEST_DNI1_CAPTURE = 4;
    private static final int REQUEST_DNI2_CAPTURE = 5;

    private MorphoTabletFPSensorDevice fpSensorCap;
    private boolean isCapturing = false;
    private Pair<byte[],Bitmap> fpInfo;
    private boolean fpVerified = false;

    private FileHelper fHelper;
    private Uri imageUri;
    private Bitmap bmpFace,bmpDni1,bmpDni2,bmpSignature;

    @InjectView(R.id.fingerprint) ImageView fingerprint;

    @OnClick(R.id.signature) void takeSignature(){
        startActivityForResult(new Intent(this, SignatureActivity.class), REQUEST_SIGNATURE_CAPTURE);
    }
    @OnClick(R.id.face) void takeFace(){
        takePicture(getString(R.string.filename_face), REQUEST_FACE_CAPTURE);
    }
    @OnClick(R.id.dni1) void takeDni1(){
        takePicture(getString(R.string.filename_dni1), REQUEST_DNI1_CAPTURE);
    }
    @OnClick(R.id.dni2) void takeDni2(){
        takePicture(getString(R.string.filename_dni2), REQUEST_DNI2_CAPTURE);
    }
    @OnClick(R.id.fingerprint) void takeFingerprint(){
        fingerprint.setBackground(null);
        startCapure(fingerprint);
    }
    @OnClick(R.id.register) void register(){
        if (!(fpVerified && fpInfo!=null && bmpFace!=null && bmpDni1!=null && bmpDni2!=null && bmpSignature!=null)){
            displayMessage(getString(R.string.register_error_images));
            return;
        }
        String names = ((TextView)ButterKnife.findById(this,R.id.names)).getText().toString();
        String document = ((TextView)ButterKnife.findById(this,R.id.document)).getText().toString();
        String flastname = ((TextView)ButterKnife.findById(this,R.id.flastname)).getText().toString();
        String mlastname = ((TextView)ButterKnife.findById(this,R.id.mlastname)).getText().toString();
        if (!(names.length()>0 && document.length()==8 && flastname.length()>0 && mlastname.length()>0)){
            displayMessage(getString(R.string.register_error_text));
            return;
        }

        new SaveInfoAsynctask(this,fHelper,names,document,flastname,mlastname,fpInfo.first,bmpFace,bmpDni1,bmpDni2,bmpSignature,fpInfo.second).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        fHelper = new FileHelper(this);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        getSupportActionBar().setIcon(R.drawable.image_nofingerprint);
    }

    private void takePicture(final String fileName, final int picId){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File(fHelper.fullCacheImage(fileName)));
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, picId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode== REQUEST_FACE_CAPTURE || requestCode == REQUEST_DNI1_CAPTURE || requestCode == REQUEST_DNI2_CAPTURE) && resultCode == Activity.RESULT_OK){
            Bitmap useBitmap = fHelper.getImageReduced(imageUri.getPath());
            fHelper.deleteFile(imageUri.getPath());
            if (requestCode == REQUEST_FACE_CAPTURE) {
                bmpFace = useBitmap;
                ((ImageView)ButterKnife.findById(this,R.id.face)).setImageBitmap(bmpFace);
            } else if (requestCode == REQUEST_DNI1_CAPTURE) {
                bmpDni1 = useBitmap;
                ((ImageView)ButterKnife.findById(this,R.id.dni1)).setImageBitmap(bmpDni1);
            } else if (requestCode == REQUEST_DNI2_CAPTURE) {
                bmpDni2 = useBitmap;
                ((ImageView)ButterKnife.findById(this,R.id.dni2)).setImageBitmap(bmpDni2);
            }
        } else if (requestCode==REQUEST_SIGNATURE_CAPTURE && resultCode == Activity.RESULT_OK){
            File file = new File(new FileHelper(this).fullCacheImage(getString(R.string.filename_signature)));
            if (file.exists()){
                ImageView signature = ButterKnife.findById(this,R.id.signature);
                signature.setBackgroundColor(getResources().getColor(R.color.white));
                bmpSignature = BitmapFactory.decodeFile(file.getAbsolutePath());
                signature.setImageBitmap(bmpSignature);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateImageView(ImageView imgPreview, final Bitmap previewBitmap, String message, final boolean flagComplete, int captureError) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fingerprint.setImageBitmap(previewBitmap);
                captureCompleted(flagComplete);
            }
        });
    }

    @Override
    public void updateImageView(final byte[] templateData, ImageView imgPreview, final Bitmap previewBitmap, String message, final boolean flagComplete, int captureError) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (fpInfo == null) {
                    fpInfo = new Pair(templateData, previewBitmap);
                    fingerprint.setImageBitmap(previewBitmap);
                    displayMessage(getString(R.string.fingerprint_confirmation));
                } else {
                    final int err = fpSensorCap.verifyMatch(fpInfo.first, templateData);
                    if (err == 0) {
                        fpVerified = true;
                        fingerprint.setImageBitmap(previewBitmap);
                        displayMessage(getString(R.string.fingerprint_successful));

                    } else {
                        fingerprint.setImageResource(R.drawable.image_nofingerprint);
                        fpVerified = false;
                        fpInfo = null;
                        displayMessage(getString(R.string.fingerprint_validationerror));
                    }
                }
                captureCompleted(flagComplete);
            }
        });
    }

    private void startCapure(ImageView fp) {
        fpSensorCap = new MorphoTabletFPSensorDevice(this);
        fpSensorCap.open(this);
        fpSensorCap.setViewToUpdate(fp);
        try {
            fpSensorCap.startCapture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void captureCompleted(boolean flagComplete) {
        if (flagComplete){
            imagesEnabled(true);
            if (fpSensorCap!=null){
                fpSensorCap.cancelLiveAcquisition();
            } else{
                this.finish();
            }
        }
    }

    private void imagesEnabled(final boolean newState){
        isCapturing = !newState;
        ButterKnife.findById(this,R.id.fingerprint).setEnabled(newState);
        ButterKnife.findById(this,R.id.fingerprint).setClickable(newState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fpSensorCap!=null){
            fpSensorCap.cancelLiveAcquisition();
        }
    }
}