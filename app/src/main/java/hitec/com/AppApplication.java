package hitec.com;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class AppApplication extends Application{
    private static AppApplication instance;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static synchronized AppApplication getInstance() {
        return instance;
    }
}
