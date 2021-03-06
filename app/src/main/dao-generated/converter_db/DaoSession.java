package converter_db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import converter_db.CurrencyEntity;
import converter_db.CurrencyPairEntity;
import converter_db.ConversionItem;

import converter_db.CurrencyEntityDao;
import converter_db.CurrencyPairEntityDao;
import converter_db.ConversionItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig currencyEntityDaoConfig;
    private final DaoConfig currencyPairEntityDaoConfig;
    private final DaoConfig conversionItemDaoConfig;

    private final CurrencyEntityDao currencyEntityDao;
    private final CurrencyPairEntityDao currencyPairEntityDao;
    private final ConversionItemDao conversionItemDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        currencyEntityDaoConfig = daoConfigMap.get(CurrencyEntityDao.class).clone();
        currencyEntityDaoConfig.initIdentityScope(type);

        currencyPairEntityDaoConfig = daoConfigMap.get(CurrencyPairEntityDao.class).clone();
        currencyPairEntityDaoConfig.initIdentityScope(type);

        conversionItemDaoConfig = daoConfigMap.get(ConversionItemDao.class).clone();
        conversionItemDaoConfig.initIdentityScope(type);

        currencyEntityDao = new CurrencyEntityDao(currencyEntityDaoConfig, this);
        currencyPairEntityDao = new CurrencyPairEntityDao(currencyPairEntityDaoConfig, this);
        conversionItemDao = new ConversionItemDao(conversionItemDaoConfig, this);

        registerDao(CurrencyEntity.class, currencyEntityDao);
        registerDao(CurrencyPairEntity.class, currencyPairEntityDao);
        registerDao(ConversionItem.class, conversionItemDao);
    }
    
    public void clear() {
        currencyEntityDaoConfig.getIdentityScope().clear();
        currencyPairEntityDaoConfig.getIdentityScope().clear();
        conversionItemDaoConfig.getIdentityScope().clear();
    }

    public CurrencyEntityDao getCurrencyEntityDao() {
        return currencyEntityDao;
    }

    public CurrencyPairEntityDao getCurrencyPairEntityDao() {
        return currencyPairEntityDao;
    }

    public ConversionItemDao getConversionItemDao() {
        return conversionItemDao;
    }

}
