package com.example.books;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ProgressBar mLoadingProgress;
    private RecyclerView rvBooks;
    URL bookUrl;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        rvBooks = (RecyclerView) findViewById(R.id.rv_books);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        Intent intent = getIntent();
        String query = intent.getStringExtra("query");

        try {
            if (query == null  || query.isEmpty()) {
                bookUrl = ApiUtil.buildUrl("Programming");
            }
            else {
                bookUrl = new URL(query);
            }
            Log.e("Query_URL",bookUrl.toString());
            new BooksQueryTask().execute(bookUrl);

        }
        catch (Exception e) {
            Log.d("error", e.getMessage());
        }

        //create the layoutManager for the books (linear in this case, scrolling vertically
        LinearLayoutManager booksLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvBooks.setLayoutManager(booksLayoutManager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu,menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchview = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchview.setOnQueryTextListener(this);
        ArrayList<String> recentList=SpUtil.getQueryList(getApplicationContext());
        int itemNum=recentList.size();
        MenuItem recentMenu;
        for (int i=0;i<itemNum;i++)
        {
            recentMenu=menu.add(Menu.NONE,i,Menu.NONE,recentList.get(i));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_advanced_search: {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
            default:{
                int position=item.getItemId()+1;
                String preferenceName=SpUtil.QUERY+String.valueOf(position);
                String query = SpUtil.getPreferenceString(getApplicationContext(),preferenceName);
                String[] prefParams=query.split("\\,");
                String[] queryParams=new String[4];
                for (int i=0;i<prefParams.length;i++)
                {
                    queryParams[i]=prefParams[i];
                }
                bookUrl = ApiUtil.buildURL(
                        (queryParams[0] == null)?"" : queryParams[0],
                        (queryParams[1] == null)?"" : queryParams[1],
                        (queryParams[2] == null)?"" : queryParams[2],
                        (queryParams[3] == null)?"" : queryParams[3]
                );
                new BooksQueryTask().execute(bookUrl);

                return super.onOptionsItemSelected(item);
            }
        }


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try{
            URL url=ApiUtil.buildUrl(query);
            new BooksQueryTask().execute(url);

        }catch (Exception e)
        {
            Log.e("Error",e.toString());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public class BooksQueryTask extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... urls) {

            URL searchUrl = urls[0];
            String result=null;
            try{
                result=ApiUtil.getJSON(searchUrl);
            }catch (Exception e)
            {
                Log.e("Error",e.toString());
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {

            TextView error=(TextView) findViewById(R.id.tv_error);
            mLoadingProgress.setVisibility(View.INVISIBLE);

            if (result==null)
            {
                rvBooks.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
                Log.e("JSON Parse","Parsing failed.");
            }
            else
            {
                rvBooks.setVisibility(View.VISIBLE);
                error.setVisibility(View.INVISIBLE);
            }
            ArrayList<Book> books = ApiUtil.getBooksFromJSON(result);
            String resultString = "";

            BooksAdapter adapter = new BooksAdapter(books);
            rvBooks.setAdapter(adapter);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }

}
