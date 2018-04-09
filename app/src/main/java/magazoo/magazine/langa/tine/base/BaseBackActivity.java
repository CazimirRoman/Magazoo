package magazoo.magazine.langa.tine.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BaseBackActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setBackArrowColour();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected abstract void setBackArrowColour();
}
