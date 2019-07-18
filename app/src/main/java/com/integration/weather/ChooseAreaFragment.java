package com.integration.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.integration.weather.db.City;
import com.integration.weather.db.County;
import com.integration.weather.db.Province;
import com.integration.weather.dummy.DummyContent.DummyItem;
import com.integration.weather.utils.HttpUtils;
import com.integration.weather.utils.LogUtils;
import com.integration.weather.utils.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChooseAreaFragment extends Fragment implements MyItemRecyclerViewAdapter.OnItemCLockListener {
    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVE_COUNTY = 2;


    private TextView mTitle;
    private TextView mBackButton;
    private RecyclerView mRecyclerView;
    private MyItemRecyclerViewAdapter mAdapter;

    private ListView mListView;

//    private MyAdapter mListViewAdapter;

    private ArrayAdapter<String> mArrayAdapter;

    private List<String> mDataList = new ArrayList<>();

    private List<Province> mProvinces = new ArrayList<>();

    private List<City> mCities = new ArrayList<>();

    private List<County> mCounties = new ArrayList<>();

    private Province mSelectedProvince;

    private City mSelectedCity;

    private int currentLevel;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**
     1 列表 ；2 瀑布流
     */
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChooseAreaFragment() {
    }


    @SuppressWarnings("unused")
    public static ChooseAreaFragment newInstance(int columnCount) {
        ChooseAreaFragment fragment = new ChooseAreaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        mTitle = view.findViewById(R.id.title);
        mBackButton = view.findViewById(R.id.bt_back);
        mListView = view.findViewById(R.id.listView);

        mArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.fragment_item, mDataList);

        mListView.setAdapter(mArrayAdapter);

        /*mRecyclerView = view.findViewById(R.id.list);
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyItemRecyclerViewAdapter(mDataList, this.mListener,this);
            mRecyclerView.setAdapter(mAdapter);*/
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    mSelectedProvince = mProvinces.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    mSelectedCity = mCities.get(position);
                    queryCounties();
                }else if (currentLevel == LEVE_COUNTY){
                    String weatherId = mCounties.get(position).getWeatherId();

                    if (getActivity() instanceof  MainActivity){
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        getActivity().startActivity(intent);

                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mDrawerLayout.closeDrawer(GravityCompat.START);
                        activity.mRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        queryProvinces();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }else if (currentLevel == LEVE_COUNTY){
                    queryCities();
                }

            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(int position) {

    }
    private void queryCounties() {

        mTitle.setText(mSelectedCity.getCityName());

        mCounties = (ArrayList<County>) DataSupport.where("cityid = ?", String.valueOf(mSelectedCity.getCityId())).find(County.class);
//        mCounties = (ArrayList<County>) DataSupport.where("cityid = ?", String.valueOf(mSelectedCity.getCityCode())).find(County.class);
        LogUtils.i(TAG, "queryCounties: "+mCounties.toString());
        if (mCounties != null && mCounties.size() > 0){
            mDataList.clear();
            for (County county : mCounties) {
                mDataList.add(county.getCountyName());
            }
            mArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVE_COUNTY;
        }else {

            int cityCode = mSelectedCity.getCityCode();

            int provinceCode = mSelectedProvince.getProvinceCode();
            String address = HttpUtils.WEB_URL + provinceCode+"/"+ cityCode;

            queryFromServer(address, "county");
        }
    }
    private void queryCities() {
        mTitle.setText(mSelectedProvince.getProvinceName());

        mCities = (ArrayList<City>) DataSupport.where("provinceid = ?", String.valueOf(mSelectedProvince.getId())).find(City.class);
        LogUtils.i(TAG, "queryCities: "+mCities.toString());
        if (mCities.size() > 0){
            mDataList.clear();
            for (City city : mCities) {
                mDataList.add(city.getCityName());
            }
            mArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {

            int provinceCode = mSelectedProvince.getProvinceCode();

            String address = HttpUtils.WEB_URL + provinceCode;

            queryFromServer(address, "city");

        }

    }
    private void queryProvinces() {
        mTitle.setText("中国");

        mProvinces = DataSupport.findAll(Province.class);
        LogUtils.i(TAG, "queryProvinces: "+mProvinces.toString());
        if (mProvinces.size() > 0){
            mDataList.clear();
            for (Province province : mProvinces) {
                mDataList.add(province.getProvinceName());
            }
            mArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = HttpUtils.WEB_URL;
            queryFromServer(address, "province");
        }
    }

    /**
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        HttpUtils.setOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure( Call call,  IOException e) {
                LogUtils.i(TAG, "onFailure: "+e.getMessage().toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse( Call call,  Response response) throws IOException {

                String responseText = response.body().string();
                LogUtils.i(TAG, "queryFromServer: "+responseText);
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }
                if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, mSelectedProvince.getId());

                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, mSelectedCity.getCityId());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)){
                                queryProvinces();
                            }
                            if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
