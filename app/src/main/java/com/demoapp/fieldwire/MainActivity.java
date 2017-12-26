package com.demoapp.fieldwire;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchFilter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    SearchView searchView;
    List<SearchItem> searchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);

        SearchHistoryTable mHistoryDatabase = new SearchHistoryTable(this);

        if (searchView != null) {
            searchView.setVersionMargins(SearchView.VersionMargins.TOOLBAR_SMALL);
            searchView.setHint(getResources().getString(R.string.search_keyword));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                  //  mHistoryDatabase.addItem(new SearchItem(query));
                    searchList.add(new SearchItem(query));
                    searchView.close(false);

                    initFragment(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            SearchAdapter searchAdapter = new SearchAdapter(this, searchList);
            searchAdapter.setOnSearchItemClickListener(new SearchAdapter.OnSearchItemClickListener() {
                @Override
                public void onSearchItemClick(View view, int position, String text) {
                 //   mHistoryDatabase.addItem(new SearchItem(text));
                    searchList.add(new SearchItem(text));
                    initFragment(text);
                    searchView.close(false);
                }
            });
            searchView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();

        }
    }

    private void initFragment(String query) {
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("params", query);

        Fragment fragment = new ListFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
