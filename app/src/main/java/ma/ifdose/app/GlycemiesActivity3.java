package ma.ifdose.app;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import ma.ifdose.app.Fragments.Fragment4;
import ma.ifdose.app.Fragments.OneFragment;
import ma.ifdose.app.Fragments.ThreeFragment;
import ma.ifdose.app.Fragments.TwoFragment;
import timber.log.Timber;


public class GlycemiesActivity3 extends AppCompatActivity {

    private static final String TAG = "GlycemiesActivity3";


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_glycemies3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(ma.ifdose.app.R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(ma.ifdose.app.R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Timber.i("tab count :" + tabLayout.getTabCount());
        int defaultValue = 0;
        int page = getIntent().getIntExtra("fragment", defaultValue);
//        viewPager.setCurrentItem(page);
        tabLayout.getTabAt(page).select();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "الإفطار");
        adapter.addFragment(new TwoFragment(), "الغداء");
        adapter.addFragment(new ThreeFragment(), "وجبة خفيفة");
        adapter.addFragment(new Fragment4(), "العشاء ");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
