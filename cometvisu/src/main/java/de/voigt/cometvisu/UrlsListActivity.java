package de.voigt.cometvisu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class UrlsListActivity extends Activity {

    private final Activity activity = this;

    private static final String VISU_KEY = "VISU_URLS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urls_view);

        final ListView lv = (ListView)findViewById(R.id.urlListView);
        final Button addButton = (Button)findViewById(R.id.addButton);
        final Button backButton = (Button)findViewById(R.id.backButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set an EditText view to get user input
                final EditText input = new EditText(activity);
                input.setText("http://");

                new AlertDialog.Builder(activity)
                        .setTitle("Neue URL")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = input.getText().toString();

                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE);

                                Set<String> urls = sharedPref.getStringSet(VISU_KEY, new HashSet<String>());

                                SharedPreferences.Editor editor = sharedPref.edit();

                                urls.add(value);

                                editor.putStringSet(VISU_KEY, urls);
                                editor.commit();

                                reload(lv);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();
               }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        reload(lv);


    }

    private void reload(ListView listView){

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE);

        Set<String> urls = sharedPref.getStringSet(VISU_KEY, new HashSet<String>());

        List<String> list = Arrays.asList(urls.toArray(new String[urls.size()]));

        ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

    }

}
