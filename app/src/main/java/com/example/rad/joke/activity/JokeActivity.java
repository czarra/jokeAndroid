package com.example.rad.joke.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rad.joke.R;
import com.example.rad.joke.api.ApiClient;
import com.example.rad.joke.api.ApiException;
import com.example.rad.joke.constants.Constants;
import com.example.rad.joke.data.Category;
import com.example.rad.joke.data.Joke;
import com.example.rad.joke.fragments.CategoryFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JokeActivity extends AppCompatActivity {


    private TextView textJoke;
    private ProgressBar progressBar;
    private String code;
    private static final Logger LOG = LoggerFactory.getLogger(CategoryFragment.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joke);


        Intent intent = getIntent();
        code =  intent.getStringExtra("code");
        progressBar= (ProgressBar) findViewById(R.id.progressBar1);
        textJoke = (TextView) findViewById(R.id.textJoke);




        RetrieveJoke retrieveTask = new RetrieveJoke() {
            @Override
            protected void onPreExecute() {
                textJoke.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            protected void onPostExecute(Joke joke) {
                textJoke.setText(joke.getValue());
                textJoke.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        };
        retrieveTask.execute();
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
            //startEndGamesActivity();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class RetrieveJoke extends AsyncTask<String, String, Joke> {

        private final ApiClient client = ApiClient.getInstance();

        protected Joke doInBackground(String... urls) {
            Joke joke = new Joke();

            try {
                LOG.error(Constants.BASE_URL_RANDOM_CATEGORY+code);
                String jsonResponse;
                if(code.length()>0) {
                    jsonResponse = client.getURL(Constants.BASE_URL_RANDOM_CATEGORY + code, String.class);

                } else {
                    jsonResponse = client.getURL(Constants.BASE_URL_RANDOM , String.class);
                }
                JSONObject jsonObject = new JSONObject(jsonResponse);
                LOG.error(jsonObject.toString());
                joke = Joke.fromJsonObject(jsonObject);

                return joke;
            } catch (ApiException | JSONException exp) {
                LOG.error(exp.getMessage(), exp);
            }
            return joke;
        }
    }


}
