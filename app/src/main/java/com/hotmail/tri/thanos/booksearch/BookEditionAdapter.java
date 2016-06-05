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

public class BookEditionAdapter extends RecyclerView.Adapter<BookEditionAdapter.BookViewHolder>{
    private List<BookEdition> books;

    public class BookViewHolder extends RecyclerView.ViewHolder{
        public ImageView cover;
        public TextView fullTitle;
        public TextView authors;
        public TextView bookType;

        public FrameLayout loadingLayout;
        public RelativeLayout rowLayout;

        public ProgressBar loadCoverSpinner;

        public BookViewHolder(View v){
            super(v);
            cover = (ImageView) v.findViewById(R.id.editionsCover);
            fullTitle = (TextView) v.findViewById(R.id.editionsFullTitle);
            authors = (TextView) v.findViewById(R.id.editionsAuthors);
            bookType = (TextView) v.findViewById(R.id.editionsBookType);

            rowLayout = (RelativeLayout) v.findViewById(R.id.editionsRowLayout);
            rowLayout.setVisibility(View.VISIBLE);

            loadingLayout = (FrameLayout) v.findViewById(R.id.editionsLoadMoreLayout);
            loadingLayout.setVisibility(View.GONE);

            loadCoverSpinner = (ProgressBar) v.findViewById(R.id.editionsLoadCoverSpinner);
            loadCoverSpinner.setVisibility(View.GONE);

            ProgressBar loadMoreSpinner = (ProgressBar) v.findViewById(R.id.editionsLoadMoreSpinner);
            loadMoreSpinner.setVisibility(View.VISIBLE);
        }
    }

    public BookEditionAdapter(List<BookEdition> books){
        this.books = books;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.editions_list_row, parent, false);

        BookViewHolder bookViewHolder = new BookViewHolder(itemView);
        return bookViewHolder;
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        BookEdition current = books.get(position);
        if(current == null){
            holder.loadingLayout.setVisibility(View.VISIBLE);
            holder.rowLayout.setVisibility(View.GONE);
            return;
        }
        holder.loadingLayout.setVisibility(View.GONE);
        holder.rowLayout.setVisibility(View.VISIBLE);

        if(!current.hasCoverURL()){
            holder.cover.setImageResource(R.drawable.no_cover);
        }
        else{
            if(current.hasCoverBitmap()){
                holder.cover.setImageBitmap(current.getCoverBitmap());
                holder.loadCoverSpinner.setVisibility(View.GONE);
                holder.cover.setVisibility(View.VISIBLE);
            }
            else{
                holder.cover.setVisibility(View.GONE);
                holder.loadCoverSpinner.setVisibility(View.VISIBLE);
            }
        }

        holder.fullTitle.setText(current.getFullTitle());
        holder.authors.setText(current.getAuthorsString());
        if(current.getNumberOfPages() <= 0)
            holder.bookType.setText(R.string.ebook_string);
        else
            holder.bookType.setText(R.string.paper_book_string);
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
