package com.example.rad.joke.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rad.joke.R;
import com.example.rad.joke.api.ApiClient;
import com.example.rad.joke.api.ApiException;
import com.example.rad.joke.constants.Constants;
import com.example.rad.joke.data.Category;
import com.example.rad.joke.data.Joke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteFragment.class);

    private OnFragmentInteractionListener listener;
    private MyFavoriteRecyclerViewAdapter adapter;
    private SQLiteDatabase myDataBase;
    private Context context;
    private TextView empty;

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Set the adapter
        context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        empty = (TextView) view.findViewById(R.id.empty);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyFavoriteRecyclerViewAdapter(new ArrayList<Joke>(), listener);
        RetrieveFavorite retrieveGameTask = new RetrieveFavorite() {
            @Override
            protected void onPreExecute() {
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(List<Joke> jokes) {
                adapter.jokes.clear();
                for (int i = 0; i < jokes.size(); i++) {
                    adapter.jokes.add(jokes.get(i));
                }
                if(jokes.size() == 0){
                    empty.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        };
        retrieveGameTask.execute();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Joke item);
    }


    class RetrieveFavorite extends AsyncTask<String, String, List<Joke>> {

        protected List<Joke> doInBackground(String... urls) {
            List<Joke> list = new ArrayList<>();
            myDataBase = context.openOrCreateDatabase("Jokes", MODE_PRIVATE, null);
            String query = "SELECT * FROM Jokes ";
            query +=" ORDER BY time ASC";

            Cursor cursor = myDataBase.rawQuery(query, null);
            Log.e("in base",""+cursor.getCount());

            while (cursor.moveToNext()) {
                Joke item  = new Joke(""+cursor.getString(1),""+cursor.getString(2));
                item.setStar(true);
                list.add(item);
            }
            myDataBase.close();
            return list;


        }
    }

}
