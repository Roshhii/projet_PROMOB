package com.example.friendsgame.other;

import static com.example.friendsgame.other.Utils.formatDate;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsgame.MainActivity;
import com.example.friendsgame.R;
import com.example.friendsgame.data.Attempt;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.AttemptViewHolder> {

    private final List<Attempt> attempts;

    public HistoryAdapter(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    @NonNull
    @Override
    public AttemptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_history, parent, false);
        return new HistoryAdapter.AttemptViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull AttemptViewHolder holder, int position) {

        Attempt item = attempts.get(position);

        holder.tvTotal.setText(String.valueOf(item.getEarned()));
        holder.tvDate.setText(formatDate(item.getCreatedTime()));
        holder.tvOpponents.setText(MainActivity.listGamers.toString());

        holder.cvParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO NOTHING
            }
        });

    }

    @Override
    public int getItemCount() {
        return attempts.size();
    }

    public static class AttemptViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOpponents, tvTotal, tvDate;
        public CardView cvParent;

        public AttemptViewHolder(View v) {
            super(v);
            tvTotal = v.findViewById(R.id.tvTotal);
            tvOpponents = v.findViewById(R.id.tvOpponents);
            tvDate = v.findViewById(R.id.tvDate);
            cvParent = v.findViewById(R.id.cvItemHistory);

        }
    }

}