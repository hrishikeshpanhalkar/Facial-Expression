package com.example.facialexpression.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facialexpression.R;
import com.example.facialexpression.model.MusicModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    Context context;
    ArrayList<MusicModel> arrayList;
    private SelectedUser selectedUser;

    public MusicAdapter(Context context, ArrayList<MusicModel> arrayList, SelectedUser selectedUser) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectedUser=selectedUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_update_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(arrayList.get(position).getSong());
        Picasso.get().load(arrayList.get(position).getCover_image()).placeholder(R.drawable.ic_account).into(holder.circleImageView);
    }

    public interface SelectedUser{
        void selectedUser(MusicModel musicModel);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilepic);
            textView = itemView.findViewById(R.id.textname);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    selectedUser.selectedUser(arrayList.get(getAdapterPosition()));
                }
            });
        }
    }
}
