package com.rejner.pomiaryapp.data;

import android.provider.BaseColumns;

public final class TablesController{
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TablesController() {}

    /* Inner class that defines the table contents */
    public static class Pomiary implements BaseColumns {
        public static final String TABLE_NAME = "pomiary";
        public static final String COLUMN_NAME_NAME = "nazwa";
        public static final String COLUMN_NAME_DATE = "data";
    }

    public static class Bloki implements BaseColumns {
        public static final String TABLE_NAME = "bloki";
        public static final String COLUMN_NAME_ID_MEASUREMENT= "id_pomiar";
        public static final String COLUMN_NAME_STREET= "ulica";
        public static final String COLUMN_NAME_CITY = "miasto";
        public static final String COLUMN_NAME_NUMBER = "numer_domu";
    }

    public static class Mieszkanie implements BaseColumns {
        public static final String TABLE_NAME = "mieszkanie";
        public static final String COLUMN_NAME_HOME_ID = "id_blok";
        public static final String COLUMN_NAME_NUMBER = "numer";

    }

    public static class Pokoj implements BaseColumns {
        public static final String TABLE_NAME = "pokoj";
        public static final String COLUMN_NAME_FLAT_ID = "id_mieszkanie";
        public static final String COLUMN_NAME_TYPE = "typ";
    }


    public static class Gniazdko implements BaseColumns {
        public static final String TABLE_NAME = "gniazdko";
        public static final String COLUMN_NAME_ROOM_ID = "id_pokoju";
        public static final String COLUMN_NAME_COMMENT = "uwagi";
        public static final String COLUMN_NAME_MEASUREMENT= "pomiar";

    }
    public static class Zdjecia implements BaseColumns {
        public static final String TABLE_NAME = "zdjecia";
        public static final String COLUMN_NAME_FLAT_ID = "id_mieszkania";
        public static final String COLUMN_NAME_IMAGE = "zdjecie";

    }
    public static class Pomiar {
        public long id;
        public String name;
        public String date;

        public Pomiar(long id, String name, String date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }
    }
    public static class Flat{
        public long id;
        public String number;
        public long home_id;

        public Flat(long home_id, String number, long id) {
            this.home_id = home_id;
            this.number = number;
            this.id = id;
        }
    }

    public static class Home {
        public long id;
        public long id_pomiar;


        public String street;
        public String city;
        public String number;

        public Home(long id, long id_pomiar, String street, String city, String number) {
            this.id = id;
            this.id_pomiar = id_pomiar;
            this.street = street;
            this.city = city;
            this.number = number;

        }
    }
}
