package edu.ggc.lutz.listpersistsolution.data;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WordContent {

    public static final List<WordItem> ITEMS = new ArrayList<>();

    public static void addItem(WordItem wordItem) {
        ITEMS.add(wordItem);
    }

    public static int removeLast() {
        int last = ITEMS.size() - 1;
        ITEMS.remove(last);
        return last;
    }

    public static void clear() {
        ITEMS.clear();
    }

    public static class WordItem { // our simple Item
        public final String content;

        public WordItem(String content) {
            this.content = content;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            return (((WordItem)other).content.equals(content)) ? true : false;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
