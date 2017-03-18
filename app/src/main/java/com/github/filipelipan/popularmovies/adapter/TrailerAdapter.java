package com.github.filipelipan.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.model.Trailer;

import java.util.ArrayList;

/**
 * Created by lispa on 21/01/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> mTrailers;
    private Context mContext;

    public TrailerAdapter(ArrayList<Trailer> trailers, Context context){
        mTrailers = trailers;
        mContext = context;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        if(mTrailers != null){
            return mTrailers.size();
        }
        return 0;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTrailerName;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            mTrailerName = (TextView) itemView.findViewById(R.id.tv_trailer_list_name);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position){
            Trailer trailer = mTrailers.get(position);

            mTrailerName.setText(trailer.getName());
        }

        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + mTrailers.get(getLayoutPosition()).getSource());
            Intent webIntent = new Intent(Intent.ACTION_VIEW,uri);
            mContext.startActivity(webIntent);
        }
    }
}
