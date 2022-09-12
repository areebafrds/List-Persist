package edu.ggc.lutz.listpersistsolution;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Locale;

import edu.ggc.lutz.listpersistsolution.data.WordContent;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id"; // fragment argument representing the item ID
    private WordContent.WordItem mItem;

    private Button btnPronounce;
    public TextToSpeech tts;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String selection = getArguments().getString(ARG_ITEM_ID);
            WordContent.WordItem wi = new WordContent.WordItem(selection);
            mItem = WordContent.ITEMS.get(WordContent.ITEMS.indexOf(wi));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        btnPronounce = rootView.findViewById(R.id.btnPronounce);
        if (mItem != null)
            ((TextView) rootView.findViewById(R.id.item_detail))
                    .setText(mItem.content + " - Someday we'll add dictionary-type definitions here");

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        btnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(mItem.content, TextToSpeech.QUEUE_FLUSH,
                        null, "aaa");
            }
        });

        return rootView;
    }
}
