package com.example.android.dizajnzaspomenar;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Resources resources = getResources();

        // Set title of Detail page
        collapsingToolbar.setTitle("O Spomenaru");

        Globals g = Globals.getInstance();

        TypedArray placePictures = resources.obtainTypedArray(g.getCakes() ? R.array.slike_cupcakes : R.array.slike);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        placePicutre.setImageDrawable(placePictures.getDrawable(1 % placePictures.length()));

        placePictures.recycle();

    }
}
