package com.example.rad.joke.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rad.joke.R;
import com.example.rad.joke.activity.JokeActivity;
import com.example.rad.joke.data.Category;
import com.example.rad.joke.data.Joke;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyFavoriteRecyclerViewAdapter extends RecyclerView.Adapter<MyFavoriteRecyclerViewAdapter.ViewHolder> {

    public final List<Joke> jokes;
    private Context context;
    private final FavoriteFragment.OnFragmentInteractionListener listener;

    public MyFavoriteRecyclerViewAdapter(List<Joke> jokes, FavoriteFragment.OnFragmentInteractionListener listener) {
        this.jokes = jokes;
        this.listener =listener;
    }

    @Override
    public MyFavoriteRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.joke = jokes.get(position);
        holder.textJoke.setText( jokes.get(position).getValue());
        if(jokes.get(position).getStar()){
            Picasso.with(context)
                    .load(R.mipmap.star)
                    .into(holder.imageStar);
        } else {
            Picasso.with(context)
                    .load(R.mipmap.empty_star)
                    .into(holder.imageStar);
        }

        holder.imageStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    //Log.d("action Listener","click"+dogs.get(position).getStar());
                    if(!jokes.get(position).getStar()){
                        // holder.imageStar.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(R.mipmap.star)
                                .into(holder.imageStar);
                        jokes.get(position).setStar(true);
                    } else {
                        //holder.imageStar.setVisibility(View.GONE);
                        Picasso.with(context)
                                .load(R.mipmap.empty_star)
                                .into(holder.imageStar);
                        jokes.get(position).setStar(false);
                    }
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onFragmentInteraction(jokes.get(position));
                }
            }
        });
        holder.sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "App Joke");
                i.putExtra(Intent.EXTRA_TEXT   , jokes.get(position).getValue());
                try {
                    context.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return jokes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final ImageView imageStar;
        final TextView textJoke;
        final Button sendEmail;
        Joke joke;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            imageStar = (ImageView) view.findViewById(R.id.imageStar);
            textJoke = (TextView) view.findViewById(R.id.textJoke);
            sendEmail = (Button) view.findViewById(R.id.sendEmail);
        }
    }
}
