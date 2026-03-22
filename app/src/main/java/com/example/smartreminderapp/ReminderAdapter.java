package com.example.smartreminderapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter for displaying reminders
 */
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;

    public ReminderAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);

        holder.tvTitle.setText(reminder.getTitle());
        holder.tvDescription.setText(reminder.getDescription());
        holder.tvTime.setText(reminder.getTime());

        // Display status (extra field for Roll No 41)
        String status = reminder.getStatus();
        if (status != null && !status.isEmpty()) {
            holder.tvStatus.setText(status);
        } else {
            holder.tvStatus.setText("Not Set");
        }

        // Display location
        String location = reminder.getLocation();
        if (location != null && !location.isEmpty()) {
            holder.tvLocation.setText(location);
        } else {
            holder.tvLocation.setText("Location not available");
        }

        // Set status indicator color based on status
        if (status != null) {
            switch (status.toLowerCase()) {
                case "completed":
                    holder.statusIndicator.setBackgroundResource(R.drawable.circle_indicator);
                    holder.statusIndicator.getBackground().setTint(0xFF10B981);
                    break;
                case "in progress":
                    holder.statusIndicator.getBackground().setTint(0xFFF59E0B);
                    break;
                default:
                    holder.statusIndicator.getBackground().setTint(0xFF6C63FF);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    /**
     * ViewHolder class for reminder items
     */
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTime, tvStatus, tvLocation;
        View statusIndicator;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvReminderTitle);
            tvDescription = itemView.findViewById(R.id.tvReminderDescription);
            tvTime = itemView.findViewById(R.id.tvReminderTime);
            tvStatus = itemView.findViewById(R.id.tvReminderStatus);
            tvLocation = itemView.findViewById(R.id.tvReminderLocation);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }
    }
}
