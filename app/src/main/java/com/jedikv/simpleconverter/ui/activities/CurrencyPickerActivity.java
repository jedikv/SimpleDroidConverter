package com.jedikv.simpleconverter.ui.activities;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jedikv.simpleconverter.R;
import com.jedikv.simpleconverter.busevents.AddCurrencyEvent;
import com.jedikv.simpleconverter.ui.adapters.CurrencyPickerAdapter;
import com.jedikv.simpleconverter.ui.base.BasePresenter;
import com.jedikv.simpleconverter.ui.base.PresenterFactory;
import com.jedikv.simpleconverter.ui.model.CurrencyModel;
import com.jedikv.simpleconverter.ui.selectcurrencyscreen.SelectCurrencyPresenterFactory;
import com.jedikv.simpleconverter.ui.selectcurrencyscreen.SelectCurrencyScreenPresenter;
import com.jedikv.simpleconverter.ui.selectcurrencyscreen.SelectCurrencyView;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import converter_db.CurrencyEntity;
import timber.log.Timber;

/**
 * Created by Kurian on 13/05/2015.
 */
public class CurrencyPickerActivity
        extends BaseActivity<SelectCurrencyScreenPresenter, SelectCurrencyView>
        implements SelectCurrencyView {

    public static final int REQUEST_CODE_ADD_CURRENCY = 1000;
    public static final int REQUEST_CODE_CHANGE_CURRENCY = 2000;

    public static final int RESULT_CODE_SUCCESS = 1001;

    public static final String EXTRA_CURRENCY_LIST = "extra_currency_list";
    public static final String EXTRA_SELECTED_CURRENCY_CODE = "extra_selected_currency_code";
    public static final String EXTRA_SELECTED_CURRENCY_ISO = "extra_selected_currency_iso";
    public static final String EXTRA_REQUEST_CODE = "extra_request_code";

    // The elevation of the toolbar when content is scrolled behind
    private static final float TOOLBAR_ELEVATION = 14f;

    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolBar;

    private SearchView searchView;

    private CurrencyPickerAdapter adapter;

    @Inject
    SelectCurrencyPresenterFactory presenterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_picker);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getApplicationComponent().inject(this);

        //recyclerView.setScrollViewCallbacks(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {

            if(extras.getLong(EXTRA_REQUEST_CODE) == REQUEST_CODE_ADD_CURRENCY) {
                adapter = new CurrencyPickerAdapter(this, presenter.getListToHide(extras.getLong(EXTRA_SELECTED_CURRENCY_CODE)));
            } else {
                adapter = new CurrencyPickerAdapter(this, Collections.EMPTY_LIST);
            }

            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_currency_picker, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager
                = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if(searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(getString(R.string.hint_search_currency));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        if(searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(CurrencyPickerActivity.this.getComponentName()));

        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void onCurrencyPicked(AddCurrencyEvent event) {

        CurrencyEntity currencyEntity = event.getCurrency();

        Timber.d("Currency Code: " + currencyEntity.getCode() + " Symbol: " + currencyEntity.getName());

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SELECTED_CURRENCY_CODE, currencyEntity.getNumericCode());
        resultIntent.putExtra(EXTRA_SELECTED_CURRENCY_ISO, currencyEntity.getCode());
        setResult(RESULT_CODE_SUCCESS, resultIntent);
        finish();
    }

    @Override
    public SelectCurrencyPresenterFactory getPresenterFactory() {
        return presenterFactory;
    }

    @Override
    public String getPresenterTag() {
        return CurrencyPickerActivity.class.getCanonicalName();
    }

    @Override
    protected void onPresenterPrepared(SelectCurrencyScreenPresenter presenter) {

    }

    @Override
    public void displayCurrencies(List<CurrencyModel> currencies) {
        adapter.loadCurrencies(currencies);
    }
}
