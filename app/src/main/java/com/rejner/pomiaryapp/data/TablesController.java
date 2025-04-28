package com.rejner.pomiaryapp.data;

import android.provider.BaseColumns;

public final class TablesController{
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TablesController() {}

    /* Inner class that defines the table contents */
    public static class Pomiary implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }
}
