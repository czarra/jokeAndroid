package com.example.rad.joke.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rad.joke.R;
import com.example.rad.joke.api.ApiClient;
import com.example.rad.joke.api.ApiException;
import com.example.rad.joke.constants.Constants;
import com.example.rad.joke.data.Joke;
import com.example.rad.joke.fragments.CategoryFragment;
import com.example.rad.joke.fragments.FavoriteFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FavoriteActivity extends AppCompatActivity implements FavoriteFragment.OnFragmentInteractionListener{


    private TextView textJoke;
    private ProgressBar progressBar;
    private String code;
    private String jokeText;
    private Button sendEmail, next;
    private ImageView imageStar;
    private Joke globalJoke;
    private SQLiteDatabase myDataBase;
    private Context context;
    private static final Logger LOG = LoggerFactory.getLogger(CategoryFragment.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        loadFragment(FavoriteFragment.newInstance());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.category) {
            startMainActivity();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_favorite, fragment);
        transaction.commit();
    }

    private void startMainActivity(){
        Intent mIntent = new Intent(FavoriteActivity.this, MainActivity.class);
        startActivity(mIntent);
    }

    private void addRemove(Joke item) {

        try {
            myDataBase = openOrCreateDatabase("Jokes", MODE_PRIVATE, null);
            String query = "SELECT * FROM Jokes WHERE ID_JOKE LIKE '"+item.getId()+"'";
            Cursor cursor = myDataBase.rawQuery(query, null);
            Log.e("in base",""+cursor.getCount());
            String text;
            if(cursor.getCount()>0){
                myDataBase.execSQL("DELETE FROM Jokes WHERE ID_JOKE LIKE '"+item.getId()+"'");
                text = "remove";

            }else {
                myDataBase.execSQL("INSERT INTO Jokes (ID_JOKE ,VALUE , TIME ) VALUES('" +
                        item.getId() + "',\"" +
                        item.getValue().replace('"',' ') + "\",'" +
                        System.currentTimeMillis() + "');");
                text = "add";
            }
            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(this, text, duration);
//            toast.show();
            myDataBase.close();
        }catch(Exception ex){
            Log.e("select","Erro in geting id "+ex.toString());
        }
    }

    @Override
    public void onFragmentInteraction(Joke joke){
        addRemove(joke);
    }

    @Override
    public void onBackPressed() {
        startMainActivity();
        finish();
    }


    class RetrieveJoke extends AsyncTask<String, String, Joke> {

        private final ApiClient client = ApiClient.getInstance();

        protected Joke doInBackground(String... urls) {
            Joke joke = new Joke();

            try {
//                LOG.error(Constants.BASE_URL_RANDOM_CATEGORY+code);
                String jsonResponse;
                if(code.length()>0) {
                    jsonResponse = client.getURL(Constants.BASE_URL_RANDOM_CATEGORY + code, String.class);

                } else {
                    jsonResponse = client.getURL(Constants.BASE_URL_RANDOM , String.class);
                }
                JSONObject jsonObject = new JSONObject(jsonResponse);
                LOG.error(jsonObject.toString());
                joke = Joke.fromJsonObject(jsonObject);

                myDataBase = openOrCreateDatabase("Jokes", MODE_PRIVATE, null);
                String query = "SELECT * FROM Jokes WHERE ID_JOKE LIKE '"+joke.getId()+"'";
                Cursor cursor = myDataBase.rawQuery(query, null);
                if(cursor.getCount()>0) {
                    joke.setStar(true);
                }
                return joke;
            } catch (ApiException | JSONException exp) {
                LOG.error(exp.getMessage(), exp);
            }
            return joke;
        }
    }


}
