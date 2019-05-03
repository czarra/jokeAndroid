package com.example.rad.joke.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rad.joke.R;
import com.example.rad.joke.data.Category;

import java.util.List;

public class MyCategoryRecyclerViewAdapter extends RecyclerView.Adapter<MyCategoryRecyclerViewAdapter.ViewHolder> {

    public final List<Category> categories;
    private Context context;
    private final CategoryFragment.OnFragmentInteractionListener listener;

    public MyCategoryRecyclerViewAdapter(List<Category> categories, CategoryFragment.OnFragmentInteractionListener listener) {
        this.categories = categories;
        this.listener =listener;
    }

    @Override
    public MyCategoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.category = categories.get(position);
        holder.categoryName.setText( categories.get(position).getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    Log.d("action Listener","click"+categories.get(position).geCode());

                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onFragmentInteraction(categories.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final TextView categoryName;
        Category category;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            categoryName = (TextView) view.findViewById(R.id.categoryName);
        }
    }
}
