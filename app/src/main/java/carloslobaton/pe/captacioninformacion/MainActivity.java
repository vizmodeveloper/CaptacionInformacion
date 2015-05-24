package carloslobaton.pe.captacioninformacion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_FINGERPRINT_CAPTURE= 1;
    private static final int REQUEST_SIGNATURE_CAPTURE = 2;
    private static final int REQUEST_FACE_CAPTURE = 3;
    private static final int REQUEST_DNI1_CAPTURE = 4;
    private static final int REQUEST_DNI2_CAPTURE = 5;

    private FileHelper fHelper;
    private Uri imageUri;
    private Bitmap bmpFace,bmpDni1,bmpDni2,bmpSignature;

    @OnClick(R.id.signature) void takeSignature(){
        startActivityForResult(new Intent(this, SignatureActivity.class), REQUEST_SIGNATURE_CAPTURE);
    }
    @OnClick(R.id.face) void takeFace(){
        takePicture("face.png",REQUEST_FACE_CAPTURE);
    }
    @OnClick(R.id.dni1) void takeDni1(){
        takePicture("dni1.png",REQUEST_DNI1_CAPTURE);
    }
    @OnClick(R.id.dni2) void takeDni2(){
        takePicture("dni2.png",REQUEST_DNI2_CAPTURE);
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
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
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
            File file = new File(new FileHelper(this).fullCacheImage("activity_signature.png"));
            if (file.exists()){
                ImageView signature = ButterKnife.findById(this,R.id.signature);
                signature.setBackgroundColor(getResources().getColor(R.color.white));
                bmpSignature = BitmapFactory.decodeFile(file.getAbsolutePath());
                signature.setImageBitmap(bmpSignature);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
