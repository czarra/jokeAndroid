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
import com.example.rad.joke.data.Category;
import com.example.rad.joke.data.Joke;
import com.example.rad.joke.fragments.CategoryFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JokeActivity extends AppCompatActivity {


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
        setContentView(R.layout.joke);


        Intent intent = getIntent();
        code =  intent.getStringExtra("code");
        progressBar= (ProgressBar) findViewById(R.id.progressBar1);
        textJoke = (TextView) findViewById(R.id.textJoke);
        sendEmail = (Button) findViewById(R.id.sendEmail);
        next = (Button) findViewById(R.id.next);
        imageStar = (ImageView) findViewById(R.id.imageStar);
        context = this;
        setData();

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send email
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "App Joke");
                i.putExtra(Intent.EXTRA_TEXT   , jokeText);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(JokeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });

        imageStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!globalJoke.getStar()){
                    Picasso.with(v.getContext())
                            .load(R.mipmap.star)
                            .into(imageStar);
                    globalJoke.setStar(true);
                    addRemove(globalJoke);
                } else {
                    Picasso.with(v.getContext())
                            .load(R.mipmap.empty_star)
                            .into(imageStar);
                    globalJoke.setStar(false);
                    addRemove(globalJoke);
                }
            }
        });
    }

    private void setData(){
        RetrieveJoke retrieveTask = new RetrieveJoke() {
            @Override
            protected void onPreExecute() {
                textJoke.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
                imageStar.setVisibility(View.INVISIBLE);

            }
            @Override
            protected void onPostExecute(Joke joke) {
                globalJoke = joke;
                jokeText = joke.getValue();
                textJoke.setText(jokeText);
                if(joke.getStar()){
                    Picasso.with(context)
                            .load(R.mipmap.star)
                            .into(imageStar);
                } else {
                    Picasso.with(context)
                            .load(R.mipmap.empty_star)
                            .into(imageStar);
                }
                sendEmail.setVisibility(View.VISIBLE);
                textJoke.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                imageStar.setVisibility(View.VISIBLE);
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
