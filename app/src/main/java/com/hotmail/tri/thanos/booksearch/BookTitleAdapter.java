package com.hotmail.tri.thanos.booksearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class BookTitleAdapter extends RecyclerView.Adapter<BookTitleAdapter.BookViewHolder>{
    private List<BookTitle> books;

    public class BookViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;
        public TextView fullTitle;
        public TextView authors;
        public TextView editionNumber;

        public FrameLayout loading;
        public RelativeLayout rowLayout;

        public ProgressBar loadCoverSpinner;

        public BookViewHolder(View v){
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.cover);
            fullTitle = (TextView) v.findViewById(R.id.fullTitle);
            authors = (TextView) v.findViewById(R.id.authors);
            editionNumber = (TextView) v.findViewById(R.id.editionNumber);

            rowLayout = (RelativeLayout) v.findViewById(R.id.rowLayout);
            rowLayout.setVisibility(View.VISIBLE);

            loading = (FrameLayout) v.findViewById(R.id.loadMoreLayout);
            loading.setVisibility(View.GONE);

            loadCoverSpinner = (ProgressBar) v.findViewById(R.id.loadCoverSpinner);
            loadCoverSpinner.setVisibility(View.GONE);

            ProgressBar loadMoreSpinner = (ProgressBar) v.findViewById(R.id.loadMoreSpinner);
            loadMoreSpinner.setVisibility(View.VISIBLE);
        }
    }

    public BookTitleAdapter(List<BookTitle> books){
        this.books = books;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_row, parent, false);

        BookViewHolder bookViewHolder = new BookViewHolder(itemView);
        return bookViewHolder;
    }

    @Override
    public void onBindViewHolder(final BookViewHolder holder, int position) {
        final BookTitle current = books.get(position);
        if(current == null){
            holder.loading.setVisibility(View.VISIBLE);
            holder.rowLayout.setVisibility(View.GONE);
            return;
        }
        holder.loading.setVisibility(View.GONE);
        holder.rowLayout.setVisibility(View.VISIBLE);

        if(!current.hasCoverBitmap()) {
            if(current.hasCoverId() || current.hasCoverOLID()) {
                holder.loadCoverSpinner.setVisibility(View.VISIBLE);
                holder.thumbnail.setVisibility(View.GONE);
            }
            else
                holder.thumbnail.setImageResource(R.drawable.no_cover);
        }
        else{
            holder.loadCoverSpinner.setVisibility(View.GONE);
            holder.thumbnail.setImageBitmap(current.getCoverBitmap());
            holder.thumbnail.setVisibility(View.VISIBLE);
        }
        holder.fullTitle.setText(current.getFullTitle());
        holder.authors.setText(current.getAuthorsString());
        int size = current.getEditionKeys().size();
        if(size == 1)
            holder.editionNumber.setText(size + " edition");
        else
            holder.editionNumber.setText(size + " editions");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
