package com.heareasy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.heareasy.R;
import com.heareasy.activity.SubscriptionPlanActivity;
import com.heareasy.model.SubscriptionListResponser;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {
    private Context context;
    private List<SubscriptionListResponser.Datum> subscriptionList;
    private int lastSelectedPosition = -1;
    private OnItemClick onItemClick;
    public static boolean isSelected = false;

    public SubscriptionAdapter(Context context, List<SubscriptionListResponser.Datum> subscriptionList, OnItemClick onItemClick) {
        this.context = context;
        this.subscriptionList = subscriptionList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public SubscriptionAdapter.SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscription_item_layout, parent, false);
        return new SubscriptionViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SubscriptionAdapter.SubscriptionViewHolder holder, int position) {
        holder.tv_months.setText(subscriptionList.get(position).getMonths() + " Months");
        if (subscriptionList.get(position).getDiscountType().equalsIgnoreCase("PERCENTAGE")) {
            holder.tv_off_price.setText(SubscriptionPlanActivity.round(subscriptionList.get(position).getDiscountPrice(),2) + " % off");
            holder.tv_price.setText("$" + SubscriptionPlanActivity.round(subscriptionList.get(position).getOfferPrice(), 2));
            holder.tv_cutPrice.setText("$" + SubscriptionPlanActivity.round(subscriptionList.get(position).getOriginalPrice(), 2));
            holder.tv_offerStart.setText("Offer start : "+subscriptionList.get(position).getOffer_start_date());
            holder.tv_offerEnd.setText("Valid till : "+subscriptionList.get(position).getOffer_end_date());
        }
        else if (subscriptionList.get(position).getDiscountType().equalsIgnoreCase("FLAT")) {
            holder.tv_off_price.setText("$" + SubscriptionPlanActivity.round(subscriptionList.get(position).getDiscountPrice(),2) + " off");
            holder.tv_price.setText("$" + SubscriptionPlanActivity.round(subscriptionList.get(position).getOfferPrice(), 2));
            holder.tv_cutPrice.setText("$" + SubscriptionPlanActivity.round(subscriptionList.get(position).getOriginalPrice(), 2));
            holder.tv_offerStart.setText("Offer start : "+subscriptionList.get(position).getOffer_start_date());
            holder.tv_offerEnd.setText("Valid till : "+subscriptionList.get(position).getOffer_end_date());
        }
        else if (subscriptionList.get(position).getDiscountType().equalsIgnoreCase("none")) {
            holder.tv_off_price.setText("$" + SubscriptionPlanActivity.round(subscriptionList.get(position).getDiscountPrice(),2) + " off");
            holder.tv_price.setText("$" + SubscriptionPlanActivity.round(subscriptionList.get(position).getOfferPrice(), 2));
            holder.tv_cutPrice.setText("");
            holder.tv_offerStart.setText("");
            holder.tv_offerEnd.setText("");
        }


        if (lastSelectedPosition == position) {
            holder.llayout.setBackgroundResource(R.drawable.bg_layout);
        } else {
            holder.llayout.setBackgroundResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    private void setAnimation(View viewToAnimate) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.1f, 1.0f
            , Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        );
        anim.setDuration(1000);
        viewToAnimate.startAnimation(anim);
    }

    public class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_months, tv_off_price, tv_price, tv_cutPrice,tv_offerEnd,tv_offerStart;
        private ConstraintLayout clayout;
        private LinearLayout llayout;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            clayout = itemView.findViewById(R.id.clayout);
            llayout = itemView.findViewById(R.id.llayout);
            tv_months = itemView.findViewById(R.id.tv_months);
            tv_off_price = itemView.findViewById(R.id.tv_off_price);
            tv_offerStart = itemView.findViewById(R.id.tv_offerStart);
            tv_offerEnd = itemView.findViewById(R.id.tv_offerEnd);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_cutPrice = itemView.findViewById(R.id.tv_cutPrice);
            tv_cutPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            llayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    onItemClick.onClick(lastSelectedPosition);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface OnItemClick {
        void onClick(int position);
    }
}

