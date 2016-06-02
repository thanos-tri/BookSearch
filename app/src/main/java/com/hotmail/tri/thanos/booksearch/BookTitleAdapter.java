package com.hotmail.tri.thanos.booksearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BookTitleAdapter extends RecyclerView.Adapter<BookTitleAdapter.BookViewHolder>{
    private List<BookTitle> books;

    public class BookViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;
        public TextView fullTitle;
        public TextView authors;
        public TextView editionNumber;

        public BookViewHolder(View v){
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.cover);
            fullTitle = (TextView) v.findViewById(R.id.fullTitle);
            authors = (TextView) v.findViewById(R.id.authors);
            editionNumber = (TextView) v.findViewById(R.id.editionNumber);
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
        if(!current.hasCoverBitmap()) {
            holder.thumbnail.setImageResource(R.drawable.no_cover);
        }
        else{
            holder.thumbnail.setImageBitmap(current.getCoverBitmap());
        }
        holder.fullTitle.setText(current.getFullTitle());
        holder.authors.setText(current.getAuthorsString());
        holder.editionNumber.setText(current.getEditionKeys().size() + " editions");
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
