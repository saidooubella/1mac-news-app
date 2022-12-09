package com.said.oubella.so.artanddesign.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.said.oubella.so.artanddesign.R;
import com.said.oubella.so.artanddesign.databinding.ArticleItemBinding;
import com.said.oubella.so.artanddesign.helpers.OnArticleClickListener;
import com.said.oubella.so.artanddesign.models.Article;

public final class ArticlesAdapter extends ListAdapter<Article, ArticlesAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Article> ARTICLES_DIFF_CALLBACK = new DiffUtil.ItemCallback<Article>() {

        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getPostId().equals(newItem.getPostId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final OnArticleClickListener listener;
    private final RequestManager manager;

    public ArticlesAdapter(RequestManager manager, OnArticleClickListener listener) {
        super(ARTICLES_DIFF_CALLBACK);
        this.manager = manager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.from(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), manager);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final OnArticleClickListener listener;
        private final ArticleItemBinding binding;

        private ViewHolder(ArticleItemBinding binding, OnArticleClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        @NonNull
        private static ViewHolder from(ViewGroup parent, OnArticleClickListener listener) {
            return new ViewHolder(ArticleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
        }

        private void bind(Article item, RequestManager manager) {

            final boolean isThumbAvailable = !item.getPostFields().getPostThumbnail().isEmpty();
            binding.thumbnail.setVisibility(isThumbAvailable ? View.VISIBLE : View.GONE);

            if (isThumbAvailable) {
                manager.load(item.getPostFields().getPostThumbnail()).error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder).into(binding.thumbnail);
            } else {
                manager.clear(binding.thumbnail);
            }

            manager.load(item.getPostTags().getAuthorPicture()).placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person).into(binding.authorImage);

            itemView.setOnClickListener(view -> listener.onClick(item));

            binding.trailText.setText(item.getPostFields().getPostTrailText());
            binding.authorName.setText(item.getPostTags().getAuthorName());
            binding.publishTime.setText(item.getPostPublicationDate());
            binding.sectionName.setText(item.getPostSectionName());
            binding.title.setText(item.getPostTitle());
        }
    }
}
