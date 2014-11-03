package com.iheartradio.infinitefibonacci.listener;

import android.widget.AbsListView;

/**
 * Created by AlexanderEmmanuel on 2014-11-02.
 * This is a custom {@link android.widget.AbsListView.OnScrollListener} which facilitates infinite
 * scrolling for listViews and GridViews.
 */
public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
	private int     mVisibleThreshold = 5;
	private int     mCurrentPage      = 0;
	private int     mStartingPage     = 0;
	private int     mTotalItemCount   = 0;
	private boolean mLoading          = true;

	public InfiniteScrollListener() {
	}

	public InfiniteScrollListener(int visibleThreshold) {
		mVisibleThreshold = visibleThreshold;
	}

	public InfiniteScrollListener(int visibleThreshold, int startPage) {
		mVisibleThreshold = visibleThreshold;
		mStartingPage = startPage;
		mCurrentPage = startPage;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (totalItemCount < mTotalItemCount) {
			mCurrentPage = mStartingPage;
			mTotalItemCount = totalItemCount;
			if (totalItemCount == 0) {
				mLoading = true;
			}
		}

		if (mLoading && (totalItemCount > mTotalItemCount)) {
			mLoading = false;
			mTotalItemCount = totalItemCount;
			mCurrentPage++;
		}

		if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
			onLoadMore(mCurrentPage + 1, totalItemCount);
			mLoading = true;
		}
	}

	public abstract void onLoadMore(int page, int totalItemsCount);

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// LEAVE EMPTY
	}
}
