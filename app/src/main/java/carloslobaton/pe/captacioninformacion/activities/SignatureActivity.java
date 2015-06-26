package carloslobaton.pe.captacioninformacion.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import carloslobaton.pe.captacioninformacion.R;
import carloslobaton.pe.captacioninformacion.views.SignatureView;
import carloslobaton.pe.captacioninformacion.utils;

/**
 * Created by DanielRolando on 20/05/2015.
 */
public class SignatureActivity extends AppCompatActivity {
    private SignatureView signatureView;

    @InjectView(R.id.content)
    FrameLayout content;

    @OnClick(R.id.cancel) void cancel(){
        if (signatureView !=null && !signatureView.isCapturing()){
            File file = new File(new utils.FileHelper(this).fullCacheImage("activity_signature.png"));
            if (file.exists()){
                file.delete();
            }
            this.finish();
        }

    }

    @OnClick(R.id.erase) void erase(){
        if (signatureView !=null && !signatureView.isCapturing()){
            signatureView.clear();
        }
    }
    @OnClick(R.id.save) void save(){
        if (signatureView !=null && !signatureView.isCapturing()){
            content.setDrawingCacheEnabled(true);
            signatureView.save(signatureView);
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.inject(this);

        signatureView = new SignatureView(this, null);
        signatureView.setBackgroundColor(Color.WHITE);
        content.addView(signatureView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        getSupportActionBar().setIcon(R.drawable.image_nofingerprint);

    }
}
