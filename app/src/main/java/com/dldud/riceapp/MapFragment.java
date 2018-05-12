package com.dldud.riceapp;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MapFragment extends Fragment implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener{

    Task task = new Task();
    String myString;
    double latitude, longitude;
    int j;

    private MapView mapView;

    ArrayList<ItemData> itemList = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //GPSTracker class
        GPSInfo gps;

        mapView = new MapView(getActivity());

        Button mapDialog, gotoMyPoint;
        final CameraUpdateFactory cameraUpdateFactory = new CameraUpdateFactory();

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        gps = new GPSInfo(getActivity());
        //GPS 사용유무
        if(gps.isGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude),1, true);

        try {
            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();
            task.jsonParser(myString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        MapReverseGeoCoder reverseGeoCoder = null;

        if (task.idx != null && !task.idx.isEmpty()) {
            String[] strlocationlat = task.locationlat.toArray(new String[task.locationlat.size()]);
            String[] strlocationlong = task.locationlong.toArray(new String[task.locationlong.size()]);
            double[] locationlat = new double[strlocationlat.length];
            double[] locationlong = new double[strlocationlong.length];
            for (int i = 0; i < strlocationlat.length; i++) {
                locationlat[i] = Double.parseDouble(strlocationlat[i]);
                locationlong[i] = Double.parseDouble(strlocationlong[i]);
            }
            for (int i = 0; i < locationlat.length; i++) {
                MapPOIItem customMarker = new MapPOIItem();
                customMarker.setItemName(task.title.get(i));
                customMarker.setTag(i + 1);
                customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(locationlat[i], locationlong[i]));
                customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                customMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                customMarker.setCustomImageResourceId(R.drawable.marker_red);


                reverseGeoCoder = new MapReverseGeoCoder(getString(R.string.kakao_app_key),
                        MapPoint.mapPointWithGeoCoord(locationlat[i], locationlong[i]),
                        this,
                        this.getActivity());

                reverseGeoCoder.startFindingAddress();

                ItemData newItem = new ItemData();
                newItem.setReverseGeoCoder(reverseGeoCoder);
                newItem.setiMarkerIndex(i + 1);

                itemList.add(newItem);

                mapView.addPOIItem(customMarker);
            }
        }



        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);

        mapDialog = (Button) v.findViewById(R.id.mapDialog);
        mapDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(),MapDialog.class));
            }
        });

        gotoMyPoint = (Button) v. findViewById(R.id.gotoMyPoint);
        gotoMyPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.moveCamera(cameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude),1));
            }
        });


        return v;
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) { // 주소 찾은 경우
        //mapReverseGeoCoder.onAddressFound();


        Log.e("map", s);


        for(ItemData m : itemList)
        {
            if(m.getReverseGeoCoder() == mapReverseGeoCoder) { // 현재 탐색중인 m이 현재 이벤트를 발생시킨 MRGC랑 같을 때
                if (m.getStrAddress() == null) {
                    m.setStrAddress(s);
                    //mapView.findPOIItemByTag(m.getiMarkerIndex()).setItemName("set"); // for debug
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*
                    List<ItemData> d = itemList.stream().filter(a -> a.getStrAddress() == s).collect(Collectors.toList());
                    List<ItemData> d2 = itemList.stream().filter(a -> a.getStrAddress() == s).map(ItemData::new).collect(Collectors.toList());
                    int itemCount = itemList.stream().filter(a -> a.getStrAddress() == s).collect(Collectors.toList()).size();*/
                    // 개씨발쓰레기같은좆드로이드스튜디오는씨발자바8문법지원안하니까자바8문법쓸생각도하지마라개씨발좆같은쓰레기새끼들똥이나쳐먹어라씨발

                    int itemCount = 0;
                    for(ItemData id : itemList)
                    {
                        if(s.equals(id.getStrAddress()))
                            itemCount++;

                        if (itemCount > 1) {
                            //           mapView.removePOIItem();
                            mapView.removePOIItem(mapView.findPOIItemByTag(m.getiMarkerIndex())); // 태그로 겹치는 주소의 POIItem 찾아서 마커를 지도에서 지움
                            break;
                        }
                    }
                }
                break;
            }
        }

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) { // 주소를 찾지 못한 경우

    }

}
