package com.example.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //declare activity variables
    TextView first;
    EditText second;
    TextView third;
    String firstly;
    String secondly;
    String thirdly;
    Button getLibz;
    ProgressDialog progressDialog;
    String apiUrl = "http://madlibz.herokuapp.com/api/random?minlength=20&maxlength=30";
    private MadLib madLib;

    ListView theList;
    MyOwnAdapter myOwnAdapter;
    ArrayList<String> blanksToFill = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get references for views
        first = (TextView)findViewById(R.id.first);
        //second = (EditText)findViewById(R.id.second);
        //third = (TextView)findViewById(R.id.third);
        getLibz = (Button)findViewById(R.id.button1);
        theList = (ListView)findViewById(R.id.the_list);

        myOwnAdapter = new MyOwnAdapter();
        theList.setAdapter(myOwnAdapter);
        //if button clicked
        getLibz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //run async task
                MadQuerious req = new MadQuerious();
                req.execute();
            }
        });
    }

    /**
     * Asynctask used to query Madlibz API. <TypeOfVariableArgParams - params sent to task upon execution
     * , ProgressValue - progress units published in background computation, ResultValue -
     * type of result of background computation
     */
    class MadQuerious extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //display progress dialog to user
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait for your MadLibz!");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }


        @Override
        protected String doInBackground(String... args) {
            String current = "";
            try {
                //code to run in background
                //result will be passed to onPostExecute
                //Connect to Website, parse JSON files for information
                URL url;
                HttpURLConnection urlConnection = null;
                try    {
                    url = new URL(apiUrl);
                    //open connection
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream input = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(input);

                    int data = isw.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isw.read();
                        System.out.print(current);
                    }
                    return current;

                    } catch (Exception e) {
                         e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                         }
            }
        }catch(Exception e) {
            e.printStackTrace();
            return "Exception:" + e.getMessage();
        }
        return current;
    }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //updates UI
            progressDialog.dismiss();
            //JSON parsing
            try{
                JSONObject jO = new JSONObject(s);
                firstly = jO.getString("title");
                first.setText("Title:" +firstly);
                JSONArray blanks = jO.getJSONArray("blanks");

                //List<String> blanksList = new ArrayList<String>();
                for(int i=0; i < blanks.length();i++){
                    blanksToFill.add(blanks.getString(i));
                    myOwnAdapter.notifyDataSetChanged();
                }

                JSONArray value = jO.getJSONArray("value");

                List<String> valueList = new ArrayList<String>();
                for(int i=0; i < value.length();i++){
                    valueList.add(value.getString(i));
                }

                madLib = new MadLib(firstly, blanksToFill, valueList);
                //secondly = blanks.get(0).toString();
                //second.setText(secondly);

            }catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected class MyOwnAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //return count of images saved in database
            return blanksToFill.size();
        }

        @Override
        public String getItem(int i) {
            //return image at clicked index
            return blanksToFill.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 1;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View newView = getLayoutInflater().inflate(R.layout.blank_row, viewGroup, false);
            String thisRow = getItem(i);

            //get textviews from rows
            EditText blankable = (EditText)newView.findViewById(R.id.blankToReplace);
            TextView blankLabel = (TextView) newView.findViewById(R.id.blankLabel);
            //TextView rowId = (TextView)newView.findViewById(R.id.row_id);

            blankable.setText( thisRow);
            blankLabel.setText( thisRow);
            //rowId.setText("ID: " + thisRow.getID());

            //return the row
            return newView;
        }
    }
}

