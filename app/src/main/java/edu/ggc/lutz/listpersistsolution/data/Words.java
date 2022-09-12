package edu.ggc.lutz.listpersistsolution.data;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

// words.txt file from https://github.com/AlexHakman/Java-challenge

public class Words extends ArrayList<String> {

    Random random;

    public Words(Context context) {
        try {
            InputStream is = context.getAssets().open("words.txt");
            readFromFile(is);
            Log.i("Words", "read " + size() + " words.");
        } catch (IOException e) {
            Log.i("Words", "Whoa Nellie!");
            e.printStackTrace();
        }
        random = new Random();
    }

    public String fetchRandom() {
        return get(random.nextInt(size()-1));
    }

    private void readFromFile(InputStream inputStream)
            throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String word;
            while ((word = reader.readLine()) != null) add(word);
        }
        finally {
            reader.close();
        }
    }
}
