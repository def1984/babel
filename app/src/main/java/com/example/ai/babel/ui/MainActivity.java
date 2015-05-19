package com.example.ai.babel.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.example.ai.babel.ui.widget.MyFloatingActionButton;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;

import com.example.ai.babel.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ListView postList;
    private MyFloatingActionButton fabBtn;
    private Boolean isCheck = false;
    private ActionBarDrawerToggle mDrawerToggle;
    private Button logoutButton ;
    private AVUser currentUser = AVUser.getCurrentUser();
    private LinearLayout mLinearLayout;
    private CircleImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UpDataPostList().execute();
        profileImage= (CircleImageView) findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
    }




    class UpDataPostList extends AsyncTask<Void, Integer, Boolean> {
        ArrayList<HashMap<String, Object>> listItemMain = new ArrayList<HashMap<String, Object>>();
        ArrayList<String> postObjIDList = new ArrayList<String>();
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            AVQuery<AVObject> query = AVQuery.getQuery("Post");
            postList = (ListView) findViewById(R.id.post_list);

            query.whereEqualTo("userObjectId", currentUser);
            List<AVObject> commentList = null;
            try {
                commentList = query.find();
            } catch (AVException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < commentList.size(); i++) {
                HashMap<String, Object> allDrawNavTag = new HashMap<String, Object>();
                allDrawNavTag.put("postImage", R.drawable.ic_tick);//加入图片
                allDrawNavTag.put("postTitle", commentList.get(i).getString("title"));
                allDrawNavTag.put("postContent", commentList.get(i).getString("content"));
                listItemMain.add(allDrawNavTag);
                postObjIDList.add(commentList.get(i).getObjectId());
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            intiView();
            logOut();
            fabBtnAm();
            addNewPost();
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(MainActivity.this, listItemMain,//需要绑定的数据
                    R.layout.content_item,
                    new String[]{"postImage", "postTitle", "postContent"},
                    new int[]{R.id.contentImage, R.id.postTitle, R.id.postContent}
            );


            postList.setAdapter(mSimpleAdapter);//为ListView绑定适配器

            postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("objectId", postObjIDList.get(position));
                    intent.setClass(MainActivity.this, DetailActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }



    private void intiView() {

        // 在这里我们获取了主题暗色，并设置了status bar的颜色
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        int color = typedValue.data;

        // 注意setStatusBarBackgroundColor方法需要你将fitsSystemWindows设置为true才会生效
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(color);
        mToolbar = getActionBarToolbar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void showAllMinFab() {
        Animation minFabSet = AnimationUtils.loadAnimation(MainActivity.this, R.anim.min_fab_anim);
        mLinearLayout = (LinearLayout) findViewById(R.id.mini_fab_content);
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            FloatingActionButton mini = (FloatingActionButton) mLinearLayout.getChildAt(i);
            mini.setVisibility(View.VISIBLE);
            mini.startAnimation(minFabSet);
        }
    }

    private void hideAllMinFab() {
        mLinearLayout = (LinearLayout) findViewById(R.id.mini_fab_content);
        Animation minFabSetRve = AnimationUtils.loadAnimation(MainActivity.this, R.anim.min_fab_anim_rev);
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            FloatingActionButton mini = (FloatingActionButton) mLinearLayout.getChildAt(i);
            minFabSetRve.setFillAfter(true);
            mini.startAnimation(minFabSetRve);
        }
    }


    private void fabBtnAm() {
        fabBtn = (MyFloatingActionButton) findViewById(R.id.fab);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animationSet = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_anim);
                ImageView fabImageView = (ImageView) findViewById(R.id.img_fab);
                if (!isCheck) {
                    animationSet.setFillAfter(true);
                    showAllMinFab();
                    fabImageView.startAnimation(animationSet);
                    isCheck = true;
                } else {
                    animationSet.setInterpolator(new ReverseInterpolator());
                    fabImageView.startAnimation(animationSet);
                    hideAllMinFab();
                    isCheck = false;
                }
            }
        });
    }

    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }

    private void logOut() {
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                Intent interIntent = new Intent(MainActivity.this, InterActivity.class);
                startActivity(interIntent);
                finish();
            }
        });
    }

    private void addNewPost(){
        FloatingActionButton addNewPost= (FloatingActionButton) findViewById(R.id.add_new_post);
        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AddNewPost.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search_into);
        searchItem.setIcon(android.support.v7.appcompat.R.drawable.abc_ic_search_api_mtrl_alpha);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_search_into:
                Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SearchActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
