package edu.ggc.lutz.listpersistsolution;

import android.provider.BaseColumns;

public final class WordListContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private WordListContract() {
    }

    /* Inner class that defines the table contents */
    public static class WordListEntry implements BaseColumns {
        public static final String TABLE_NAME = "wordlist";
        public static final String COLUMN_NAME_CONTENT = "content";
    }
}
