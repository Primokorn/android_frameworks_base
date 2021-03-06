package com.android.systemui.quicksettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;

public class ImmersiveModeTile extends QuickSettingsTile {
    private boolean mEnabled = false;

    public ImmersiveModeTile(Context context, 
            QuickSettingsController qsc, Handler handler) {
        super(context, qsc);

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the system setting
                Settings.System.putIntForUser(mContext.getContentResolver(),
                        Settings.System.GLOBAL_IMMERSIVE_MODE_STATE, mEnabled ? 0 : 1,
                        UserHandle.USER_CURRENT);
            }
        };

        mOnLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.settings",
                        "com.android.settings.Settings$SystemSettingsActivity");
                startSettingsActivity(intent);
                return true;
            }
        };

        Uri stateUri = Settings.System.getUriFor(Settings.System.GLOBAL_IMMERSIVE_MODE_STATE);
        qsc.registerObservedContent(stateUri, this);
    }

    void onPostCreate() {
        updateTile();
        super.onPostCreate();
    }

    @Override
    public void updateResources() {
        updateTile();
        super.updateResources();
    }

    private synchronized void updateTile() {
        mEnabled = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.GLOBAL_IMMERSIVE_MODE_STATE, 0, UserHandle.USER_CURRENT) == 1;
        if (mEnabled) {
            mDrawable = R.drawable.ic_qs_immersive_mode_on;
            mLabel = mContext.getString(R.string.quick_settings_immersive_mode);
        } else {
            mDrawable = R.drawable.ic_qs_immersive_mode_off;
            mLabel = mContext.getString(R.string.quick_settings_immersive_mode_off);
        }
    }

    @Override
    public void onChangeUri(ContentResolver resolver, Uri uri) {
        updateResources();
    }
}
