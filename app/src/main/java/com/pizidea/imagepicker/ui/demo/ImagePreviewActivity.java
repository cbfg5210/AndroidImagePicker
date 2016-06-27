/*
 *
 *  * Copyright (C) 2015 Eason.Lai (easonline7@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.pizidea.imagepicker.ui.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pizidea.imagepicker.bean.ImageItem;
import com.pizidea.imagepicker.util.AndroidImagePicker;
import com.pizidea.imagepicker.util.GlideImagePresenter;
import com.pizidea.imagepickerDemo.R;

import java.util.List;

public class ImagePreviewActivity extends FragmentActivity implements View.OnClickListener,AndroidImagePicker.OnImageSelectedListener{
    private static final String TAG = ImagePreviewActivity.class.getSimpleName();
    TextView mTitleCount;
    CheckBox mCbSelected;
    TextView mBtnOk;

    List<ImageItem> mImageList;
    AndroidImagePicker androidImagePicker;

    ViewPager mViewPager;
    TouchImageAdapter mAdapter;
    private int mCurrentItemPosition = 0;
    private View topBar;
    private View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pre);

        androidImagePicker = AndroidImagePicker.getInstance();
        androidImagePicker.addOnImageSelectedListener(this);//必须要设置

        mImageList = AndroidImagePicker.getInstance().getImageItemsOfCurrentImageSet();
        mCurrentItemPosition = getIntent().getIntExtra(AndroidImagePicker.KEY_PIC_SELECTED_POSITION, 0);

        initView();
    }

    private void initView() {
        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mCbSelected = (CheckBox) findViewById(R.id.btn_check);

        mTitleCount = (TextView) findViewById(R.id.tv_title_count);
        mTitleCount.setText("1/" + mImageList.size());

        int selectedCount = AndroidImagePicker.getInstance().getSelectImageCount();
        onImageSelected(0,null,selectedCount,androidImagePicker.getSelectLimit());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mAdapter = new TouchImageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItemPosition, false);

        ImageItem item = mImageList.get(mCurrentItemPosition);
        boolean isSelected =androidImagePicker.isSelect(mCurrentItemPosition,item);
        onImagePageSelected(mCurrentItemPosition, mImageList.get(mCurrentItemPosition), isSelected);

        topBar = findViewById(R.id.top_bar);
        bottomBar = findViewById(R.id.bottom_bar);

        setListeners();
    }

    private void setListeners(){
        mBtnOk.setOnClickListener(this);
        findViewById(R.id.btn_backpress).setOnClickListener(this);

        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (androidImagePicker.getSelectImageCount() > androidImagePicker.getSelectLimit()) {
                    if (mCbSelected.isChecked()) {
                        mCbSelected.toggle();
                        String toast = getResources().getString(R.string.you_have_a_select_limit, androidImagePicker.getSelectLimit());
                        Toast.makeText(ImagePreviewActivity.this, toast, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectCurrent(isChecked);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mCurrentItemPosition = position;
                ImageItem item = mImageList.get(mCurrentItemPosition);
                boolean isSelected = androidImagePicker.isSelect(position, item);

                onImagePageSelected(mCurrentItemPosition,item,isSelected);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onImageSelected(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        if(selectedItemsCount > 0){
            mBtnOk.setEnabled(true);
            mBtnOk.setText(getResources().getString(R.string.select_complete,selectedItemsCount,maxSelectLimit));
        }else{
            mBtnOk.setText(getResources().getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }
        Log.i(TAG, "=====EVENT:onImageSelected");
    }

    private void onImagePageSelected(int position, ImageItem item,boolean isSelected) {
        mTitleCount.setText(position + 1 + "/" + mImageList.size());
        mCbSelected.setChecked(isSelected);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pic_rechoose:
                finish();
                break;
            case R.id.btn_ok:
                setResult(RESULT_OK);// select complete
                finish();
                break;
            case R.id.btn_backpress:
                finish();
                break;
            default:
                break;
        }
    }

    public void selectCurrent(boolean isCheck) {
        ImageItem item = mImageList.get(mCurrentItemPosition);
        boolean isSelect = androidImagePicker.isSelect(mCurrentItemPosition, item);
        if(isCheck&&!isSelect){
            AndroidImagePicker.getInstance().addSelectedImageItem(mCurrentItemPosition, item);
            return;
        }
        if(!isCheck&&isSelect){
            AndroidImagePicker.getInstance().deleteSelectedImageItem(mCurrentItemPosition, item);
        }
    }

    class TouchImageAdapter extends FragmentStatePagerAdapter {
        public TouchImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public Fragment getItem(int position) {
            SinglePreviewFragment fragment = new SinglePreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SinglePreviewFragment.KEY_URL, mImageList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @SuppressLint("ValidFragment")
    private class SinglePreviewFragment extends Fragment {
        public static final String KEY_URL = "key_url";
        private ImageView imageView;
        private String url;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();

            ImageItem imageItem = (ImageItem) bundle.getSerializable(KEY_URL);
            url = imageItem.path;

            imageView = new ImageView(ImagePreviewActivity.this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTopBottomBarVisibility();
                }
            });

            GlideImagePresenter.loadFileImage(imageView,url);
        }

        private void setTopBottomBarVisibility(){
            Animation topBarAnim;
            Animation bottomBarAnim;
            int visibility;
            if (topBar.getVisibility() == View.VISIBLE) {
                topBarAnim= AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.top_out);
                bottomBarAnim=AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.fade_out);
                visibility=View.GONE;
            } else {
                topBarAnim=AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.top_in);
                bottomBarAnim=AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.fade_in);
                visibility=View.VISIBLE;
            }
            topBar.setAnimation(topBarAnim);
            bottomBar.setAnimation(bottomBarAnim);
            topBar.setVisibility(visibility);
            bottomBar.setVisibility(visibility);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return imageView;
        }
    }
}