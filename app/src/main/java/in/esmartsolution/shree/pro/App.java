package in.esmartsolution.shree.pro;


import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;

import in.esmartsolution.shree.pro.api.ApiRequestHelper;
import in.esmartsolution.shree.pro.preferences.Preferences;


public class App extends Application {
    private Preferences preferences;
    private ApiRequestHelper apiRequestHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        doInit();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());
        }
    }

    private void doInit() {
        this.preferences = new Preferences(this);
        this.apiRequestHelper = ApiRequestHelper.init(this);
    }

    public synchronized ApiRequestHelper getApiRequestHelper() {
        return apiRequestHelper;
    }

    public synchronized Preferences getPreferences() {
        return preferences;
    }
}
