package com.iheartradio.infinitefibonacci.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iheartradio.infinitefibonacci.R;
import com.iheartradio.infinitefibonacci.callback.TaskCallback;
import com.iheartradio.infinitefibonacci.listener.InfiniteScrollListener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexanderEmmanuel on 2014-11-02.
 * This is the main activity for the application. It manages the main List view and also contains the
 * FibonacciNumbersList Adapter as well as the AsyncTask which generates the Fibonacci sub sequences
 */
public class FibonacciListActivity extends Activity {


	//This value is the amount of indicies which are within each fibonacci sub sequence.
	// Increasing this number will increase the amount of values which are generated at one time.
	// The value 10 was chosen arbitrarily

	private static final int FIBONACCI_SEQUENCE_STEP        = 10;
	private static final int FIBONACCI_LIST_FOOTER_RESOURCE = R.layout.fibonacci_list_footer;

	private boolean mFooterShown = false;


	private List<BigInteger> mFibonacciNumbersList = new ArrayList<BigInteger>();
	private ListView mFibonacciNumbersListView;
	private View     footerProgressView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fibonacci_list);

		mFibonacciNumbersListView = (ListView) findViewById(R.id.fibonacci_list_view);
		mFibonacciNumbersListView.setAdapter(new FibonacciListAdapter(getBaseContext(), R.layout.fibonacci_list_item, mFibonacciNumbersList));

		mFibonacciNumbersListView.setOnScrollListener(new InfiniteScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				new LoadNewFibonacciSubSequence(
						new TaskCallback() {
							@Override
							public void onCallback(Object params) {
								((ArrayAdapter) ((HeaderViewListAdapter) mFibonacciNumbersListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
								toggleProgressFooter(false);
							}
						}
				).execute();
			}
		});

		new LoadNewFibonacciSubSequence(
				new TaskCallback<List<BigInteger>>() {
					@Override
					public void onCallback(List<BigInteger> fibonacciSubSequence) {
						toggleProgressFooter(false);
					}
				}).execute();



	}

	private void toggleProgressFooter(boolean show) {
		if (show) {
			if (!mFooterShown) {
				LayoutInflater inflater = (LayoutInflater)
						getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				footerProgressView = inflater.inflate(FIBONACCI_LIST_FOOTER_RESOURCE, null);

				mFibonacciNumbersListView.addFooterView(footerProgressView);
				mFooterShown = true;
			}
		} else {
			mFibonacciNumbersListView.removeFooterView(footerProgressView);
		}
	}


	// ------ INNER CLASSES ------

	private class LoadNewFibonacciSubSequence extends AsyncTask<Void, Void, List<BigInteger>> {

		TaskCallback mCallback;

		private LoadNewFibonacciSubSequence(TaskCallback callback) {
			mCallback = callback;
		}

		@Override
		protected void onPreExecute() {
			toggleProgressFooter(true);
		}

		@Override
		protected List<BigInteger> doInBackground(Void... voids) {
			int size = mFibonacciNumbersList.size();
			BigInteger n1;
			BigInteger n2;

			if (size == 0 || size == 1) {
				n1 = BigInteger.ONE;
				n2 = BigInteger.ONE;
			} else {
				n1 = mFibonacciNumbersList.get(size - 2);
				n2 = mFibonacciNumbersList.get(size - 1);
			}

			return generateFibonacciSubSequence(n1, n2, size, FIBONACCI_SEQUENCE_STEP);
		}


		@Override
		protected void onPostExecute(List<BigInteger> fibonacciSubSequence) {
			mFibonacciNumbersList.addAll(fibonacciSubSequence);
			mCallback.onCallback(fibonacciSubSequence);
		}

		private List<BigInteger> generateFibonacciSubSequence(BigInteger n1, BigInteger n2, int start, int size) {
			List<BigInteger> fibonacciSubSequence = new ArrayList<BigInteger>();

			if (start == 0) {
				fibonacciSubSequence.add(BigInteger.ONE);
				fibonacciSubSequence.add(BigInteger.ONE);
				start = 2;
			} else {
				fibonacciSubSequence.add(n1.add(n2));
				fibonacciSubSequence.add(n2.add(n1.add(n2)));
			}

			for (int i = start; i < size; i++) {
				fibonacciSubSequence.add(fibonacciSubSequence.get(fibonacciSubSequence.size() - 1).add(fibonacciSubSequence.get(fibonacciSubSequence.size() - 2)));
			}

			return fibonacciSubSequence;
		}
	}

	// ----- ADAPTERS ------

	private static class ViewHolder {
		TextView fibonacciNumberTextView;
	}

	private class FibonacciListAdapter extends ArrayAdapter {

		private final Context mContext;
		private final int     mResource;

		private FibonacciListAdapter(Context context, int resource, List objects) {
			super(context, resource, objects);
			mContext = context;
			mResource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(mResource, null);

				viewHolder = new ViewHolder();
				viewHolder.fibonacciNumberTextView = (TextView) convertView.findViewById(R.id.fibonacci_list_item_number_text_view);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.fibonacciNumberTextView.setText(mFibonacciNumbersList.get(position).toString());


			return convertView;
		}
	}


}