package com.said.oubella.so.artanddesign.helpers;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class ItemDecorator extends RecyclerView.ItemDecoration {

    private final int spacing;

    public ItemDecorator(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, RecyclerView.State state) {
        final int position = parent.getChildViewHolder(view).getAdapterPosition();
        final int itemCount = state.getItemCount();
        outRect.bottom = position == itemCount - 1 ? spacing : 0;
        outRect.right = spacing;
        outRect.left = spacing;
        outRect.top = spacing;
    }
}