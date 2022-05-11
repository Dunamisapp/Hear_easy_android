package com.heareasy.common_classes;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.github.nukc.stateview.StateView;
import com.heareasy.R;

/**
 * Created by ather on 12/12/17.
 */

public class MyStateView {

    public interface ProgressClickListener {
        void onRetryClick();

    }


    private ImageView iv_loading;
    private Context context;
    private ProgressClickListener mListener;
    private StateView mStateView;


    public MyStateView(Activity activity, View view) {
        this.context = activity;
        mListener = (ProgressClickListener) activity;
        if (view == null) {
            mStateView = StateView.inject(activity);
        } else {
            mStateView = StateView.inject(view);
        }
        mStateView.setRetryResource(R.layout.view_retry);
        mStateView.setEmptyResource(R.layout.view_empty);
        mStateView.setLoadingResource(R.layout.loading);


        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                mListener.onRetryClick();
            }
        });
    }


    public MyStateView(Fragment fragment, View view) {
        this.context = fragment.getActivity();

        mListener = (ProgressClickListener) fragment;
        if (view == null) {
            mStateView = StateView.inject(fragment.getView());
            mStateView.animate();
        } else {
            mStateView = StateView.inject(view);
        }

//        AVLoadingIndicatorView img = new AVLoadingIndicatorView(context);
//        mStateView.setRetryResource(R.layout.view_retry);
//        mStateView.setEmptyResource(R.layout.view_empty);
//        mStateView.setLoadingResource(R.layout.loading);

        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // iv_loading = inflater.inflate(R.layout.loading, (ViewGroup) mStateView.getParent(), false).findViewById(R.id.iv_loading);
        //   Animation animation = AnimationUtils.loadAnimation(context, R.anim.sequential);
        // iv_loading.setAnimation(animation);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                mStateView.showContent();
                mListener.onRetryClick();
            }
        });
    }

    public MyStateView(Fragment fragment, View view, ProgressClickListener progressClickListener) {
        this.context = fragment.getActivity();
        mListener = progressClickListener;
        if (view == null) {
            mStateView = StateView.inject(fragment.getView());
        } else {
            mStateView = StateView.inject(view);
        }

        mStateView.setRetryResource(R.layout.view_retry);
        mStateView.setEmptyResource(R.layout.view_empty);
        mStateView.setLoadingResource(R.layout.loading);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                mListener.onRetryClick();
            }
        });
    }


    public void showLoading() {
        mStateView.showLoading();
       // View view1= LayoutInflater.from(context).inflate(R.layout.loading,null);
       // SwipeRefreshLayout swipe = view1.findViewById(R.id.swipeToRefresh);

       // swipe.setRefreshing(false);

    }

    public void showRetry() {
        mStateView.showRetry();
    }

    public void showContent() {
        mStateView.showContent();
    }


    public void showEmpty() {
        mStateView.showEmpty();
    }

    public void setEmptyResource(int emptyResource) {
        mStateView.setEmptyResource(emptyResource);
    }

    public void setLoadingResource(int emptyResource) {
        mStateView.setLoadingResource(emptyResource);
    }

    public void setRetryResource(int emptyResource) {
        mStateView.setRetryResource(emptyResource);
    }

}
