package com.example.android.dizajnzaspomenar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public int position;
    public static int current_position;
    public static int BROJ_PITANJA;
    public static int  id_usera;

    private  ViewPager vpPager;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    public static final List<String> TitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Globals g = Globals.getInstance();

        FloatingActionButton fab_question = (FloatingActionButton) findViewById(R.id.fab_new_question);
        fab_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_question();
            }
        });
        if(!g.isLogged()){fab_question.setVisibility(View.GONE); }
        else
        {
            DBAdapter db = new DBAdapter(this);
            db.open();
            if(db.isAdmin(g.getId())) {fab_question.setVisibility(View.VISIBLE);}
            else{fab_question.setVisibility(View.GONE);}
        }

        FloatingActionButton fab_answer = (FloatingActionButton) findViewById(R.id.fab_new_answer);
        fab_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_answer();
            }
        });
        if(!g.isLogged()) {fab_answer.setVisibility(View.GONE); }
        else{fab_answer.setVisibility(View.VISIBLE);}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //provjera();

       /* SpannableString s = new SpannableString("Spomenar");
        s.setSpan(new TypefaceSpan(this, "Sacramento-Regular.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);*/

        id_usera = g.getId();
        dohvatiPitanja();

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, com.example.android.dizajnzaspomenar.AboutActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, QuestionsList.class);
            this.startActivity(intent);

        }else if (id == R.id.nav_manage) {
                Globals g = Globals.getInstance();
                g.setCakes();
                recreate();

        } else if (id == R.id.nav_logout) {
            Globals g = Globals.getInstance();
            if(g.isLogged())
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.logout_message).setTitle(R.string.logout_title);

                builder.setPositiveButton(R.string.logout_ok, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        Globals g = Globals.getInstance();
                        g.logout();
                        Intent intent = new Intent(getApplicationContext(), com.example.android.dizajnzaspomenar.LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } );

                builder.setNegativeButton(R.string.logout_cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {/*User cancelled the dialog*/}
                } );

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            else{
                Intent intent = new Intent(this, com.example.android.dizajnzaspomenar.LoginActivity.class);
                this.startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Style a {@link Spannable} with a custom {@link Typeface}.
     *
     * @author Tristan Waddington
     */
   /* public class TypefaceSpan extends MetricAffectingSpan {
         An <code>LruCache</code> for previously loaded typefaces.
        private LruCache<String, Typeface> sTypefaceCache =
                new LruCache<String, Typeface>(12);

        private Typeface mTypeface;


       // Load the {@link Typeface} and apply to a {@link Spannable}.

        public TypefaceSpan(Context context, String typefaceName) {
            mTypeface = sTypefaceCache.get(typefaceName);

            if (mTypeface == null) {
                mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                        .getAssets(), String.format("fonts/%s", typefaceName));

                // Cache the loaded Typeface
                sTypefaceCache.put(typefaceName, mTypeface);
            }
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
    } */



    public void obrisiPitanja()
    {
        DBAdapter db = new DBAdapter(this);

        db.open();
        db.deleteQuestionsTable();
        db.deleteAnswersTable();
        db.deleteUsersTable();
        db.close();
    }

    public void provjera(int position)
    {
        DBAdapter db = new DBAdapter(this);

        db.open();
        Cursor c = db.getAllContacts();
        if (c.moveToFirst()){
            do {
                //Toast.makeText(this, c.getString(0), Toast.LENGTH_LONG).show();
                Toast.makeText(this, String.valueOf(position), Toast.LENGTH_LONG).show();
            } while (c.moveToNext());
        }
        db.close();
    }


    public void new_answer()
    {
        final int id_pitanja = current_position + 1;
        String tekst_pitanja = TitleList.get(current_position);

        final DBAdapter db = new DBAdapter(getApplicationContext());
        db.open();
        int nije_odgovoreno =db.notAnswered(id_usera, id_pitanja);

        //Toast.makeText(getApplicationContext(), "notAnswered = " + nije_odgovoreno, Toast.LENGTH_SHORT).show();
        if ( nije_odgovoreno == 0) {//id_usera, id_pitanja

            Snackbar.make(findViewById(android.R.id.content),
                    "Već ste odgovorili na ovo pitanje!",
                    Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        else {
            android.support.v7.app.AlertDialog.Builder newAnswer =
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this,  R.style.MyDialogStyle);

            final TextView naslov = new TextView(MainActivity.this);
            naslov.setText("Unesite odgovor na pitanje \"" + tekst_pitanja + "\" :");
            naslov.setGravity(Gravity.CENTER);
            //naslov.setHeight(12);

            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            lp.setMargins(24, 24, 24, 24); //TRBL

            input.setLayoutParams(lp);
            naslov.setLayoutParams(lp);
            //layoutnaslov.addView(naslov);

            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            layout.addView(naslov);
            layout.addView(input);

            newAnswer.setView(layout);

            newAnswer.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    //---update answer to question ---
                        int id2;
                        // id_usera, id_pitanja, tekst odgovora
                        id2 = db.updateAnswer(id_usera, id_pitanja, input.getText().toString());
                        if (id2 > 0) {
                            Toast.makeText(getApplicationContext(), id2 + " redak je ažuriran!", Toast.LENGTH_SHORT).show();
                           /* recreate();
                            vpPager.setAdapter(adapterViewPager);
                            vpPager.setCurrentItem(current_position-1); */
                           osvjezi();

                        }
                        db.close();
                }
            });
            newAnswer.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    //Toast.makeText(getApplicationContext(), "Odustali ste od odgovora.", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog dialog = newAnswer.create();
            dialog.show();
        }

    }

    public void new_question()
    {
        final DBAdapter db = new DBAdapter(getApplicationContext());
        db.open();

        android.support.v7.app.AlertDialog.Builder newQuestion =
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this,  R.style.MyDialogStyle);

        final TextView naslov = new TextView(MainActivity.this);
        naslov.setText("Unesite novo pitanje:");
        naslov.setGravity(Gravity.CENTER);

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        lp.setMargins(24, 24, 24, 24);  //TRBL

        input.setLayoutParams(lp);
        naslov.setLayoutParams(lp);
        //layoutnaslov.addView(naslov);

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(naslov);
        layout.addView(input);

        newQuestion.setView(layout);


        newQuestion.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //---add a question---

                long id3;
                id3 = db.insertNewQuestion( input.getText().toString());
                if ( id3 > 0 ) {
                    Toast.makeText(getApplicationContext(), "Novo pitanje je uspješno dodano!", Toast.LENGTH_SHORT).show();
                    db.close();
                    osvjezi();

                }
                db.close();
            }

        });
        newQuestion.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
               // Toast.makeText(getApplicationContext(), "Odustali ste od novog pitanja.", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = newQuestion.create();
        dialog.show();
    }
    //------------------------------------------------------------------------------

    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            NUM_ITEMS = BROJ_PITANJA;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            return PageFragment.newInstance( position, TitleList.get(position) );
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            //return "Page " + position;
            //current_position = position;
            return TitleList.get(position);
        }

        @Override
        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }
    }

    public void dohvatiPitanja()
    {
        int broj_pitanja = 0;
        DBAdapter db = new DBAdapter(this);

        db.open();
        Cursor c = db.getAllQuestions();
        if (c.moveToFirst()) {
            do {
                TitleList.add(c.getString(1));
                ++broj_pitanja;
            } while (c.moveToNext());
        }
        db.close();
        BROJ_PITANJA = broj_pitanja;
    }

    public void osvjezi()
    {
        MyPagerAdapter adapter  = new MyPagerAdapter(getSupportFragmentManager());
        //MyPagerAdapter adapter = ((MyPagerAdapter)vpPager.getAdapter());
        vpPager.setAdapter(adapter);
    }
}


