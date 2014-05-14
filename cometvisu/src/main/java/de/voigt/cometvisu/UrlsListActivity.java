package de.voigt.cometvisu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UrlsListActivity extends Activity {

    private static final String VISU_KEY = "VISU_URLS";
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urls_view);

        final ListView lv = (ListView) findViewById(R.id.urlListView);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final Button backButton = (Button) findViewById(R.id.backButton);

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

                                String newUrl = input.getText().toString();
                                addUrlToPreferences(newUrl);

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

    private void addUrlToPreferences(String newUrl) {
        Set<String> urls = loadUrlStringsFromSharedPreferences();
        urls.add(newUrl);

        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(VISU_KEY);
        boolean deleted = editor.commit();
        Log.i("sharedprefs", "deleted key: " + deleted);

        Set<String> urlsEmpty = sharedPref.getStringSet(VISU_KEY, new HashSet<String>());
        Log.i("sharedprefs", "urls empty? " + urlsEmpty);

        editor.putStringSet(VISU_KEY, urls);
        Log.i("sharedprefs", "urls to be saved? " + urls);
        boolean committed = editor.commit();
        Log.i("sharedprefs", "commit successful? " + committed);

    }

    private void reload(ListView listView) {

        Set<String> urls = loadUrlStringsFromSharedPreferences();

        List<String> list = Arrays.asList(urls.toArray(new String[urls.size()]));

        ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

    }

    private Set<String> loadUrlStringsFromSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences();

        return sharedPref.getStringSet(VISU_KEY, new HashSet<String>());
    }

    private SharedPreferences getSharedPreferences() {
        return getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE);
    }

}
