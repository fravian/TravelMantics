package francis.mariki.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder>  {
    public static final String DEAL_SELECTED = "francis.mariki.travelmantics.DEAL_SELECTED";
    public static final String TRAVELDEALS_DB_REFFERENCE = "traveldeals";
    private ArrayList<TravelDeals> deals;
    private FirebaseDatabase mFireBaseDataBase;
    private DatabaseReference mDataBaseReference;
    private ChildEventListener mChildEventListener;
    private ImageView imageView;


    public DealsAdapter(){
        mFireBaseDataBase=FireBaseUtility.mFireBaseDatabase;
        mDataBaseReference=FireBaseUtility.mDatabaseReference;
        deals=FireBaseUtility.mDeals;
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeals travelDeals=dataSnapshot.getValue(TravelDeals.class);
                Log.d("Deal",travelDeals.getTitle());
                travelDeals.setId(dataSnapshot.getKey());
                //addition of children is done when there is insertion or when an activity is started
                if (dataSnapshot.hasChildren())
                deals.add(travelDeals);
                notifyItemInserted(deals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View itemView= LayoutInflater.from(context).inflate(R.layout.list_view,parent,false);
        Log.d("on view Holder","i was called");
        return new ViewHolder(itemView);
    }
    public ArrayList<TravelDeals> getDeals() {
        return deals;
    }

    public void setDeals(ArrayList<TravelDeals> deals) {
        this.deals = deals;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelDeals deal= deals.get(position);
        Log.d("on bind ","i was called");
        holder.bind(deal);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return deals.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         TextView text_title;
         TextView text_description;
         TextView text_price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_title=itemView.findViewById(R.id.text_title);
            text_description=itemView.findViewById(R.id.text_description);
            text_price=itemView.findViewById(R.id.text_price);
            imageView=itemView.findViewById(R.id.image_deal);
            itemView.setOnClickListener(this);

        }

        public void bind(TravelDeals deal){
            Log.d("Check Ob",deal.getTitle());
            text_title.setText(deal.getTitle());
            text_description.setText(deal.getDescription());
            text_price.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        public void showImage(String Url){
            if(Url!=null&& Url.isEmpty()==false){
                Picasso.get()
                        .load(Url)
                        .resize(160,160)
                        .centerCrop()
                        .into(imageView);
            }
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            TravelDeals dealSelected=deals.get(position);
            Intent intent=new Intent(v.getContext(),DealsActivity.class);
            intent.putExtra(DEAL_SELECTED,dealSelected);
            v.getContext().startActivity(intent);
        }
    }

}
