package com.example.samriddha.firebaseauthentication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdaptar extends RecyclerView.Adapter<RecyclerViewAdaptar.RecyclerViewHolder> {

    private Context mContext ;
    private List<Upload> muploadClass ;
    private OnItemClickListner clickListner ;

    public RecyclerViewAdaptar(Context mContext, List<Upload> muploadClass) {
        this.mContext = mContext;
        this.muploadClass = muploadClass;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.rec_image_show , parent , false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        Upload currentUpload = muploadClass.get(position);
        holder.imageName.setText(currentUpload.getName());
        Picasso.with(mContext).load(currentUpload.getImageUrl()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        return muploadClass.size();
    }

    public void setOnItemClickListner(OnItemClickListner clickListner){
        this.clickListner = clickListner ;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener
    ,MenuItem.OnMenuItemClickListener
    {

        TextView imageName ;
        ImageView mImage ;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.rectextviewid);
            mImage = itemView.findViewById(R.id.reciamgeviewid);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if (clickListner!=null){
                int pos = getAdapterPosition();
                if (pos!=RecyclerView.NO_POSITION){
                    clickListner.OnSimpleClick(pos);
                }

            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Choose Action");
            MenuItem doNothing = menu.add(Menu.NONE , 1 , 1 ,"Do Nothing");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete Image");

            doNothing.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (clickListner!=null){
                int pos = getAdapterPosition();
                if (pos!=RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            clickListner.OnDoNothingClick(pos);
                            return true;
                        case 2:
                            clickListner.OnDeleteImageClick(pos);
                            return true ;
                    }
                }

            }
            return false;
        }
    }

}
