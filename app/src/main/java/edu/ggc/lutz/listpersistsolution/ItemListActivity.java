package edu.ggc.lutz.listpersistsolution;

import static edu.ggc.lutz.listpersistsolution.WordListContract.WordListEntry.COLUMN_NAME_CONTENT;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.ggc.lutz.listpersistsolution.data.WordContent;
import edu.ggc.lutz.listpersistsolution.data.Words;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    private boolean mTwoPane;  // Whether or not the activity is in two-pane mode

    private SimpleItemRecyclerViewAdapter adapter;

    protected static Words words;



    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int totalCount = 0;
    private  TextView visits;
    private  TextView lastVisited;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        words = new Words(getApplicationContext());


        visits = (TextView)findViewById(R.id.tvVisits);
        lastVisited = (TextView)findViewById(R.id.tvLastVisit);

        prefs = getPreferences(Context.MODE_PRIVATE);





    }
    @Override
    protected void onResume() {
        super.onResume();

        WordListDbHelper dbHelper = new WordListDbHelper(ItemListActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from wordList", null);
        if (c.getCount() >0)
            WordContent.ITEMS.clear();
        while(c.moveToNext()) {
            String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT));
            WordContent.addItem((new WordContent.WordItem(content)));
        }
        Log.i("ListPersist", "read in and added: " + c.getCount());
        c.close();
        db.close();

        editor = prefs.edit();

        totalCount = prefs.getInt("counter", 0);
        totalCount++;
        editor.putInt("counter", totalCount);
        editor.commit();

        visits.setText("Visits: " + totalCount);

        SimpleDateFormat df = new SimpleDateFormat("EEE MM-dd-yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());


        editor.putString("lastVisit", "Last Visit: " + date);
        lastVisited.setText("Last Visited: " + date);
        }

    protected void onPause() {

        super.onPause();

        WordListDbHelper dbHelper = new WordListDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("wordlist",null,null);

        ContentValues values = new ContentValues();
        for(WordContent.WordItem item : WordContent.ITEMS){
            values.put(COLUMN_NAME_CONTENT, item.content);

            db.insert("wordlist",null,values);
        }

        Cursor mCount = db.rawQuery("select count(*) from wordlist",null);
        mCount.moveToFirst();
        Log.v("ListPersist","written = " + mCount.getInt(0));
        mCount.close();
        db.close();







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_master_list, menu);

        List<Integer> targetIDs = new ArrayList<>(Arrays.asList(
            R.id.action_add,
            R.id.action_delete_last)
        );

        for(int i = 0; i < menu.size(); i++) { // scales up "+" and "-" by 2x proportion
            MenuItem item = menu.getItem(i);
            if (targetIDs.contains(item.getItemId())) {
                String t = item.getTitle().toString();
                SpannableString span = new SpannableString(t);
                RelativeSizeSpan r = new RelativeSizeSpan(2.0f);
                span.setSpan(r, 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                item.setTitle(span);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                WordContent.addItem(
                    new WordContent.WordItem(words.fetchRandom()));
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                break;
            case R.id.action_delete_last:
                if (WordContent.ITEMS.size() > 0)
                    adapter.notifyItemRemoved(WordContent.removeLast());
                break;

            case R.id.action_clear:
                WordContent.clear();
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(this, WordContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<WordContent.WordItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WordContent.WordItem item = (WordContent.WordItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.content);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.content);
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<WordContent.WordItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position).content);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = view.findViewById(R.id.content);
            }
        }
    }
}
