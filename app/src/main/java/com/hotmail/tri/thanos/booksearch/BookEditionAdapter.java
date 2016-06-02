package com.hotmail.tri.thanos.booksearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BookEditionAdapter extends RecyclerView.Adapter<BookEditionAdapter.BookViewHolder>{
    private List<BookEdition> books;

    public class BookViewHolder extends RecyclerView.ViewHolder{
        public ImageView cover;
        public TextView fullTitle;
        public TextView authors;

        public BookViewHolder(View v){
            super(v);
            cover = (ImageView) v.findViewById(R.id.cover);
            fullTitle = (TextView) v.findViewById(R.id.fullTitle);
            authors = (TextView) v.findViewById(R.id.authors);
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
        Book current = books.get(position);
        if(current.hasCoverBitmap())
            holder.cover.setImageBitmap(current.getCoverBitmap());
        else
            holder.cover.setImageResource(R.drawable.no_cover);
        holder.fullTitle.setText(current.getFullTitle());
        holder.authors.setText(current.getAuthorsString());
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
