package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.utils.Utils;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kpirwani on 2/21/16.
 */
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder>  {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.ivAvatar)
        public ImageView ivAvatar;
        @Bind(R.id.tvName)
        public TextView tvName;
        @Bind(R.id.tvHandle)
        public TextView tvHandle;
        @Bind(R.id.tvTime)
        public TextView tvTime;
        @Bind(R.id.tvBody)
        public TextView tvBody;
        @Bind(R.id.ivContent)
        public ImageView ivContent;
        @Bind(R.id.btnReply)
        public ImageButton btnReply;
        @Bind(R.id.btnRetweet)
        public ImageButton btnRetweet;
        @Bind(R.id.btnFavorite)
        public ImageButton btnFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private List<TweetModel> tweets;
    private Context context;
    private ITweetActionListener mListener;

    public TweetsArrayAdapter(Context context, List<TweetModel> tweets, ITweetActionListener listener) {
        this.context = context;
        this.tweets = tweets;
        this.mListener = listener;
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TweetModel tweetModel = tweets.get(position);

        Glide.with(context).load(tweetModel.getUser().getProfileImageUrl()).into(holder.ivAvatar);
        holder.tvName.setText(tweetModel.getUser().getName());
        holder.tvHandle.setText("@" + tweetModel.getUser().getScreenName());
        holder.tvBody.setText(getColoredBodyString(tweetModel.getBody()));
        String relativeTime = new PrettyTime().format(Utils.getTwitterDate(tweetModel.getCreatedAt()));
        holder.tvTime.setText(relativeTime);

        if (tweetModel.getImageUrl() != null) {
            holder.ivContent.setVisibility(View.VISIBLE);
            Glide.with(context).load(tweetModel.getImageUrl()).fitCenter().override(300, 300).into(holder.ivContent);
        } else {
            holder.ivContent.setVisibility(View.GONE);
        }

        if (tweetModel.isFavorited()) {
            holder.btnFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.filled_heart));
        } else {
            holder.btnFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.unfilled_heart));
        }

        if (tweetModel.isRetweeted()) {
            holder.btnFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.retweet_done));
        } else {
            holder.btnRetweet.setImageDrawable(context.getResources().getDrawable(R.drawable.retweet));
        }

        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.profile(tweetModel);
            }
        });
        setupButtons(tweetModel, holder);
    }

    private void setupButtons(final TweetModel tweetModel, final ViewHolder holder) {
        holder.btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweetModel.isRetweeted()) {
                    holder.btnRetweet.setImageDrawable(context.getResources().getDrawable(R.drawable.retweet_done));
                } else {
                    holder.btnRetweet.setImageDrawable(context.getResources().getDrawable(R.drawable.retweet));
                }
                mListener.retweet(tweetModel);
            }
        });

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweetModel.isFavorited() == true) {
                    holder.btnFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.unfilled_heart));
                    mListener.unfavorite(tweetModel);
                } else {
                    holder.btnFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.filled_heart));
                    mListener.favorite(tweetModel);
                }
            }
        });

        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.reply(tweetModel);
            }
        });

        //style
        buttonEffect(holder.btnFavorite);
        buttonEffect(holder.btnReply);
        buttonEffect(holder.btnRetweet);
    }

    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    private SpannableStringBuilder getColoredBodyString(String body) {
        SpannableStringBuilder bodyStringBuilder = new SpannableStringBuilder(body);
        colorHashtagsInText(bodyStringBuilder, body, 0);
        colorTagsInText(bodyStringBuilder, body, 0);
        return bodyStringBuilder;
    }

    private void colorHashtagsInText(SpannableStringBuilder inBuilder, String text, int spanBeginning) {
        final String hashtagDelimeter = "#";
        colorSymbolInText(hashtagDelimeter, inBuilder, text, spanBeginning);
    }

    private void colorTagsInText(SpannableStringBuilder inBuilder, String text, int spanBeginning) {
        final String tagDelimeter = "@";
        colorSymbolInText(tagDelimeter, inBuilder, text, spanBeginning);
    }

    private void colorSymbolInText(String symbol, SpannableStringBuilder inBuilder, String text, int spanBeginning) {
        boolean commentContainsSymbol = text.contains(symbol);
        if (commentContainsSymbol == true) {
            for (int index = text.indexOf(symbol); index >= 0; index = text.indexOf(symbol, index + 1)) {

                int endOfSymbol = text.indexOf(" ", index + 1);
                if (endOfSymbol == -1) {
                    endOfSymbol = text.length();
                }
                inBuilder.setSpan(new StyleSpan(Typeface.ITALIC), index + spanBeginning, endOfSymbol + spanBeginning, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                inBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.hastag)), index + spanBeginning, endOfSymbol + spanBeginning, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public interface ITweetActionListener {
        void profile(TweetModel tweetModel);
        void retweet(TweetModel tweetModel);
        void reply(TweetModel tweetModel);
        void favorite(TweetModel tweetModel);
        void unfavorite(TweetModel tweetModel);
    }
}
