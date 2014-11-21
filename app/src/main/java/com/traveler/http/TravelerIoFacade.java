package com.traveler.http;

import com.traveler.PlaceType;
import com.traveler.models.flickr.Photo;
import com.traveler.models.google.PlaceDetailsResponse;
import com.traveler.models.google.PlaceItemsResponse;
import com.traveler.models.wikipedia.DescriptionResponse;

import java.util.List;

/**
 * @author vgrec, created on 8/22/14.
 */
public interface TravelerIoFacade {

    void getDescription(TaskFinishedListener<DescriptionResponse> listener);

    void getPhotos(TaskFinishedListener<List<Photo>> listener);

    void getVideos(TaskFinishedListener<String> listener);

    void getPlaces(TaskFinishedListener<PlaceItemsResponse> listener, PlaceType placeType);

    void getPlaceDetails(String placeId, TaskFinishedListener<PlaceDetailsResponse> listener);
}
