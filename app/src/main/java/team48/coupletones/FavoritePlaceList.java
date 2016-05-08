package team48.coupletones;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zhuyupei on 5/7/16.
 */
public class FavoritePlaceList {
    ArrayList<FavoritePlace> list;

    public FavoritePlaceList(){
        list = new ArrayList<>();
    }

    private int find (FavoritePlace favoritePlace){

        int i;
        for(i = 0; i < list.size(); i++){
            if(favoritePlace.compareTo(list.get(i))==0)
                break;
        }
        return i;
    }

    public boolean add(FavoritePlace favoritePlace){
        if(find(favoritePlace)==list.size()) {
            list.add(favoritePlace);
            return true;
        }else
            return false;
    }

    public boolean delete (FavoritePlace favoritePlace){
        if(find(favoritePlace)==list.size())
            return false;
        else{
            list.remove(find(favoritePlace));
            return true;
        }
    }

    public FavoritePlace[] getArray(){
        FavoritePlace[] array = new FavoritePlace[list.size()];
        return list.toArray(array);
    }

}
