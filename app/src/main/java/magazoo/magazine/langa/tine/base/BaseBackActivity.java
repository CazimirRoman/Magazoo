package magazoo.magazine.langa.tine.base;

import android.graphics.drawable.Drawable;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.annotation.Nullable;

import magazoo.magazine.langa.tine.R;

public abstract class BaseBackActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
