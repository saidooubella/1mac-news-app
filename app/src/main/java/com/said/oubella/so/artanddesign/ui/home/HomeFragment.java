package com.said.oubella.so.artanddesign.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.Hold;
import com.said.oubella.so.artanddesign.AppContainer;
import com.said.oubella.so.artanddesign.ArtAndDesignApp;
import com.said.oubella.so.artanddesign.R;
import com.said.oubella.so.artanddesign.adapters.ArticlesAdapter;
import com.said.oubella.so.artanddesign.databinding.ArticlesViewContentBinding;
import com.said.oubella.so.artanddesign.databinding.FragmentHomeBinding;
import com.said.oubella.so.artanddesign.helpers.Cancellable;
import com.said.oubella.so.artanddesign.helpers.ItemDecorator;
import com.said.oubella.so.artanddesign.loaders.ArticlesLoader;
import com.said.oubella.so.artanddesign.models.Article;
import com.said.oubella.so.artanddesign.preferences.AppPreferences;
import com.said.oubella.so.artanddesign.repository.ArticlesRepository;
import com.said.oubella.so.artanddesign.state.FetchingResult;

import java.util.List;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<FetchingResult> {

    private ArticlesViewContentBinding contentBinding;
    private FragmentHomeBinding homeBinding;

    private ArticlesRepository articlesRepository;
    private ArticlesAdapter articlesAdapter;
    private LoaderManager loader;

    private Cancellable callbackCancellable;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentBinding = homeBinding.articlesViews;
        navController = Navigation.findNavController(view);

        final AppContainer container = ((ArtAndDesignApp) requireActivity().getApplication()).container();
        final AppPreferences preferences = container.preferences();
        articlesRepository = container.articlesRepository();

        articlesAdapter = new ArticlesAdapter(Glide.with(this), article -> {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(article.getPostUrl()));
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        contentBinding.articlesList.addItemDecoration(new ItemDecorator(16));
        contentBinding.articlesList.setAdapter(articlesAdapter);

        loader = LoaderManager.getInstance(this);

        // Passing args here isn't required. Check SearchFragment where i've passed them :).
        loader.initLoader(0, null, this);

        contentBinding.swipeRefresh.setOnRefreshListener(() -> {
            // Passing args here isn't required. Check SearchFragment where i've passed them :).
            loader.restartLoader(0, null, this);
        });

        // I have provided a cache so even if there is no internet the user will be able to see
        // the last content the app loaded.

        // This load existing cached articles or new refreshed articles collected in case the
        // triggered Loader retrieved some new data
        articlesRepository.articles().observe(getViewLifecycleOwner(), this::showArticles);

        callbackCancellable = container.connectivity().addListener(isConnected -> {
            showSnackbar(isConnected ? R.string.connected_message : R.string.no_connection_message);
            if (isConnected && isListEmpty()) loader.restartLoader(0, null, this);
        });

        homeBinding.dayNightButton.setOnClickListener(v -> preferences.toggleNightTheme());

        homeBinding.searchButton.setOnClickListener(v -> navigateToSearchFragment());
    }

    private void navigateToSearchFragment() {

        setExitTransition(new Hold());
        setReenterTransition(new Hold());

        final int destination = R.id.action_homeFragment_to_searchFragment;
        final FragmentNavigator.Extras.Builder extrasBuilder = new FragmentNavigator.Extras.Builder()
                .addSharedElement(homeBinding.searchButton, homeBinding.searchButton.getTransitionName());

        navController.navigate(destination, null, null, extrasBuilder.build());
    }

    private void startLoading() {
        if (isListEmpty()) contentBinding.articlesList.setVisibility(View.GONE);
        contentBinding.statusIndicator.setVisibility(View.GONE);
        contentBinding.swipeRefresh.setRefreshing(true);
    }

    private void showError(@NonNull FetchingResult error) {
        contentBinding.indicatorImage.setImageResource(error.icon());
        contentBinding.indicatorText.setText(error.message());
        contentBinding.articlesList.setVisibility(View.GONE);
        contentBinding.statusIndicator.setVisibility(View.VISIBLE);
    }

    private void showArticles(@NonNull List<Article> result) {
        if (!result.isEmpty()) {
            articlesAdapter.submitList(result);
            contentBinding.articlesList.setVisibility(View.VISIBLE);
            contentBinding.statusIndicator.setVisibility(View.GONE);
        }
    }

    private void showSnackbar(@StringRes int message) {
        Snackbar.make(homeBinding.coordinatorLayout, message, Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
    }

    private boolean isListEmpty() {
        return articlesAdapter.getCurrentList().isEmpty();
    }

    @NonNull
    @Override
    public Loader<FetchingResult> onCreateLoader(int id, @Nullable Bundle args) {
        startLoading();
        return new ArticlesLoader(requireActivity(), articlesRepository).init();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<FetchingResult> loader, FetchingResult data) {
        contentBinding.swipeRefresh.setRefreshing(false);
        if (data != FetchingResult.OK && isListEmpty()) {
            showError(data);
        } else if (data != FetchingResult.OK) {
            showSnackbar(data.message());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<FetchingResult> loader) {
        startLoading();
    }

    @Override
    public void onDestroyView() {
        callbackCancellable.cancel();
        super.onDestroyView();
    }
}
