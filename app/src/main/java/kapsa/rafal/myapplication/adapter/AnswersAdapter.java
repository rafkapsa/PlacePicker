package kapsa.rafal.myapplication.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import kapsa.rafal.myapplication.R;
import kapsa.rafal.myapplication.data.model.Result;

import static android.content.ContentValues.TAG;

/**
 * Created by Rafal on 2017-11-23.
 */

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {
    private List<Result> mResults;
    private Context mContext;
    private PostResultListener mResultListener;

    private static final int M_MAX_ENTRIES = 3;

    private GeoDataClient mGeoDataClient;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView placeName;
        public ImageView placePhoto;
        PostResultListener mResultListener;

        public ViewHolder(View itemView, PostResultListener postResultListener) {
            super(itemView);
            placeName = (TextView) itemView.findViewById(R.id.place_name_text_view);
            placePhoto = (ImageView) itemView.findViewById(R.id.photo_place_image_view);

            this.mResultListener = postResultListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Result result = getResult(getAdapterPosition());
            this.mResultListener.onPostClick(result.getId());

            notifyDataSetChanged();
        }
    }

    public AnswersAdapter(Context context, List<Result> postsResults, GeoDataClient geoDataClient, PostResultListener itemListener) {
        mResults = postsResults;
        mContext = context;
        mGeoDataClient = geoDataClient;
        mResultListener = itemListener;
    }

    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.recycle_view_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView, this.mResultListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            Result result = mResults.get(position);
            final TextView textView = holder.placeName;
            // Get Place by placeID
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(result.getPlaceId());
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    PlaceBufferResponse places = task.getResult();
                    if(places != null) {
                        Place place = places.get(0);
                        if (place != null) {
                            textView.setText(formatPlaceDetails(mContext.getResources(), place.getName(),
                                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                                    place.getWebsiteUri()));
                        }
                    }
                    places.release();
                }
            });


            final ImageView imageView = holder.placePhoto;
            // Request photos and metadata for the specified place.
                    final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(result.getPlaceId());
                    photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                            // Get the list of photos.
                            PlacePhotoMetadataResponse photos = task.getResult();
                            // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                            PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                            // Get the first photo in the list.
                            if(photoMetadataBuffer != null){
                            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                            // Get the attribution text.
                            CharSequence attribution = photoMetadata.getAttributions();
                            // Get a full-size bitmap for the photo.
                            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                    PlacePhotoResponse photo = task.getResult();
                                    Bitmap bitmap = photo.getBitmap();
                                        imageView.setImageBitmap(bitmap);

                                }
                            });
                            }
                            photoMetadataBuffer.release();
                        }
                    });
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void updateAnswers(List<Result> results) {
            if(results.size() < M_MAX_ENTRIES){
                mResults=results;
            }else{

                /* Reduce list to M_MAX_ENTRIES value*/
                List<Result> mList = new ArrayList<Result>();
                for(int i=0; i < M_MAX_ENTRIES; i++){
                    mList.add(results.get(i));
                }
                mResults = mList;
            }

        notifyDataSetChanged();
    }

    private Result getResult(int adapterPosition) {
        return mResults.get(adapterPosition);
    }

    public List<Result> getmResultsList(){
        return mResults;
    }

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
    }

    public interface PostResultListener {
        void onPostClick(String id);
    }
}
