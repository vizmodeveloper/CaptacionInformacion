package carloslobaton.pe.captacioninformacion;

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

/**
 * Created by DanielRolando on 20/05/2015.
 */
public class SignatureActivity extends AppCompatActivity {
    private Signature signature;

    @InjectView(R.id.content)
    FrameLayout content;

    @OnClick(R.id.cancel) void cancel(){
        if (signature!=null && !signature.isCapturing()){
            File file = new File(new FileHelper(this).fullCacheImage("activity_signature.png"));
            if (file.exists()){
                file.delete();
            }
            this.finish();
        }

    }

    @OnClick(R.id.erase) void erase(){
        if (signature!=null && !signature.isCapturing()){
            signature.clear();
        }
    }
    @OnClick(R.id.save) void save(){
        if (signature!=null && !signature.isCapturing()){
            content.setDrawingCacheEnabled(true);
            signature.save(signature);
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.inject(this);

        signature = new Signature(this, null);
        signature.setBackgroundColor(Color.WHITE);
        content.addView(signature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        getSupportActionBar().setIcon(R.drawable.image_nofingerprint);

    }
}
