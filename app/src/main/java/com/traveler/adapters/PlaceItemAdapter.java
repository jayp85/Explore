package com.traveler.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.traveler.Constants;
import com.traveler.ImageHelper;
import com.traveler.R;
import com.traveler.models.google.Place;

import java.util.List;

/**
 * @author vgrec, created on 11/4/14.
 */
public class PlaceItemAdapter extends RecyclerView.Adapter<PlaceItemAdapter.ViewHolder> {

    private List<Place> places;
    private Context context;

    public PlaceItemAdapter(List<Place> places) {
        this.places = places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_place, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Place place = places.get(position);
        viewHolder.name.setText(place.getName());
        viewHolder.rating.setText(place.getRating());

        if (place.getPhotos() != null && place.getPhotos().size() > 0) {
            String url = String.format(Constants.Google.THUMBNAIL_URL, place.getPhotos().get(0).getPhotoReference());
            ImageHelper.loadImage(context, url, viewHolder.placePicture);
        } else {
            ImageHelper.loadImage(context, place.getIconUrl(), viewHolder.placePicture);
        }
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView rating;
        public NetworkImageView placePicture;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            rating = (TextView) itemView.findViewById(R.id.rating);
            placePicture = (NetworkImageView) itemView.findViewById(R.id.place_picture);
        }
    }
}