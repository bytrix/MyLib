package com.example.jack.mylib;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private class LoadBookDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String url = strings[0];

            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();

                String jsonData = response.body().string();

                return jsonData;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);

//            Toast.makeText(MainActivity.this, jsonData, Toast.LENGTH_SHORT).show();
            JSONArray array = (JSONArray) JSON.parse(jsonData);
            List<Book> books = new ArrayList<>();
            for(int i = 0; i < array.size(); i++) {
                JSONObject item = (JSONObject) array.get(i);
                Book book = new Book();
                book.setTitle(item.getString("title"));
                book.setAuthor(item.getString("author"));
                book.setPublisher(item.getString("publisher"));
                book.setImage(item.getString("image"));
                books.add(book);
            }

            ListAdapter adapter = new BookAdapter(MainActivity.this, books);
            lv_book.setAdapter(adapter);

        }
    }

    private class AddBookTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String url = strings[0];
                String isbn = strings[1];
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        "{\"isbn\":"+isbn+"}"
                );
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            JSONObject data = (JSONObject) JSON.parse(jsonData);
            String message = data.getString("message");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

            LoadBookDataTask loadBookDataTask = new LoadBookDataTask();
            loadBookDataTask.execute(API_GET_BOOK);
        }
    }

    private static final String API_GET_BOOK = "http://115.28.168.47:3000/book/get";
    private static final String API_ADD_BOOK = "http://115.28.168.47:3000/book/add";

    private ListView lv_book;
    private Button btn_add;
    private static final int SCAN_OK = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_book = findViewById(R.id.lv_book);
        btn_add = findViewById(R.id.btn_scan);

        LoadBookDataTask loadBookDataTask = new LoadBookDataTask();
        loadBookDataTask.execute(API_GET_BOOK);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivityForResult(intent, SCAN_OK);
            }
        });

//        ListAdapter adapter = new BookAdapter(MainActivity.this);
//        lv_book.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case SCAN_OK:
                String result = data.getStringExtra("RESULT");
                if(result != null) {
                    AddBookTask addBookTask = new AddBookTask();
                    addBookTask.execute(API_ADD_BOOK, result);
                    Toast.makeText(MainActivity.this, "ISBN: " + result, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
