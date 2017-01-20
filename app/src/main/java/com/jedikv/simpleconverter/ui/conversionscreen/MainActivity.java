package com.jedikv.simpleconverter.ui.conversionscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jedikv.simpleconverter.R;
import com.jedikv.simpleconverter.ui.activities.BaseActivity;
import com.jedikv.simpleconverter.ui.activities.CurrencyPickerActivity;
import com.jedikv.simpleconverter.ui.adapters.CurrencyConversionsAdapter;
import com.jedikv.simpleconverter.ui.adapters.gestures.CurrencyTouchItemCallback;
import com.jedikv.simpleconverter.ui.base.PresenterFactory;
import com.jedikv.simpleconverter.ui.model.ConversionItemModel;
import com.jedikv.simpleconverter.ui.model.CurrencyModel;
import com.jedikv.simpleconverter.ui.views.CurrencyInputView;
import com.jedikv.simpleconverter.utils.Constants;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import converter_db.CurrencyEntity;
import icepick.State;
import timber.log.Timber;


public class MainActivity extends BaseActivity<ConversionViewPresenter, ConversionView>
        implements ConversionView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.currency_input)
    CurrencyInputView currencyInputView;
    @BindView(R.id.rl_container)
    RelativeLayout rlContainer;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout parent;
    @BindView(R.id.toolbar)
    Toolbar toolBar;

    @Inject
    ConversionPresenterFactory presenterFactory;

    private CurrencyConversionsAdapter mCurrencyConversionsAdapter;

    @State
    String mInputedValueString;

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);

        mCurrencyConversionsAdapter = new CurrencyConversionsAdapter(this, parent, getCurrentSourceCurrency());


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mCurrencyConversionsAdapter);


        setUpTouchGestures();



        currencyInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_NULL:
                    case KeyEvent.KEYCODE_ENTER:
                        currencyInputView.dismissKeyboard();
                        conversionPresenter.updateFromSourceCurrency(getCurrentSourceCurrencyCode());
                        return true;

                    default:
                        return false;
                }

            }
        });


        currencyInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    conversionPresenter.convertValue(s.toString());
            }
        });


        mInputedValueString = getDefaultSharedPrefs().getString(Constants.PREFS_CACHED_SAVED_INPUT_VALUE, "0.00");
        Timber.d("Cached input value: " + mInputedValueString);

        currencyInputView.setValue(mInputedValueString);


        rlContainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currencyInputView.dismissKeyboard();
                conversionPresenter.updateFromSourceCurrency(getCurrentSourceCurrencyCode());
                return true;
            }
        });

        conversionPresenter.updateFromSourceCurrency(getCurrentSourceCurrencyCode());

    }

    @OnClick(R.id.fab)
    public void addCurrency() {
        startCurrencyPicker(CurrencyPickerActivity.REQUEST_CODE_ADD_CURRENCY);
    }

    @OnClick(R.id.ib_flag)
    public void changeSourceCurrency() {

        startCurrencyPicker(CurrencyPickerActivity.REQUEST_CODE_CHANGE_CURRENCY);
    }

    private void setUpTouchGestures() {

        ItemTouchHelper touchHelper = new ItemTouchHelper(new CurrencyTouchItemCallback(mCurrencyConversionsAdapter));
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void startCurrencyPicker(int requestCode) {

        Bundle bundle = new Bundle();
        bundle.putLong(CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY_CODE, getCurrentSourceCurrencyCode());
        Intent pickCurrencyIntent = new Intent(this, CurrencyPickerActivity.class);
        pickCurrencyIntent.putExtras(bundle);
        startActivityForResult(pickCurrencyIntent, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void updateSourceCurrency(long currencyCode) {

        CurrencyEntity currencyEntity = mCurrencyEntityHelper.getById(currencyCode);
        Timber.d("id: " + currencyCode + " code: " + currencyEntity.getCode());

        if(getDefaultSharedPrefs().edit().putString(Constants.PREFS_CURRENTLY_SELECTED_CURRENCY, currencyEntity.getCode()).commit()) {
            getDefaultSharedPrefs().edit().putLong(Constants.PREFS_CURRENTLY_SELECTED_CURRENCY_CODE, currencyCode).commit();
            updateViews();
        }
        conversionPresenter.updateFromSourceCurrency(currencyCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == CurrencyPickerActivity.RESULT_CODE_SUCCESS) {

            final long currencyCode
                    = data.getLongExtra(CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY_CODE, -1);

            final String isoCode
                    = data.getStringExtra(CurrencyPickerActivity.EXTRA_SELECTED_CURRENCY_ISO);
            Timber.d("Result currency code: 1%$s", currencyCode);
            switch (requestCode) {

                case CurrencyPickerActivity.REQUEST_CODE_ADD_CURRENCY: {
                    addCurrencyToList(currencyCode);
                    break;
                }

                case CurrencyPickerActivity.REQUEST_CODE_CHANGE_CURRENCY: {
                    updateSourceCurrency(currencyCode);
                    getPresenter().cacheSourceEntry(isoCode, mInputedValueString);
                    break;
                }
            }
        }
    }

    @Override
    public void updateSelectedCurrency(CurrencyModel source, int value) {
    }

    @Override
    public void updateConversions(List<ConversionItemModel> items) {
    }

    @Override
    public void insertConversionItem(ConversionItemModel conversionItem) {
    }

    public void updateList(BigDecimal inputValue) {
        mCurrencyConversionsAdapter.updateCurrencyTargets(getCurrentSourceCurrency(), inputValue);
    }

    /* Handling lifecycle lifetimes of presenters */
    @Override
    public PresenterFactory<ConversionViewPresenter> getPresenterFactory() {
        return presenterFactory;
    }

    @Override
    public String getPresenterTag() {
        return MainActivity.class.getCanonicalName();
    }

    @Override
    protected void onPresenterPrepared(ConversionViewPresenter presenter) {
        Timber.d("onPresenterPrepared %1$s", getPresenterTag());
    }
}
