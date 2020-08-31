package org.perfit.fitness.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.perfit.fitness.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class DashBoard extends Fragment {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final int NUM_PAGES = 0;
    private LinearLayout mDotLayout;
    public static final String YOUTUBE_API_KEY = "AIzaSyCIKAac1hrXKaDlRX8QUjSF7IVpme9HP6A";

    private final int[] myListViewImage = new int[]{R.drawable.squats_thumb, R.drawable.pushup_thumb, R.drawable.plank_thumb};

    private final int[] myImageList = new int[]{R.drawable.dashboard_image_one, R.drawable.dashboard_image_two, R.drawable.dashboard_image_three};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        mDotLayout = view.findViewById(R.id.dashboardDotLayout);
        ArrayList<DashboardModel> imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        mPager = view.findViewById(R.id.dashBoard_top_auto_slider);
        mPager.setAdapter(new SlidingImage_Adapter(getContext(), imageModelArrayList));
        addDotsIndicator(0);
        mPager.addOnPageChangeListener(viewListener);

        HorizontalListView horizontalListView = view.findViewById(R.id.HorizontalListView);

        ArrayList<Dashboard_List_Model> listImageModelArrayList = populateData();
        Log.d("hjhjh", listImageModelArrayList.size()+"");
        HorizontalListAdapter customeAdapter = new HorizontalListAdapter(getContext(), listImageModelArrayList);
        horizontalListView.setAdapter(customeAdapter);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES || currentPage == 3) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, false);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        return view;
    }

    private ArrayList<DashboardModel> populateList(){

        ArrayList<DashboardModel> list = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            DashboardModel imageModel = new DashboardModel();
            imageModel.setImage_placeholders(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }

    private ArrayList<Dashboard_List_Model> populateData(){

        ArrayList<Dashboard_List_Model> list = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            Dashboard_List_Model imageModel = new Dashboard_List_Model();
            imageModel.setList_Images(myListViewImage[i]);
            list.add(imageModel);
        }

        return list;
    }

    private void addDotsIndicator(int position){
        TextView[] mdots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i< mdots.length; i++){
            mdots[i]=new TextView(getContext());
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(getResources().getColor(R.color.dashboardNotSelectedColor));

            mDotLayout.addView(mdots[i]);
        }
        if (mdots.length>0) {
            mdots[position].setTextColor(getResources().getColor(R.color.dotsColor));
        }
    }

    private final ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
