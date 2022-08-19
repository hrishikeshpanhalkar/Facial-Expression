package com.example.facialexpression.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facialexpression.R;
import com.example.facialexpression.model.User_update;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Update_Adapter extends RecyclerView.Adapter<User_Update_Adapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<User_update> user_updates;
    private List<User_update> filteroutput;
    private SelectedUser selectedUser;

    public User_Update_Adapter(Context c,ArrayList<User_update> t, SelectedUser selectedUser){
        this.context=c;
        this.user_updates=t;
        this.filteroutput=user_updates;
        this.selectedUser=selectedUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_update_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.Name.setText(user_updates.get(position).getName());
            holder.Email.setText(user_updates.get(position).getEmail());
            holder.profilePic.setBackground(null);
            Picasso.get().load(user_updates.get(position).getImageURL()).placeholder(R.drawable.ic_account).into(holder.profilePic);
            //holder.linearLayout.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_animation));
        }

    @Override
    public int getItemCount() {
        return user_updates.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                if(constraint == null | constraint.length() == 0){
                    filterResults.count=filteroutput.size();
                    filterResults.values=filteroutput;
                }else {
                    String searchchar=constraint.toString().toLowerCase();
                    List<User_update> resultData=new ArrayList<>();
                    for(User_update user_update: filteroutput){
                        if(user_update.getName().toLowerCase().contains(searchchar)){
                            resultData.add(user_update);
                        }
                    }
                    filterResults.count=resultData.size();
                    filterResults.values=resultData;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                user_updates= (ArrayList<User_update>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public interface SelectedUser{
        void selectedUser(User_update user_update);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Name,Email;
        CircleImageView profilePic;
       // LinearLayout linearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Name=(TextView)itemView.findViewById(R.id.textname);
            Email=(TextView)itemView.findViewById(R.id.textemail);
            profilePic=(CircleImageView) itemView.findViewById(R.id.profilepic);
           // linearLayout=(LinearLayout) itemView.findViewById(R.id.linearlayout);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    selectedUser.selectedUser(user_updates.get(getAdapterPosition()));
                }
            });
        }
    }

}