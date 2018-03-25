package ir.kushaweb.www.imdbproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_movie;
    Button btn_show;
    TextView txt_title;
    ImageView img_poster;
    TextView txt_director;
    TextView txt_writer;
    TextView txt_year;
    TextView txt_actors;
    TextView txt_runtime;
    TextView txt_country;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindviews();
        progressDialog= new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Please wait");
        Hawk.init(MainActivity.this).build();
    }


    void bindviews() {
        edt_movie = (EditText) findViewById(R.id.edt_movie);
        btn_show = (Button) findViewById(R.id.btn_show);
        txt_title = (TextView) findViewById(R.id.txt_title);
        img_poster = (ImageView) findViewById(R.id.img_poster);
        txt_actors = (TextView) findViewById(R.id.txt_actors);
        txt_director = (TextView) findViewById(R.id.txt_director);
        txt_runtime = (TextView) findViewById(R.id.txt_runtime);
        txt_country = (TextView) findViewById(R.id.txt_country);
        txt_year = (TextView) findViewById(R.id.txt_year);
        txt_writer = (TextView) findViewById(R.id.txt_writer);
        btn_show.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_show) {
            search(edt_movie.getText().toString());
        }
    }

    void search(String word) {
        AsyncHttpClient client = new AsyncHttpClient();
        final String url = "http://www.omdbapi.com/?t="+word+"&apikey=271f7a2a";
        client.get(url, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this,throwable.toString(),Toast.LENGTH_SHORT).show();
                parsResponse((String) Hawk.get(url));
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                parsResponse(responseString);
                addoffline(url,responseString);
            }
        });
    }


    void parsResponse(String serverRespons) {
        try {
            JSONObject serverObject = new JSONObject(serverRespons);
            String director = serverObject.getString("Director");
            String year = serverObject.getString("Year");
            String title = serverObject.getString("Title");
            String country = serverObject.getString("Country");
            String writer = serverObject.getString("Writer");
            String actors = serverObject.getString("Actors");
            String runtime = serverObject.getString("Runtime");
            String posterURL = serverObject.getString("Poster");

            txt_actors.setText(actors);
            txt_runtime.setText(runtime);
            txt_year.setText(year);
            txt_writer.setText(writer);
            txt_title.setText(title);
            txt_director.setText(director);
            txt_country.setText(country);
            Picasso.get().load(posterURL).into(img_poster);


        } catch (Exception e) {

        }
    }
    void addoffline(String key , String value){

        Hawk.put(key,value);

    }
}