package com.yowoo.newbuyhouse.slider;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Button;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.model.House;

public class AutoScrollViewPagerDemo extends BaseActivity {

    private AutoScrollViewPager viewPager;
    private TextView            indexText;

    private Button              innerViewPagerDemo;

    //private List<Integer>       imageIdList;
    //private List<String>       imageIdList;
    
    private List<House> houseList = new ArrayList<House>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.auto_scroll_view_pager_demo);

        viewPager = (AutoScrollViewPager)findViewById(R.id.view_pager);
        indexText = (TextView)findViewById(R.id.view_pager_index);

//        imageIdList = new ArrayList<String>();
//        imageIdList.add("http://static.ettoday.net/images/289/d289652.jpg");
//        imageIdList.add("http://img.epochtimes.com/i6/801271031361758.jpg");
//        imageIdList.add("http://www.sc.xinhuanet.com/content/2015-06/16/1115632692_14344263584051n.jpg");
//        viewPager.setAdapter(new ImagePagerAdapter(this, imageIdList).setInfiniteLoop(true));
//        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        
//        House house = new House();
//        house.name = "test1";
//        house.imgDefault = "http://static.ettoday.net/images/289/d289652.jpg";
//        houseList.add(house);
//        
//        house = new House();
//        house.name = "test2";
//        house.imgDefault = "http://img.epochtimes.com/i6/801271031361758.jpg";
//        houseList.add(house);
//        
//        house = new House();
//        house.name = "test3";
//        house.imgDefault = "http://www.sc.xinhuanet.com/content/2015-06/16/1115632692_14344263584051n.jpg";
//        houseList.add(house);
//        
//        viewPager.setAdapter(new CollectPagerAdapter(this, houseList).setInfiniteLoop(true));
//        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//
//        
//        viewPager.setInterval(2000);
//        viewPager.startAutoScroll();
//        viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % houseList.size());

        // the more properties whose you can set
        // // set whether stop auto scroll when touching, default is true
        // viewPager.setStopScrollWhenTouch(false);
        // // set whether automatic cycle when auto scroll reaching the last or first item
        // // default is true
        // viewPager.setCycle(false);
        // /** set auto scroll direction, default is AutoScrollViewPager#RIGHT **/
        // viewPager.setDirection(AutoScrollViewPager.LEFT);
        // // set how to process when sliding at the last or first item
        // // default is AutoScrollViewPager#SLIDE_BORDER_NONE
        // viewPager.setBorderProcessWhenSlide(AutoScrollViewPager.SLIDE_BORDER_CYCLE);
        // viewPager.setScrollDurationFactor(3);
        // viewPager.setBorderAnimation(false);

    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            indexText.setText(new StringBuilder().append((position) % houseList.size() + 1).append("/")
                    .append(houseList.size()));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        viewPager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start auto scroll when onResume
        viewPager.startAutoScroll();
    }
}