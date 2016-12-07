package com.dnbitstudio.londoncycles.ui;

import com.dnbitstudio.londoncycles.R;
import com.dnbitstudio.londoncycles.ui.list.BikePointListActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    // Constants
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    private static String mAccountType;
    // Instance fields
    Account mAccount;
    private String mAutority;

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, mAccountType);

        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);

        return newAccount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAutority = getString(R.string.content_authority);
        mAccountType = getString(R.string.account_type);
        // Create the dummy account
        mAccount = CreateSyncAccount(this);
        setupSync();

        BikePointListActivity.launchActivity(this);
        finish();
    }

    public void setupSync() {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.setIsSyncable(mAccount, mAutority, 1);
        ContentResolver.setSyncAutomatically(mAccount, mAutority, true);

        ContentResolver.addPeriodicSync(mAccount, mAutority, Bundle.EMPTY, 30);
    }
}
