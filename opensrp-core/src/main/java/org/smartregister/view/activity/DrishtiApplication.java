package org.smartregister.view.activity;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.BuildConfig;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.util.BitmapImageCache;
import org.smartregister.util.CrashLyticsTree;
import org.smartregister.util.CredentialsHelper;
import org.smartregister.util.OpenSRPImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

import static org.smartregister.util.Log.logError;

public abstract class DrishtiApplication extends Application {

    protected static DrishtiApplication mInstance;
    private static BitmapImageCache memoryImageCache;
    private static OpenSRPImageLoader cachedImageLoader;
    protected Locale locale = null;
    protected Context context;
    protected Repository repository;
    private byte[] password;
    private String username;
    private static CredentialsHelper credentialsHelper;

    public static synchronized <X extends DrishtiApplication> X getInstance() {
        return (X) mInstance;
    }

    @Nullable
    public P2PClassifier<JSONObject> getP2PClassifier() {
        return null;
    }

    public static BitmapImageCache getMemoryCacheInstance() {
        if (memoryImageCache == null) {
            memoryImageCache = new BitmapImageCache(BitmapImageCache
                    .calculateMemCacheSize(AllConstants.ImageCache.MEM_CACHE_PERCENT));
        }

        return memoryImageCache;
    }

    public static String getAppDir() {
        File appDir = DrishtiApplication.getInstance().getApplicationContext()
                .getDir("opensrp", android.content.Context.MODE_PRIVATE); //Creating an internal
        // dir;
        return appDir.getAbsolutePath();
    }

    public static OpenSRPImageLoader getCachedImageLoaderInstance() {
        if (cachedImageLoader == null) {
            cachedImageLoader = new OpenSRPImageLoader(
                    DrishtiApplication.getInstance().getApplicationContext(),
                    R.drawable.woman_placeholder).setFadeInImage((Build.VERSION.SDK_INT >= 12));
        }

        return cachedImageLoader;
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            initializeCrashLyticsTree();

            mInstance = this;
            SQLiteDatabase.loadLibs(this);
        } catch (UnsatisfiedLinkError e) {
            logError("Error on onCreate: " + e);
        }
    }

    /**
     * Plant the crashlytics tree fro every application to use
     */
    public void initializeCrashLyticsTree() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashLyticsTree());
        }
    }

    public abstract void logoutCurrentUser();

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public Repository getRepository() {
        ArrayList<DrishtiRepository> drishtiRepositoryList = CoreLibrary.getInstance().context().sharedRepositories();
        DrishtiRepository[] drishtiRepositoryArray = drishtiRepositoryList.toArray(new DrishtiRepository[drishtiRepositoryList.size()]);
        if (repository == null) {
            repository = new Repository(getInstance().getApplicationContext(), null, drishtiRepositoryArray);
        }
        return repository;
    }

    public CredentialsHelper credentialsProvider() {

        if (credentialsHelper == null) {
            credentialsHelper = new CredentialsHelper(context);
        }

        return credentialsHelper;
    }

    public final byte[] getPassword() {

        if (password == null) {
            password = credentialsProvider().getCredentials(getUsername(), CredentialsHelper.CREDENTIALS_TYPE.DB_AUTH);
        }

        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    @NonNull
    public ClientProcessorForJava getClientProcessor() {
        return ClientProcessorForJava.getInstance(context.applicationContext());
    }

    public String getUsername() {
        if (username == null) {
            username = context.userService().getAllSharedPreferences().fetchRegisteredANM();
        }
        return username;
    }

    @Override
    public void onTerminate() {
        closePendingTransactions();
        super.onTerminate();
    }

    private void closePendingTransactions() {
        if (repository != null && repository.getWritableDatabase().isOpen()
                && repository.getWritableDatabase().inTransaction()) {
            Timber.e(new RuntimeException("Application closed while transactions are in progress. Data maybe lost"));
            repository.getWritableDatabase().endTransaction();
            context.allSharedPreferences().updateTransactionsKilledFlag(true);
        }
    }

    public Context getContext() {
        return context;
    }

}