package cn.edu.xmu.android_course.sampleasynctaskloaderapplist;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.Collator;

/**
 * This class holds the per-item data in our Loader.
 */
public class AppEntry implements Comparable<AppEntry> {
    private final AppListLoader mLoader;
    private final PackageManager mPackageManager;
    private final ApplicationInfo mApplicationInfo;
    private final File mApkFile;
    private String mLabel;
    private Drawable mIcon;
    private boolean mMounted;

    public AppEntry(AppListLoader loader, ApplicationInfo info) {
        mLoader = loader;
        mPackageManager = loader.mPackageManager;

        mApplicationInfo = info;
        mApkFile = new File(info.sourceDir);
    }

    public ApplicationInfo getApplicationInfo() {
        return mApplicationInfo;
    }

    public String getLabel() {
        return mLabel;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                mIcon = mApplicationInfo.loadIcon(mPackageManager);
                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mApplicationInfo.loadIcon(mPackageManager);
                return mIcon;
            }
        } else {
            return mIcon;
        }

        return mLoader.getContext().getResources().getDrawable(
                android.R.drawable.sym_def_app_icon);
    }

    @Override
    public String toString() {
        return mLabel;
    }

    void loadLabel(Context context) {
        if (mLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mLabel = mApplicationInfo.packageName;
            } else {
                mMounted = true;
                CharSequence label = mApplicationInfo.loadLabel(context.getPackageManager());
                mLabel = label != null ? label.toString() : mApplicationInfo.packageName;
            }
        }
    }

    @Override
    public int compareTo(@NonNull AppEntry o) {
        final Collator sCollator = Collator.getInstance();

        return sCollator.compare(this.getLabel(), o.getLabel());
    }
}
