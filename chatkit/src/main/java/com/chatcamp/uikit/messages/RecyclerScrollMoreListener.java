package com.chatcamp.uikit.messages;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public class RecyclerScrollMoreListener
        extends RecyclerView.OnScrollListener {

    private OnLoadMoreListener loadMoreListener;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private boolean isLoadable = true;

    private RecyclerView.LayoutManager mLayoutManager;

    public RecyclerScrollMoreListener(LinearLayoutManager layoutManager, OnLoadMoreListener loadMoreListener) {
        this.mLayoutManager = layoutManager;
        this.loadMoreListener = loadMoreListener;
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if(!isLoadable) {
            return;
        }
        if (loadMoreListener != null) {
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            } else if (mLayoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            } else if (mLayoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }

            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = 0;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }

            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            int visibleThreshold = 5;
            //((lastVisibleItemPosition + 1) != totalItemCount) to check if the number of items are
            // less than what can be accomodated on the screen, if they are less we dont have to call
            // onLoadMore().
            if (!loading && ((lastVisibleItemPosition + 1) != totalItemCount) &&
                    (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                loadMoreListener.onLoadMore(currentPage, totalItemCount);
                loading = true;
            }
        }
    }

    public void resetLoading() {
        loading = true;
        currentPage = 0;
        previousTotalItemCount = 0;
        isLoadable = true;
    }

    public void stopLoading() {
        isLoadable = false;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int page, int total);
    }
}
