package com.jedikv.simpleconverter.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.jedikv.simpleconverter.App;
import com.jedikv.simpleconverter.dbutils.CurrencyDbHelper;
import com.jedikv.simpleconverter.dbutils.CurrencyPairDbHelper;
import com.jedikv.simpleconverter.utils.Constants;

import java.util.Currency;

import icepick.Icepick;

/**
 * Created by Kurian on 08/05/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private CurrencyDbHelper mCurrencyEntityHelper;
    private CurrencyPairDbHelper mCurrencyPairEntityHelper;

    @Override
    protected void onStart() {
        super.onStart();
        App.getBusInstance().register(this);
    }

    @Override
    protected void onStop() {
        App.getBusInstance().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        mCurrencyEntityHelper = new CurrencyDbHelper(this);
        mCurrencyPairEntityHelper = new CurrencyPairDbHelper(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }


    protected SharedPreferences getDefaultSharedPrefs() {

        return PreferenceManager.getDefaultSharedPreferences(App.get(this));
    }

    protected CurrencyDbHelper getCurrencyDbHelper() {
        return mCurrencyEntityHelper;
    }

    protected CurrencyPairDbHelper getPairDbHelper() { return mCurrencyPairEntityHelper; }
}
