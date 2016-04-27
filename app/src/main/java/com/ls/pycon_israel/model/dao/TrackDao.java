package com.ls.pycon_israel.model.dao;

import com.ls.pycon_israel.model.AppDatabaseInfo;
import com.ls.pycon_israel.model.data.Track;
import com.ls.pycon_israel.model.database.AbstractEntityDAO;

public class TrackDao extends AbstractEntityDAO<Track, Long> {

    public static final String TABLE_NAME = "table_track";

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
    protected Track newInstance() {
        return new Track();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }
}
