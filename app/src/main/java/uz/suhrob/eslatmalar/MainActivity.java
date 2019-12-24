package uz.suhrob.eslatmalar;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.List;

import uz.suhrob.eslatmalar.adapter.EventAdapter;
import uz.suhrob.eslatmalar.database.EventDBHelper;
import uz.suhrob.eslatmalar.models.Event;

public class MainActivity extends AppCompatActivity {

    SpaceNavigationView spaceNavigationView;
    ConstraintLayout homeWindow, settingsWindow;
    RecyclerView recyclerView;

    List<Event> eventList;

    EventDBHelper eventDbHelper;

    EventAdapter eventAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeWindow = findViewById(R.id.home_window);
        settingsWindow = findViewById(R.id.settings_window);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        eventDbHelper = new EventDBHelper(getApplicationContext());

        //  BottomNavigationBar
        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("SETTINGS", R.drawable.ic_settings_black_24dp));
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                startActivity(new Intent(getApplicationContext(), AddEventActivity.class));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    homeWindow.setVisibility(View.VISIBLE);
                    settingsWindow.setVisibility(View.INVISIBLE);
                } else if (itemIndex == 1) {
                    homeWindow.setVisibility(View.INVISIBLE);
                    settingsWindow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {}
        });



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = null;
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    newText = newText.trim();
                    eventAdapter.getFilter().filter(newText);
                    return false;
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    loadEvents();
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void loadEvents() {
        eventList = eventDbHelper.getAll();
        eventAdapter = new EventAdapter(getApplicationContext(), eventList);
        recyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();
    }

}
