// 지도 띄우기
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

var container = document.getElementById('map');

var options = { // 지도의 중심 좌표
    center: new kakao.maps.LatLng(37.50185785121449, 126.78766910206697),
    level: 3
};

// 지도 생성
var map = new kakao.maps.Map(container, options);

// 장소 검색 객체 생성
var ps = new kakao.maps.services.Places(map);

// 키워드 주소 검색 함수
function searchCon() {
    let keyword = $('.location-input').find('input').val();
    ps.keywordSearch(keyword, placesSearchCB, {
        category_group_code: 'CS2'
    });
}

// 키워드 검색 완료 시 호출되는 콜백 함수
function placesSearchCB (data, status) {
    if (status === kakao.maps.services.Status.OK) {

        var bounds = new kakao.maps.LatLngBounds();

        for (var i=0; i<data.length; i++) {
            displayMarker(data[i]);
            bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
        }
        map.setBounds(bounds);
    }
}

// 지도에 마커 표시하는 함수
function displayMarker(place) {

    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
    });

    var locationFinal = $('.location-final');
    var locationInput = $('.location-input');

    kakao.maps.event.addListener(marker, 'click', function() {

        infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
        infowindow.open(map, marker);

        locationInput.val(place.place_name);
        locationFinal.text(place.place_name);
        locationFinal.removeClass(('no-location'));

    });
}

export { searchCon };