package com.example.jack.mylib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class BookAdapter extends BaseAdapter {

    private Context context;
    private List list;

    public BookAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Book book = (Book) list.get(position);

        View view = View.inflate(context, R.layout.activity_book_item, null);

        TextView tv_book_title = view.findViewById(R.id.tv_book_title);
        TextView tv_book_author = view.findViewById(R.id.tv_book_author);
        TextView tv_book_publisher = view.findViewById(R.id.tv_book_publisher);

        tv_book_title.setText(book.getTitle());
        tv_book_author.setText(book.getAuthor());
        tv_book_publisher.setText(book.getPublisher());

        ImageView iv_book_image = view.findViewById(R.id.iv_book_image);
        Glide.with(context)
                .load(book.getImage())
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_background))
                .into(iv_book_image);

        return view;
    }
}
