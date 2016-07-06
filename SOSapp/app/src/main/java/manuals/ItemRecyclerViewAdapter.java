package manuals;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import manuals.ItemFragment.OnListFragmentInteractionListener;

import com.example.root.sosapp.R;

import manuals.doc.DocContent.Document;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private final Document mValues [];
    private final OnListFragmentInteractionListener mListener;

    public ItemRecyclerViewAdapter(Document items[], OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues[position];
        holder.mContentView.setText(mValues[position].content);
        holder.mSummaryView.setText(mValues[position].summary);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mContentView;
        public final TextView mSummaryView;
        public Document mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSummaryView = (TextView) view.findViewById(R.id.summary);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
