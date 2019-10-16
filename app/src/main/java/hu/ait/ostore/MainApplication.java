package hu.ait.ostore;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {

	private Realm realmItem;

	@Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }

	public void openRealm() {
		RealmConfiguration config = new RealmConfiguration
				.Builder()
				.deleteRealmIfMigrationNeeded()
				.build();
		realmItem = Realm.getInstance(config);
	}

	public void closeRealm() {
		realmItem.close();
	}

	public Realm getRealmItem() {
		return realmItem;
	}


}
