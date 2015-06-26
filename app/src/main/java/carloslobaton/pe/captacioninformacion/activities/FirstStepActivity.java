package carloslobaton.pe.captacioninformacion.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.morpho.capture.MorphoTabletFPSensorDevice;
import com.morpho.dao.AuthBfdCap;
import com.morpho.morphosmart.sdk.TemplateType;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import carloslobaton.pe.captacioninformacion.R;
import carloslobaton.pe.captacioninformacion.entities.Document;

/**
 * Created by DanielRolando on 26/06/2015.
 */
public class FirstStepActivity  extends BaseActivity implements AuthBfdCap {
    private MorphoTabletFPSensorDevice fpSensorCap;
    private boolean isCapturing = false;
    private Pair<byte[],Bitmap> fpInfo;

    @InjectView(R.id.dni)
    EditText dni;

    @InjectView(R.id.step)
    TextView step;

    @InjectView(R.id.fingerprint) ImageView fingerprint;

    @OnClick(R.id.fingerprint) void takeFingerprint(){
        if (!isCapturing){
            imagesEnabled(false);
            fingerprint.setBackground(null);
            startCapture();
        }
    }

    Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firststep);
        ButterKnife.inject(this);
        fingerprint.setEnabled(false);
        dni.addTextChangedListener(new DNIWatcher());
    }

    private void prepareFingerprint() {
        step.setText(getString(R.string.step3));
        fingerprint.setEnabled(true);
        document = new Document(dni.getText().toString());
        Toast.makeText(this,"CHARS " + dni.getText().toString(),Toast.LENGTH_SHORT).show();
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
                fpInfo = new Pair(templateData, previewBitmap);
                fingerprint.setImageBitmap(previewBitmap);
                displayMessage(getString(R.string.fingerprint_confirmation));
                final int err = fpSensorCap.verifyMatch(fpInfo.first, templateData, TemplateType.MORPHO_PK_ANSI_378);
                if (err == 0) {
                    Toast.makeText(FirstStepActivity.this,"VERIFIED",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FirstStepActivity.this,"NOT VERIFIED",Toast.LENGTH_SHORT).show();
                }
                captureCompleted(flagComplete);
            }
        });
    }

    private void startCapture() {
        fpSensorCap = new MorphoTabletFPSensorDevice(this);
        fpSensorCap.open(this);
        fpSensorCap.setViewToUpdate(fingerprint);
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

    private void imagesEnabled(final boolean newState) {
        isCapturing = !newState;
        fingerprint.setEnabled(newState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fpSensorCap!=null){
            fpSensorCap.cancelLiveAcquisition();
        }
    }

    private class DNIWatcher implements TextWatcher{
        private Timer timer= null;
        private final long DELAY = 5000;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (timer==null){
                step.setText(getString(R.string.step2));
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            if (timer!=null){
                timer.cancel();
            }

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prepareFingerprint();
                        }
                    });
                }
            }, DELAY);
        }
    }
}