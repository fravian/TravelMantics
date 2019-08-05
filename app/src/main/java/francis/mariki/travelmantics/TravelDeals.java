package francis.mariki.travelmantics;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TravelDeals implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;



    private String imageName;
    private static TravelDeals travelDeals;

    public TravelDeals(String id,String title, String description, String price, String imageUrl,String imageName) {
        this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
        this.setImageName(imageName);
    }

    public TravelDeals() {

    }
    public TravelDeals(Parcel parcel) {
        id=parcel.readString();
       title=parcel.readString();
       price=parcel.readString();
       description=parcel.readString();
       imageUrl=parcel.readString();
       imageName=parcel.readString();
    }

    public String getId() {
        return id;
    }
    public static TravelDeals getInstance(){
        if(travelDeals==null) {
            travelDeals = new TravelDeals();
        }
        return travelDeals;
    }
    //method overloading
    public static TravelDeals getInstance(String id,String title, String description, String price, String imageUrl,String imageName){
        if (travelDeals==null) {
            travelDeals = new TravelDeals(id,title, description, price, imageUrl,imageName);
        }
        return travelDeals;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(imageName);

    }
    public  static final  Creator<TravelDeals> CREATOR =new Creator<TravelDeals>() {
        @Override
        public TravelDeals createFromParcel(Parcel source) {
            return new TravelDeals(source);
        }

        @Override
        public TravelDeals[] newArray(int size) {
            return new TravelDeals[size];
        }
    };
}
