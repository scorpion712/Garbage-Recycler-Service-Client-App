package com.example.lauti.finalintromoviles.model;

import android.provider.BaseColumns;

public class UserContract {

    public static abstract class UserEntry implements BaseColumns{
        public static final String TABLE_NAME= "users";

        public static final String FIRSTNAME = "firstname";
        public static final String LASTNAME = "lastname";
        public static final String USERNAME = "username";
        public static final String ADDRESS = "address";
        public static final String EMAIL = "email";

    }
}