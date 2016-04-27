package com.ls.pycon_israel.model.dao;

import com.ls.pycon_israel.model.AppDatabaseInfo;
import com.ls.pycon_israel.model.data.Location;
import com.ls.pycon_israel.model.database.AbstractEntityDAO;


public class LocationDao extends AbstractEntityDAO<Location, Long> {

    public static final String TABLE_NAME = "table_location";

    @Override
    protected String getSearchCondition() {
        return "_id=?";
    }

    @Override
    protected String[] getSearchConditionArguments(Long theId) {
        return new String[]{theId.toString()};
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDatabaseName() {
        return AppDatabaseInfo.DATABASE_NAME;
    }

    @Override
    protected Location newInstance() {
        return new Location();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }
}
