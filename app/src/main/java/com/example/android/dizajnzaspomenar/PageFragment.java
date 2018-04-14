package com.example.android.dizajnzaspomenar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.dizajnzaspomenar.DetailActivity.EXTRA_POSITION;
import static com.example.android.dizajnzaspomenar.R.drawable.a;

/**
 * Created by Tena on 5/10/2017.
 */

public class PageFragment extends Fragment {
    
    private static String title;
    private static int page;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    

    // newInstance constructor for creating fragment with arguments
    public static PageFragment newInstance(int page, String title) {
        PageFragment fragmentFirst = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     /*   View view = inflater.inflate(R.layout.content_settings, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.tv);
        tvLabel.setText(page + " -- " + title);
        return view; */
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, com.example.android.dizajnzaspomenar.DetailActivity.class);
                    intent.putExtra(EXTRA_POSITION, getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.

        private int LENGTH; // broj ljudi u Spomenaru

        private final String[] mPlaces;
        private final String[] mPlaceDescription;
        private final Drawable[] mPlaceAvators;
        private List<String> usersList = new ArrayList<>();
        private List<String> answersList = new ArrayList<>();


        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            //mPlaces = resources.getStringArray(R.array.places);

            DBAdapter db = new DBAdapter(context);
            db.open();
            Cursor c = db.getAllContacts();
            if (c.moveToFirst()) {
                do {
                    usersList.add(c.getString(1));
                } while (c.moveToNext());
            }
            LENGTH = usersList.size();

            c = db.getAnswers(page+1);
            Log.e("QUESTIONID", String.valueOf(page+1));
            if (c.moveToFirst()) {
                do {
                    if (c.getInt(2) == page+1) {
                        answersList.add(c.getString(3));
                        Log.e("TEKST ODGOVORA", c.getString(3));
                    }
                } while (c.moveToNext());
            }
            db.close();
            //if (getQuestionId() == 1)
              //  increase();

            mPlaces = usersList.toArray(new String[0]);
            // mPlaceDescription = resources.getStringArray(R.array.place_desc);
            mPlaceDescription = answersList.toArray(new String[0]);
            Globals g = Globals.getInstance();
            TypedArray a = resources.obtainTypedArray( g.getCakes() ? R.array.cakes_pictures : R.array.place_avator);
            mPlaceAvators = new Drawable[a.length()];

            for (int i = 0; i < mPlaceAvators.length; i++) {
                mPlaceAvators[i] = a.getDrawable(i);
            }
            a.recycle();

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
            holder.name.setText(mPlaces[position % mPlaces.length]);
            holder.description.setText(mPlaceDescription[position % mPlaceDescription.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

}
