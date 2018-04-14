package com.example.android.dizajnzaspomenar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QuestionsList extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Log.e("POSITION", String.valueOf(position));
        Resources resources = getResources();

        // Set title of Detail page
        collapsingToolbar.setTitle("Sva pitanja");

        Globals g = Globals.getInstance();

        TypedArray placePictures = resources.obtainTypedArray(g.getCakes() ? R.array.slike_cupcakes : R.array.slike);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        placePicutre.setImageDrawable(placePictures.getDrawable(0 % placePictures.length()));

        placePictures.recycle();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_details_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new MyAdapter(this, position + 1);
        mRecyclerView.setAdapter(mAdapter);

        //SmartFragmentStatePagerAdapter userDetails = new SmartFragmentStatePagerAdapter();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list_users, parent, false));
            name = (TextView) itemView.findViewById(R.id.list_title);
            //description = (TextView) itemView.findViewById(R.id.list_desc);
        }
    }

    public static class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        private int LENGTH; // broj ljudi u Spomenaru

        private final String[] mPlaces;
        //private final String[]  mPlaceDescription;
        private List<String> usersList = new ArrayList<>();
        // private List<String> answersList = new ArrayList<>();


        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, int user_id) {
            DBAdapter db = new DBAdapter(context);
            db.open();
            //ovdje umjesto korisnika idu pitanja
            Cursor c = db.getAllQuestions();
            if (c.moveToFirst())
            {
                do {
                    usersList.add(c.getString(1));
                } while (c.moveToNext());
            }
            LENGTH = usersList.size();

            //a ovdje umjesto pitanja odgovori
         /*   c = db.getAllAnswers();
            // Log.e("QUESTIONID", String.valueOf(getQuestionId()));
            if (c.moveToFirst())
            {
                do {
                    Log.e("USER ID", String.valueOf(user_id));
                    Log.e("TABLICA ID", String.valueOf(c.getInt(0)));
                    if( c.getInt(0) == user_id )
                    {
                        answersList.add(c.getString(3));
                        Log.e("TEKST ODGOVORA", c.getString(3));
                    }
                } while (c.moveToNext());
            } */
            db.close();
            //increase();

            mPlaces = usersList.toArray(new String[0]);
            //  mPlaceDescription = answersList.toArray(new String[0]);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(mPlaces[position % mPlaces.length]);
            //  holder.description.setText( mPlaceDescription[position %  mPlaceDescription.length]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() { return LENGTH; }
    }
}
