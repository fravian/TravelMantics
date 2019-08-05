package francis.mariki.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealsActivity extends AppCompatActivity {
    public static final int PICTURE_RESULT = 42;
    private FirebaseDatabase mFireBaseDatabase;
   private DatabaseReference mDataBaseReference;
   EditText textTitle;
   EditText textPrice;
   EditText textDescritpion;
   ImageView  imageView;
    private TravelDeals deals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        textPrice=findViewById(R.id.text_price);
        textTitle=findViewById(R.id.text_title);
        textDescritpion=findViewById(R.id.text_description);
        imageView=findViewById(R.id.image);
//        FireBaseUtility.getInstance(DealsAdapter.TRAVELDEALS_DB_REFFERENCE,this);
        mFireBaseDatabase=FireBaseUtility.mFireBaseDatabase;
        mDataBaseReference=FireBaseUtility.mDatabaseReference;
        Intent intent=getIntent();
        TravelDeals deal= (TravelDeals) intent.getParcelableExtra(DealsAdapter.DEAL_SELECTED);
        if(deal==null){
            deals = new TravelDeals();
        }else {
            deals = deal;
        }
           textTitle.setText(deals.getTitle());
           textPrice.setText(deals.getPrice());
           textDescritpion.setText(deals.getDescription());
           showImage(deals.getImageUrl());

        Button btnImage=findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
             intent.setType("image/jpeg");
             intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
             startActivityForResult(intent.createChooser(intent,getResources().getString(R.string.Insert_picture_title)), PICTURE_RESULT);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICTURE_RESULT && resultCode==RESULT_OK && data!=null&& data.getData()!=null){
            Uri imageUri= data.getData();
            final StorageReference storageReference=FireBaseUtility.mStorageReference.child(imageUri.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(imageUri);


           Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
               @Override
               public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                   if(!task.isSuccessful()){
                       throw  task.getException();
                   }
                   return storageReference.getDownloadUrl();
               }
           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                   if(task.isSuccessful()){
                       Uri downloadUri=task.getResult();
                       if(downloadUri!=null) {
                           deals.setImageName(downloadUri.getLastPathSegment().toString());
                           deals.setImageUrl(downloadUri.toString());
                       }
                       showImage(downloadUri.toString());
                   }else{
                       Toast.makeText(DealsActivity.this,R.string.upload_feedback,Toast.LENGTH_LONG).show();
                       Log.d("UpLoadFailure","Up load has failed ");
                   }
               }
           });

           /* Log.d("url",imageUri.toString());
            uploadTask.addOnFailureListener(this,new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Mariki","failed to upload" );


                }
            }).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Nathan","your image is uploaded");

                    String url=storageReference.getDownloadUrl().toString();
                    deals.setImageUrl(url);
                    showImage(url);
                }
            });*/

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FireBaseUtility.detachAuthListerner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FireBaseUtility.attachAuthListerner();
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id= item.getItemId();
        if(id==R.id.save_menu){
            saveDeal();
            Toast.makeText(this,"Deal Saved",Toast.LENGTH_LONG).show();
            clean();
            backToListing();
            return true;
        }else if(id==R.id.delete_item_menu){
            deleteDeal();
            Toast.makeText(this,"Deal Deleted",Toast.LENGTH_LONG).show();
            backToListing();
            return  true;
        }else {
            return  super.onOptionsItemSelected(item);
        }

    }

    private void saveDeal() {

       deals.setTitle(textTitle.getText().toString());
       deals.setPrice(textPrice.getText().toString());
       deals.setDescription(textDescritpion.getText().toString());
       if(deals.getId()==null){
           mDataBaseReference.push().setValue(deals);
       }else{
           mDataBaseReference.child(deals.getId()).setValue(deals);
       }

//       TravelDeals deal=TravelDeals.getInstance(title,description,price,"");

      clean();

    }
    private  void  deleteDeal(){
        if(deals.getId()!=null)
        mDataBaseReference.child(deals.getId()).removeValue();

        if(deals.getImageName()!=null&&deals.getImageName().isEmpty()==false){
            StorageReference storageReference=FireBaseUtility.mStorageReference.getStorage().getReference().child(deals.getImageName());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DealsActivity.this,R.string.delete_feedback,Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DealsActivity.this,R.string.delete_feedback_failure,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void backToListing(){
        Intent intent =new Intent(this, ListActivity.class);
        startActivity(intent);
    }
    private void clean() {
        textPrice.setText("");
        textTitle.setText("");
        textDescritpion.setText("");
        textTitle.requestFocus();
    }

    public void showImage(String Url){
        if(Url!=null && Url.isEmpty()==false){
            int width= Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(Url)
                    .resize(width,width*2/3)
                    .centerCrop()
                    .into(imageView);

        }
    }
    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     *
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);



        if(FireBaseUtility.isAdmin){
            menu.findItem(R.id.save_menu).setVisible(true);
            menu.findItem(R.id.delete_item_menu).setVisible(true);
            findViewById(R.id.btnImage).setEnabled(true);
            enableTextField(true);
        }else {
            menu.findItem(R.id.save_menu).setVisible(false);
            menu.findItem(R.id.delete_item_menu).setVisible(false);
            findViewById(R.id.btnImage).setEnabled(false);
            enableTextField(false);
        }


        return true;
    }


    public void enableTextField(boolean enable){
            textDescritpion.setEnabled(enable);
            textPrice.setEnabled(enable);
            textTitle.setEnabled(enable);
    }
}
