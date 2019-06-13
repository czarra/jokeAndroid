package com.example.rad.joke.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.rad.joke.R;
import com.example.rad.joke.api.ApiClient;
import com.example.rad.joke.api.ApiException;
import com.example.rad.joke.constants.Constants;
import com.example.rad.joke.data.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryFragment.class);

    private OnFragmentInteractionListener listener;
    private MyCategoryRecyclerViewAdapter adapter;

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Set the adapter
        Context context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyCategoryRecyclerViewAdapter(new ArrayList<Category>(), listener);
        RetrieveCategory retrieveCategory = new RetrieveCategory() {
            @Override
            protected void onPreExecute() {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(List<Category> categories) {
                adapter.categories.clear();
                for (int i = 0; i < categories.size(); i++) {
                    adapter.categories.add(categories.get(i));
                }
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        };
        retrieveCategory.execute();
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
        void onFragmentInteraction(Category item);
    }


    class RetrieveCategory extends AsyncTask<String, String, List<Category>> {

        private final ApiClient client = ApiClient.getInstance();

        protected List<Category> doInBackground(String... urls) {
            List<Category> list = new ArrayList<>();
            list.add( new Category("Random"));
            try {
                String response = client.getURL(Constants.BASE_URL_ALL_CATEGORY, String.class);
                LOG.error(response);
                String[] arrOfStr = response.split(",");
                for (String name : arrOfStr) {
                    Category item = new Category(name,name);
                    LOG.error(item.getName());
                    list.add(item);
                }

                return list;
            } catch (ApiException exp) {
                LOG.error(exp.getMessage(), exp);
            }
            return list;
        }
    }

}
