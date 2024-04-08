package com.melodify.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melodify.music.databinding.ItemFeedbackBinding;
import com.melodify.music.model.Feedback;

import java.util.List;

public class AdminFeedbackAdapter extends RecyclerView.Adapter<AdminFeedbackAdapter.AdminFeedbackViewHolder> {

    private final List<Feedback> mListFeedback;

    public AdminFeedbackAdapter(List<Feedback> mListFeedback) {
        this.mListFeedback = mListFeedback;
    }

    @NonNull
    @Override
    public AdminFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeedbackBinding itemFeedbackBinding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AdminFeedbackViewHolder(itemFeedbackBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFeedbackViewHolder holder, int position) {
        Feedback feedback = mListFeedback.get(position);
        if (feedback == null) {
            return;
        }
        holder.mItemFeedbackBinding.tvEmail.setText(feedback.getEmail());
        holder.mItemFeedbackBinding.tvFeedback.setText(feedback.getComment());
    }

    @Override
    public int getItemCount() {
        if (mListFeedback != null) {
            return mListFeedback.size();
        }
        return 0;
    }

    public static class AdminFeedbackViewHolder extends RecyclerView.ViewHolder {

        private final ItemFeedbackBinding mItemFeedbackBinding;

        public AdminFeedbackViewHolder(@NonNull ItemFeedbackBinding itemFeedbackBinding) {
            super(itemFeedbackBinding.getRoot());
            this.mItemFeedbackBinding = itemFeedbackBinding;
        }
    }
}
