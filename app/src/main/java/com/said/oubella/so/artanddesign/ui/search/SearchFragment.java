package com.said.oubella.so.artanddesign.ui.search;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialContainerTransform;
import com.said.oubella.so.artanddesign.AppContainer;
import com.said.oubella.so.artanddesign.ArtAndDesignApp;
import com.said.oubella.so.artanddesign.R;
import com.said.oubella.so.artanddesign.adapters.ArticlesAdapter;
import com.said.oubella.so.artanddesign.connectivity.Connectivity;
import com.said.oubella.so.artanddesign.databinding.ArticlesViewContentBinding;
import com.said.oubella.so.artanddesign.databinding.FilterDialogLayoutBinding;
import com.said.oubella.so.artanddesign.databinding.FragmentSearchBinding;
import com.said.oubella.so.artanddesign.helpers.Cancellable;
import com.said.oubella.so.artanddesign.helpers.ItemDecorator;
import com.said.oubella.so.artanddesign.loaders.ArticlesSearchLoader;
import com.said.oubella.so.artanddesign.network.Order;
import com.said.oubella.so.artanddesign.network.RequestUrlBuilder;
import com.said.oubella.so.artanddesign.repository.ArticlesRepository;
import com.said.oubella.so.artanddesign.state.SearchResult;
import com.said.oubella.so.artanddesign.ui.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public final class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<SearchResult> {

    private static final String URL_EXTRA = "requestUrl";

    private static final String START_DATE_KEY = "startDate";
    private static final String END_DATE_KEY = "endDate";
    private static final String ORDER_KEY = "order";
    private static final String QUERY_KEY = "query";

    private ArticlesViewContentBinding contentBinding;
    private FragmentSearchBinding searchBinding;

    private ArticlesRepository articlesRepository;
    private ArticlesAdapter articlesAdapter;

    private InputMethodManager inputMethodManager;
    private Cancellable callbackCancellable;
    private Connectivity connectivity;
    private LoaderManager loader;

    private Date startDate;
    private Date endDate;
    private String query;
    private Order order;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MaterialContainerTransform enterTransition = new MaterialContainerTransform();
        enterTransition.setContainerColor(ContextCompat.getColor(requireActivity(), R.color.colorBackground));
        enterTransition.setStartContainerColor(ContextCompat.getColor(requireActivity(), R.color.colorSecondary));
        enterTransition.setEndContainerColor(ContextCompat.getColor(requireActivity(), R.color.colorBackground));
        enterTransition.setScrimColor(Color.TRANSPARENT);
        setSharedElementEnterTransition(enterTransition);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        return searchBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentBinding = searchBinding.articlesViews;
        restoreSavedState(savedInstanceState);

        final AppContainer container = ((ArtAndDesignApp) requireActivity().getApplication()).container();
        inputMethodManager = container.inputMethodManager();
        articlesRepository = container.articlesRepository();
        connectivity = container.connectivity();

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
        loader.initLoader(0, getLoaderArgs(), this);

        initViewsCallbacks();
    }

    private void initViewsCallbacks() {

        contentBinding.swipeRefresh.setOnRefreshListener(() -> loader.restartLoader(0, getLoaderArgs(), this));

        searchBinding.clearButton.setOnClickListener(v -> searchBinding.searchField.setText(null));

        searchBinding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        searchBinding.searchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                final View decorView = requireActivity().getWindow().getDecorView();
                inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
                query = Objects.requireNonNull(searchBinding.searchField.getText()).toString();
                loader.restartLoader(0, getLoaderArgs(), this);
                return true;
            }
            return false;
        });

        searchBinding.searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final boolean isTextEmpty = s.length() <= 0;
                searchBinding.clearButton.setVisibility(isTextEmpty ? View.GONE : View.VISIBLE);
                if (isTextEmpty) showState(SearchResult.Other.NONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBinding.filtersButton.setOnClickListener(v -> {

            final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            FilterDialogLayoutBinding binding = FilterDialogLayoutBinding.inflate(getLayoutInflater());
            builder.setTitle(R.string.apply_filters_title);
            builder.setView(binding.getRoot());

            // Converted to single element array to have the ability to access 'em from lambdas
            final Order[] localOrder = {order};
            final Date[] localStartDate = {startDate};
            final Date[] localEndDate = {endDate};

            binding.articlesOrder.check(
                    localOrder[0] == Order.RELEVANCE ? R.id.relevance_chip_order
                            : localOrder[0] == Order.NEWEST ? R.id.newest_chip_order
                            : R.id.oldest_chip_order
            );

            binding.articlesOrder.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.relevance_chip_order: localOrder[0] = Order.RELEVANCE; break;
                    case R.id.newest_chip_order: localOrder[0] = Order.NEWEST; break;
                    case R.id.oldest_chip_order: localOrder[0] = Order.OLDEST; break;
                }
            });

            binding.selectRangeDateButton.setOnClickListener(startDateButton -> {

                final MaterialDatePicker.Builder<Pair<Long, Long>> pickerBuilder = MaterialDatePicker.Builder.dateRangePicker();
                pickerBuilder.setTitleText(R.string.pick_date_title);
                pickerBuilder.setSelection(getSelectionDatePair());

                final MaterialDatePicker<Pair<Long, Long>> datePicker = pickerBuilder.build();
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    localStartDate[0] = new Date(selection.first != null ? selection.first : 0);
                    localEndDate[0] = new Date(selection.second != null ? selection.second : 0);
                });

                datePicker.show(getChildFragmentManager(), datePicker.getTag());
            });

            builder.setPositiveButton(R.string.filter_label, (dialog, which) -> {
                order = localOrder[0];
                startDate = localStartDate[0];
                endDate = localEndDate[0];
                if (isQueryNotEmpty()) {
                    loader.restartLoader(0, getLoaderArgs(), this);
                }
                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.cancel_label, (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        callbackCancellable = connectivity.addListener(isConnected -> {
            showSnackbar(isConnected ? R.string.connected_message : R.string.no_connection_message);
            if (isConnected && isQueryNotEmpty() && isListEmpty()) {
                loader.restartLoader(0, getLoaderArgs(), this);
            }
        });
    }

    private void startLoading() {
        if (isListEmpty()) contentBinding.articlesList.setVisibility(View.GONE);
        contentBinding.statusIndicator.setVisibility(View.GONE);
        contentBinding.swipeRefresh.setRefreshing(true);
    }

    private void showState(@NonNull SearchResult.Other otherState) {
        contentBinding.indicatorImage.setImageResource(otherState.icon());
        contentBinding.indicatorText.setText(otherState.message());
        contentBinding.articlesList.setVisibility(View.GONE);
        contentBinding.statusIndicator.setVisibility(View.VISIBLE);
    }

    private void showArticles(@NonNull SearchResult.Result result) {
        articlesAdapter.submitList(result.articles());
        contentBinding.articlesList.setVisibility(View.VISIBLE);
        contentBinding.statusIndicator.setVisibility(View.GONE);
    }

    private void showSnackbar(@StringRes int message) {
        Snackbar.make(searchBinding.coordinatorLayout, message, Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
    }

    private void restoreSavedState(@Nullable Bundle bundle) {

        final long startTime = bundle != null ? bundle.getLong(START_DATE_KEY) : 0L;
        final long endTime = bundle != null ? bundle.getLong(END_DATE_KEY) : 0L;

        startDate = startTime != 0L ? new Date(startTime) : null;
        endDate = endTime != 0L ? new Date(endTime) : null;

        order = bundle != null ? Order.valueOf(bundle.getString(ORDER_KEY)) : Order.RELEVANCE;
        query = bundle != null ? bundle.getString(QUERY_KEY) : "";
    }

    private Pair<Long, Long> getSelectionDatePair() {
        final long timeNow = getTimeNowInMillis();
        return new Pair<>(
                startDate == null ? timeNow : startDate.getTime(),
                endDate == null ? timeNow : endDate.getTime()
        );
    }

    private boolean isQueryNotEmpty() {
        return !TextUtils.isEmpty(query);
    }

    private Bundle getLoaderArgs() {

        final Bundle args = new Bundle();

        final RequestUrlBuilder requestUrlBuilder = new RequestUrlBuilder()
                .setFromDate(startDate)
                .setToDate(endDate)
                .setOrderBy(order)
                .setQuery(query)
                .nullifyWhenQueryIsEmpty();

        args.putString(URL_EXTRA, requestUrlBuilder.build());

        return args;
    }

    private boolean isListEmpty() {
        return articlesAdapter.getCurrentList().isEmpty();
    }

    @NonNull
    @Override
    public Loader<SearchResult> onCreateLoader(int id, @Nullable Bundle args) {
        startLoading();
        final Bundle argsBundle = Objects.requireNonNull(args);
        final String url = argsBundle.getString(URL_EXTRA);
        return new ArticlesSearchLoader(requireActivity(), url, articlesRepository).init();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<SearchResult> loader, SearchResult data) {
        contentBinding.swipeRefresh.setRefreshing(false);
        if (data instanceof SearchResult.Result) {
            showArticles((SearchResult.Result) data);
        } else if (data instanceof SearchResult.Other && isListEmpty()) {
            showState((SearchResult.Other) data);
        } else if (data instanceof SearchResult.Other) {
            showSnackbar(((SearchResult.Other) data).message());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<SearchResult> loader) {
        startLoading();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if (startDate != null) {
            outState.putLong(START_DATE_KEY, startDate.getTime());
        }

        if (startDate != null) {
            outState.putLong(END_DATE_KEY, endDate.getTime());
        }

        outState.putString(ORDER_KEY, order.name());
        outState.putString(QUERY_KEY, query);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        callbackCancellable.cancel();
        super.onDestroyView();
    }

    private static long getTimeNowInMillis() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }
}
