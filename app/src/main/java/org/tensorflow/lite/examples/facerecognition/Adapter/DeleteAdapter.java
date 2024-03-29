package org.tensorflow.lite.examples.facerecognition.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.tensorflow.lite.examples.facerecognition.TimerCount;
import org.tensorflow.lite.examples.facerecognition.fragments.DeleteFragment.BlankFragment;
import org.tensorflow.lite.examples.facerecognition.fragments.DeleteFragment.DeleteAllFragment;

public class DeleteAdapter extends FragmentStateAdapter {

    private static final String TAG_NAME = "m_name";
    private static final String TAG_TEMP = "m_temp";
    private static final String TAG_PRICE = "m_price";
    public int mCount;

    public DeleteAdapter(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        int index = getRealPosition(position);

        for(int i=0; i<mCount-1; i++){

            if(index == i) return new BlankFragment(
                    TimerCount.DELETE_MENU_ARRAY.get(i).get(TAG_TEMP),
                    TimerCount.DELETE_MENU_ARRAY.get(i).get(TAG_NAME),
                    TimerCount.DELETE_MENU_ARRAY.get(i).get(TAG_PRICE));

        }
        return new DeleteAllFragment();


    }

    private int getRealPosition(int position) {
        return position % mCount;
    }


    @Override
    public int getItemCount() {
        return 2000;
    }
}
