package com.example.books;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText etTitle=(EditText)findViewById(R.id.etTitle);
        final EditText etPublisher=(EditText)findViewById(R.id.etPublisher);
        final EditText etISBN=(EditText)findViewById(R.id.etISBN);
        final EditText etAuthor=(EditText)findViewById(R.id.etAuthor);
        final Button etButton=(Button) findViewById(R.id.btnSearch);

        etButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString().trim();
                String author = etAuthor.getText().toString().trim();
                String publisher=etPublisher.getText().toString().trim();
                String isbn=etISBN.getText().toString().trim();

                if(title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty())
                {
                    String msg=getString(R.string.no_search_data);
                    Toast.makeText(view.getContext(),msg,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    URL queryUrl = ApiUtil.buildURL(title,author,isbn,publisher);

                    Context c=getApplicationContext();
                    int position=SpUtil.getPreferenceInt(c,SpUtil.POSITION);
                    if(position==0 || position==5)
                    {
                        position=1;
                    }
                    else{
                        position++;
                    }

                    String key=SpUtil.QUERY+String.valueOf(position);
                    String value=title+", "+author+", "+publisher+", "+isbn;
                    SpUtil.setPreferenceString(c,key,value);
                    SpUtil.setPreferenceInt(c,SpUtil.POSITION,position);

                    Intent intent = new Intent(getApplicationContext(),BookListActivity.class);
                    Log.e("URL",queryUrl.toString());
                    intent.putExtra("query",queryUrl.toString());

                    startActivity(intent);
                }
            }
        });


    }
}
