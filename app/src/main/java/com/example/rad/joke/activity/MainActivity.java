package com.example.rad.joke.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rad.joke.R;
import com.example.rad.joke.data.Category;
import com.example.rad.joke.data.Joke;
import com.example.rad.joke.fragments.CategoryFragment;

public class MainActivity extends AppCompatActivity implements CategoryFragment.OnFragmentInteractionListener{

    private SQLiteDatabase myDataBase;
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Jokes( ID integer primary key autoincrement,ID_JOKE VARCHAR,VALUE VARCHAR, TIME INT);";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDataBase = openOrCreateDatabase("Jokes", MODE_PRIVATE, null);
        myDataBase.execSQL(CREATE_TABLE);
        myDataBase.close();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(CategoryFragment.newInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.favorite) {
            startFavoriteActivity();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.commit();
    }
    @Override
    public void onFragmentInteraction(Category category) {
        starJokeActivity(category);
    }

    private void starJokeActivity(Category category) {
        Intent mIntent = new Intent(MainActivity.this, JokeActivity.class);
        mIntent.putExtra("code", category.getCode());
        startActivity(mIntent);
        finish();
    }

    private void startFavoriteActivity(){
        Intent mIntent = new Intent(MainActivity.this, FavoriteActivity.class);
        startActivity(mIntent);
    }


}
