package ma.ifdose.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bugfender.sdk.Bugfender;

import org.greenrobot.greendao.database.Database;

import ma.ifdose.app.db.DaoMaster;
import ma.ifdose.app.db.DaoSession;
import ma.ifdose.app.utils.BugfenderTree;
import timber.log.Timber;

/**
 * Created by aaaa on 10/23/2017.
 */

public class App extends Application {
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                ENCRYPTED ? "aliments-db-encrypted" : "aliments-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("IfDoSe") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        if (getString(R.string.app_mode).equals("dev")) {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            boolean pref_debug = sp.getBoolean("pref_debug", false);

            String fName = sp.getString("nom", "Nom");
            String lName = sp.getString("pren", "Prenom");

            Bugfender.init(this, getString(ma.ifdose.app.R.string.bugfender_app_key), pref_debug);
//            Bugfender.enableLogcatLogging();
//            Bugfender.enableUIEventLogging(this);
            Bugfender.setDeviceString("Patient", fName + " " + lName);
            if (pref_debug)
                Timber.plant(new BugfenderTree());
            else
                Timber.plant(new Timber.DebugTree());
        } else
            Timber.plant(new Timber.DebugTree());

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
