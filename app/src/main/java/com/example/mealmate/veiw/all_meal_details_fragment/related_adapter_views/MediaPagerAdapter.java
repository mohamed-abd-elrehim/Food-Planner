package com.example.mealmate.veiw.all_meal_details_fragment.related_adapter_views;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.R;
import com.example.mealmate.model.MediaItem;

import java.util.List;

public class MediaPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MediaPagerAdapter";
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;

    private Context context;
    private List<MediaItem> mediaItems;

    public MediaPagerAdapter(Context context, List<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getItemViewType(int position) {
        return mediaItems.get(position).isVideo() ? TYPE_VIDEO : TYPE_IMAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slide_item, parent, false);
            return new ImageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_slide_item, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_IMAGE) {
            ((ImageViewHolder) holder).bind(mediaItems.get(position));
        } else {
            ((VideoViewHolder) holder).bind(mediaItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    // ViewHolder for Images
    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(MediaItem mediaItem) {
            Glide.with(context)
                    .load(mediaItem.getUrl())
                    .into(imageView);
        }
    }

    // ViewHolder for Videos
    class VideoViewHolder extends RecyclerView.ViewHolder {
        WebView webView;

        VideoViewHolder(View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);  // Enable JavaScript for video playback
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webView.setWebViewClient(new WebViewClient());  // Handle links in the WebView itself
            webView.setWebChromeClient(new WebChromeClient());  // Handle video playback
        }

        void bind(MediaItem mediaItem) {
            String mediaUrl = mediaItem.getUrl();
            String videoId = Uri.parse(mediaUrl).getQueryParameter("v"); // Extract video ID
            if (videoId != null) {
                String videoHtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" allowfullscreen></iframe>";
                webView.loadDataWithBaseURL(null, videoHtml, "text/html", "UTF-8", null);
            } else {
                Log.e(TAG, "Invalid YouTube URL: " + mediaUrl);
            }
            Log.i(TAG, "bind: " + mediaUrl);
        }
    }
}
